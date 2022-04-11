package com.tengjiao.assembly.captcha.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author tengjiao
 * @description
 * @date 2021/8/26 20:12
 */

@Component
@PropertySource(value = {"classpath:config/captcha.properties", "file:${config.captcha}"}, encoding = "utf-8", ignoreResourceNotFound = true)
@ConfigurationProperties(prefix = "captcha", ignoreInvalidFields = true)
public class CaptchaProperties {
    private Integer captchaSize = 5;
    private Integer captchaDuration = 5;

    public Integer getCaptchaSize() {
        return captchaSize;
    }

    public void setCaptchaSize(Integer captchaSize) {
        this.captchaSize = captchaSize;
    }

    public Integer getCaptchaDuration() {
        return captchaDuration;
    }

    public void setCaptchaDuration(Integer captchaDuration) {
        this.captchaDuration = captchaDuration;
    }

}
