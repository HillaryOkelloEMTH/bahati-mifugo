//package com.emtech.dairyapp.Auth.Utilities;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.tomcat.util.codec.binary.Base64;
//import org.springframework.stereotype.Service;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//
//
//
//@Service
//@Slf4j
//public class Encryption {
//    public String encrypt(String key, String initVector, String value) {
//        try {
//            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
//            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
//            cipher.init(1, skeySpec, iv);
//            byte[] encrypted = cipher.doFinal(value.getBytes());
//            return Base64.encodeBase64String(encrypted);
//        } catch (Exception ex) {
//            log.info("{ Error During Encryption } { "+ ex.getLocalizedMessage()+" }");
//            return ex.getLocalizedMessage();
//        }
//    }
//
//    public String decrypt(String key, String initVector, String encrypted) {
//        try {
//            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
//            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
//            cipher.init(2, skeySpec, iv);
//            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));
//            return new String(original);
//        } catch (Exception ex) {
//            log.info("{ Error During Decryption } { "+ ex.getLocalizedMessage()+" }");
//            return ex.getLocalizedMessage();
//        }
//    }
//
//}
