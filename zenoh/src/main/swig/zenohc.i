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
  // TODO: Below we assume that the ByteBuffer has offset=0 and is used at full capacity.
  //       This could not be the case and we should deal with offset and limit
  $1 = (unsigned char *) JCALL1(GetDirectBufferAddress, jenv, $input);
  if ($1 == NULL) {
    SWIG_JavaThrowException(jenv, SWIG_JavaRuntimeException, "Unable to get address of a java.nio.ByteBuffer direct byte buffer. Buffer must be a direct buffer and not a non-direct buffer.");
  }
  $2 = (int)JCALL1(GetDirectBufferCapacity, jenv, $input);
}
%typemap(memberin) (const unsigned char *payload, size_t length) {
  if ($input) {
    $1 = $input;
  } else {
    $1 = 0;
  }
}
%typemap(freearg) (const unsigned char *payload, size_t length) ""

/*----- typemap for subscriber_callback+arg to Listener -------*/
%typemap(jni) subscriber_callback_t *callback "jobject";
%typemap(jtype) subscriber_callback_t *callback "io.zenoh.swig.JNISubscriberCallback";
%typemap(jstype) subscriber_callback_t *callback "io.zenoh.swig.JNISubscriberCallback";
%typemap(javain) subscriber_callback_t *callback "$javainput";
%typemap(in,numinputs=1) (subscriber_callback_t *callback, void *arg) {
  // Get JVM, JNISubscriberCallback object and its handle() method
  // to pass those as 'arg' at each notification
  sub_callback_arg *jarg = malloc(sizeof(sub_callback_arg));;
  int status = (*jenv)->GetJavaVM(jenv, &(jarg->jvm));
  assert(status == 0);

  const jclass callbackClass = (*jenv)->FindClass(jenv, "io/zenoh/swig/JNISubscriberCallback");
  assert(callbackClass);
  jarg->handle_method = (*jenv)->GetMethodID(jenv, callbackClass,
    "handle", "(JLjava/nio/ByteBuffer;J)V");
  assert(jarg->handle_method);

  jarg->byte_buffer_class = (*jenv)->FindClass(jenv, "java/nio/ByteBuffer");
  assert(jarg->byte_buffer_class);
  jarg->wrap_method = (*jenv)->GetStaticMethodID(jenv, jarg->byte_buffer_class,
    "wrap", "([B)Ljava/nio/ByteBuffer;");
  assert(jarg->wrap_method);

  jarg->callback_object = JCALL1(NewGlobalRef, jenv, $input);
  JCALL1(DeleteLocalRef, jenv, $input);

  $1 = java_subscriber_callback;
  $2 = jarg;
}


%{
#define ZENOH_C_SWIG 1

#include <stdint.h>

typedef  size_t  z_vle_t;

/*------------------ Temporal Properties ------------------*/
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

typedef void z_reply_callback_t(z_reply_value_t reply);

typedef void subscriber_callback_t(z_resource_id_t rid, const unsigned char *data, size_t length, z_data_info_t info, void *arg);
typedef void jni_subscriber_callback_t(long ridPtr, const unsigned char *data, size_t length, long infoPtr, void *arg);

typedef struct {
  const char* rname;
  const unsigned char *data;
  size_t length;
  unsigned short encoding;
  unsigned short kind; 
} z_resource_t;

typedef struct { unsigned int length; z_resource_t* elem; } z_array_z_resource_t;

typedef z_array_z_resource_t query_handler_t(const char *rname, const char *predicate, void *arg);
typedef void replies_cleaner_t(z_array_z_resource_t replies, void *arg);

#include "zenoh/config.h"
#include "zenoh/types.h"

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


#include "zenoh/msg.h"
#include "zenoh/codec.h"




z_zenoh_p_result_t 
z_open(char* locator, on_disconnect_t *on_disconnect, const z_vec_t *ps);

int z_start_recv_loop(z_zenoh_t* z);

int z_stop_recv_loop(z_zenoh_t* z);

z_zenoh_t * 
z_open_wup(char* locator, const char * uname, const char *passwd);

z_sub_p_result_t 
z_declare_subscriber(z_zenoh_t *z, const char* resource, z_sub_mode_t *sm, subscriber_callback_t *callback, void *arg);

z_pub_p_result_t 
z_declare_publisher(z_zenoh_t *z, const char *resource);

z_sto_p_result_t 
z_declare_storage(z_zenoh_t *z, const char* resource, subscriber_callback_t *callback, query_handler_t *handler, replies_cleaner_t *cleaner, void *arg);

int z_stream_compact_data(z_pub_t *pub, const unsigned char *payload, size_t length);
int z_stream_data(z_pub_t *pub, const unsigned char *payload, size_t length);
int z_write_data(z_zenoh_t *z, const char* resource, const unsigned char *payload, size_t length);

int z_stream_data_wo(z_pub_t *pub, const unsigned char *payload, size_t length, uint8_t encoding, uint8_t kind);
int z_write_data_wo(z_zenoh_t *z, const char* resource, const unsigned char *payload, size_t length, uint8_t encoding, uint8_t kind);

int z_query(z_zenoh_t *z, const char* resource, const char* predicate, z_reply_callback_t *callback);

#include <assert.h>
typedef struct {
  JavaVM *jvm;
  jobject callback_object;
  jmethodID handle_method;
  jclass byte_buffer_class;
  jmethodID wrap_method;
} sub_callback_arg;


JNIEnv * attach_native_thread(JavaVM *jvm) {
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


void java_subscriber_callback(z_resource_id_t rid, const unsigned char *data, size_t length, z_data_info_t info, void *arg) {
  sub_callback_arg *jarg = arg;
  JNIEnv *jenv = attach_native_thread(jarg->jvm);

  jlong jrid = 0 ;
  z_resource_id_t * ridPtr = (z_resource_id_t *) malloc(sizeof(z_resource_id_t));
  memmove(ridPtr, &rid, sizeof(z_resource_id_t));
  *(z_resource_id_t **)&jrid = ridPtr;

  jbyteArray jarray = (*jenv)->NewByteArray(jenv, length);
  (*jenv)->SetByteArrayRegion(jenv, jarray, 0, length, (const jbyte*) data);
  jobject jbuffer = (*jenv)->CallStaticObjectMethod(jenv, jarg->byte_buffer_class, jarg->wrap_method, jarray);
  if ((*jenv)->ExceptionCheck(jenv)) {
      (*jenv)->ExceptionDescribe(jenv);
  }
  assert(jbuffer);

  jlong jinfo = 0 ;
  z_data_info_t * infoPtr = (z_data_info_t *) malloc(sizeof(z_data_info_t));
  memmove(infoPtr, &info, sizeof(z_data_info_t));
  *(z_data_info_t **)&jinfo = infoPtr;

  (*jenv)->CallVoidMethod(jenv, jarg->callback_object, jarg->handle_method, jrid, jbuffer, jinfo);
  if ((*jenv)->ExceptionCheck(jenv)) {
      (*jenv)->ExceptionDescribe(jenv);
  }
}

%}

#define ZENOH_C_SWIG 1

#include <stdint.h>

typedef  size_t  z_vle_t;


/*------------------ Temporal Properties ------------------*/
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

typedef void z_reply_callback_t(z_reply_value_t reply);

typedef void subscriber_callback_t(z_resource_id_t rid, const unsigned char *data, size_t length, z_data_info_t info, void *arg);

typedef struct {
  const char* rname;
  const unsigned char *data;
  size_t length;
  unsigned short encoding;
  unsigned short kind; 
} z_resource_t;

typedef struct { unsigned int length; z_resource_t* elem; } z_array_z_resource_t;

typedef z_array_z_resource_t query_handler_t(const char *rname, const char *predicate, void *arg);
typedef void replies_cleaner_t(z_array_z_resource_t replies, void *arg);


#include "zenoh/config.h"
#include "zenoh/types.h"

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


#include "zenoh/msg.h"
#include "zenoh/codec.h"



z_zenoh_p_result_t 
z_open(char* locator, on_disconnect_t *on_disconnect, const z_vec_t *ps);

int z_start_recv_loop(z_zenoh_t* z);

int z_stop_recv_loop(z_zenoh_t* z);

z_zenoh_t * 
z_open_wup(char* locator, const char * uname, const char *passwd);

z_sub_p_result_t 
z_declare_subscriber(z_zenoh_t *z, const char* resource, z_sub_mode_t *sm, subscriber_callback_t *callback, void *arg);

z_pub_p_result_t 
z_declare_publisher(z_zenoh_t *z, const char *resource);

z_sto_p_result_t 
z_declare_storage(z_zenoh_t *z, const char* resource, subscriber_callback_t *callback, query_handler_t *handler, replies_cleaner_t *cleaner, void *arg);

int z_stream_compact_data(z_pub_t *pub, const unsigned char *payload, size_t length);
int z_stream_data(z_pub_t *pub, const unsigned char *payload, size_t length);
int z_write_data(z_zenoh_t *z, const char* resource, const unsigned char *payload, size_t length);

int z_stream_data_wo(z_pub_t *pub, const unsigned char *payload, size_t length, uint8_t encoding, uint8_t kind);
int z_write_data_wo(z_zenoh_t *z, const char* resource, const unsigned char *payload, size_t length, uint8_t encoding, uint8_t kind);

int z_query(z_zenoh_t *z, const char* resource, const char* predicate, z_reply_callback_t *callback);
