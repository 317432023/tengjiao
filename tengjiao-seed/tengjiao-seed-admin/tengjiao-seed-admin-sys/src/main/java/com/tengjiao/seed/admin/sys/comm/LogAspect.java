package com.tengjiao.seed.admin.sys.comm;

import com.tengjiao.seed.admin.security.SecurityUtil;
import com.tengjiao.tool.third.ip.HutoolIpTool;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.tengjiao.seed.admin.service.sys.LogService;
import com.tengjiao.seed.admin.model.sys.entity.Log;

import javax.servlet.http.HttpServletRequest;

/**
 * @author kangtengjiao
 */
@Slf4j
@Aspect
@Order(Integer.MAX_VALUE)
@Component
public class LogAspect {
  private LogService logService;
  private HttpServletRequest request;

  public LogAspect(LogService logService, HttpServletRequest request) {
    this.logService = logService;
    this.request = request;
  }


  /**
   * 切点
   */
  @Pointcut("@annotation(com.tengjiao.seed.admin.sys.comm.LogRequired)")
  public void pointcut() {
  }

  @Around("pointcut()")
  public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    Log log = new Log();
    long currentTime = System.currentTimeMillis();
    Object res = proceedingJoinPoint.proceed();
    log.setSpendTime((int) (System.currentTimeMillis() - currentTime));
    log.setIp(HutoolIpTool.getClientIpAddress(request));
    log.setUsername(SecurityUtil.getCurrentUser().getUsername());
    MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
    String opName = signature.getMethod().getAnnotation(LogRequired.class).value();
    log.setOpName(opName);
    // 注意：该方法没有事务标注支持
    logService.save(log);
    return res;
  }
}