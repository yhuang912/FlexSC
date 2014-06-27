 %module Aes

%include "various.i"
  //%include "carrays.i"
  //%array_functions(unsigned char, char_array)
%apply char *BYTE {char *}

 %{
 extern char* intel_AES_enc128_char(char *plainText, char *cipherText, char *key, size_t numBlocks);

 extern char* intel_AES_dec128_char(char *cipherText, char *plainText, char *key,size_t numBlocks);

 extern char * garble(char* a, char* b, long long gid, char * R);
 // extern void garble();
 %}
 
/*extern int test128_CBC(int n);

%typemap(in,numinputs=0) int *data_len
   "int temp_len;
   $1 = &temp_len;"
*/

/* %typemap(jtype) char *plainText "byte[]"
 %typemap(jstype) char *plainText "byte[]"
 %typemap(jni) char *plainText "jbyteArray"
 %typemap(javain) char *plainText "$javainput"

 %typemap(jtype) char *cipherText "byte[]"
 %typemap(jstype) char *cipherText "byte[]"
 %typemap(jni) char *cipherText "jbyteArray"
 %typemap(javain) char *cipherText "$javainput"

 %typemap(jtype) char *key "byte[]"
 %typemap(jstype) char *key "byte[]"
 %typemap(jni) char *key "jbyteArray"
 %typemap(javain) char *key "$javainput"
*/
/* %typemap(jstype) char *intel_AES_enc128_char "byte[]"
%typemap(jtype) char *intel_AES_enc128_char "byte[]"
%typemap(jni) char *intel_AES_enc128_char "jbyteArray"
%typemap(javaout) char *intel_AES_enc128_char {
  return $jnicall;
  }

 %typemap(jstype) char *intel_AES_dec128_char "byte[]"
%typemap(jtype) char *intel_AES_dec128_char "byte[]"
%typemap(jni) char *intel_AES_dec128_char "jbyteArray"
%typemap(javaout) char *intel_AES_dec128_char {
  return $jnicall;
  }*/
/*
 %typemap(jstype) short *get_data "short[]"
%typemap(jtype) short *get_data "short[]"
%typemap(jni) short *get_data "jshortArray"
%typemap(javaout) short *get_data {
  return $jnicall;
  }

%typemap(out) short *get_data {
  $result = JCALL1(NewShortArray, jenv, temp_len);
  JCALL4(SetShortArrayRegion, jenv, $result, 0, temp_len, $1);
  // If the result was malloc()'d free it here
  }*/

%typemap(out) char * garble {
    if (result) jresult = (*jenv)->NewByteArray(jenv, 50);
    (*jenv)->SetByteArrayRegion(jenv, jresult, 0, 50, result);
}

//extern short * get_data(unsigned char *plainText, unsigned char *key, int *data_len);

extern char* intel_AES_enc128_char(char *plainText, char *cipherText, char *key, size_t numBlocks);

extern char* intel_AES_dec128_char(char *cipherText, char *plainText, char *key,size_t numBlocks);

extern char * garble(char* a, char *b, long long gid, char * R);
//  extern void garble();
