package com.tengjiao.seed.admin.sample;

import com.tengjiao.part.springboot.ApplicationStartedEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

/**
 * @author administrator
 * @version V1.0
 * @description seed-admin
 * @date 2020/11/12 23:11
 */
@EnableCaching
@SpringBootApplication
@ServletComponentScan("com.tengjiao.tool.indep.web.body")
@org.mybatis.spring.annotation.MapperScan("com.**.mapper")
public class MainApplication {
    public static void main(String[] args) {
        final Class<?> clazz = MainApplication.class;
        // SpringApplication.run(clazz, args);
        final String pidfile = args != null && args.length > 0 ? args[0] : null;

        SpringApplicationBuilder builder = new SpringApplicationBuilder(clazz)
          .initializers((ConfigurableApplicationContext context) -> {
              //System.setProperty("serverId", context.getEnvironment().getProperty("spring.application.name"));
          });
        SpringApplication app = builder.build();
        //SpringApplication app = new SpringApplication(clazz);
        if (!StringUtils.isEmpty(pidfile)) {
            app.addListeners(new ApplicationPidFileWriter(pidfile));
        }
        app.addListeners(new ApplicationStartedEventListener());
        ConfigurableApplicationContext ac = app.run(args);
    }

}
