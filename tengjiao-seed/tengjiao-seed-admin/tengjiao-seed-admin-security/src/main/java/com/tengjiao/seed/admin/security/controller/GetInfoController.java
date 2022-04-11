package com.tengjiao.seed.admin.security.controller;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson.JSON;
import com.tengjiao.part.redis.ModeDict;
import com.tengjiao.part.redis.RedisTool;
import com.tengjiao.part.springmvc.ResponseResult;
import com.tengjiao.seed.admin.comm.Constants;
import com.tengjiao.seed.admin.security.SecurityUtil;
import com.tengjiao.seed.admin.security.config.SettingProperties;
import com.tengjiao.seed.admin.security.domain.AdminUserDetails;
import com.tengjiao.tool.indep.model.RC;
import com.tengjiao.tool.indep.model.SystemException;
import com.tengjiao.tool.indep.web.CookieTool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tengjiao.comm.Constants.LOGIN_TOKEN_PREFIX;
import static com.tengjiao.seed.admin.comm.Constants.ADMINISTRATOR_ID;

/**
 * 拉取用户信息
 * @author administrator
 * @since 2020/11/15 19:49
 */
@Api(value="sec-认证权限",tags="sec-认证权限")
@RestController
@ResponseResult
@Slf4j
@AllArgsConstructor
public class GetInfoController {

  private RedisTool redisTool;
  private SettingProperties settingProperties;

  /**
   * 拉取用户信息（过滤器见starter.tinymis.control.module.security.filter.*AuthenticationFilter）
   * <p>
   *   包含用户名和角色列表
   * </p>
   * @return
   */
  @ApiOperation("拉取用户信息")
  @GetMapping("/getInfo")
  public Map<String, Object> getInfo(HttpServletRequest request) {
    AdminUserDetails userDetails = null;
    String token = null;
    Constants.SecurityStrategyType securityStrategyType = Enum.valueOf(Constants.SecurityStrategyType.class, settingProperties.getSecurityStrategy());
    if(Constants.SecurityStrategyType.session == securityStrategyType) {
      // SpringSecurity，发送给客户端的会话ID经过了Base64
      token = CookieTool.getCookieValue(request, "SESSION");
      log.info("拉取用户信息 当前会话SESSION base64 =" + token);
      token = Base64Decoder.decodeStr(token);
      log.info("拉取用户信息 当前会话SESSION=" + token);
      Assert.isTrue(request.getRequestedSessionId().equals(token), "当前会话已过期或在当前服务器上不存在(可能使用集群多节点导致)");
      userDetails = SecurityUtil.getCurrentUser();
    }else if(Constants.SecurityStrategyType.token == securityStrategyType) {
      String authHeader = request.getHeader(settingProperties.getTokenHeaderKey());
      token = authHeader.substring(settingProperties.getTokenValPrefix().length());
      log.info("拉取用户信息 当前令牌token=" + token);
      String stringAdminUserDetails = redisTool.getString(LOGIN_TOKEN_PREFIX + token, ModeDict.APP);
      if(stringAdminUserDetails != null) {
        userDetails = JSON.parseObject(stringAdminUserDetails, AdminUserDetails.class);
      }
    }

    if(userDetails != null){

      Map<String, Object> map0 = new HashMap<>(4);
      List<String> roles = userDetails.getAuthorities().stream().map(e->e.getAuthority()).collect(Collectors.toList());
      if( userDetails.getId().longValue() != ADMINISTRATOR_ID && roles.size() == 0) {
        throw SystemException.create().setCode(RC.PERMISSION_DENIED) ;
      }
      map0.put("roles", roles.size()>0?roles: ListUtil.of("")); // 让前端正常走
      map0.put("username", userDetails.getUsername());
      map0.put("nickname", userDetails.getNickname());
      map0.put("avatar", userDetails.getAvatar());
      return map0;
    }else{
      throw SystemException.create().setCode(RC.NOT_AUTHENTICATED);
    }
  }

}
