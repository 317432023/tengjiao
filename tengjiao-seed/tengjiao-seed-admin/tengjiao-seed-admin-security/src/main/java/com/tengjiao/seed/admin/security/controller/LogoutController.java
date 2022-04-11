package com.tengjiao.seed.admin.security.controller;

import com.tengjiao.part.redis.RedisTool;
import com.tengjiao.part.springmvc.ResponseResult;
import com.tengjiao.seed.admin.comm.Constants;
import com.tengjiao.seed.admin.security.SecurityUtil;
import com.tengjiao.seed.admin.security.config.SettingProperties;
import com.tengjiao.tool.indep.model.R;
import com.tengjiao.tool.indep.model.RC;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * LogoutController
 * <p>退出</p>
 * @author Administrator
 * @since 2020/11/15 19:48
 */
@RestController
@ResponseResult
@Api(value="sec-认证权限",tags="sec-认证权限")
public class LogoutController {

  private final SettingProperties settingProperties;
  private final RedisTool redisTool;

  public LogoutController(SettingProperties settingProperties, RedisTool redisTool) {
    this.settingProperties = settingProperties;
    this.redisTool = redisTool;
  }

  /**
   * ！！！说明：/logout 接口由 SpringSecurity全面接管，其内部调用了SecurityContextHolder.clearContext();
   * 在WebSecurityConfig中可以设置钩子函数返回json给前台
   * @return
   */
  @ApiOperation("注销")
  @PostMapping("/logout")
  public R logout(HttpServletRequest request) {
    Constants.SecurityStrategyType securityStrategyType = Enum.valueOf(Constants.SecurityStrategyType.class, settingProperties.getSecurityStrategy());
    SecurityUtil.logout(request, redisTool, securityStrategyType, settingProperties.getTokenHeaderKey(), settingProperties.getTokenValPrefix());
    return RC.SUCCESS.toR().setMessage("注销成功");
  }


}
