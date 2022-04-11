package com.tengjiao.seed.member.security;

/**
 * @author tengjiao
 * @description
 * @date 2021/8/24 21:27
 */
public interface Constants {

    /**
     * 服务请求头
     */
    interface ServiceHeaders {
        /* 请求头 令牌Key，仅loginRequired为true的情况下需要校验（禁止包含下划线_） */
        String TOKEN_KEY = "X-Token";
        /* 请求头 客户端版本(APP、小程序、PC打包的版本) Key */
        String VERSION_KEY = "version";
        /* 请求头 时间戳Key，单位毫秒 */
        String TIMESTAMP_KEY = "timestamp";
        /* 请求头 签名后的消息摘要 Key */
        String SIGNATURE_KEY = "signature";
        /* 请求头 设备类型 Key */
        String DEVICE_KEY = "device";
        /* 请求头 终端类型 Key */
        String TERMINAL_KEY = "terminal";
        /* 请求头 站点id Key，仅当使用多站点时需要校验 */
        String SAAS_KEY = "station-id";
    }

}
