package com.tengjiao.seed.member.security;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 存放在 线程上下文 的会话模型
 * @author tengjiao
 * @create 2021/8/23 15:16
 */
@Getter
@Setter
@Accessors(chain = true)
public final class SessionModel {
    /**
     * 缓存信息
     */
    private OnlineInfo loginStore;

    private String token;
    private String stationId;
    private Integer device;
    private Integer terminal;
    private String version;

    /**
     * 其他信息
     */
    private Map<String, Object> extras;
}
