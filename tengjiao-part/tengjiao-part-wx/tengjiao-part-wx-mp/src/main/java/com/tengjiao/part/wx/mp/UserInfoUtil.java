package com.tengjiao.part.wx.mp;

import cn.hutool.core.codec.Base64;
import com.tengjiao.tool.third.security.crypto.AesCoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @author tengjiao
 * @description
 * @date 2021/11/21 0:03
 */
@Slf4j
public class UserInfoUtil {

    /**
     * 接口如果涉及敏感数据（如wx.getUserInfo当中的 openId 和unionId ），接口的明文内容将不包含这些敏感数据。
     * 开发者如需要获取敏感数据，需要对接口返回的加密数据( encryptedData )进行对称解密
     * @param encryptedData
     * @param sessionKey
     * @param iv
     * @return
     */
    public static String getUserInfo(String encryptedData, String sessionKey, String iv){
        // 被加密的数据

        byte[] dataByte = Base64.decode(encryptedData);

        // 加密秘钥

        byte[] keyByte = Base64.decode(sessionKey);

        // 偏移量

        byte[] ivByte = Base64.decode(iv);

        // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要

        int base = 16;

        if (keyByte.length % base != 0) {

            int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);

            byte[] temp = new byte[groups * base];

            Arrays.fill(temp, (byte) 0);

            System.arraycopy(keyByte, 0, temp, 0, keyByte.length);

            keyByte = temp;

        }

        try {

            byte[] origData = AesCoder.decrypt(AesCoder.AES_CBC_PKCS7Padding, dataByte, keyByte, ivByte);

            return new String(origData, "UTF-8");

        } catch (Exception  e) {

            log.error("decrypt failed", e);

        }

        return null;

    }

}
