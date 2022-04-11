package com.tengjiao.part.wx.oa.model;

/**
 * 网页授权凭证，该类对应于前端微信用户会话对象
 * @author tengjiao
 * @description
 */
public class SnsToken {
    /** 网页授权接口调用凭证*/
    private String access_token;
    /** 凭证有效时长，腾讯规定默认7200秒，也就是说保留2个小时*/
    private int expires_in;
    /** 用于刷新凭证，腾讯保存30天*/
    private String refresh_token;
    /** 用户标识*/
    private String openid;
    /** 用户授权的作用域，使用逗号（,）分隔*/
    private String scope;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
