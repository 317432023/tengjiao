package com.tengjiao.part.mybatis;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;

/**
 * 乐观锁：数据版本插件 <br>
 * 注意：开启此插件会有一定的性能损耗<br>
 *     需要在实体类和表结构中都定义version属性和字段才能使用此插件<br>
 *     旧版和新版jsqlparser有差异，见 buildVersionExpression 方法 ：update.getUpdateSets().add(new UpdateSet(versionColumn, add)); // 新版 jsqlparser 4.0 + <br>
 *     尽量避免和mybatisplus 的乐观锁功能同时使用(未验证是否会起冲突)<br>
 *     使用插件<br>
 * 方法一；
 *      直接在此插件上加 @Component 注解
 * 方法二：
 * @Configuration
 * public class MybatisConfig {
 *     @Bean
 *     ConfigurationCustomizer mybatisConfigurationCustomizer() {
 *         return new ConfigurationCustomizer() {
 *             @Override
 *             public void customize(org.apache.ibatis.session.Configuration configuration) {
 *                 configuration.addInterceptor(new MyPageInterceptor());
 *             }
 *         };
 *     }
 * }
 * 方法三：(该方法经过验证可用)
 * application.properties内容：
 * mybatis:
 *   config-location: classpath:mybatis.xml
 *   type-aliases-package: me.zingon.pagehelper.model
 *   mapper-locations: classpath:mapper/*.xml
 *
 * mybatis.xml内容：
 * <?xml version="1.0" encoding="UTF-8" ?>
 * <!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
 *         "http://mybatis.org/dtd/mybatis-3-config.dtd">
 * <configuration>
 *     <typeAliases>
 *         <package name="me.zingon.pacargle.model"/>
 *     </typeAliases>
 *     <plugins>
 *         <plugin interceptor="me.zingon.pagehelper.interceptor.MyPageInterceptor">
 *             <property name="dialect" value="mysql"/>
 *         </plugin>
 *     </plugins>
 * </configuration>
 * @author kangtengjiao
 */
@Intercepts(
    @Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}
    )
)
public class VersionInterceptor implements Interceptor {

    private static final String VERSION_COLUMN_NAME = "version";

    private static final Logger logger = LogManager.getLogger(VersionInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取 StatementHandler，实际是 RoutingStatementHandler
        StatementHandler handler = (StatementHandler) processTarget(invocation.getTarget());
        // 包装原始对象，便于获取和设置属性
        MetaObject metaObject = SystemMetaObject.forObject(handler);
        // MappedStatement 是对SQL更高层次的一个封装，这个对象包含了执行SQL所需的各种配置信息
        MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        // SQL类型
        SqlCommandType sqlType = ms.getSqlCommandType();
        if(sqlType != SqlCommandType.UPDATE) {
            return invocation.proceed();
        }
        // 获取版本号
        Object originalVersion = null;
        try {
            originalVersion = metaObject.getValue("delegate.boundSql.parameterObject." + VERSION_COLUMN_NAME);
        } catch (Exception e) {
            //ignore
        }
        if(originalVersion == null){
            return invocation.proceed();
        }
        // 获取绑定的SQL
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        // 原始SQL
        String originalSql = boundSql.getSql();
        // 加入version的SQL
        originalSql = addVersionToSql(originalSql, originalVersion);
        // 修改 BoundSql
        metaObject.setValue("delegate.boundSql.sql", originalSql);

        // proceed() 可以执行被拦截对象真正的方法，该方法实际上执行了method.invoke(target, args)方法
        return invocation.proceed();
    }

    /**
     * Plugin.wrap 方法会自动判断拦截器的签名和被拦截对象的接口是否匹配，只有匹配的情况下才会使用动态代理拦截目标对象.
     *
     * @param target 被拦截的对象
     * @return 代理对象
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 设置参数
     */
    @Override
    public void setProperties(Properties properties) {
        logger.warn(properties.toString());
    }

    /**
     * 获取代理的原始对象
     *
     * @param target
     * @return
     */
    private static Object processTarget(Object target) {
        if(Proxy.isProxyClass(target.getClass())) {
            MetaObject mo = SystemMetaObject.forObject(target);
            return processTarget(mo.getValue("h.target"));
        }
        return target;
    }

    /**
     * 为原SQL添加version
     *
     * @param originalSql 原SQL
     * @param originalVersion 原版本号
     * @return 加入version的SQL
     */
    private String addVersionToSql(String originalSql, Object originalVersion){
        try{
            Statement stmt = CCJSqlParserUtil.parse(originalSql);
            if(!(stmt instanceof Update)){
                return originalSql;
            }
            Update update = (Update)stmt;
            if(!containsVersion(update)){
                buildVersionExpression(update);
            }
            Expression where = update.getWhere();
            if(where != null){
                AndExpression and = new AndExpression(where, buildVersionEquals(originalVersion));
                update.setWhere(and);
            }else{
                update.setWhere(buildVersionEquals(originalVersion));
            }
            return stmt.toString();
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            return originalSql;
        }
    }

    private boolean containsVersion(Update update){
        List<Column> columns = update.getColumns(); // 旧版 jsqlparser 4.0 以下
        //List<Column> columns = update.getUpdateSets().get(0).getColumns(); // 新版 jsqlparser 4.0 +
        for(Column column : columns){
            if(column.getColumnName().equalsIgnoreCase(VERSION_COLUMN_NAME)){
                return true;
            }
        }
        return false;
    }

    /**
     * 修改update set语句，往里面加入 version = version + 1
     * @param update
     */
    private void buildVersionExpression(Update update){
        // 列 version
        Column versionColumn = new Column();
        versionColumn.setColumnName(VERSION_COLUMN_NAME);

        // 值 version+1
        Addition add = new Addition();
        add.setLeftExpression(versionColumn);
        add.setRightExpression(new LongValue(1));

        update.getColumns().add(versionColumn); // 旧版 jsqlparser 4.0 以下
        update.getExpressions().add(add); // 旧版 jsqlparser 4.0 以下

        //update.getUpdateSets().add(new UpdateSet(versionColumn, add)); // 新版 jsqlparser 4.0 + cai
    }

    /**
     * 构建(相等)条件表达式
     * @param originalVersion
     */
    private Expression buildVersionEquals(Object originalVersion){
        Column column = new Column();
        column.setColumnName(VERSION_COLUMN_NAME);

        // 条件 version = originalVersion
        EqualsTo equal = new EqualsTo();
        equal.setLeftExpression(column);
        equal.setRightExpression(new LongValue(originalVersion.toString()));
        return equal;
    }

}
