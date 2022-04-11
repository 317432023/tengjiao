package com.tengjiao.seed.admin.security;

import com.alibaba.fastjson.JSON;
import com.tengjiao.tool.indep.model.R;
import com.tengjiao.tool.indep.model.RC;
import com.tengjiao.tool.indep.web.PrintTool;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用来处理 匿名用户 访问无权限资源时的异常
 * @author Administrator
 */
@Component
public class LoginAuthenticationEntryPoint implements AuthenticationEntryPoint {
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
    R result = "403".equals(authException.getMessage())?
      RC.PERMISSION_DENIED.toR().setMessage("权限不足(403 permission denied)"):
      RC.NOT_AUTHENTICATED.toR().setMessage("匿名用户无此权限(401 not Authenticated)")
      ;
    response.setStatus(result.getCode());
    PrintTool.printJson(response, JSON.toJSONString(result));
  }
}