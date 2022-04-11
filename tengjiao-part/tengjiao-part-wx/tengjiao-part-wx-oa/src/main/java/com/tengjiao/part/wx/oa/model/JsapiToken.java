package com.tengjiao.part.wx.oa.model;

/**
 * jsapi安全域名 调用jsapi的票据，它的生命周期与GlobToken是一致的<br>
 *     https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/JS-SDK.html
 * @description 先登录微信公众平台进入“公众号设置”的“功能设置”里填写“JS接口安全域名”，如果需要使用支付类接口，需要确保支付目录在支付的安全域名下，否则将无法完成支付！（注：登录后可在“开发者中心”查看对应的接口权限）<br>
 *     通过使用微信JS-SDK，网页开发者可借助微信高效地使用拍照、选图、语音、位置等手机系统的能力，同时可以直接使用微信分享、扫一扫、卡券、支付等微信特有的能力
 * @author tengjiao
 * @description
 * @date 2021/10/16 20:48
 */
public class JsapiToken {
    /** 0-成功获取ticket；其他-失败 */
    private Integer errcode;
    private String errmsg;
    private String ticket;
    /** 有效时长秒，默认7200秒*/
    private Long expires_in;

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Long expires_in) {
        this.expires_in = expires_in;
    }

    @Override
    public String toString() {
        return "JsapiToken{" +
          "errcode='" + errcode + '\'' +
          ", errmsg='" + errmsg + '\'' +
          ", ticket='" + ticket + '\'' +
          ", expires_in=" + expires_in +
          '}';
    }
}
