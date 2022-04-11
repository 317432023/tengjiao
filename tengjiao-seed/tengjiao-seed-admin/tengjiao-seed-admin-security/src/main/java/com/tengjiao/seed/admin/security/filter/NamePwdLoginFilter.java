package com.tengjiao.seed.admin.security.filter;

import com.alibaba.fastjson.JSON;
import com.tengjiao.part.redis.RedisTool;
import com.tengjiao.seed.admin.comm.Constants;
import com.tengjiao.seed.admin.security.SecurityUtil;
import com.tengjiao.seed.admin.security.domain.AuthenticationToken;
import com.tengjiao.tool.indep.model.R;
import com.tengjiao.tool.indep.model.RC;
import com.tengjiao.tool.indep.web.PrintTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 登录前后操作
 * @author Administrator
 * @version 2020/11/18 20:21
 */
@Slf4j
public class NamePwdLoginFilter extends UsernamePasswordAuthenticationFilter {
  private final Constants.SecurityStrategyType securityStrategyType;
  private final RedisTool redisTool;

  /** 密码策略_是否加密密码 */
  private final Boolean encryptPassword;
  /** 密码策略_用于解密的私钥 */
  private final String privateKey;

  /** 登录持续周期 */
  private final Long duration;

  public NamePwdLoginFilter(AuthenticationManager authManager,
                            Constants.SecurityStrategyType securityStrategyType,
                            RedisTool redisTool,
                            String privateKey,
                            Boolean encryptPassword,
                            Long duration) {

    this.securityStrategyType = securityStrategyType;
    this.redisTool = redisTool;
    this.privateKey = privateKey;
    this.encryptPassword = encryptPassword;
    this.duration = duration;

    // 父类的无参构造器已经设置了需要过滤登录路径为/login，并且请求方法为POST 的 一个 AntPathRequestMatcher 实例
    setAuthenticationManager(authManager);

    // 认证失败的处理
    this.setAuthenticationFailureHandler((req, resp, e) -> {
      if (e instanceof BadCredentialsException) {
        PrintTool.printJson(resp, JSON.toJSONString(RC.USER_CREDENTIALS_ERROR.toR().setMessage("用户名或密码错误")));
      } else if (e instanceof DisabledException) {
        PrintTool.printJson(resp, JSON.toJSONString(RC.USER_ACCOUNT_DISABLE.toR().setMessage("账户被禁用，请联系管理员")));
      } else if (e instanceof AuthenticationServiceException) {
        PrintTool.printJson(resp, JSON.toJSONString(RC.FAILURE.toR().setMessage("登录失败，原因：" + e.getMessage())));
      }
    });
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
    throws IOException, ServletException {
    // POST 请求 /login 登录时拦截， 由此方法触发执行登录认证流程，可以在此覆写整个登录认证逻辑
    super.doFilter(req, res, chain);
  }

  /**
   * 取得用户名密码进行登录认证
   * <p>
   * 可以在此覆写尝试进行登录认证的逻辑，登录成功之后等操作不再此方法内
   * 如果使用此过滤器来触发登录认证流程，注意登录请求数据格式的问题
   * 此过滤器的用户名密码默认从request.getParameter()获取，但是这种
   * 读取方式不能读取到如 application/json 等 post 请求数据，需要
   * 用户名密码的读取逻辑修改为到流中读取request.getInputStream()
   * </p>
   * @param request
   * @param response
   * @return
   * @throws AuthenticationException
   */
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

    AuthenticationToken authToken = SecurityUtil.getAuthentication(request, response, redisTool, encryptPassword?privateKey:"");

    // Allow subclasses to set the "details" property
    setDetails(request, authToken);

    // 该方法会调用 loadUserByUsername 根据用户名查出用户进行密码比对 （可选：如果是单体应用的话，loadUserByUsername 应该同时将拥有的 权限列表 放进 User implements UserDetails 对象 中）
    return this.getAuthenticationManager().authenticate(authToken);

  }

  /**
   * 登录成功后响应
   * @param request
   * @param response
   * @param chain
   * @param authentication
   * @throws IOException
   * @throws ServletException
   */
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                          Authentication authentication) {
    // 记住我服务
    getRememberMeServices().loginSuccess(request, response, authentication);

    // 触发事件监听器
    if (this.eventPublisher != null) {
      eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authentication, this.getClass()));
    }

    String token = SecurityUtil.generateToken(request, authentication,
      securityStrategyType, redisTool, duration);

    PrintTool.printJson(response, JSON.toJSONString(new R(token).setMessage("登录成功")));
  }
}
