package com.tengjiao.seed.admin.security.handler;

import com.alibaba.fastjson.JSON;
import com.tengjiao.tool.indep.model.R;
import com.tengjiao.tool.indep.web.PrintTool;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName MyLogoutSuccessHandler
 * @Description TODO
 * @Author Administrator
 * @Date 2020/11/19 15:51
 * @Version V1.0
 */
@Component
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

  @Override
  public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
    PrintTool.printJson(httpServletResponse, JSON.toJSONString(new R().setMessage("注销成功。")));
  }
}
