package com.tengjiao.seed.member.security;

import com.tengjiao.tool.indep.model.SystemContext;
import com.tengjiao.tool.indep.model.SystemException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

import static com.tengjiao.seed.member.security.ServiceRestrictIntercept.getAnnotation;
import static com.tengjiao.seed.member.security.ServiceRestrictIntercept.getRequest;

/**
 * @author tengjiao
 * @description
 * @date 2021/8/27 17:10
 */
@Component
@Aspect
@Order(1)
public class RegisteredIntercept {
    private OnlineStorage onlineStorage;

    public RegisteredIntercept(OnlineStorage onlineStorage) {
        this.onlineStorage = onlineStorage;
    }

    /**
     * 切点拦截controller下的所有方法
     */
    @Pointcut("execution(public * com..*.controller..*.*(..))")
    public void registered() {}

    @After("registered()")
    public void afterInform() {

        SystemContext.remove();
    }

    /**
     * 切面拦截器具体实现
     *
     * @param
     * @return
     */
    @Around("registered()") //指定拦截器规则；
    @ResponseBody
    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Class<?> targetClass = pjp.getTarget().getClass();
        Method method = signature.getMethod();

        Registered registered = getAnnotation(targetClass, method, Registered.class);

        if(registered == null || !registered.registeredRequired()) {
            return pjp.proceed();
        }

        OnlineInfo onlineInfo = onlineStorage.get(true);


        ServiceRestrict serviceRestrict = getAnnotation(targetClass, method, ServiceRestrict.class);
        if(serviceRestrict == null) {
            SystemContext.set(
              SessionUtils.getSession().setLoginStore(onlineInfo)
            );
        }

        if( onlineInfo.getMemberId() == 0L ) {
            throw SystemException.create("用户未注册为会员");
        }

        return pjp.proceed();
    }

}
