package com.tengjiao.seed.member.security;

import java.util.Arrays;

/**
 * 终端类型 枚举
 * @author tengjiao
 * @description
 * @date 2021/8/22 21:01
 */
public enum TerminalType {
    /** 未知终端类型 */
    Unknown(0, 0, OnlineRenewStrategy.NEVER_RENEW),
    /** 电脑浏览器 */
    PC(1, 30*60, OnlineRenewStrategy.RENEW_EVERY_REQUEST_BEFORE_EXPIRE),

    /** app */
    APP(2, 30*24*3600, OnlineRenewStrategy.NEVER_RENEW),

    /** 微信小程序 */
    WeChat_Mini(3, 30*24*3600, OnlineRenewStrategy.NEVER_RENEW),

//    /** @deprecated 手机端，包含了app + 小程序 */
//    HANDSET(4, 7*24*3600, SessionRenewStrategy.NEVER_RENEW),



    ;


    /** 类型代号 */
    public final int typeCode;

    /** 会话存活时间单位秒 */
    public final long duration;

    /** 会话续约策略 */
    public final OnlineRenewStrategy strategy;

    private TerminalType(int typeCode, long duration, OnlineRenewStrategy strategy) {
        this.typeCode = typeCode;
        this.duration = duration;
        this.strategy = strategy;
    }

    public static TerminalType getByTypeCode(int typeCode) {
        return Arrays.stream(TerminalType.values()).filter(e->e.typeCode == typeCode).findFirst().orElse(Unknown);
    }
}
