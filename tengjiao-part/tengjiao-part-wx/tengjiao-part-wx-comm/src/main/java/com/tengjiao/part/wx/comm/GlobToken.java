package com.tengjiao.part.wx.comm;

/**
 * 适用于 微信公众号、小程序  的全局appid令牌
 * @author tengjiao
 * @description
 * @date 2021/11/20 11:43
 */
public class GlobToken {
    // https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
    // 小程序 文档 https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/access-token/auth.getAccessToken.html
    // 公众号 文档 https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Get_access_token.html

    /** access_token是公众号的全局唯一接口调用凭据，公众号调用各接口时都需使用access_token。开发者需要进行妥善保存。access_token的存储至少要保留512个字符空间。access_token的有效期目前为2个小时，需定时刷新，重复获取将导致上次获取的access_token失效。 */
    private String access_token;
    /** 持续的有效时间（秒）*/
    private Integer expires_in;

    public String getAccess_token() {
        return access_token;
    }

    public Integer getExpires_in() {
        return expires_in;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
    }
}
