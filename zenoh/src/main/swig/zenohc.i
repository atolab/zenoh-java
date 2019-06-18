%module zenohc 
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

typedef void subscriber_callback_t(z_resource_id_t rid, const unsigned char *data, size_t length, z_data_info_t info);

typedef struct {
  const char* rname;
  const unsigned char *data;
  size_t length;
  unsigned short encoding;
  unsigned short kind; 
} z_resource_t;

typedef struct { unsigned int length; z_resource_t* elem; } z_array_z_resource_t;

typedef z_array_z_resource_t query_handler_t(const char *rname, const char *predicate);
typedef void replies_cleaner_t(z_array_z_resource_t replies);

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
z_declare_subscriber(z_zenoh_t *z, const char* resource, z_sub_mode_t sm, subscriber_callback_t *callback);

z_pub_p_result_t 
z_declare_publisher(z_zenoh_t *z, const char *resource);

z_sto_p_result_t 
z_declare_storage(z_zenoh_t *z, const char* resource, subscriber_callback_t *callback, query_handler_t *handler, replies_cleaner_t *cleaner);

int z_stream_compact_data(z_pub_t *pub, const unsigned char *payload, size_t len);
int z_stream_data(z_pub_t *pub, const unsigned char *payload, size_t len);
int z_write_data(z_zenoh_t *z, const char* resource, const unsigned char *payload, size_t length);

int z_stream_data_wo(z_pub_t *pub, const unsigned char *payload, size_t len, uint8_t encoding, uint8_t kind);
int z_write_data_wo(z_zenoh_t *z, const char* resource, const unsigned char *payload, size_t len, uint8_t encoding, uint8_t kind);

int z_query(z_zenoh_t *z, const char* resource, const char* predicate, z_reply_callback_t *callback);


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

typedef void subscriber_callback_t(z_resource_id_t rid, unsigned char *data, size_t length, z_data_info_t info);

typedef struct {
  const char* rname;
  const unsigned char *data;
  size_t length;
  unsigned short encoding;
  unsigned short kind; 
} z_resource_t;

typedef struct { unsigned int length; z_resource_t* elem; } z_array_z_resource_t;

typedef z_array_z_resource_t query_handler_t(const char *rname, const char *predicate);
typedef void replies_cleaner_t(z_array_z_resource_t replies);


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
z_declare_subscriber(z_zenoh_t *z, const char* resource, z_sub_mode_t sm, subscriber_callback_t *callback);

z_pub_p_result_t 
z_declare_publisher(z_zenoh_t *z, const char *resource);

z_sto_p_result_t 
z_declare_storage(z_zenoh_t *z, const char* resource, subscriber_callback_t *callback, query_handler_t *handler, replies_cleaner_t *cleaner);

int z_stream_compact_data(z_pub_t *pub, const unsigned char *payload, size_t len);
int z_stream_data(z_pub_t *pub, const unsigned char *payload, size_t len);
int z_write_data(z_zenoh_t *z, const char* resource, const unsigned char *payload, size_t length);

int z_stream_data_wo(z_pub_t *pub, const unsigned char *payload, size_t len, uint8_t encoding, uint8_t kind);
int z_write_data_wo(z_zenoh_t *z, const char* resource, const unsigned char *payload, size_t len, uint8_t encoding, uint8_t kind);

int z_query(z_zenoh_t *z, const char* resource, const char* predicate, z_reply_callback_t *callback);
