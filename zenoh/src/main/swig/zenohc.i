%module zenohc 


/*----- typemap for basic types -------*/
%include "stdint.i"

/*----- typemap for z_vec_t to Properties -------*/
%typemap(jni) z_vec_t "jobject"
%typemap(jtype) z_vec_t "java.util.Properties"
%typemap(jstype) z_vec_t "java.util.Properties"
%typemap(out) z_vec_t {
  jobject jprops = (*jenv)->NewObject(jenv, properties_class, properties_constr);

  z_array_uint8_t array_value;
  char *hexa_value;
  jstring str_key;
  jstring str_val;

  array_value = ((z_property_t *)z_vec_get(&$1, Z_INFO_PEER_KEY))->value;
  hexa_value = malloc((array_value.length+1)*sizeof(char));
  memcpy(hexa_value, array_value.elem, array_value.length);
  hexa_value[array_value.length+1] = 0;
  str_key = (*jenv)->NewStringUTF(jenv, "peer");
  str_val = (*jenv)->NewStringUTF(jenv, hexa_value);
  (*jenv)->CallObjectMethod(jenv, jprops, set_property_method, str_key, str_val);
  free(hexa_value);

  array_value = ((z_property_t *)z_vec_get(&$1, Z_INFO_PID_KEY))->value;
  hexa_value = malloc((array_value.length*2+1)*sizeof(char));
  for(int i = 0; i < array_value.length; ++i){
    sprintf(hexa_value + 2*i, "%02x", array_value.elem[i]);
  }
  hexa_value[array_value.length*2+1] = 0;
  str_key = (*jenv)->NewStringUTF(jenv, "pid");
  str_val = (*jenv)->NewStringUTF(jenv, hexa_value);
  (*jenv)->CallObjectMethod(jenv, jprops, set_property_method, str_key, str_val);
  free(hexa_value);

  array_value = ((z_property_t *)z_vec_get(&$1, Z_INFO_PEER_PID_KEY))->value;
  hexa_value = malloc((array_value.length*2+1)*sizeof(char));
  for(int i = 0; i < array_value.length; ++i){
    sprintf(hexa_value + 2*i, "%02x", array_value.elem[i]);
  }
  hexa_value[array_value.length*2+1] = 0;
  str_key = (*jenv)->NewStringUTF(jenv, "peer_pid");
  str_val = (*jenv)->NewStringUTF(jenv, hexa_value);
  (*jenv)->CallObjectMethod(jenv, jprops, set_property_method, str_key, str_val);
  free(hexa_value);

  $result = jprops;
}
%typemap(javaout) z_vec_t {
  return $jnicall;
}

/*----- typemap for Properties to z_vec_t* -------*/
%typemap(jni) (const z_vec_t *ps) "jobject"
%typemap(jtype) (const z_vec_t *ps) "java.util.Properties"
%typemap(jstype) (const z_vec_t *ps) "java.util.Properties"
%typemap(javain) (const z_vec_t *ps) "$javainput"
%typemap(in) (const z_vec_t *ps) {
  z_vec_t vec = z_vec_make(2);
  if($input != NULL) {
    jstring user = (jstring) (*jenv)->CallObjectMethod(jenv, $input, get_property_method, (*jenv)->NewStringUTF(jenv, "user"));
    if(user != NULL) z_vec_append(&vec, z_property_make_from_str(Z_USER_KEY, (char *)(*jenv)->GetStringUTFChars(jenv, user, 0)));
    jstring password = (jstring) (*jenv)->CallObjectMethod(jenv, $input, get_property_method, (*jenv)->NewStringUTF(jenv, "password"));
    if(password != NULL) z_vec_append(&vec, z_property_make_from_str(Z_PASSWD_KEY, (char *)(*jenv)->GetStringUTFChars(jenv, password, 0)));
  }
  $1 = &vec;
}


/*----- typemap for payload+length IN argument to ByteBuffer -------*/
%typemap(jni) (const unsigned char *payload, size_t length) "jobject"
%typemap(jtype) (const unsigned char *payload, size_t length) "java.nio.ByteBuffer"
%typemap(jstype) (const unsigned char *payload, size_t length) "java.nio.ByteBuffer"
%typemap(javain, pre="  assert $javainput.isDirect() : \"Buffer must be allocated direct.\";") (const unsigned char *payload, size_t length) "$javainput"
// %typemap(javaout) (const unsigned char *payload, size_t length) {
//   return $jnicall;
// }
%typemap(in) (const unsigned char *payload, size_t length) {
  jbuffer_to_native(jenv, $input, $1, $2);
}
%typemap(freearg) (const unsigned char *payload, size_t length) {
  release_intermediate_byte_array(jenv, $input, $1, $2);
}
// %typemap(memberin) (const unsigned char *payload, size_t length) {
//   if ($input) {
//     $1 = $input;
//   } else {
//     $1 = 0;
//   }
// }

/*----- typemap for on_disconnect_t : erase it in Java and pass NULL to C -------*/
%typemap(in, numinputs=0) z_on_disconnect_t on_disconnect {
  $1 = NULL;
}


/*----- typemap for z_data_handler_t + arg in z_declare_subscriber -------*/
%typemap(jni) z_data_handler_t data_handler "jobject";
%typemap(jtype) z_data_handler_t data_handler "io.zenoh.DataHandler";
%typemap(jstype) z_data_handler_t data_handler "io.zenoh.DataHandler";
%typemap(javain) z_data_handler_t data_handler "$javainput";
%typemap(in,numinputs=1) (z_data_handler_t data_handler, void *arg) {
  // Store DataHandler object in a handler_arg
  // that will be passed to jni_handledata() at each notification
  handler_arg *jarg = malloc(sizeof(handler_arg));
  jarg->handler_object = (*jenv)->NewGlobalRef(jenv, $input);
  jarg->context = NULL;
  (*jenv)->DeleteLocalRef(jenv, $input);

  $1 = jni_handledata;
  $2 = jarg;
};

/*----- typemap for z_data_handler_t + z_query_handler_t + arg in z_declare_storage -------*/
%typemap(jni) (z_data_handler_t data_handler, z_query_handler_t query_handler) "jobject";
%typemap(jtype) (z_data_handler_t data_handler, z_query_handler_t query_handler) "io.zenoh.StorageHandler";
%typemap(jstype) (z_data_handler_t data_handler, z_query_handler_t query_handler) "io.zenoh.StorageHandler";
%typemap(javain) (z_data_handler_t data_handler, z_query_handler_t query_handler) "$javainput";
%typemap(in,numinputs=1) (z_data_handler_t data_handler, z_query_handler_t query_handler, void *arg) {
  // Store the StorageHandler object in a handler_arg
  // that will be passed to each call to jni_handledata and jni_handlequery
  handler_arg *jarg = malloc(sizeof(handler_arg));
  jarg->handler_object = (*jenv)->NewGlobalRef(jenv, $input);
  jarg->context = NULL;
  (*jenv)->DeleteLocalRef(jenv, $input);

  $1 = jni_handledata;
  $2 = jni_handlequery;
  $3 = jarg;
};

/*----- typemap for z_query_handler_t + arg in z_declare_eval -------*/
%typemap(jni) (z_query_handler_t query_handler) "jobject";
%typemap(jtype) (z_query_handler_t query_handler) "io.zenoh.QueryHandler";
%typemap(jstype) (z_query_handler_t query_handler) "io.zenoh.QueryHandler";
%typemap(javain) (z_query_handler_t query_handler) "$javainput";
%typemap(in,numinputs=1) (z_query_handler_t query_handler, void *arg) {
  // Store the QueryHandler object in a handler_arg
  // that will be passed to each call to jni_handlequery
  handler_arg *jarg = malloc(sizeof(handler_arg));
  jarg->handler_object = (*jenv)->NewGlobalRef(jenv, $input);
  jarg->context = NULL;
  (*jenv)->DeleteLocalRef(jenv, $input);

  $1 = jni_handlequery;
  $2 = jarg;
};

/*----- typemap for z_reply_handler_t + arg in z_query -------*/
%typemap(jni) z_reply_handler_t reply_handler "jobject";
%typemap(jtype) z_reply_handler_t reply_handler "io.zenoh.ReplyHandler";
%typemap(jstype) z_reply_handler_t reply_handler "io.zenoh.ReplyHandler";
%typemap(javain) z_reply_handler_t reply_handler "$javainput";
%typemap(in,numinputs=1) (z_reply_handler_t reply_handler, void *arg) {
  // Store ReplyHandler object in a handler_arg
  // that will be passed to jni_handlereply() at each notification
  handler_arg *jarg = malloc(sizeof(handler_arg));
  jarg->handler_object = (*jenv)->NewGlobalRef(jenv, $input);
  jarg->context = NULL;
  (*jenv)->DeleteLocalRef(jenv, $input);

  $1 = jni_handlereply;
  $2 = jarg;
};

/*----- typemap for z_array_resource_t to Resource[] -------*/
%typemap(jni) (z_array_resource_t replies) "jobjectArray"
%typemap(jtype) (z_array_resource_t replies) "io.zenoh.Resource[]"
%typemap(jstype) (z_array_resource_t replies) "io.zenoh.Resource[]"
%typemap(javain) (z_array_resource_t replies) "$javainput"
%typemap(in) (z_array_resource_t replies) {
  // Convert io.zenoh.Resource[] into z_array_resource_t
  if ($input == NULL) {
    $1.length = 0;
    $1.elem = NULL;
  } else {
    jsize len = (*jenv)->GetArrayLength(jenv, $input);
    assert_no_exception;
    $1.length = len;
    $1.elem = (z_resource_t**)malloc(sizeof(z_resource_t *) * $1.length);
    for (int i = 0; i < len; ++i) {
      jobject jres = (*jenv)->GetObjectArrayElement(jenv, $input, i);
      $1.elem[i] = (z_resource_t *)malloc(sizeof(z_resource_t));

      // rname
      jstring jrname = (jstring) (*jenv)->CallObjectMethod(jenv, jres, resource_get_rname_method);
      $1.elem[i]->rname = (*jenv)->GetStringUTFChars(jenv, jrname, 0);
      assert_no_exception;
      $1.elem[i]->context = (void*) jrname;

      // data + length
      jobject jbuffer = (*jenv)->CallObjectMethod(jenv, jres, resource_get_data_method);
      assert_no_exception;
      jbuffer_to_native(jenv, jbuffer, $1.elem[i]->data, $1.elem[i]->length);

      // encoding and kind
      $1.elem[i]->encoding = (*jenv)->CallIntMethod(jenv, jres, resource_get_encoding_method);
      assert_no_exception;
      $1.elem[i]->kind = (*jenv)->CallIntMethod(jenv, jres, resource_get_kind_method);
      assert_no_exception;
    }
  }

}
%typemap(freearg) (const unsigned char *payload, size_t length) {
  release_intermediate_byte_array(jenv, $input, $1, $2);
}


%{
#include <stdint.h>
#include "zenoh/private/msg.h"
#include "zenoh/config.h"
#include "zenoh/types.h"
#include "zenoh/codec.h"
#include "zenoh/recv_loop.h"
#include "zenoh/rname.h"
#include "zenoh.h"
#include <assert.h>

#if (ZENOH_DEBUG == 0)
#define assert_no_exception
#else
#define assert_no_exception \
  if ((*jenv)->ExceptionCheck(jenv)) { \
    jthrowable jex = (*jenv)->ExceptionOccurred(jenv); \
    (*jenv)->Throw(jenv, jex); \
  }
#endif


/*------ Caching of Java VM, classes, methods... ------*/
JavaVM *jvm = NULL;
jclass zenoh_class = NULL;
jclass properties_class = NULL;
jclass byte_buffer_class = NULL;
jclass data_info_class = NULL;
jclass reply_value_class = NULL;
jclass replies_sender_class = NULL;
jmethodID log_exception_method = NULL;
jmethodID properties_constr = NULL;
jmethodID set_property_method = NULL;
jmethodID get_property_method = NULL;
jmethodID byte_buffer_is_direct_method = NULL;
jmethodID byte_buffer_has_array_method = NULL;
jmethodID byte_buffer_array_method = NULL;
jmethodID byte_buffer_array_offset_method = NULL;
jmethodID byte_buffer_position_method = NULL;
jmethodID byte_buffer_remaining_method = NULL;
jmethodID byte_buffer_wrap_method = NULL;
jmethodID handledata_method = NULL;
jmethodID handlequery_method = NULL;
jmethodID replies_sender_constr = NULL;
jmethodID handlereply_method = NULL;
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
  jclass z_class = (*jenv)->FindClass(jenv, "io/zenoh/Zenoh");
  assert_no_exception;
  zenoh_class = (jclass) (*jenv)->NewGlobalRef(jenv, z_class);
  assert_no_exception;
  jclass prop_class = (*jenv)->FindClass(jenv, "java/util/Properties");
  assert_no_exception;
  properties_class = (jclass) (*jenv)->NewGlobalRef(jenv, prop_class);
  assert_no_exception;
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
  jclass rs_class = (*jenv)->FindClass(jenv, "io/zenoh/RepliesSender");
  assert_no_exception;
  replies_sender_class = (jclass) (*jenv)->NewGlobalRef(jenv, rs_class);
  assert_no_exception;

  // Non-cached classes that are used below to get methods IDs
  jclass datahandler_class = (*jenv)->FindClass(jenv, "io/zenoh/DataHandler");
  assert_no_exception;
  jclass storagehandler_class = (*jenv)->FindClass(jenv, "io/zenoh/StorageHandler");
  assert_no_exception;
  jclass queryhandler_class = (*jenv)->FindClass(jenv, "io/zenoh/QueryHandler");
  assert_no_exception;
  jclass replyhandler_class = (*jenv)->FindClass(jenv, "io/zenoh/ReplyHandler");
  assert_no_exception;
  jclass resource_class = (*jenv)->FindClass(jenv, "io/zenoh/Resource");
  assert_no_exception;


  // Caching methods IDs.
  log_exception_method = (*jenv)->GetStaticMethodID(jenv, zenoh_class,
    "LogException", "(Ljava/lang/Throwable;Ljava/lang/String;)V");
  assert_no_exception;
  properties_constr = (*jenv)->GetMethodID(jenv, properties_class,
    "<init>", "()V");
  assert_no_exception;
  set_property_method = (*jenv)->GetMethodID(jenv, properties_class,
    "setProperty", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;");
  assert_no_exception;
  get_property_method = (*jenv)->GetMethodID(jenv, properties_class,
    "getProperty", "(Ljava/lang/String;)Ljava/lang/String;");
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
  byte_buffer_position_method = (*jenv)->GetMethodID(jenv, byte_buffer_class,
    "position", "()I");
  assert_no_exception;
  byte_buffer_remaining_method = (*jenv)->GetMethodID(jenv, byte_buffer_class,
    "remaining", "()I");
  assert_no_exception;
  byte_buffer_wrap_method = (*jenv)->GetStaticMethodID(jenv, byte_buffer_class,
    "wrap", "([B)Ljava/nio/ByteBuffer;");
  assert_no_exception;

  handledata_method = (*jenv)->GetMethodID(jenv, datahandler_class,
    "handleData", "(Ljava/lang/String;Ljava/nio/ByteBuffer;Lio/zenoh/DataInfo;)V");
  assert_no_exception;
  handlequery_method = (*jenv)->GetMethodID(jenv, queryhandler_class,
    "handleQuery", "(Ljava/lang/String;Ljava/lang/String;Lio/zenoh/RepliesSender;)V");
  assert_no_exception;
  replies_sender_constr = (*jenv)->GetMethodID(jenv, replies_sender_class,
    "<init>", "(JJ)V");
  assert_no_exception;
  handlereply_method = (*jenv)->GetMethodID(jenv, replyhandler_class,
   "handleReply", "(Lio/zenoh/ReplyValue;)V");
  assert_no_exception;
  data_info_constr = (*jenv)->GetMethodID(jenv, data_info_class,
    "<init>", "(JIJI)V");
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

JNIEnv * get_jenv() {
  JNIEnv * jenv;
  int getEnvStat = (*jvm)->GetEnv(jvm, (void **)&jenv, JNI_VERSION_1_8);
  if (getEnvStat == JNI_OK) {
    // nothing to do
  } else if (getEnvStat == JNI_EDETACHED) {
    printf("JNI ERROR: the current thread is not attached to the JVM. Either attach it or use a Java thread.\n");
    assert(0);
  } else if (getEnvStat == JNI_EVERSION) {
    printf("JNI ERROR: JNI_VERSION_1_8 not supported\n");
    assert(0);
  } else {
    printf("JNI ERROR: unexpected status attaching current thread: %d\n", getEnvStat);
    assert(0);
  }

  return jenv;
}

void catch_and_log_exception(JNIEnv* jenv, const char* msg) {
  if ((*jenv)->ExceptionCheck(jenv)) {
    jthrowable jex = (*jenv)->ExceptionOccurred(jenv);
    jstring jmsg = (*jenv)->NewStringUTF(jenv, msg);
    (*jenv)->CallStaticVoidMethod(jenv, zenoh_class, log_exception_method, jex, jmsg);
    (*jenv)->ExceptionClear(jenv);
    (*jenv)->DeleteLocalRef(jenv, jmsg);
    assert_no_exception;
  }
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
  (*jenv)->DeleteLocalRef(jenv, jbuffer_array); \
  assert_no_exception;

// delete a Java ByteBuffer created by native_to_jbuffer
#define delete_jbuffer(jenv, jbuffer) \
  (*jenv)->DeleteLocalRef(jenv, jbuffer); \
  assert_no_exception;



typedef struct {
  jobject handler_object;
  void *context;
} handler_arg;


void jni_handledata(const z_resource_id_t *rid, const unsigned char *data, size_t length, const z_data_info_t *info, void *arg) {
  handler_arg *jarg = arg;
  JNIEnv *jenv = get_jenv();

  jstring jrname = NULL;
  if (rid->kind == Z_STR_RES_ID) {
    jrname = (*jenv)->NewStringUTF(jenv, rid->id.rname);
  } else {
    printf("INTERNAL ERROR: jni_handledata received a non-string z_resource_id_t with kind=%d", rid->kind);
    return;
  }

  jobject jbuffer;
  native_to_jbuffer(jenv, data, length, jbuffer);

  jobject jinfo = (*jenv)->NewObject(jenv, data_info_class, data_info_constr, info->flags, info->encoding, info->tstamp.time, info->kind);
  assert_no_exception;

  // Call DataHandler.handleData()
  (*jenv)->CallVoidMethod(jenv, jarg->handler_object, handledata_method, jrname, jbuffer, jinfo);
  catch_and_log_exception(jenv, "Exception caught calling DataHandler.handleData()");

  (*jenv)->DeleteLocalRef(jenv, jinfo);
  assert_no_exception;
  delete_jbuffer(jenv, jbuffer);
  (*jenv)->DeleteLocalRef(jenv, jrname);
  assert_no_exception;
}

void jni_handlequery(const char *rname, const char *predicate, z_replies_sender_t send_replies, void *query_handle, void *arg) {
  handler_arg *jarg = arg;
  JNIEnv *jenv = get_jenv();

  if (jarg->context != NULL) {
    printf("Internal error in jni_handlequery: cannot serve query, as their is already an ongoing query (context is not NULL)\n");
    z_array_resource_t replies;
    replies.length = 0;
    replies.elem = NULL;
    send_replies(query_handle, replies);
    return;
  }

  jstring jrname = (*jenv)->NewStringUTF(jenv, rname);
  jstring jpredicate = (*jenv)->NewStringUTF(jenv, predicate);

  // Create RepliesSender object
  jlong send_replies_ptr = (jlong)send_replies;
  jlong query_handle_ptr = (jlong)query_handle;
  jobject jrepliesSender = (*jenv)->NewObject(jenv, replies_sender_class, replies_sender_constr,
    send_replies_ptr, query_handle_ptr);
  assert_no_exception;

  // Call QueryHandler.handleQuery()
  (*jenv)->CallVoidMethod(jenv, jarg->handler_object, handlequery_method, jrname, jpredicate, jrepliesSender);
  catch_and_log_exception(jenv, "Exception caught calling QueryHandler.handleQuery()");

  (*jenv)->DeleteLocalRef(jenv, jrepliesSender);
  assert_no_exception;
  (*jenv)->DeleteLocalRef(jenv, jrname);
  assert_no_exception;
  (*jenv)->DeleteLocalRef(jenv, jpredicate);
  assert_no_exception;
}

void jni_handlereply(const z_reply_value_t *reply, void *arg) {
  handler_arg *jarg = arg;
  JNIEnv *jenv = get_jenv();

  jbyteArray jstoid = 0;
  if (reply->kind != Z_REPLY_FINAL) {
    jstoid = (*jenv)->NewByteArray(jenv, reply->stoid_length);
    assert_no_exception;
    (*jenv)->SetByteArrayRegion(jenv, jstoid, 0, reply->stoid_length, (const jbyte*) reply->stoid);
    assert_no_exception;
  }

  jobject jbuffer = 0;
  if (reply->kind == Z_STORAGE_DATA || reply->kind == Z_EVAL_DATA) {
    native_to_jbuffer(jenv, reply->data, reply->data_length, jbuffer);
  }

  jobject jinfo = (*jenv)->NewObject(jenv, data_info_class, data_info_constr,
    reply->info.flags, reply->info.encoding, reply->info.tstamp.time, reply->info.kind);
  assert_no_exception;

  jstring jrname = (*jenv)->NewStringUTF(jenv, reply->rname);

  jobject jreply = (*jenv)->NewObject(jenv, reply_value_class, reply_value_constr,
    reply->kind, jstoid, reply->rsn, jrname, jbuffer, jinfo);

  // Call ReplyHandler.handleReply()
  (*jenv)->CallVoidMethod(jenv, jarg->handler_object, handlereply_method, jreply);
  catch_and_log_exception(jenv, "Exception caught calling ReplyHandler.handleReply()");

  (*jenv)->DeleteLocalRef(jenv, jreply);
  assert_no_exception;
  (*jenv)->DeleteLocalRef(jenv, jrname);
  assert_no_exception;
  (*jenv)->DeleteLocalRef(jenv, jinfo);
  assert_no_exception;
  if (reply->kind == Z_STORAGE_DATA || reply->kind == Z_EVAL_DATA) {
    delete_jbuffer(jenv, jbuffer);
  }
  (*jenv)->DeleteLocalRef(jenv, jstoid);
  assert_no_exception;
}

void call_replies_sender(jlong send_replies_ptr, jlong query_handle_ptr, z_array_resource_t replies) {
  z_replies_sender_t send_replies = (z_replies_sender_t)send_replies_ptr;
  void* query_handle = (void*)query_handle_ptr;
  send_replies(query_handle, replies);
}

%}

void call_replies_sender(jlong send_replies_ptr, jlong query_handle_ptr, z_array_resource_t replies);

#include <stdint.h>

//
// Copied from zenoh/types.h
//
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

typedef void (*z_reply_handler_t)(const z_reply_value_t *reply, void *arg);

typedef void (*z_data_handler_t)(const z_resource_id_t *rid, const unsigned char *data, size_t length, z_data_info_t info, void *arg);

typedef void (*z_replies_sender_t)(void* query_handle, z_array_resource_t replies);
typedef void (*z_query_handler_t)(const char *rname, const char *predicate, z_replies_sender_t send_replies, void *query_handle, void *arg);

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

typedef struct {
  z_zenoh_t *z;
  z_vle_t rid;
  z_vle_t id;
} z_eva_t;

enum result_kind {
  Z_OK_TAG,
  Z_ERROR_TAG
};

typedef struct { enum result_kind tag; union { z_zenoh_t * zenoh; int error; } value;} z_zenoh_p_result_t; 
typedef struct { enum result_kind tag; union { z_sub_t * sub; int error; } value;} z_sub_p_result_t;
typedef struct { enum result_kind tag; union { z_pub_t * pub; int error; } value;} z_pub_p_result_t; 
typedef struct { enum result_kind tag; union { z_sto_t * sto; int error; } value;} z_sto_p_result_t; 
typedef struct { enum result_kind tag; union { z_eva_t * eval; int error; } value;} z_eval_p_result_t; 

typedef struct {
  uint8_t kind;
  uint8_t nb;
} z_query_dest_t;

//
// Copied from zenoh/recv_loop.h
//
void* z_recv_loop(z_zenoh_t* z);

int z_running(z_zenoh_t* z);

int z_start_recv_loop(z_zenoh_t* z);

int z_stop_recv_loop(z_zenoh_t* z);


//
// Copied from zenoh.h
//
z_zenoh_p_result_t 
z_open(char* locator, z_on_disconnect_t on_disconnect, const z_vec_t *ps);

z_vec_t
z_info(z_zenoh_t *z);

z_sub_p_result_t 
z_declare_subscriber(z_zenoh_t *z, const char* resource, const z_sub_mode_t *sm, z_data_handler_t data_handler, void *arg);

z_pub_p_result_t 
z_declare_publisher(z_zenoh_t *z, const char *resource);

z_sto_p_result_t 
z_declare_storage(z_zenoh_t *z, const char* resource, z_data_handler_t data_handler, z_query_handler_t query_handler, void *arg);

z_eval_p_result_t 
z_declare_eval(z_zenoh_t *z, const char* resource, z_query_handler_t query_handler, void *arg);

int z_stream_compact_data(z_pub_t *pub, const unsigned char *payload, size_t length);
int z_stream_data(z_pub_t *pub, const unsigned char *payload, size_t length);
int z_write_data(z_zenoh_t *z, const char* resource, const unsigned char *payload, size_t length);

int z_stream_data_wo(z_pub_t *pub, const unsigned char *payload, size_t length, uint8_t encoding, uint8_t kind);
int z_write_data_wo(z_zenoh_t *z, const char* resource, const unsigned char *payload, size_t length, uint8_t encoding, uint8_t kind);

int z_pull(z_sub_t *sub);

int z_query_wo(z_zenoh_t *z, const char* resource, const char* predicate, z_reply_handler_t reply_handler, void *arg, z_query_dest_t dest_storages, z_query_dest_t dest_evals);

int z_undeclare_subscriber(z_sub_t *z);
int z_undeclare_publisher(z_pub_t *z);
int z_undeclare_storage(z_sto_t *z);
int z_undeclare_eval(z_eva_t *z);

int z_close(z_zenoh_t *z);


//
// Copied from zenoh/rname.h
//
int intersect(char *c1, char *c2);
