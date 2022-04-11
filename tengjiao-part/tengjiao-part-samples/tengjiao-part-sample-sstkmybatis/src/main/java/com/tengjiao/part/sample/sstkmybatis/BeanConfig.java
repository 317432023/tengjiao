package com.tengjiao.part.sample.sstkmybatis;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <p>扫描存放在主类包外部的一切Spring Bean</p>
 * 包含@Service、@Component、@Aspect、@Repository、@Configuration等spring标注
 * @author kangtengjiao
 */
@Configuration
@ComponentScan(basePackages = {
        "com.tengjiao.part.springmvc",
        //"com.tengjiao.part.jpa",
        //"com.tengjiao.part.tkmybatis",
        "com.tengjiao.part.aspect", // 自定义切面
        "com.tengjiao.part.redis",  // redis 配置序列化重写 + redisTool

        // service 比如 "starter.*.service",
        // dao 比如 "starter.*.dao.hib"

})
public class BeanConfig {


}
