package com.tengjiao.seed.admin.security.controller;

import com.tengjiao.part.redis.RedisTool;
import com.tengjiao.part.springmvc.ResponseResult;
import com.tengjiao.seed.admin.comm.Constants;
import com.tengjiao.seed.admin.security.SecurityUtil;
import com.tengjiao.seed.admin.security.config.SettingProperties;
import com.tengjiao.tool.indep.model.SystemException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * LoginController
 * <p>登录</p>
 *
 * @author Administrator
 * @since 2020/11/15 19:48
 */
@Slf4j
@RestController
@ResponseResult
@Api(value="sec-认证权限",tags="sec-认证权限")
public class LoginController {

  private SettingProperties settingProperties;
  private RedisTool redisTool;
  private AuthenticationManager authenticationManager;
  private UserDetailsService userDetailsService;

  public LoginController(SettingProperties settingProperties,
                         RedisTool redisTool,
                         AuthenticationManager authenticationManager,
                         @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
    this.settingProperties = settingProperties;
    this.redisTool = redisTool;
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
  }

  /**
   * ！！！说明：若使用了SpringSecurity并且配置了NamePwdLoginFilter，将由 NamePwdLoginFilter#attemptAuthentication 接管登录逻辑
   * @param request
   * @return
   */
  @PostMapping("/login")
  @ApiOperation("登录")
  public String login(HttpServletRequest request, HttpServletResponse response) {
    try {

      Constants.SecurityStrategyType securityStrategyType = Enum.valueOf(Constants.SecurityStrategyType.class, settingProperties.getSecurityStrategy());

      return SecurityUtil.login(request, response,
        authenticationManager,
        userDetailsService,
        securityStrategyType,
        redisTool,
        settingProperties.getEncryptPassword()?settingProperties.getPrivateKey():"",
        settingProperties.getDuration());

    } catch( BadCredentialsException e ) {

      throw SystemException.create("用户名或密码错误");

    } catch( DisabledException e ) {

      throw SystemException.create("账户被禁用，请联系管理员");

    } catch( UsernameNotFoundException e) {

      throw SystemException.create(e.getMessage());

    } catch( AuthenticationServiceException e ) {

      throw SystemException.create(e.getMessage());

    } catch( Exception e ) {

      throw SystemException.create("登录失败");

    }

  }

}
