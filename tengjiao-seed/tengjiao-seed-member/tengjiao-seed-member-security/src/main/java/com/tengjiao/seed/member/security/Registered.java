package com.tengjiao.seed.member.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author tengjiao
 * @description
 * @date 2021/8/27 17:08
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Registered {

    boolean registeredRequired() default true;

}
