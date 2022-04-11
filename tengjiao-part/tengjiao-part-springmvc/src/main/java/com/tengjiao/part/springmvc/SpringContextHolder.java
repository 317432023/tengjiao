package com.tengjiao.part.springmvc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 获取Spring管理的Bean，此bean引入 springmvc context 中管理
 * @author kangtengjiao
 */
@Component
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {
    private static Logger logger = LogManager.getLogger(SpringContextHolder.class);

    /** 当前的Spring上下文 */
    private static ApplicationContext applicationContext;

    /**
     * 检查ApplicationContext不为空.
     */
    private static void assertContextInjected() {
        if (applicationContext == null) {
            throw new IllegalStateException("applicaitonContext属性未注入, 请在applicationContext" +
                    ".xml中定义SpringContextHolder或在SpringBoot启动类中注册SpringContextHolder.");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext arg0)
            throws BeansException {
        applicationContext = arg0;
    }
    @Override
    public void destroy(){
        logger.debug("清除SpringContextHolder中的ApplicationContext:"
                + applicationContext);
        applicationContext = null;
    }

    public static ApplicationContext getContext() {
        return applicationContext;
    }

    /**
     * 通过BeanId获取Spring管理的对象
     * @param beanName bean Id
     * @return
     */
    public static Object getObject(String beanName) {
        assertContextInjected();
        return applicationContext.getBean(beanName);
    }

    /**
     * 通过类获取Spring管理的对象
     * @param clazz 要获取的Bean类
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        assertContextInjected();
        return applicationContext.getBean(clazz);
    }
    public static <T> T getBean(String beanName, Class<T> clazz) {
        assertContextInjected();
        return applicationContext.getBean(beanName, clazz);
    }

    public static String getProperty(String key) {
        //return applicationContext.getEnvironment().getProperty(key);
        return applicationContext.getBean(Environment.class).getProperty(key);
    }

    /**
     * 取得activeProfile
     * @return
     */
    public static String getActiveProfile() {
        return applicationContext.getEnvironment().getActiveProfiles()[0];
    }

}