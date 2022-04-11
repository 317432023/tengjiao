package com.tengjiao.part.sample.ssj;

import com.tengjiao.part.jpa.BaseRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

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
@EntityScan("com.tengjiao.part.sample.*.entity") // 用於 JPA 扫描存放在主类包外部的实体
@EnableJpaRepositories(basePackages = "com.tengjiao.part.sample.*.dao", repositoryBaseClass = BaseRepositoryImpl.class)
//@org.mybatis.spring.annotation.MapperScan("starter.**.mapper")
public class MainApplication {
  public static void main(String[] args) {
    SpringApplication.run(MainApplication.class, args);
  }

}
