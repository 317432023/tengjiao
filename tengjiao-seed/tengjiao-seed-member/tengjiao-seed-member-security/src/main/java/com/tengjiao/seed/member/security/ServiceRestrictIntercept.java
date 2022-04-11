package com.tengjiao.seed.member.security;

import com.alibaba.fastjson.JSON;
import com.tengjiao.part.redis.ModeDict;
import com.tengjiao.part.redis.RedisTool;
import com.tengjiao.tool.indep.RegexTool;
import com.tengjiao.tool.indep.model.RC;
import com.tengjiao.tool.indep.model.SystemContext;
import com.tengjiao.tool.indep.model.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static com.tengjiao.tool.indep.RegexTool.Patterns.INTEGER;
import static com.tengjiao.tool.indep.RegexTool.Patterns.POSITIVE_NUMBER;
import static com.tengjiao.tool.indep.web.body.HttpBodyTool.getBodyString;

/**
 * @author tengjiao
 * @description
 * @date 2021/8/22 22:38
 */
@Component
@Aspect
@Slf4j
@Order(0)
public class ServiceRestrictIntercept {

    private final RedisTool redisTool;

    public ServiceRestrictIntercept(RedisTool redisTool) {
        this.redisTool = redisTool;
    }

    @Value("${tengjiao.member-security.paramSignRequired:#{null}}")
    private Boolean paramSignRequired;

    /**
     * 切点拦截controller下的所有方法
     */
    @Pointcut("execution(public * com..*.controller..*.*(..))")
    public void serviceRestrict() {}

    @After("serviceRestrict()")
    public void afterInform() {
        SystemContext.remove();
    }

    private static boolean checkTime(long time, long variable){
        long currentTimeMillis = System.currentTimeMillis();
        long addTime = currentTimeMillis + variable;
        long subTime = currentTimeMillis - variable;
        if (addTime > time && time > subTime){
            return true;
        }
        return false;
    }

    static <T extends Annotation> T getAnnotation(Class<?> targetClass, Method method, Class<T> clazz) {

        return method.isAnnotationPresent(clazz)?method.getAnnotation(clazz):targetClass.isAnnotationPresent(clazz)?targetClass.getAnnotation(clazz):null;
    }

    static HttpServletRequest getRequest() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        return sra.getRequest();
    }

    private static List<String> getParamValues(HttpServletRequest request) {
        List<String> list = new ArrayList<>();
        Enumeration<String> paraNames = request.getParameterNames();

        Map<String, String> paramMap = new TreeMap<>();
        for (Enumeration<String> e = paraNames; e.hasMoreElements(); ) {
            String thisName = e.nextElement();
            String thisValue = request.getParameter(thisName);

            if (thisValue != null && !"".equals(thisValue.trim())) {
                paramMap.put(thisName, thisValue.trim());
            }
        }

        for(Map.Entry<String, String> e : paramMap.entrySet()) {
            list.add(e.getValue());
        }

        return list;
    }

    /**
     * 会话续约
     * @param fullTokenKey
     * @param terminalType
     */
    private void renewSession(String fullTokenKey, TerminalType terminalType) {
        if( terminalType.strategy == OnlineRenewStrategy.RENEW_EVERY_REQUEST_BEFORE_EXPIRE ) {
            redisTool.expire( fullTokenKey, ModeDict.APP_GROUP, terminalType.duration );
        }
    }

    /**
     * 切面拦截器具体实现
     *
     * @param
     * @return
     */
    @Around("serviceRestrict()") //指定拦截器规则；
    @ResponseBody
    public Object Intercept(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Class<?> targetClass = pjp.getTarget().getClass();
        Method method = signature.getMethod();

        ServiceRestrict serviceRestrict = getAnnotation(targetClass, method, ServiceRestrict.class);

        if(serviceRestrict == null) {
            return pjp.proceed();
        }

        HttpServletRequest request = getRequest();

        // 线程会话变量
        SessionModel model = (SessionModel)SystemContext.get(SessionModel.class);

        // 提取终端类型
        String terminal = request.getHeader(Constants.ServiceHeaders.TERMINAL_KEY);
        int typeCode = 0;
        if( !StringUtils.isEmpty(terminal) ) {
            if (!RegexTool.is(terminal, INTEGER)) {
                throw SystemException.create("illegal param of : " + Constants.ServiceHeaders.TERMINAL_KEY)
                  .setCode(RC.PARAM_ERR.code);
            }
            typeCode = Integer.parseInt(terminal);

            // 终端类型放进 会话
            model.setTerminal(typeCode);
        }
        TerminalType terminalType = TerminalType.getByTypeCode(typeCode);
        if( ! Arrays.asList( serviceRestrict.supportTerminalTypes() ).contains( terminalType ) ) {
            throw SystemException.create("unsupported terminal : " + terminal)
              .setCode(RC.PARAM_ERR.code);
        }

        // 提取设备类型
        String device = request.getHeader(Constants.ServiceHeaders.DEVICE_KEY);
        typeCode = 0;
        if( !StringUtils.isEmpty(device) ) {
            if (!RegexTool.is(device, INTEGER)) {
                throw SystemException.create("illegal param of : " + Constants.ServiceHeaders.DEVICE_KEY)
                  .setCode(RC.PARAM_ERR.code);
            }
            typeCode = Integer.parseInt(device);

            // 设备类型放进 会话
            model.setDevice(typeCode);
        }
        DeviceType deviceType = DeviceType.getByTypeCode(typeCode);
        if( deviceType == null ) {
            throw SystemException.create("unsupported device : " + device)
              .setCode(RC.PARAM_ERR.code);
        }

        // 提取令牌
        String clientToken = request.getHeader(Constants.ServiceHeaders.TOKEN_KEY);

        // 是否需要登录
        boolean needLogin = serviceRestrict.loginRequired();

        // 登录后 redis 存储的登录信息（对象有值，代表已登录）
        OnlineInfo loginStore = null;

        if(  ! StringUtils.isEmpty(clientToken) ) {

            final String storeToken = Constants.ServiceHeaders.TOKEN_KEY + ":" + clientToken;
            String storeString = redisTool.getString(storeToken, ModeDict.APP_GROUP);
            if( ! StringUtils.isEmpty(storeString) ) {
                loginStore = JSON.parseObject(storeString, OnlineInfo.class);

                // 把登录信息放到 会话对象中
                model.setLoginStore(loginStore);

                // 会话续约 getByTypeCode()
                renewSession( storeToken, terminalType );

            }

            // 令牌放进会话
            model.setToken(clientToken);
        }

        if( needLogin && loginStore == null ) {
            throw SystemException.create("not authenticated")
              .setCode(RC.NOT_AUTHENTICATED);
        }

        // 客户端版本号
        String ver_id = request.getHeader(Constants.ServiceHeaders.VERSION_KEY);
        if( !StringUtils.isEmpty(ver_id) ) {
            model.setVersion(ver_id);
        }

        boolean needSign = paramSignRequired == null ? serviceRestrict.paramSignRequired() : paramSignRequired;
        if( needSign ) {
            String headerSignature = request.getHeader(Constants.ServiceHeaders.SIGNATURE_KEY);
            if( StringUtils.isEmpty(headerSignature) ) {
                throw SystemException.create("signature required.")
                  .setCode(RC.PARAM_ERR.code);
            }

            // 访问时间限制
            String ts_str = request.getHeader(Constants.ServiceHeaders.TIMESTAMP_KEY);
            if(ts_str == null || !RegexTool.is(ts_str, POSITIVE_NUMBER)) {
                throw SystemException.create("illegal param")
                  .setCode(RC.PARAM_ERR);
            }

            long timestamp = Long.parseLong(ts_str);
            long variable = serviceRestrict.expires();

            if(variable > 0 && !checkTime(timestamp, variable * 1000L)) {
                throw SystemException.create("access timeout");
            }

            StringBuilder urlSign = new StringBuilder(256);

            // 提取 body 内容
            // body: !important: 注意需要配合 HttpServletRequestWrapperFilter 使用
            String contentType = request.getHeader("Content-Type"), methodType = request.getMethod();
            if ( ("POST".equals(methodType) || "PUT".equals(methodType) || "PATCH".equals(methodType))
              && contentType.startsWith("application/json")) {

                String body = getBodyString(request);
                urlSign.append(body);
            }

            // 提取参数值
            List<String> values = getParamValues(request);
            for(String value:values) {
                urlSign.append(value);
            }

            // 头信息签名 [令牌, 站点, 来源终端, 设备类型, 版本号]放进 线程会话

            if( needLogin && ! StringUtils.isEmpty(clientToken) ) {
                urlSign.append(clientToken);
            }
            if(serviceRestrict.useMultiStation()){
                String saas_id = request.getHeader(Constants.ServiceHeaders.SAAS_KEY);
                if( ! StringUtils.isEmpty(saas_id) ) {
                    urlSign.append(saas_id);
                    model.setStationId(saas_id);
                }
            }

            urlSign.append(terminal);

            if(serviceRestrict.useDevice()  && ! StringUtils.isEmpty(device) ){
                urlSign.append(device);
            }
            if(serviceRestrict.useVersion() && ! StringUtils.isEmpty(ver_id)){
                urlSign.append(ver_id);
            }

            // 提取盐
            String theSalt = OnlineInfo.DEFAULT_SALT;
            if( needLogin && loginStore !=null ) {

                String _salt = loginStore.getSalt();
                if ( ! StringUtils.isEmpty(_salt) ) {
                    theSalt = _salt;
                }
            }

            // signature 注意：不需要登录的接口不要传送 token
            // MD5 (body内容（POST|PUT） + 参数值(自然排序) + 可变消息头值[ [令牌], [站点], 来源终端, 设备类型, 版本号 ] + 时间戳消息头值 + 盐 )
            String urlSignToString = urlSign.append(timestamp).append(theSalt).toString();

            if(log.isDebugEnabled()) {
                log.debug("===urlSignToString===: "+urlSignToString);
            }

            String sign = DigestUtils.md5DigestAsHex(urlSignToString.getBytes("UTF-8"));

            if ( !sign.equals(headerSignature) ) {
                throw SystemException.create("signature error.")
                  .setCode(RC.PARAM_ERR);
            }

        }

        // 将本次计算得到的sessionModel放进线程上下文中，供下一个切面或最终执行的方法使用
        SystemContext.set(model);

        return pjp.proceed();
    }

}
