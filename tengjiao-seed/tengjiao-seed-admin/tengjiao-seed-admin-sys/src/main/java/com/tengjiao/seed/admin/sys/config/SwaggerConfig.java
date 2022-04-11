package com.tengjiao.seed.admin.sys.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.base.Predicate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.PathProvider;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.Paths;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/** 文档美化*/
@EnableKnife4j
@Configuration
@EnableSwagger2
public class SwaggerConfig {

  /**
   * 指定欲生成api的包，如果需要的话用 apiPredicate(3) 用到
   */
  private final static String apiBasePackage = "com.tengjiao.seed.admin.sys.controller";

  @Bean
  public Docket createRestApi() {
    // 添加请求参数，我们这里把token作为请求头部参数传入后端
    ParameterBuilder parameterBuilder = new ParameterBuilder();
    List<Parameter> parameters = new ArrayList<Parameter>();
    parameterBuilder.name("Authorization").description("令牌").modelRef(new ModelRef("string")).parameterType("header")
      .required(false).build();
    parameters.add(parameterBuilder.build());

    return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
      .select().apis(this.apiPredicate(1)).paths(PathSelectors.any())
      .build().globalOperationParameters(parameters)
      .pathProvider(pathProvider());
  }


  /**
   * api生成选择器
   * @param flag
   *  1- 对包含ApiOperation标注的控制器方法生成API文档 <br>
   *  2- 对包含Api标注的控制器生成API文档 <br>
   *  3- 对指定包下面的控制器方法生成API文档 <br>
   *  其余情况- 对所有控制器方法生成API文档
   * @return
   */
  private Predicate<RequestHandler> apiPredicate(int flag) {
    switch(flag) {
      case 1:
        return RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class);
      case 2:
        return RequestHandlerSelectors.withClassAnnotation(Api.class);
      case 3:
        return RequestHandlerSelectors.basePackage(apiBasePackage);
      default:
        break;
    }
    return RequestHandlerSelectors.any();
  }

  /**
   * 指定api元数据信息
   * @return
   */
  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
      .title("系统API文档")
      .description("如题")
      .termsOfServiceUrl("https://github.com/317432023/tengjiao-seed")
      .contact(new Contact("rise", "https://github.com/317432023/", "317432023@qq.com"))
      .version("1.0")
      .build();
  }

  @Value("${server.servlet.context-path:/}")
  private String servletContextPath;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Value("${tengjiao.swagger.customContextPath:/unknown}")
  private String customContextPath;

  /**
   * 重写 PathProvider
   */
  @Bean
  @Primary
  public PathProvider pathProvider() {
    return new RelativePathProvider(webApplicationContext.getServletContext()) {

      ///**
      // * 解决 context-path 重复问题
      // * @param operationPath
      // * @return
      // */
      //@Override
      //public String getOperationPath(String operationPath) {
      //  // operationPath = operationPath.replaceFirst(servletContextPath, operationPath);
      //  operationPath = getApplicationBasePath() + ( operationPath.startsWith("/") ? operationPath: "/"+operationPath );
      //  if(operationPath.startsWith("//")) {
      //    operationPath = operationPath.substring(1);
      //  }
      //
      //  UriComponentsBuilder uriComponentBuilder = UriComponentsBuilder.fromPath("/");
      //  return Paths.removeAdjacentForwardSlashes(uriComponentBuilder.path(operationPath).build().toString());
      //}


      @Override
      public String getApplicationBasePath() {
        return "/unknown".equals(customContextPath)?servletContextPath:customContextPath;
      }

    };
  }

}