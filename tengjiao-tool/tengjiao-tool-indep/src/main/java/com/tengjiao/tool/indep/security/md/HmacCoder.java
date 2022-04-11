package com.tengjiao.tool.indep.security.md;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 消息摘要算法 HMAC
 * 消息摘要算法包括MD(Message Digest，消息摘要算法)、SHA(Secure Hash Algorithm，安全散列算法)、MAC(Message AuthenticationCode，消息认证码算法)共3大系列，常用于验证数据的完整性，是数字签名算法的核心算法。
 * MD5和SHA1分别是MD、SHA算法系列中最有代表性的算法。
 * 如今，MD5已被发现有许多漏洞，从而不再安全。SHA算法比MD算法的摘要长度更长，也更加安全。
 * @author kangtengjiao
 */
public class HmacCoder{
    /**
     * JDK支持HmacMD5, HmacSHA1,HmacSHA256, HmacSHA384, HmacSHA512
     */
    public enum HmacTypeEn {
        HmacMD5, HmacSHA1, HmacSHA256, HmacSHA384, HmacSHA512;
    }

    public static byte[] encode(byte[] plaintext, byte[] secretKey, HmacTypeEn type) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec keySpec = new SecretKeySpec(secretKey, type.name());
        Mac mac = Mac.getInstance(keySpec.getAlgorithm());
        mac.init(keySpec);
        return mac.doFinal(plaintext);
    }

}