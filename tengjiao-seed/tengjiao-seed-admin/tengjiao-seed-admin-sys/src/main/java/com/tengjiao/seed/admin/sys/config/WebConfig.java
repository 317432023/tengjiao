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
     */
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
     * 表单全局日期转换器，仅针对请求头为 Content-Type 为 application/x-www-form-urlencoded；
     * 注：若要对请求头 Content-Type 为 application/json 类型的，有两种方法：
     * 1\使用 spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
     * 2\或者直接在请求体中标注 如 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
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
        factoryBean.setConverters(converters);
        return factoryBean.getObject();
    }

}