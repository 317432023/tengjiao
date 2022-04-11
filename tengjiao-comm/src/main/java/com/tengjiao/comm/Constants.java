package com.tengjiao.comm;

/**
 * 常量定义
 * @author rise
 * @date 2020/06/04
 */
public interface Constants {
    String
      /**用户名参数名*/
      PAR_USER_NAME = "username",
      /**密码参数名*/
      PAR_PASSWORD = "password",
      /**图形验证码参数名*/
      PAR_CAPTCHA = "captcha";

    String
      /**redis键分隔符*/
      KEY_DELIMITER = ":",
      /**图形验证码令牌键|cookie属性名*/
      CAPTCHA_TOKEN_NAME = "captchaToken",
      /**base64图片前缀*/
      BASE_64_IMG_PREFIX = "data:image/png;base64,";

    String
      /**短信码缓存键前缀*/
      SMS_CODE_KEY = "smsCodeKey",
      /**令牌缓存键前缀_登录*/
      LOGIN_TOKEN_NAME = "loginToken",
      /**令牌缓存键前缀_修改密码*/
      MODIFY_PWD_TOKEN_NAME = "modifyPwdToken";

    String
      CAPTCHA_TOKEN_PREFIX = CAPTCHA_TOKEN_NAME + KEY_DELIMITER,
      LOGIN_TOKEN_PREFIX = LOGIN_TOKEN_NAME + KEY_DELIMITER,
      SMS_CODE_KEY_PREFIX = SMS_CODE_KEY + KEY_DELIMITER;

    enum ProfileType {
        prod,test,dev;
    }

}
