package com.tengjiao.part.redis;

/**
 * redis 存储模式
 * @author tengjiao
 * @description
 * @date 2021/9/4 17:52
 */
public enum ModeDict {
    NONE(0, "不包含任何应用前缀"),
    APP(1, "使用自身应用前缀"),
    APP_GROUP(2, "使用应用组前缀");

    public final int typeCode;
    public final String name;

    ModeDict(int typeCode, String name) {
        this.typeCode = typeCode;
        this.name = name;
    }

}
