package com.tengjiao.part.wx.oa.model;

/**
 * 微信用户信息
 * @author tengjiao
 * @description
 * @date 2021/10/16 19:04
 */
public abstract class AbsUserInfo {

    /** 用户标识 */
    private String openid;
    private String unionid;
    /** 用户昵称 */
    private String nickname;
    /** 性别（1是男性，2是女性，0是未知） */
    private int sex;
    /** 国家 */
    private String country;
    /** 省份 */
    private String province;
    /** 城市 */
    private String city;
    /** 用户头像链接 */
    private String headimgurl;
    /** 语言 */
    private String language;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }



}
