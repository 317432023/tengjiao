package com.tengjiao.part.mybatisplus.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;

@Slf4j
@Configuration
public class MyBatisPlusConfig {

    /**
     * 新版本（3.4.0以后）新的方式添加拦截器添加插件，一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     * 分页插件、乐观锁插件...
     */
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setUseDeprecatedExecutor(false);
    }
    /**
     * 分页拦截器配置<br>
     * 当类路径下存在 Interceptor 这个 class 并且 tengjiao.mybatisplus-pagination 等于 havingValue 指定的值时，此拦截器生效<br>
     *     注：在application.properties配置"tengjiao.mybatisplus-pagination" <br>
     * 如无必要请关闭 以免影响性能!!!
     */
    @ConditionalOnClass(Interceptor.class)
    @ConditionalOnProperty(prefix = "tengjiao", name = "mybatisplus-pagination", havingValue = "true")
    @Bean public PaginationInnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
        paginationInnerInterceptor.setOverflow(false);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInnerInterceptor.setMaxLimit(100L);
        return paginationInnerInterceptor;
    }
    /**
     * 乐观锁拦截器配置<br>
     * 当类路径下存在 Interceptor 这个 class 并且 tengjiao.mybatisplus-optimistic 等于 havingValue 指定的值时，此拦截器生效<br>
     *     注：在application.properties配置"tengjiao.mybatisplus-optimistic" <br>
     * 如无必要请关闭 以免影响性能!!!
     */
    @ConditionalOnClass(Interceptor.class)
    @ConditionalOnProperty(prefix = "tengjiao", name = "mybatisplus-optimistic", havingValue = "true")
    @Bean public OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        try {
            // 添加新的 分页插件
            interceptor.addInnerInterceptor(paginationInnerInterceptor());
        } catch (NoSuchBeanDefinitionException e) {
            log.warn("未开启 MyBatisPlus 分页拦截插件");
        }
        try {
            // 添加新的 乐观锁插件
            interceptor.addInnerInterceptor(optimisticLockerInnerInterceptor());
        } catch (NoSuchBeanDefinitionException e) {
            log.warn("未开启 MyBatisPlus 乐观锁拦截插件");
        }
        return interceptor;
    }

    /**
     * 旧的分页拦截器
     * @deprecated
     * @return
     */
    /*@Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
        // paginationInterceptor.setOverflow(false);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        // paginationInterceptor.setLimit(100);
        // 开启 count 的 join 优化,只针对部分 left join
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
        return paginationInterceptor;
    }*/

    /**
     * 旧的乐观锁拦截器
     * @deprecated
     * @return
     */
    /*
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }*/

}
