package com.tengjiao.assembly.sample.upload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.util.StringUtils;
/**
 * @author tengjiao
 * @description
 * @date 2021/8/26 17:27
 */
@SpringBootApplication
public class UploadSampleMain {
    public static void main(String[] args) {
        final Class<?> clazz = UploadSampleMain.class;
        final String pidfile = args != null && args.length > 0?args[0]:null;
        if(!StringUtils.isEmpty(pidfile)) {
            SpringApplication sa = new SpringApplication(clazz);
            sa.addListeners(new ApplicationPidFileWriter(pidfile));
            sa.run(args);
        }else {
            SpringApplication.run(clazz, args);
        }

    }
}