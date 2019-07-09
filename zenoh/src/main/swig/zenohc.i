%module zenohc 


/*----- typemap for basic types -------*/
%include "stdint.i"


/*----- typemap for payload+length IN argument to ByteBuffer -------*/
%typemap(jni) (const unsigned char *payload, size_t length) "jobject"
%typemap(jtype) (const unsigned char *payload, size_t length) "java.nio.ByteBuffer"
%typemap(jstype) (const unsigned char *payload, size_t length) "java.nio.ByteBuffer"
%typemap(javain, pre="  assert $javainput.isDirect() : \"Buffer must be allocated direct.\";") (const unsigned char *payload, size_t length) "$javainput"
%typemap(javaout) (const unsigned char *payload, size_t length) {
  return $jnicall;
}
%typemap(in) (const unsigned char *payload, size_t length) {
  jbuffer_to_native(jenv, $input, $1, $2);
}
%typemap(freearg) (const unsigned char *payload, size_t length) {
  release_intermediate_byte_array(jenv, $input, $1, $2);
}
%typemap(memberin) (const unsigned char *payload, size_t length) {
  if ($input) {
    $1 = $input;
  } else {
    $1 = 0;
  }
}

/*----- typemap for on_disconnect_t : erase it in Java and pass NULL to C -------*/
%typemap(in, numinputs=0) on_disconnect_t on_disconnect {
  $1 = NULL;
}


/*----- typemap for subscriber_callback_t + arg in z_declare_subscriber -------*/
%typemap(jni) subscriber_callback_t callback "jobject";
%typemap(jtype) subscriber_callback_t callback "io.zenoh.SubscriberCallback";
%typemap(jstype) subscriber_callback_t callback "io.zenoh.SubscriberCallback";
%typemap(javain) subscriber_callback_t callback "$javainput";
%typemap(in,numinputs=1) (subscriber_callback_t callback, void *arg) {
  // Store SubscriberCallback object in a callback_arg
  // that will be passed to jni_subscriber_callback() at each notification
  callback_arg *jarg = malloc(sizeof(callback_arg));
  jarg->callback_object = (*jenv)->NewGlobalRef(jenv, $input);
  jarg->context = NULL;
  (*jenv)->DeleteLocalRef(jenv, $input);

  $1 = jni_subscriber_callback;
  $2 = jarg;
};

/*----- typemap for subscriber_callback_t + query_handler_t + replies_cleaner_t + arg in z_declare_storage -------*/
%typemap(jni) (subscriber_callback_t callback, query_handler_t handler, replies_cleaner_t cleaner) "jobject";
%typemap(jtype) (subscriber_callback_t callback, query_handler_t handler, replies_cleaner_t cleaner) "io.zenoh.Storage";
%typemap(jstype) (subscriber_callback_t callback, query_handler_t handler, replies_cleaner_t cleaner) "io.zenoh.Storage";
%typemap(javain) (subscriber_callback_t callback, query_handler_t handler, replies_cleaner_t cleaner) "$javainput";
%typemap(in,numinputs=1) (subscriber_callback_t callback, query_handler_t handler, replies_cleaner_t cleaner, void *arg) {
  // Store the Storage object in a callback_arg
  // that will be passed to each call to jni_storage_subscriber_callback, jni_storage_query_handler and jni_storage_replies_cleaner
  callback_arg *jarg = malloc(sizeof(callback_arg));
  jarg->callback_object = (*jenv)->NewGlobalRef(jenv, $input);
  jarg->context = NULL;
  (*jenv)->DeleteLocalRef(jenv, $input);

  $1 = jni_storage_subscriber_callback;
  $2 = jni_storage_query_handler;
  $3 = jni_storage_replies_cleaner;
  $4 = jarg;
};

/*----- typemap for z_reply_callback_t + arg in z_query -------*/
%typemap(jni) z_reply_callback_t callback "jobject";
%typemap(jtype) z_reply_callback_t callback "io.zenoh.ReplyCallback";
%typemap(jstype) z_reply_callback_t callback "io.zenoh.ReplyCallback";
%typemap(javain) z_reply_callback_t callback "$javainput";
%typemap(in,numinputs=1) (z_reply_callback_t callback, void *arg) {
  // Store ReplyCallback object in a callback_arg
  // that will be passed to jni_reply_callback() at each notification
  callback_arg *jarg = malloc(sizeof(callback_arg));
  jarg->callback_object = (*jenv)->NewGlobalRef(jenv, $input);
  jarg->context = NULL;
  (*jenv)->DeleteLocalRef(jenv, $input);

  $1 = jni_reply_callback;
  $2 = jarg;
};


%{
#include <stdint.h>
#include "zenoh/config.h"
#include "zenoh/types.h"
#include "zenoh/msg.h"
#include "zenoh/codec.h"
#include "zenoh/recv_loop.h"
#include "zenoh.h"
#include <assert.h>

#if (ZENOH_DEBUG == 0)
#define assert_no_exception
#else
#define assert_no_exception \
  if ((*jenv)->ExceptionCheck(jenv)) { \
    (*jenv)->ExceptionDescribe(jenv); \
    assert(0); \
  }
#endif

/*------ Caching of Java VM, classes, methods... ------*/
JavaVM *jvm = NULL;
jclass byte_buffer_class = NULL;
jclass data_info_class = NULL;
jclass reply_value_class = NULL;
jmethodID byte_buffer_is_direct_method = NULL;
jmethodID byte_buffer_has_array_method = NULL;
jmethodID byte_buffer_array_method = NULL;
jmethodID byte_buffer_array_offset_method = NULL;
jmethodID byte_buffer_position_method = NULL;
jmethodID byte_buffer_remaining_method = NULL;
jmethodID byte_buffer_wrap_method = NULL;
jmethodID subscriber_handle_method = NULL;
jmethodID storage_subscriber_callback_method = NULL;
jmethodID storage_query_handler_method = NULL;
jmethodID storage_replies_cleaner_method = NULL;
jmethodID reply_handle_method = NULL;
jmethodID data_info_constr = NULL;
jmethodID reply_value_constr = NULL;
jmethodID resource_get_rname_method = NULL;
jmethodID resource_get_data_method = NULL;
jmethodID resource_get_encoding_method = NULL;
jmethodID resource_get_kind_method = NULL;


jint JNI_OnLoad(JavaVM* vm, void* reserved) {
  jvm = vm;
  JNIEnv* jenv;
  if ((*vm)->GetEnv(vm, (void **) &jenv, JNI_VERSION_1_6) != JNI_OK) {
    printf("Unexpected error retrieving JNIEnv in JNI_OnLoad()\n");
    return JNI_ERR;
  }

  // Caching classes. Note that we need to convert those as a GlobalRef since they are local by default and might be GCed.
  jclass bb_class = (*jenv)->FindClass(jenv, "java/nio/ByteBuffer");
  assert_no_exception;
  byte_buffer_class = (jclass) (*jenv)->NewGlobalRef(jenv, bb_class);
  assert_no_exception;
  jclass di_class = (*jenv)->FindClass(jenv, "io/zenoh/DataInfo");
  assert_no_exception;
  data_info_class = (jclass) (*jenv)->NewGlobalRef(jenv, di_class);
  assert_no_exception;
  jclass rv_class = (*jenv)->FindClass(jenv, "io/zenoh/ReplyValue");
  assert_no_exception;
  reply_value_class = (jclass) (*jenv)->NewGlobalRef(jenv, rv_class);
  assert_no_exception;

  // Non-cached classes that are used below to get methods IDs
  jclass subscriber_callback_class = (*jenv)->FindClass(jenv, "io/zenoh/SubscriberCallback");
  assert_no_exception;
  jclass storage_class = (*jenv)->FindClass(jenv, "io/zenoh/Storage");
  assert_no_exception;
  jclass reply_callback_class = (*jenv)->FindClass(jenv, "io/zenoh/ReplyCallback");
  assert_no_exception;
  jclass resource_class = (*jenv)->FindClass(jenv, "io/zenoh/Resource");
  assert_no_exception;


  // Caching methods IDs.
  byte_buffer_position_method = (*jenv)->GetMethodID(jenv, byte_buffer_class,
    "position", "()I");
  assert_no_exception;
  byte_buffer_is_direct_method = (*jenv)->GetMethodID(jenv, byte_buffer_class,
    "isDirect", "()Z");
  assert_no_exception;
  byte_buffer_has_array_method = (*jenv)->GetMethodID(jenv, byte_buffer_class,
    "hasArray", "()Z");
  assert_no_exception;
  byte_buffer_array_method = (*jenv)->GetMethodID(jenv, byte_buffer_class,
    "array", "()[B");
  assert_no_exception;
  byte_buffer_array_offset_method = (*jenv)->GetMethodID(jenv, byte_buffer_class,
    "arrayOffset", "()I");
  assert_no_exception;
  byte_buffer_remaining_method = (*jenv)->GetMethodID(jenv, byte_buffer_class,
    "remaining", "()I");
  assert_no_exception;
  byte_buffer_wrap_method = (*jenv)->GetStaticMethodID(jenv, byte_buffer_class,
    "wrap", "([B)Ljava/nio/ByteBuffer;");
  assert_no_exception;

  subscriber_handle_method = (*jenv)->GetMethodID(jenv, subscriber_callback_class,
    "handle", "(Ljava/lang/String;Ljava/nio/ByteBuffer;Lio/zenoh/DataInfo;)V");
  assert_no_exception;
  storage_subscriber_callback_method = (*jenv)->GetMethodID(jenv, storage_class,
    "subscriberCallback", "(Ljava/lang/String;Ljava/nio/ByteBuffer;Lio/zenoh/DataInfo;)V");
  assert_no_exception;
  storage_query_handler_method = (*jenv)->GetMethodID(jenv, storage_class,
    "queryHandler", "(Ljava/lang/String;Ljava/lang/String;)[Lio/zenoh/Resource;");
  assert_no_exception;
  storage_replies_cleaner_method = (*jenv)->GetMethodID(jenv, storage_class,
    "repliesCleaner", "([Lio/zenoh/Resource;)V");
  assert_no_exception;
  reply_handle_method = (*jenv)->GetMethodID(jenv, reply_callback_class,
   "handle", "(Lio/zenoh/ReplyValue;)V");
  assert_no_exception;
  data_info_constr = (*jenv)->GetMethodID(jenv, data_info_class,
    "<init>", "(JII)V");
  assert_no_exception;
  reply_value_constr = (*jenv)->GetMethodID(jenv, reply_value_class,
    "<init>", "(I[BJLjava/lang/String;Ljava/nio/ByteBuffer;Lio/zenoh/DataInfo;)V");
  assert_no_exception;
  resource_get_rname_method = (*jenv)->GetMethodID(jenv, resource_class,
   "getRname", "()Ljava/lang/String;");
  resource_get_data_method = (*jenv)->GetMethodID(jenv, resource_class,
   "getData", "()Ljava/nio/ByteBuffer;");
  resource_get_encoding_method = (*jenv)->GetMethodID(jenv, resource_class,
   "getEncoding", "()I");
  resource_get_kind_method = (*jenv)->GetMethodID(jenv, resource_class,
   "getKind", "()I");

  return JNI_VERSION_1_6;
}

void JNI_OnUnload(JavaVM *vm, void *reserved) {
  JNIEnv* env;
  if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_6) != JNI_OK) {
      printf("Unexpected error retrieving JNIEnv in JNI_OnUnload()\n");
      return;
  }
  
  // Delete global references to cached classes
  if (byte_buffer_class != NULL) {
    (*env)->DeleteGlobalRef(env, byte_buffer_class);
    byte_buffer_class = NULL;
  }
}

JNIEnv * attach_native_thread() {
  // NOTE: a native thread calling Java must be attached to the JVM.
  // Each callback from Zenoh should call this operation.
  // We don't detach the thread as it causes huge impact on performances...

  JNIEnv * jenv;
  int getEnvStat = (*jvm)->GetEnv(jvm, (void **)&jenv, JNI_VERSION_1_8);
  if (getEnvStat == JNI_EDETACHED) {
    int status = (*jvm)->AttachCurrentThread(jvm, (void **) &jenv, NULL);
    assert(status == 0);
  } else if (getEnvStat == JNI_OK) {
    // nothing to do
  } else if (getEnvStat == JNI_EVERSION) {
    printf("JNI ERROR: JNI_VERSION_1_8 not supported\n");
    assert(0);
  } else {
    printf("JNI ERROR: unexpected status attaching current thread: %d\n", getEnvStat);
    assert(0);
  }

  return jenv;
}

// Convert a Java ByteBuffer declared as 'jbuffer' into an 'unsigned char *data' and a 'int length'
// NOTE: call release_intermediate_byte_array(jenv) after usage of jbuffer
#define jbuffer_to_native(jenv, jbuffer, data, length) \
  if ((*jenv)->CallIntMethod(jenv, jbuffer, byte_buffer_is_direct_method)) { \
    data = (unsigned char *) (*jenv)->GetDirectBufferAddress(jenv, jbuffer); \
  } else if ((*jenv)->CallIntMethod(jenv, jbuffer, byte_buffer_has_array_method)) { \
    jarray array = (jbyteArray) (*jenv)->CallObjectMethod(jenv, jbuffer, byte_buffer_array_method); \
    int offset = (int) (*jenv)->CallIntMethod(jenv, jbuffer, byte_buffer_array_offset_method); \
    int position = (int) (*jenv)->CallIntMethod(jenv, jbuffer, byte_buffer_position_method); \
    jboolean is_copy; \
    data = (unsigned char *) (*jenv)->GetByteArrayElements(jenv, array, &is_copy); \
    data = &data[offset+position]; \
  } else { \
    SWIG_JavaThrowException(jenv, SWIG_JavaRuntimeException, "The ByteBuffer is neither a direct buffer, neither a wrap of an array - it's not supported"); \
  } \
  length = (int) (*jenv)->CallIntMethod(jenv, jbuffer, byte_buffer_remaining_method);

// Release an intermediate byte[] that may have been created by release_intermediate_byte_array
#define release_intermediate_byte_array(jenv, jbuffer, data, length) \
  if ((*jenv)->CallIntMethod(jenv, jbuffer, byte_buffer_has_array_method)) { \
    jarray array = (jbyteArray) (*jenv)->CallObjectMethod(jenv, jbuffer, byte_buffer_array_method); \
    int offset = (int) (*jenv)->CallIntMethod(jenv, jbuffer, byte_buffer_array_offset_method); \
    int position = (int) (*jenv)->CallIntMethod(jenv, jbuffer, byte_buffer_position_method); \
    (*jenv)->ReleaseByteArrayElements(jenv, array, (jbyte*) &data[-offset-position], JNI_ABORT); \
  }

// Convert an 'unsigned char *data' and a 'int length' into a Java ByteBuffer declared as 'jbuffer'
#define native_to_jbuffer(jenv, data, length, jbuffer) \
  jbyteArray jbuffer_array = (*jenv)->NewByteArray(jenv, length); \
  assert_no_exception; \
  (*jenv)->SetByteArrayRegion(jenv, jbuffer_array, 0, length, (const jbyte*) data); \
  assert_no_exception; \
  jbuffer = (*jenv)->CallStaticObjectMethod(jenv, byte_buffer_class, byte_buffer_wrap_method, jbuffer_array); \
  assert_no_exception; \

// delete a Java ByteBuffer created by native_to_jbuffer
#define delete_jbuffer(jenv, jbuffer) \
  (*jenv)->DeleteLocalRef(jenv, jbuffer); \
  (*jenv)->DeleteLocalRef(jenv, jbuffer_array); \
  assert_no_exception;



typedef struct {
  jobject callback_object;
  void *context;
} callback_arg;


void jni_subscriber_callback(const z_resource_id_t *rid, const unsigned char *data, size_t length, const z_data_info_t *info, void *arg) {
  callback_arg *jarg = arg;
  JNIEnv *jenv = attach_native_thread();

  jstring jrname = NULL;
  if (rid->kind == Z_STR_RES_ID) {
    jrname = (*jenv)->NewStringUTF(jenv, rid->id.rname);
  } else {
    printf("INTERNAL ERROR: jni_subscriber_callback received a non-string z_resource_id_t with kind=%d", rid->kind);
    return;
  }

  jobject jbuffer;
  native_to_jbuffer(jenv, data, length, jbuffer);

  jobject jinfo = (*jenv)->NewObject(jenv, data_info_class, data_info_constr, info->flags, info->encoding, info->kind);
  assert_no_exception;

  (*jenv)->CallVoidMethod(jenv, jarg->callback_object, subscriber_handle_method, jrname, jbuffer, jinfo);
  assert_no_exception;

  (*jenv)->DeleteLocalRef(jenv, jinfo);
  assert_no_exception;
  delete_jbuffer(jenv, jbuffer);
  (*jenv)->DeleteLocalRef(jenv, jrname);
  assert_no_exception;
}

void jni_storage_subscriber_callback(const z_resource_id_t *rid, const unsigned char *data, size_t length, const z_data_info_t *info, void *arg) {
  callback_arg *jarg = arg;
  JNIEnv *jenv = attach_native_thread();

  jstring jrname = NULL;
  if (rid->kind == Z_STR_RES_ID) {
    jrname = (*jenv)->NewStringUTF(jenv, rid->id.rname);
  } else {
    printf("INTERNAL ERROR: jni_subscriber_callback received a non-string z_resource_id_t with kind=%d", rid->kind);
    return;
  }

  jobject jbuffer;
  native_to_jbuffer(jenv, data, length, jbuffer);

  jobject jinfo = (*jenv)->NewObject(jenv, data_info_class, data_info_constr, info->flags, info->encoding, info->kind);
  assert_no_exception;

  (*jenv)->CallVoidMethod(jenv, jarg->callback_object, storage_subscriber_callback_method, jrname, jbuffer, jinfo);
  assert_no_exception;

  delete_jbuffer(jenv, jbuffer);
  (*jenv)->DeleteLocalRef(jenv, jrname);
  assert_no_exception;
}

void jni_storage_query_handler(const char *rname, const char *predicate, z_array_resource_t *replies, void *arg) {
  callback_arg *jarg = arg;
  JNIEnv *jenv = attach_native_thread();

  if (jarg->context != NULL) {
    printf("Internal error in jni_storage_query_handler: cannot serve query, as their is already an ongoing query (context is not NULL)\n");
    Z_ARRAY_H_INIT(z_resource_t, replies, 0);
    return;
  }

  // Push a local frame that will be pop-ed at the end of jni_storage_replies_cleaner()
  (*jenv)->PushLocalFrame(jenv, 1);
  assert_no_exception;

  jstring jrname = (*jenv)->NewStringUTF(jenv, rname);
  jstring jpredicate = (*jenv)->NewStringUTF(jenv, predicate);

  // Call Storage.queryHandler()
  jobjectArray jreplies = (jobjectArray) (*jenv)->CallObjectMethod(jenv, jarg->callback_object, storage_query_handler_method, jrname, jpredicate);
  assert_no_exception;
  // store jreplies in context to be used again in jni_storage_replies_cleaner()
  jarg->context = (void*) jreplies;

  (*jenv)->DeleteLocalRef(jenv, jrname);
  assert_no_exception;
  (*jenv)->DeleteLocalRef(jenv, jpredicate);
  assert_no_exception;

  // Convert io.zenoh.Resource[] into z_array_resource_t
  if (jreplies == NULL) {
    Z_ARRAY_H_INIT(z_resource_t, replies, 0);
    return;
  } else {
    jsize len = (*jenv)->GetArrayLength(jenv, jreplies);
    assert_no_exception;
    Z_ARRAY_H_INIT(z_resource_t, replies, len);
    for (int i = 0; i < len; ++i) {
      jobject jres = (*jenv)->GetObjectArrayElement(jenv, jreplies, i);

      // rname
      jstring jrname = (jstring) (*jenv)->CallObjectMethod(jenv, jres, resource_get_rname_method);
      replies->elem[i].rname = (*jenv)->GetStringUTFChars(jenv, jrname, 0);
      assert_no_exception;
      replies->elem[i].context = (void*) jrname;

      // data + length
      jobject jbuffer = (*jenv)->CallObjectMethod(jenv, jres, resource_get_data_method);
      assert_no_exception;
      jbuffer_to_native(jenv, jbuffer, replies->elem[i].data, replies->elem[i].length);

      // encoding and kind
      replies->elem[i].encoding = (*jenv)->CallIntMethod(jenv, jres, resource_get_encoding_method);
      assert_no_exception;
      replies->elem[i].kind = (*jenv)->CallIntMethod(jenv, jres, resource_get_kind_method);
      assert_no_exception;
    }
    return;
  }
}

void jni_storage_replies_cleaner(z_array_resource_t *replies, void *arg) {
  callback_arg *jarg = arg;
  JNIEnv *jenv = attach_native_thread();

  if (jarg->context == NULL) {
    printf("Internal error in jni_storage_replies_cleaner: context is NULL ! No replies to clean\n");
    return;
  }

  // Call Storage.repliesCleaner()
  jobjectArray jreplies = (jobjectArray) jarg->context;
  (*jenv)->CallVoidMethod(jenv, jarg->callback_object, storage_replies_cleaner_method, jreplies);
  assert_no_exception;

  // clean java rname for each resource
  for (int i = 0; i < replies->length; ++i) {
    jstring jrname = (jstring) replies->elem[i].context;
    (*jenv)->ReleaseStringUTFChars(jenv, jrname, replies->elem[i].rname);
    assert_no_exception;
  }

  // reset context
  jarg->context = NULL;

  // Pop local Frame to release Resource[] allocated in jni_storage_query_handler()
  (*jenv)->PopLocalFrame(jenv, NULL);
  assert_no_exception;

  // Free the C z_array_resource_t replies
  Z_ARRAY_H_FREE(replies);
}

void jni_reply_callback(const z_reply_value_t *reply, void *arg) {
  callback_arg *jarg = arg;
  JNIEnv *jenv = attach_native_thread();

  jbyteArray jstoid = (*jenv)->NewByteArray(jenv, reply->stoid_length);
  assert_no_exception;
  (*jenv)->SetByteArrayRegion(jenv, jstoid, 0, reply->stoid_length, (const jbyte*) reply->stoid);
  assert_no_exception;

  jobject jbuffer;
  native_to_jbuffer(jenv, reply->data, reply->data_length, jbuffer);

  jobject jinfo = (*jenv)->NewObject(jenv, data_info_class, data_info_constr,
    reply->info.flags, reply->info.encoding, reply->info.kind);
  assert_no_exception;

  jstring jrname = (*jenv)->NewStringUTF(jenv, reply->rname);

  jobject jreply = (*jenv)->NewObject(jenv, reply_value_class, reply_value_constr,
    reply->kind, jstoid, reply->rsn, jrname, jbuffer, jinfo);

  (*jenv)->CallVoidMethod(jenv, jarg->callback_object, reply_handle_method, jreply);

  (*jenv)->DeleteLocalRef(jenv, jreply);
  assert_no_exception;
  (*jenv)->DeleteLocalRef(jenv, jrname);
  assert_no_exception;
  (*jenv)->DeleteLocalRef(jenv, jinfo);
  assert_no_exception;
  delete_jbuffer(jenv, jbuffer);
  (*jenv)->DeleteLocalRef(jenv, jstoid);
  assert_no_exception;
}


%}

#include <stdint.h>

typedef  size_t  z_vle_t;

typedef struct {
    z_vle_t origin;
    z_vle_t period;
    z_vle_t duration;
} z_temporal_property_t;

typedef struct {  
  uint8_t kind;
  z_temporal_property_t tprop;
} z_sub_mode_t;


typedef struct {
  unsigned int flags;
  // TODO: Add support for timestamp
  // unsigned long long timestamp;
  unsigned short encoding;
  unsigned short kind;  
} z_data_info_t;

typedef struct {
  char kind;
  const unsigned char *stoid; 
  size_t stoid_length; 
  z_vle_t rsn;
  const char* rname;
  const unsigned char *data;
  size_t data_length;
  z_data_info_t info;
} z_reply_value_t;

typedef union {  
  z_vle_t rid;
  char *rname;
} z_res_id_t;

typedef struct {
  int kind;
  z_res_id_t id; 
} z_resource_id_t;

typedef void (*z_reply_callback_t)(const z_reply_value_t *reply, void *arg);

typedef void (*subscriber_callback_t)(const z_resource_id_t *rid, const unsigned char *data, size_t length, z_data_info_t info, void *arg);

typedef struct {
  const char* rname;
  const unsigned char *data;
  size_t length;
  unsigned short encoding;
  unsigned short kind; 
} z_resource_t;

typedef struct { unsigned int length; z_resource_t* elem; } z_array_resource_t;

typedef z_array_resource_t (*query_handler_t)(const char *rname, const char *predicate, void *arg);
typedef void (*replies_cleaner_t)(z_array_resource_t replies, void *arg);


typedef struct {
  z_zenoh_t *z;
  z_vle_t rid;
  z_vle_t id;
} z_sub_t;

typedef struct {
  z_zenoh_t *z;
  z_vle_t rid;
  z_vle_t id;
} z_sto_t;

typedef struct {
  z_zenoh_t *z;
  z_vle_t rid;
  z_vle_t id;
} z_pub_t;

enum result_kind {
  Z_OK_TAG,
  Z_ERROR_TAG    
};


typedef struct { enum result_kind tag; union { z_zenoh_t * zenoh; int error; } value;} z_zenoh_p_result_t; 
typedef struct { enum result_kind tag; union { z_sub_t * sub; int error; } value;} z_sub_p_result_t;
typedef struct { enum result_kind tag; union { z_pub_t * pub; int error; } value;} z_pub_p_result_t; 
typedef struct { enum result_kind tag; union { z_sto_t * sto; int error; } value;} z_sto_p_result_t; 



z_zenoh_p_result_t 
z_open(char* locator, on_disconnect_t on_disconnect, const z_vec_t *ps);

int z_start_recv_loop(z_zenoh_t* z);

int z_stop_recv_loop(z_zenoh_t* z);

z_zenoh_p_result_t 
z_open_wup(char* locator, const char * uname, const char *passwd);

z_sub_p_result_t 
z_declare_subscriber(z_zenoh_t *z, const char* resource, const z_sub_mode_t *sm, subscriber_callback_t callback, void *arg);

z_pub_p_result_t 
z_declare_publisher(z_zenoh_t *z, const char *resource);

z_sto_p_result_t 
z_declare_storage(z_zenoh_t *z, const char* resource, subscriber_callback_t callback, query_handler_t handler, replies_cleaner_t cleaner, void *arg);

int z_stream_compact_data(z_pub_t *pub, const unsigned char *payload, size_t length);
int z_stream_data(z_pub_t *pub, const unsigned char *payload, size_t length);
int z_write_data(z_zenoh_t *z, const char* resource, const unsigned char *payload, size_t length);

int z_stream_data_wo(z_pub_t *pub, const unsigned char *payload, size_t length, uint8_t encoding, uint8_t kind);
int z_write_data_wo(z_zenoh_t *z, const char* resource, const unsigned char *payload, size_t length, uint8_t encoding, uint8_t kind);

int z_query(z_zenoh_t *z, const char* resource, const char* predicate, z_reply_callback_t callback, void *arg);
