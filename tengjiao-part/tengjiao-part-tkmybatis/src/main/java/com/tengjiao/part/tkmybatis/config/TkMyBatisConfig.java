package com.tengjiao.part.tkmybatis.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kangtengjiao
 * TkMyBatis相关配置.<br>
 * <bold>tkMybatis和orgMybatis 不要扫描到 对方定义的配置</bold>
 * <bold>tkMybatis和orgMybatis 不要扫到tk.mybatis.myMapper(如定义**.myMapper).</bold>
 */
@Configuration
public class TkMyBatisConfig {
    private final static Logger log = LogManager.getLogger(TkMyBatisConfig.class);
    /**
     * tkMybatis Mapper扫描配置.
     */
    @Bean("tkMybatisMapperScannerConfigurer")
    @ConditionalOnClass(tk.mybatis.spring.mapper.MapperScannerConfigurer.class)
    @ConditionalOnMissingBean(tk.mybatis.spring.mapper.MapperScannerConfigurer.class)
    public static tk.mybatis.spring.mapper.MapperScannerConfigurer tkMybatisMapperScannerConfigurer() {
        log.info("自定义Tk.MyBatis扫描mapper接口配置类 实例化");
        final String basePackage = "com.**.dao.tkmybatis";
        tk.mybatis.spring.mapper.MapperScannerConfigurer mapperScannerConfigurer = new tk.mybatis.spring.mapper.MapperScannerConfigurer();
        // 设置dao也就是mapper接口的路径，格式如：**.im.**.;com.**.myMapper.mybatis
        mapperScannerConfigurer.setBasePackage(basePackage);
        return mapperScannerConfigurer;
    }
}
