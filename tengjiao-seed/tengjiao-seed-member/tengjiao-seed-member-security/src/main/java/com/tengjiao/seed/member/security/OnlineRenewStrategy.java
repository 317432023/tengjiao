package com.tengjiao.seed.member.security;

/**
 * 在线状态续约策略
 * @author tengjiao
 * @description
 * @date 2021/8/22 21:46
 */
public enum OnlineRenewStrategy {

    /** 永不续约 */
    NEVER_RENEW,

    /** 每次请求自动续约至满 */
    RENEW_EVERY_REQUEST_BEFORE_EXPIRE,



    ;
}
