package com.tengjiao.part.wx.comm;

import cn.hutool.json.JSONObject;
import com.tengjiao.tool.indep.HttpTool;
import lombok.extern.slf4j.Slf4j;

/**
 * 获取 微信公众号、小程序  的全局令牌
 * @author tengjiao
 * @description <br>
 *  注意：登录“微信公众平台-开发-基本配置”提前将服务器IP地址添加到IP白名单中，点击查看设置方法，否则将无法调用成功。**小程序无需配置IP白名单。
 * @date 2021/11/20 12:20
 */
@Slf4j
public final class GlobTokenUtil {
    public static GlobToken fetch(String appid, String appsecret) {
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+appsecret;

        String responseText = null;

        try {
            responseText = HttpTool.get(requestUrl, 3000, 5000);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
        log.info("获取GlobToken => {}", responseText);

        JSONObject jsonObject = new JSONObject(responseText);

        if (null != jsonObject) {
            try {
                GlobToken ot = new GlobToken();
                ot.setAccess_token(jsonObject.getStr("access_token"));
                ot.setExpires_in(jsonObject.getInt("expires_in"));
                return ot;
            } catch (Exception var7) {
                int errorCode = jsonObject.getInt("errcode");
                String errorMsg = jsonObject.getStr("errmsg");
                log.error("获取全局票据失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            }
        }

        return null;
    }
}
