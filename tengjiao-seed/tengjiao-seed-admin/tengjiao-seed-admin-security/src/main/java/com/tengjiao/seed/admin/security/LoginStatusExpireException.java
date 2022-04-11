package com.tengjiao.seed.admin.security;

/**
 * @author Administrator
 */
public class LoginStatusExpireException extends RuntimeException {

    private static final long serialVersionUID = -1818734902306548641L;

    @Override
    public String getMessage() {
        return "登陆状态过期，请重新登陆";
    }
}