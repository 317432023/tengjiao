package com.tengjiao.comm.sms.model;

/**
 * @author tengjiao
 * @description
 * @date 2021/9/7 19:01
 */
public enum CommSmsStatusDict {
    INIT(0, "待发送"),
    PENDING(1, "发送中"),
    SUCCESS(2, "发送成功"),
    FAIL(3, "发送失败"),
    CANCEL(4, "取消发送");

    //发送状态（0待发送、1发送中、2发送成功、3发送失败、4取消发送）
    public final int typeCode;
    public final String name;

    CommSmsStatusDict(int typeCode, String name) {
        this.typeCode = typeCode;
        this.name = name;
    }

}
