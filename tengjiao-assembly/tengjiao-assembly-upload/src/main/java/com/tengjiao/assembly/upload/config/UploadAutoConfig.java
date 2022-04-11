package com.tengjiao.assembly.upload.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * @author tengjiao
 */
@Configuration
@EnableConfigurationProperties(UploadProperties.class)
public class UploadAutoConfig {

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("UTF-8");//
        multipartResolver.setMaxUploadSize(10485760);//10m size
        multipartResolver.setMaxInMemorySize(40960);//40k buffer
        return multipartResolver;
    }
}
