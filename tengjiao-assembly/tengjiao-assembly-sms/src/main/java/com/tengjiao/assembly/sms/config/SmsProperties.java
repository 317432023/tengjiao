package com.tengjiao.assembly.sms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author tengjiao
 */
@Component
@PropertySource(value = {"classpath:config/sms.properties", "file:${config.sms}"}, encoding = "utf-8", ignoreResourceNotFound = true)
@ConfigurationProperties(prefix = "sms", ignoreInvalidFields = true)
public class SmsProperties {
    private String sender = "aliSmsManager";

    private Integer smsCodeSize = 5;
    private Integer smsCodeDuration = 5;
    private String smsCodeSource = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234567890";
    private Integer smsCodeTempletId = 0;

    public Integer getSmsCodeSize() {
        return smsCodeSize;
    }

    public void setSmsCodeSize(Integer smsCodeSize) {
        this.smsCodeSize = smsCodeSize;
    }

    public Integer getSmsCodeDuration() {
        return smsCodeDuration;
    }

    public void setSmsCodeDuration(Integer smsCodeDuration) {
        this.smsCodeDuration = smsCodeDuration;
    }

    public String getSmsCodeSource() {
        return smsCodeSource;
    }

    public void setSmsCodeSource(String source) {
        this.smsCodeSource = source;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Integer getSmsCodeTempletId() {
        return smsCodeTempletId;
    }

    public void setSmsCodeTempletId(Integer smsCodeTempletId) {
        this.smsCodeTempletId = smsCodeTempletId;
    }


}
