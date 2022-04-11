package com.tengjiao.tool.indep.test.security.md;

//import com.tengjiao.tool.core.security.md.HmacCoder;
//import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.codec.digest.HmacAlgorithms;
//import org.apache.commons.codec.digest.HmacUtils;
//import org.junit.Test;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import java.io.UnsupportedEncodingException;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//
//public class HmacCoderTest {
//
//    @Test
//    public void test() throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException {
//        String msg = "Message";
//        byte[] secretKey = "secret".getBytes("UTF8");
//        byte[] digest = HmacCoder.encode(msg.getBytes(), secretKey, HmacCoder.HmacTypeEn.HmacSHA256);
//        System.out.println("原文: " + msg);
//        System.out.println("摘要: " + Base64.encodeBase64String(digest));
//    }
//    @Test
//    public void test2() throws UnsupportedEncodingException {
//        String valueToDigest = "Message";
//        byte[] secretKey = "secret".getBytes("UTF8");
//        HmacUtils hm1 = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secretKey);
//        byte[] digest = hm1.hmac(valueToDigest);
//        System.out.println("原文: " + valueToDigest);
//        System.out.println("摘要: " + Base64.encodeBase64String(digest));
//    }
//    @Test
//    public void test3() {
//        try {
//            String message = "Message";
//            String secret = "secret";
//
//            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
//            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(),
//                    "HmacSHA256");
//            sha256_HMAC.init(secret_key);
//
//            String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(message
//                    .getBytes()));
//            System.out.println(hash);
//        } catch (Exception e) {
//            System.out.println("Error");
//        }
//    }
//}