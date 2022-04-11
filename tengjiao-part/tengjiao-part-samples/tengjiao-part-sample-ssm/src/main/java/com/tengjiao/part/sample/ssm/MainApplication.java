package com.tengjiao.part.sample.ssm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @ClassName MainApplication
 * @Description
 * @Author kangtengjiao
 * @Date 2020/11/12 23:11
 * @Version V1.0
 */
@SpringBootApplication
@ServletComponentScan("com.tengjiao.tool.core.web.body")
@EnableCaching
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
//@org.mybatis.spring.annotation.MapperScan(basePackages = { "com.**.dao.mybatis" })
public class MainApplication {
  public static void main(String[] args) {
    SpringApplication.run(MainApplication.class, args);
  }

}
