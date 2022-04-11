package com.tengjiao.seed.member.security;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 在线信息
 * @author tengjiao
 * @description 会员登录成功后存储到缓存的数据
 * @date 2021/8/23 0:41
 */
@Getter
@Setter
public class OnlineInfo implements Serializable {
    private static final long serialVersionUID = -7480163108902337234L;

    public static final String DEFAULT_SALT = "food";

    private int platform;
    private String userKey;
    private String keyExtra;
    private long memberId;

    /** 消息签名加盐 */
    private String salt;
}
