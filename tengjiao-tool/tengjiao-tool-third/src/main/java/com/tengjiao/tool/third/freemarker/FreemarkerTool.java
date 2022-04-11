package com.tengjiao.tool.third.freemarker;

import freemarker.template.*;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Freemarker工具类 <br>
 * @author kangtengjiao
 */
public class FreemarkerTool {
    private static final Map<String, Configuration> configMap = new ConcurrentHashMap<String, Configuration>();

    /**
     * 主调方法：根据模板与数据构建最终文本
     *
     * @param contextPath eg. where/you/store/templates
     * @param fileName 模板文件eg. test.ftl
     * @param rootMap 填充参数值
     * @return
     */
    public static String build(String contextPath, String fileName, Map<String, ?> rootMap) {

        /* 在整个生命周期，这个工作本该只做一次，但是做了同步化操作可以做多次 */
        // 创建和调整配置
        Configuration cfg = getConfiguration(contextPath);

        /* 在整个生命周期，这个工作可以执行多次 */
        // 获取或创建模版
        Template tpl = null;
        Writer out = new StringWriter();
        try {
            tpl = cfg.getTemplate(fileName);

            // 将模板和数据模型合并
            tpl.process(rootMap, out);
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out.toString();
    }

    /**
     * 获得Configuration对象<br>
     * 为不同的模板目录取得一个不重复的Configuration对象，相同的模板目录的Configuration对象得到重复利用
     *
     * @param contextPath eg. where/you/store/templates
     *
     * @return
     *
     */
    private static Configuration getConfiguration(String contextPath) {

        Set<String> keySet = configMap.keySet();

        synchronized (configMap) {
            Iterator<String> i = keySet.iterator();
            while (i.hasNext()) {
                String key = i.next();
                if (key.equals(contextPath)) {
                    return configMap.get(key);
                }
            }

            // 1.创建和调整配置
            Configuration cfg = new Configuration(Configuration.getVersion());

            // this scriptlet below was used in relative classpath
            // cfg.setTemplateLoader( new ClassTemplateLoader( Config.class, "templates" ) );

            // this scriptlet below was used in servlet. // - Templates are stoted in the WEB-INF/templates directory of the Web app.
            // cfg.setServletContextForTemplateLoading( getServletContext(), "WEB-INF/templates" );

            // classpath文件目录中找不到，则从jar中获取
            boolean found = true;
            try {
                final String fileDir = FreemarkerTool.class.getClassLoader().getResource("").getPath();
                cfg.setDirectoryForTemplateLoading(new File(fileDir + '/' + contextPath));
            } catch (IOException e) {
                found = false;
            }
            if(!found){
                cfg.setClassForTemplateLoading(FreemarkerTool.class, "/" + contextPath);
            }

            cfg.setDefaultEncoding("utf-8");

            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
            DefaultObjectWrapper defaultObjectWrapper = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_28).build();
            cfg.setObjectWrapper(defaultObjectWrapper);

            configMap.put(contextPath, cfg);

            return cfg;
        }// Failure to follow this advice may result in non-deterministic behavior.

    }

}