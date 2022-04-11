package com.tengjiao.part.wx.oa.util;

import cn.hutool.json.JSONObject;
import com.tengjiao.part.wx.oa.model.SnsToken;
import com.tengjiao.tool.indep.HttpTool;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tengjiao
 * @description 网页授权票据获取
 * @date 2021/10/15 15:15
 */
@Slf4j
public class SnsTokenUtil {

    /**
     * 获取网页授权凭证
     *
     * @param appid 公众账号的唯一标识
     * @param appsecret 公众账号的密钥
     * @param code
     * @return SnsToken
     */
    public static SnsToken fetch(String appid, String appsecret, String code) {
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+appsecret+"&code="+code+"&grant_type=authorization_code";

        return request(requestUrl);
    }

    /**
     * 刷新获取网页授权凭证
     *
     * @param appid 公众账号的唯一标识
     * @param refreshToken 网页授权刷新凭证
     * @return SnsToken
     */
    public static SnsToken refresh(String appid, String refreshToken) {

        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid="+appid+"&grant_type=refresh_token&refresh_token="+refreshToken;

        return request(requestUrl);
    }

    private static SnsToken request(String requestUrl) {

        String responseText = null;
        try{
            responseText = HttpTool.get(requestUrl, 3000, 5000);
        }catch(Exception e){
            log.error("获取SnsToken异常",e);
            return null;
        }
        log.info("获取SnsToken => {}", responseText);
        JSONObject jsonObject = new JSONObject(responseText);
        if( jsonObject == null || jsonObject.isEmpty() ) {
            log.error("获取SnsToken失败，返回结果不是有效的json格式");
            return null;
        }
        if( jsonObject.containsKey("errcode") ) {
            Integer errorCode = jsonObject.getInt("errcode");
            String errorMsg = jsonObject.getStr("errmsg");
            log.error("获取SnsToken失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            return null;
        }

        SnsToken snsToken = new SnsToken();
        snsToken.setAccess_token(jsonObject.getStr("access_token"));
        snsToken.setExpires_in(jsonObject.getInt("expires_in"));
        snsToken.setRefresh_token(jsonObject.getStr("refresh_token"));
        snsToken.setOpenid(jsonObject.getStr("openid"));
        snsToken.setScope(jsonObject.getStr("scope"));
        return snsToken;
    }

}
