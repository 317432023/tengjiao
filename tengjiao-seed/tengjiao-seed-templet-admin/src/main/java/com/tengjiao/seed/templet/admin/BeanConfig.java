package com.tengjiao.seed.templet.admin;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <p>扫描存放在主类包外部的一切Spring Bean</p>
 * 包含@Service、@Component、@Aspect、@Repository、@Configuration等spring标注
 * @author kangtengjiao
 */
@Configuration
@ComponentScan(basePackages = {
  // part 比如 "com.tengjiao.part",
  "com.tengjiao.part.springmvc",
  "com.tengjiao.part.springboot",
  "com.tengjiao.part.redis",       // redis 配置序列化重写 + redisTool
  "com.tengjiao.part.mybatisplus", // mybatisplus
  /*"starter.core.knife4j",*/ // knife4j 文档 美化 + swagger
  "com.tengjiao.seed.admin",       // 种子项目 admin

  // service 比如 "com.tengjiao.*.service",
  // dao 比如 "com.tengjiao.*.dao.hib"
})
public class BeanConfig {


}
