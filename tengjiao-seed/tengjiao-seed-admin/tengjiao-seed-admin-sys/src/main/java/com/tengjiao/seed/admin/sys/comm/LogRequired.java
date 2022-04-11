package com.tengjiao.seed.admin.sys.comm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>操作日志</p>
 * 配置LogAspect切面使用，由于LogAspect中需要注入记录日志的业务Bean，故将LogAspect放在具体的工程目录下
 * @author Administrator
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRequired {
    String value() default "";
}