package com.tengjiao.seed.admin.sys.listener;

import com.tengjiao.part.redis.ModeDict;
import com.tengjiao.part.redis.RedisTool;
import com.tengjiao.seed.admin.comm.Constants;
import com.tengjiao.seed.admin.model.sys.entity.Config;
import com.tengjiao.seed.admin.service.sys.ConfigService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 钩子函数类，用于系统启动时初始化
 * @author tengjiao
 * @date 2021/8/5 22:39
 * <bean class="com.xx.web.listener.StartupListener"/>
 * 1 => StartupListener.setApplicationContext
 * 2 => StartupListener.setServletContext
 * 3 => StartupListener.afterPropertiesSet
 * 4.1 => StartupListener.onApplicationEvent
 */
@Component
@AllArgsConstructor
@Slf4j
public class StartupListener implements ApplicationContextAware, ServletContextAware,
        InitializingBean, ApplicationListener<ContextRefreshedEvent> {

    private final RedisTool redisTool;
    private final ConfigService configService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
    @Override
    public void setServletContext(ServletContext servletContext) {

    }
    @Override
    public void afterPropertiesSet() throws Exception {

    }
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        List<Config> list = configService.list();
        int size = list.size();
        Map<String, String> configMap = new HashMap<>();
        list.forEach(e->configMap.put(e.getName(), e.getValue()));
        redisTool.hmset(Constants.SYS_CONFIG_KEY, configMap, ModeDict.APP_GROUP);
        log.info("已加载系统配置项[" + size + "]个到Redis缓存");
    }

}
