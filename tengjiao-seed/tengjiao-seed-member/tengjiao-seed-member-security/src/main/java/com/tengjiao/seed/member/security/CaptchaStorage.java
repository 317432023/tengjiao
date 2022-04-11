package com.tengjiao.seed.member.security;

import com.tengjiao.part.redis.ModeDict;
import com.tengjiao.part.redis.RedisTool;
import com.tengjiao.tool.indep.model.SystemException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import java.util.UUID;

import static com.tengjiao.comm.Constants.*;

/**
 * @author tengjiao
 * @description
 * @date 2021/9/2 11:48
 */
@Component
@AllArgsConstructor
public class CaptchaStorage {

    private RedisTool redisTool;
    private HttpServletRequest request;

    public void checkCaptcha(String captchaCode) {
        final String captchaToken = request.getHeader(CAPTCHA_TOKEN_NAME);
        if(!captchaCode.equals( redisTool.getString( CAPTCHA_TOKEN_PREFIX + captchaToken, ModeDict.APP_GROUP ) ) ) {
            throw SystemException.create("图形验证码错误");
        }
    }

    public String get() {
        final String captchaToken = request.getHeader(CAPTCHA_TOKEN_NAME);
        final String captchaCode = redisTool.getString( CAPTCHA_TOKEN_PREFIX + captchaToken, ModeDict.APP_GROUP);
        return captchaCode;
    }

    private String put(String captchaToken, String captchaCode) {
        redisTool.setString( CAPTCHA_TOKEN_PREFIX + captchaToken, captchaCode, ModeDict.APP_GROUP);
        return captchaToken;
    }

    public String put(String captchaCode) {
        final String captchaToken = UUID.randomUUID().toString().replaceAll("-", "");
        return put(captchaToken, captchaCode);
    }

}
