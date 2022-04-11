package com.tengjiao.part.sample.ssm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tengjiao.part.springmvc.CustomDateConverter;
import com.tengjiao.part.springmvc.CustomDateFormat;
import com.tengjiao.part.springmvc.GlobalInterceptor;
import com.tengjiao.tool.core.StringTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.annotation.Order;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author kangtengjiao
 */
@PropertySource(value = {"file:${config.path}"}, encoding="utf-8")
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private GlobalInterceptor globalInterceptor;

    /**
     * 装配/注入方式：构造器装配/注入
     * @param globalInterceptor
     */
    public WebConfig(GlobalInterceptor globalInterceptor) {
        this.globalInterceptor = globalInterceptor;
    }

    private static String staticCustomLocations;

    @Value("${staticCustomLocations}")
    public void setStaticCustomLocations(String customLocations) {
        staticCustomLocations = customLocations;
    }

    /**
     * 设置全局UTF-8编码过滤器
     * @return
     */
    @Bean
    @Order(Integer.MAX_VALUE)
    public FilterRegistrationBean<CharacterEncodingFilter> characterFilterRegistration() {
        FilterRegistrationBean<CharacterEncodingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CharacterEncodingFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("paramName", "paramValue");
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
        if (!StringTool.isBlank(staticCustomLocations)) {
            String[] locations = staticCustomLocations.split(",");
            registry.addResourceHandler("/**").addResourceLocations(locations);
        }
    }

    /**
     * 注册拦截器，我们自己写好的拦截器需要通过这里添加注册才能生效
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalInterceptor)
          // 表示拦截所有的请求
          .addPathPatterns("/**")
          // 不拦截图标与静态资源
          .excludePathPatterns("/favicon.ico","/static/**")
          .excludePathPatterns("/error")
          .excludePathPatterns("/doc.html","/swagger-ui.html")
          .excludePathPatterns("/swagger-resources","/service-worker.js");
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
        converters.add(getMappingJackson2HttpMessageConverter());
    }

    /**
     * JSON全局日期转换器
     */
    @Bean
    public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        //设置日期格式
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDateFormat(CustomDateFormat.instance);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);

        //设置中文编码格式
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(mediaTypes);
        return mappingJackson2HttpMessageConverter;
    }

    /**
     * 表单全局日期转换器
     */
    @Bean
    public CustomDateConverter customDateConverter() {
        return new CustomDateConverter();
    }

    @Bean
    public ConversionService getConversionService(@Autowired CustomDateConverter customDateConverter){
        ConversionServiceFactoryBean factoryBean = new ConversionServiceFactoryBean();
        Set<Converter> converters = new HashSet<>();
        converters.add(customDateConverter);
        factoryBean.setConverters(converters);
        return factoryBean.getObject();
    }

}