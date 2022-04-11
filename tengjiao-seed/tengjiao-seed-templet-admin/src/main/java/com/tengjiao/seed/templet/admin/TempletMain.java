package com.tengjiao.seed.templet.admin;

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
 * @ClassName MainApplication
 * @Description seed-admin
 * @Author Administrator
 * @Date 2020/11/12 23:11
 * @Version V1.0
 */
@EnableCaching
@SpringBootApplication
@ServletComponentScan("com.tengjiao.tool.core.web.body")
@org.mybatis.spring.annotation.MapperScan("com.**.mapper")
public class TempletMain {
    public static void main(String[] args) {
        // 改为在 java 参数里设置 -Duser.timezone=GMT+08
        // TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));

        final Class<?> clazz = TempletMain.class;
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
