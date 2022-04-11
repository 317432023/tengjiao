package com.tengjiao.part.mybatis.config;

import com.tengjiao.part.mybatis.VersionInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kangtengjiao
 * MyBatis相关配置.<br>
 * <bold>tkMybatis和orgMybatis 不要扫描到 对方定义的配置</bold>
 * <bold>tkMybatis和orgMybatis 不要扫到tk.mybatis.mapper(如定义**.mapper).</bold>
 */
@Configuration
public class MyBatisConfig {
    private final static Logger log = LogManager.getLogger(MyBatisConfig.class);

    /**
     * Mybatis Mapper接口扫描配置.<br>
     *     当类路径下存在 MapperScannerConfigurer 这个 class 并且还未实例化这个对象时，创建 MapperScannerConfigurer 对象
     */
    @Bean("mybatisMapperScannerConfigurer")
    @ConditionalOnClass(MapperScannerConfigurer.class)
    @ConditionalOnMissingBean(MapperScannerConfigurer.class)
    public static MapperScannerConfigurer mapperScannerConfigurer() {
        log.info("自定义MyBatis扫描mapper接口配置类 实例化");
        final String basePackage = "com.**.dao.mybatis";
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        // 设置dao也就是mapper接口的路径，格式如：**.im.**.;com.**.mapper.mybatis
        mapperScannerConfigurer.setBasePackage(basePackage);
        return mapperScannerConfigurer;
    }


    /**
     * 乐观锁拦截器配置<br>
     * 当类路径下存在 Interceptor 这个 class 并且 tengjiao.mybatis-optimistic 等于 havingValue 指定的值时，此拦截器生效<br>
     *     注：在application.properties配置"tengjiao.mybatis-optimistic" <br>
     * 乐观锁version处理插件，如无必要请关闭 以免影响性能!!!
     */
    @Bean
    @ConditionalOnClass(Interceptor.class)
    @ConditionalOnProperty(prefix = "tengjiao", name = "mybatis-optimistic", havingValue = "true")
    public Interceptor versionInterceptor() {
        log.info("MyBatis 乐观锁拦截器已开启，详见 tengjiao.mybatis-optimistic 等于 true");
        return new VersionInterceptor();
    }

    /**
     * 另外一种方式配置插件
     */
    //@Bean
    //public ConfigurationCustomizer mybatisConfigurationCustomizer() {
    //    log.info("MyBatis 乐观锁拦截器已开启，详见 tengjiao.mybatis-optimistic 等于 true");
    //    return new ConfigurationCustomizer() {
    //        @Override
    //        public void customize(org.apache.ibatis.session.Configuration configuration) {
    //            configuration.addInterceptor(new VersionInterceptor());
    //        }
    //    };
    //}
}
