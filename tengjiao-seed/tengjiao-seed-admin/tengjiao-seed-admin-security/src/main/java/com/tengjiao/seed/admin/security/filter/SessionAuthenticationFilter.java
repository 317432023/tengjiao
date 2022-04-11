package com.tengjiao.seed.admin.security.filter;

import com.tengjiao.seed.admin.security.SecurityUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 会话认证检查过滤器
 *
 * @author Administrator
 */
public class SessionAuthenticationFilter extends BasicAuthenticationFilter {

  public SessionAuthenticationFilter(AuthenticationManager authenticationManager) {
    super(authenticationManager);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
    // 未登录
    if(SecurityContextHolder.getContext().getAuthentication() == null) {
      SecurityContextHolder.getContext().setAuthentication(SecurityUtil.anonymousAuthenticationToken); // 设置匿名用户
    }
    chain.doFilter(request, response);
  }

}