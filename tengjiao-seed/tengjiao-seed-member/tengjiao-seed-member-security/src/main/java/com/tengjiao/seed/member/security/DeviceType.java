package com.tengjiao.seed.member.security;

import java.util.Arrays;

/**
 * 设备类型 枚举
 * @author tengjiao
 * @description
 * @date 2021/8/24 21:35
 */
public enum DeviceType {
    Unknown(0,"未知设备"),
    Android(1,"安卓"),
    IOS(2, "苹果IOS")
    ;
    public final int typeCode;
    public final String name;
    private DeviceType(int typeCode, String name) {
        this.typeCode = typeCode;
        this.name = name;
    }
    public static DeviceType getByTypeCode(int typeCode) {
        return Arrays.stream(DeviceType.values()).filter(e->e.typeCode == typeCode).findFirst().orElse(Unknown);
    }
}
