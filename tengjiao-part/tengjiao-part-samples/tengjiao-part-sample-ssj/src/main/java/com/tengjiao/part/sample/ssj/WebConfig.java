package com.tengjiao.part.sample.ssj;

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
     * ??????/??????????????????????????????/??????
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
     * ????????????UTF-8???????????????
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
     * springmvc?????????????????????????????????
     * <b>????????????????????? springBoot ??? application*.yml
     *  ?????????spring.mvc.static-path-pattern???
     *      spring.resources.static-locations
     * ??????????????????????????????????????????????????????????????????????????????????????????
     * </b>
     * <p>?????? swagger ??? knife4j</p>
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
     * ??????????????????????????????????????????????????????????????????????????????????????????
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalInterceptor)
          // ???????????????????????????
          .addPathPatterns("/**")
          // ??????????????????????????????
          .excludePathPatterns("/favicon.ico","/static/**")
          .excludePathPatterns("/error")
          .excludePathPatterns("/doc.html","/swagger-ui.html")
          .excludePathPatterns("/swagger-resources","/service-worker.js");
    }

    /**
     * ??????url????????????(?????????????????????)
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        antPathMatcher.setCaseSensitive(false);
    }

    ///**
    // * ????????? CORS ???????????????
    // * @Deprecated use formContentFilter + corsFilter <br>
    // * ????????????????????????????????????????????????????????????Mapping????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    // * @param registry
    // * @since 4.2
    // */
    //@Override
    //public void addCorsMappings(CorsRegistry registry) {
    //    registry.addMapping("/**")    // ???????????????????????????
    //      .allowedOrigins("*")    // ????????????????????????
    //      .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS","HEAD")    // ??????????????????
    //      .maxAge(3600)    // ??????????????????
    //      .allowedHeaders("*")  // ??????????????????
    //      .allowCredentials(true);    // ????????????cookie
    //}
    /**
     * ???????????? http PUT|DELETE|PATCH methods
     * @return
     */
    @Bean
    public FormContentFilter formContentFilter() {
        return new FormContentFilter();
    }

    /**
     * ???????????????<br>
     *   ?????????????????????????????????????????????????????????@CrossOrigin???
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
     * JSON?????????????????????
     */
    @Bean
    public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        //??????????????????
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDateFormat(CustomDateFormat.instance);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);

        //????????????????????????
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(mediaTypes);
        return mappingJackson2HttpMessageConverter;
    }

    /**
     * ???????????????????????????
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