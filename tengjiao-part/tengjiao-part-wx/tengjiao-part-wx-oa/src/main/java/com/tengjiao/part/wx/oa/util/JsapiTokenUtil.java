package com.tengjiao.part.wx.oa.util;

import cn.hutool.json.JSONObject;
import com.tengjiao.part.wx.oa.model.JsapiToken;
import com.tengjiao.tool.indep.HttpTool;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author tengjiao
 * @description
 * @date 2021/10/16 21:09
 */
@Slf4j
public class JsapiTokenUtil {

    /**
     * 获取Jsapi票据
     *
     * @param globToken
     * @return
     */
    public static JsapiToken fetch(String globToken) {
        String jsapiTicketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+globToken+"&type=jsapi";

        String responseText = null;
        try {
            responseText = HttpTool.get(jsapiTicketUrl, 3000, 5000);
        } catch (Exception e) {
            log.error("获取JsapiToken异常", e);
            return null;
        }
        log.info("获取JsapiToken => {}", responseText);

        JSONObject jsonObject = new JSONObject(responseText);
        if (jsonObject == null || jsonObject.isEmpty()) {
            log.error("获取JsapiToken失败，返回结果不是有效的json格式");
            return null;
        }

        Integer errcode = jsonObject.getInt("errcode");
        String errmsg = jsonObject.getStr("errmsg");
        if (errcode == null || errcode != 0) {
            log.error("获取JsapiToken失败，errcode = {}, errmsg = {}", errcode, jsonObject.getStr("errmsg"));
            return null;
        }

        JsapiToken jsapiToken = new JsapiToken();

        jsapiToken.setErrcode(errcode);
        jsapiToken.setErrmsg(errmsg);
        jsapiToken.setExpires_in(jsonObject.getLong("expires_in"));
        jsapiToken.setTicket(jsonObject.getStr("ticket"));

        return jsapiToken;

    }

    /**
     * 前端jssdk页面配置需要用到的配置参数
     * @param appid 微信公众号id
     * @param jsapiTicket jsapi_ticket票据
     * @param url url
     * @return
     */
    public static Map<String, String> jsSDK_SIGN(String appid, String jsapiTicket, String url) {
        String noncestr = UUID.randomUUID().toString();
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000); // 时间戳

        // 将参数按排序并拼接字符串
        String str = "jsapi_ticket=" + jsapiTicket + "&noncestr=" + noncestr + "&timestamp=" + timestamp + "&url=" + url;

        // 字符串进行签名并返回结果

        MessageDigest crypt = null;
        try {
            crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-1实例初始化异常", e);
            return null;
        } catch (UnsupportedEncodingException e) {
            log.error("SHA-1消息摘要签名异常", e);
            return null;
        }

        String signature = byteToHex(crypt.digest());

        HashMap<String, String> jssdk = new HashMap<String, String>();
        jssdk.put("appId", appid);
        jssdk.put("timestamp", timestamp);
        jssdk.put("nonceStr", noncestr);
        jssdk.put("signature", signature);

        return jssdk;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

}
