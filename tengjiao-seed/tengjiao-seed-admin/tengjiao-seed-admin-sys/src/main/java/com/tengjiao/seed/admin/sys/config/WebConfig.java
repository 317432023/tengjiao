package com.tengjiao.seed.admin.sys.config;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.tengjiao.part.springmvc.CustomDateConverter;
import com.tengjiao.part.springmvc.GlobalInterceptor;
import com.tengjiao.seed.admin.security.config.SettingProperties;
import com.tengjiao.tool.third.json.FastJsonTool;
import com.tengjiao.tool.third.json.JackJsonTool;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.FormContentFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;
import java.util.*;

/**
 * @author Administrator
 * <p> 关于日期转换
 * spring 将前端 json 转后端 pojo 底层使用的是Json序列化Jackson工具（HttpMessgeConverter）
 * spring 而时间日期字符串作为普通请求参数传入时，转换用的是 Converter
 * 两者在处理方式上有区别。
 *
 * 后端 接收前端(非 JSON ) 请求传递 Date 的格式 配置
 *      * spring.mvc.date-format=yyyy-MM-dd HH:mm:ss                                   仅适用于 GET DELETE 请求
 *      * 或者直接在接收的请求参数中标注 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  仅适用于 GET DELETE 请求
 *      * 使用自定义参数转换器 参见 CustomDateConverter                                   仅适用于 POST PUT PATCH 请求
 *      * 或者 使用 ControllerAdvice 配合 initBinder，参见 ERP 的控制器基类 BaseController 仅适用于 POST PUT PATCH 请求
 *      * 支持java8日期api
 *
 * 后端 接收和响应 前端 JSON (spring jackson) 体的 日期序列化 格式 配置
 *      * spring.jackson.time-zone=GMT+8
 *        spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
 *        spring.jackson.serialization.write-dates-as-timestamps=false
 *      * 或者直接在响应对象日期类型属性中标注 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
 *      * 不支持java8日期api，要求输入和输出使用统一的格式
 *      * 重写 Jackson的JSON序列化和反序列化 行为 适合大型项目在基础包中全局设置 见 JackJsonTool.makeCustomObjectMapper()
 *
 * https://www.csdn.net/tags/MtjaggxsNzU0NTUtYmxvZwO0O0OO0O0O.html
 * </p>
 */
@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private SettingProperties settingProperties;
    private GlobalInterceptor globalInterceptor;

    /**
     * 设置全局UTF-8编码过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean<CharacterEncodingFilter> characterFilterRegistration() {
        FilterRegistrationBean<CharacterEncodingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CharacterEncodingFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("encoding", "UTF-8");
        registration.setName("characterEncodingFilter");
        return registration;
    }

    /**
     * springmvc代码方式的资源文件查找
     * <b>实际上也可以在 springBoot 的 application*.yml
     *  中配置spring.mvc.static-path-pattern和
     *      spring.resources.static-locations
     * 但是这种方式更加灵活，如果同时配置的话最终生效以代码方式为准
     * </b>
     * <p>兼容 swagger 和 knife4j</p>
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc.html","/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        String[] locations = settingProperties.getStaticCustomLocations().split(",");
        registry.addResourceHandler("/**").addResourceLocations(locations);
    }

    /**
     * 注册拦截器，我们自己写好的拦截器需要通过这里添加注册才能生效
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalInterceptor)
          // 不拦截图标与静态资源
          .excludePathPatterns("/favicon.ico","/static/**")
          .excludePathPatterns("/error")
          .excludePathPatterns("/doc.html","/swagger-ui.html")
          .excludePathPatterns("/swagger-resources","/service-worker.js")
          // 拦截其他所有的请求
          .addPathPatterns("/**");
    }

    /**
     * 重写url路径匹配(忽略大小写敏感)
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        antPathMatcher.setCaseSensitive(false);
    }

    ///**
    // * 配置对 CORS 请求的处理
    // * @Deprecated use formContentFilter + corsFilter <br>
    // * 当请求到来时会先进入拦截器中，而不是进入Mapping映射中，如果拦截器直接返回，头信息中并没有配置的跨域信息。浏览器就会报跨域异常。
    // * @param registry
    // * @since 4.2
    // */
    //@Override
    //public void addCorsMappings(CorsRegistry registry) {
    //    registry.addMapping("/**")    // 允许跨域访问的路径
    //      .allowedOrigins("*")    // 允许跨域访问的源
    //      .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS","HEAD")    // 允许请求方法
    //      .maxAge(3600)    // 预检间隔时间
    //      .allowedHeaders("*")  // 允许头部设置
    //      .allowCredentials(true);    // 是否发送cookie
    //}
    /**
     * 允许处理 http PUT|DELETE|PATCH methods
     * @return
     */
    @Bean
    public FormContentFilter formContentFilter() {
        return new FormContentFilter();
    }

    /**
     * 跨域过滤器<br>
     *   对所有路径生效，可以删除所有控制器上的@CrossOrigin了
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // JackJson 和 Fastjson 二选一，选择一种 即可
        converters.add(getMappingJackson2HttpMessageConverter());
        //converters.add(getFastJsonHttpMessageConverter());
    }

    /**
     * JackJson 序列化器
     *
     * 使用此方法, 以下 spring-boot: jackson 全局日期格式化 配置 将会失效 详见 JackJsonTool.makeCustomObjectMapper()
     * spring.jackson.time-zone=GMT+8
     * spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
     * 原因: 会覆盖 @EnableAutoConfiguration 关于 WebMvcAutoConfiguration 的配置
     * */
    @Bean
    public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(JackJsonTool.makeCustomObjectMapper());

        //设置中文编码格式
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(mediaTypes);
        return mappingJackson2HttpMessageConverter;
    }

    /**
     * Fastjson 序列化器
     * @return
     */
    /*@Bean
    public FastJsonHttpMessageConverter getFastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

        FastJsonConfig fastJsonConfig = new FastJsonConfig();

        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        FastJsonTool.customConfig(serializeConfig);

        fastJsonConfig.setSerializeConfig(serializeConfig);
        fastJsonConfig.setSerializerFeatures(FastJsonTool.features);
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");// 设置JSON全局日期格式

        fastConverter.setFastJsonConfig(fastJsonConfig);
        fastConverter.setDefaultCharset(Charset.forName("UTF-8"));

        // 解决中文乱码问题，相当于在Controller上的@RequestMapping中加了个属性produces = "application/json"
        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        fastConverter.setSupportedMediaTypes(mediaTypeList);
        return fastConverter;
    }*/

    /**
     * 注意：仅针对 普通字符串（非 JSON ）的 提交
     * spring 接收 请求全局日期转换器，仅针对请求头为 Content-Type 为 application/x-www-form-urlencoded
     */
    @Bean
    public CustomDateConverter customDateConverter() {
        return new CustomDateConverter();
    }

    @Bean
    public ConversionService getConversionService(){
        ConversionServiceFactoryBean factoryBean = new ConversionServiceFactoryBean();
        Set<Converter<String, Date>> converters = new HashSet<>();
        converters.add(customDateConverter());
        // converters.add(customJava8DateConverter()); //  包括 LocalDate LocalDateTime LocalTime 三个对象(java8)
        factoryBean.setConverters(converters);
        return factoryBean.getObject();
    }

    /**
     * 解决文件名中含有":\\"等特殊字符时，接口400的问题
     *      Tomcat在 7.0.73, 8.0.39, 8.5.7 版本后，，添加了对于http头的验证。就是严格按照 RFC 3986规范进行访问解析，而 RFC 3986规范定义了Url中只允许包含英文字母（a-zA-Z）、数字（0-9）、-_.~4个特殊字符
     *      以及所有保留字符(RFC3986中指定了以下字符为保留字符：! * ' ( ) ; : @ & = + $ , / ? # [ ])。
     *
     * <p>
     *  java.lang.IllegalArgumentException:
     *    Invalid character found in the request target. The valid characters are defined in RFC 7230 and RFC 3986.
     * </p>
     * 对于Get请求还可以通过以下方法解决，这种方法不需要配置 relaxedQueryChars，但是较繁琐
     * <p>
     *      var name = "张三:";
     *      name = encodeURIComponent(name);
     *      name = encodeURIComponent(name);//二次编码
     *      //alert(name);
     *      url = url + "?name="+name;
     *      window.location.href = url;
     *
     *     @GetMapping("/test")
     *     public String test(@RequestParam("name") String name) throws UnsupportedEncodingException{
     *         name = URLDecoder.decode(name, "UTF-8");
     *         System.out.println(name);
     *         return "index";
     *     }
     * </p>
     * 总结：最简单的方法是配置服务器（在 application.properties 中添加一行）server.tomcat.relaxed-query-chars=<>[\]^`{|}:
     * @return
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> containerCustomizer(){
        return new WebServerFactoryCustomizer<TomcatServletWebServerFactory>() {
            @Override
            public void customize(TomcatServletWebServerFactory factory) {
                factory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
                    connector.setAttribute("relaxedPathChars", "<>[\\]^`{|}:");
                    connector.setAttribute("relaxedQueryChars", "<>[\\]^`{|}:");
                });
            }
        };
    }

}
