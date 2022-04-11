package com.tengjiao.seed.admin.security.config;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author tengjiao
 * @create 2021/8/16 22:57
 */
@PropertySource(value = {"file:${config.setting}"}, encoding="utf-8", ignoreResourceNotFound=true)
@Component
@Getter
public class SettingProperties {
    @Resource
    private Environment environment;

    /** 安全认证策略(session|token) */
    //@Value("${securityStrategy}")
    private String securityStrategy = "token";
    //@Value("${tokenHeaderKey}")
    private String tokenHeaderKey = "Authorization";
    //@Value("${tokenValPrefix}")
    private String tokenValPrefix = "Bearer ";

    /** 登录处理器类型(controller|filter) */
    //@Value("${loginProcessor}")
    private String loginProcessor = "filter";

    /** 图形验证码长度 */
    private Integer captchaSize = 5;
    /** 图形验证码有效期(分钟) */
    private Integer captchaDuration = 5;

    /** 登录持续周期(分钟) */
    //@Value("${duration}")
    private Long duration = 30L;

    /** 密码策略_是否加密密码 */
    //@Value("${rsa.password.encrypt}")
    private Boolean encryptPassword = Boolean.TRUE;
    /** 密码策略_用于解密的私钥 */
    //@Value("${rsa.privateKey}")
    private String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBANHtrETGQh9E2ZeFv2DqS3IOJGL35y8e6nzJnrl2lkVkY5niZTpT/RGrgDv5tBHJbbsYu0lw7vbBmpIS2+D7hKGhpqnmXcd91HYajsbvvxbnuX1i9mbCclce2AzypLfWbi4hSIiFXKgdy6OX9+IX+bJ5UBkLkyYVHGdWfbWvLutLAgMBAAECgYBi2QHzxFLgPGAky9KIhOoPg384prhR5MHPDnTRqXgqppGLpSG7LE8OYe3Yz8QtV+Z2YbfWZfnvcMwimgS01Fnnk0T6xCjz2IJRjTzISElWyiGslFWm0qjFrVEydDucM0kw3jBmw4roeVGGKkmbR0C2QjB4czdERqihZZxOgu3+kQJBAOyCViBiJcOhzzEzM9SuHcsYoi5Mwia93fzoroojNPWShmIx/PSvE3LxYzZszFBpy3uzTN1hMslu3fQg9s1MKdkCQQDjOo9HKLxnHTT0tB6h1aySJqt8+8dD9rQ7J8HYrD9idcPXVZ5WyM6x0i8YGj2u+TR1KDx/9fnZlAjwOy4JKYPDAkAVonqUbcX8llGos4FbRC4vhOpZB+Z+usGSwAovUrcme6fVVbNJArbpvdEQinVKKdNWVnmCisClkoG1MkrBTuVJAkAlxYA3pe/bfCFnY+GLqUcsbD0pY3j2NTY0dyFj8Q4AQe8yBktxxaOGL5gge4wxX792kh+sbakAn/j0ZgH7B+mdAkAIN2hkHJPpgc6L0111TdtdH/lvgxmJ9QgOd1wb5pHL2mtUUvXK3SkOF08agg3kv8I9qmDOcjs7A6zEjAV7Dijn";
    /** 静态资源默认存放位置 */
    //@Value("${staticCustomLocations}")
    private String staticCustomLocations = "classpath:/META-INF/resources/,classpath:/resources/,classpath:/static/,classpath:/public/";

    @PostConstruct
    private void init() {

        securityStrategy = StringUtils.isNotBlank(environment.getProperty("securityStrategy"))? environment.getProperty("securityStrategy"):securityStrategy;
        tokenHeaderKey = StringUtils.isNotBlank(environment.getProperty("tokenHeaderKey"))? environment.getProperty("tokenHeaderKey"):tokenHeaderKey;
        tokenValPrefix = StringUtils.isNotBlank(environment.getProperty("tokenValPrefix"))? environment.getProperty("tokenValPrefix"):tokenValPrefix;
        loginProcessor = StringUtils.isNotBlank(environment.getProperty("loginProcessor"))? environment.getProperty("loginProcessor"):loginProcessor;

        captchaSize = StringUtils.isNotBlank(environment.getProperty("captchaSize"))?Integer.parseInt(environment.getProperty("captchaSize")):captchaSize;
        captchaDuration = StringUtils.isNotBlank(environment.getProperty("captchaDuration"))?Integer.parseInt(environment.getProperty("captchaDuration")):captchaDuration;

        duration = StringUtils.isNotBlank(environment.getProperty("duration"))?Long.parseLong(environment.getProperty("duration")):duration;
        encryptPassword = StringUtils.isNotBlank(environment.getProperty("rsa.password.encrypt"))?Boolean.valueOf(environment.getProperty("rsa.password.encrypt")):encryptPassword;
        privateKey = StringUtils.isNotBlank(environment.getProperty("rsa.privateKey"))? environment.getProperty("rsa.privateKey"):privateKey;

        staticCustomLocations = StringUtils.isNotBlank(environment.getProperty("staticCustomLocations"))? staticCustomLocations + "," + environment.getProperty("staticCustomLocations")
          : staticCustomLocations
        ;
    }
}
