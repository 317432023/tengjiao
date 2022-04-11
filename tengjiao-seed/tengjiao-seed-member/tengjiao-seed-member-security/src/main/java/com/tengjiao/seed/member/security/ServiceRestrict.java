package com.tengjiao.seed.member.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口约束
 * @author tengjiao
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ServiceRestrict {

    /**
     * 支持的终端类型 1-PC 2-APP 3-(微信)小程序
     * @return
     */
    TerminalType[] supportTerminalTypes() default { TerminalType.PC, TerminalType.APP, TerminalType.WeChat_Mini};

    /**
     * 是否需要登录
     * @return
     */
    boolean loginRequired() default true;

    /**
     * 会话续约策略
     */
    OnlineRenewStrategy sessionRenewStrategy() default OnlineRenewStrategy.RENEW_EVERY_REQUEST_BEFORE_EXPIRE;

    /**
     * 是否使用 接口请求期限 限制
     * @return
     */
    boolean useServiceExpire() default false;
    /**
     * 接口请求期限（接口请求发送的时间戳 与 服务器的时间相差，单位秒，大于此值将拒绝进一步的请求）
     * @return
     */
    int expires() default 10;

    /**
     * 是否区分设备
     * @return
     */
    boolean useDevice() default true;
    /**
     * 是否区分客户端版本
     * @return
     */
    boolean useVersion() default true;

    /**
     * 是否开启参数签名校验
     * @return
     */
    boolean paramSignRequired() default false;


    /**
     * 是否使用多站点
     * @return
     */
    boolean useMultiStation() default false;
}
