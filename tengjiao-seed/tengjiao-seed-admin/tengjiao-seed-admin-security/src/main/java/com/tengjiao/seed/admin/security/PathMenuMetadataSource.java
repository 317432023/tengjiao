package com.tengjiao.seed.admin.security;

import com.tengjiao.seed.admin.model.sys.pojo.MenuRolesDo;
import com.tengjiao.seed.admin.security.config.WebSecurityConfig;
import com.tengjiao.seed.admin.security.domain.AdminUserDetails;
import com.tengjiao.seed.admin.service.sys.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.tengjiao.seed.admin.comm.Constants.ADMINISTRATOR_ID;
import static com.tengjiao.seed.admin.security.config.WebSecurityConfig.PERMITS;

/**
 * 根据当前请求URL获得哪些角色有访问权限
 * <p>
 *
 * @author Administrator
 */
@Component
public class PathMenuMetadataSource implements FilterInvocationSecurityMetadataSource {
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();
  private final MenuService menuService;


  @Autowired
  public PathMenuMetadataSource(MenuService menuService) {
    this.menuService = menuService;
  }

  /**
   * 取得对当前请求的URL有访问权限的角色列表 (取得 请求URL需要的角色)
   *
   * @param object /
   * @return /
   * @throws IllegalArgumentException /
   */
  @Override
  public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {

    // 当前请求的URL
    String reqUrl = ((FilterInvocation) object).getRequest().getServletPath();//((FilterInvocation) object).getRequestUrl();

    // 优先可匿名访问的路径或资源
    for ( String p : PERMITS) {
      if (antPathMatcher.match(p, reqUrl)) {
        return SecurityConfig.createList("NOT_NEED_AUTH");
      }
    }

    if(SecurityUtil.checkAuthentication()) {
      AdminUserDetails userDetails = SecurityUtil.getCurrentUser();
      // 超级管理员不需要认证角色
      if(userDetails.getId().longValue() == ADMINISTRATOR_ID) {
        return SecurityConfig.createList("NOT_NEED_AUTH");
      }
    } else {
      // 此异常将被 LoginAuthenticationEntryPoint 处理
      throw new InsufficientAuthenticationException("匿名用户无此权限(401 not Authenticated)");
    }

    // 取得 URL列表，每个URL中包含了允许访问的角色列表
    List<MenuRolesDo> menus = menuService.selectAllMenuRoles()
      .parallelStream()
      .filter(e-> e!=null && e.getType()!=2 && !StringUtils.isEmpty(e.getPattern()))
      .collect(Collectors.toList());

    for (MenuRolesDo menuRolesDo : menus) {
      if (antPathMatcher.match(menuRolesDo.getPattern(), reqUrl)) {
        // 匹配到URL，设置当前请求URL 需要的角色列表作为 ConfigAttribute 集合 并返回它
        if(menuRolesDo.getRoles().size() == 0) {
          // 此处抛出的异常无法被 MyAccessDeniedHandler 捕获，只能被LoginAuthenticationEntryPoint捕获
          throw new AccessDeniedException("403");
        }
        // /*Role::getId*/
        String[] roles = menuRolesDo.getRoles().stream().map(e-> e.getId().toString()).toArray(String[]::new);
        return SecurityConfig.createList(roles);
      }
    }

    // 未匹配到请求的URL，说明当前请求不需要认证角色

    // 如果当前请求的URL 需要登录才能访问的情况，设置一个虚拟角色名为 NEED_AUTH 创建 ConfigAttribute 集合并返回它
    for (String needAuthPath : WebSecurityConfig.NEED_AUTH_URL) {
      if (antPathMatcher.match(needAuthPath, reqUrl)) {
        return SecurityConfig.createList("NEED_AUTH");
      }
    }

    // 其余一律可以访问，设置一个虚拟角色名为 NOT_NEED_AUTH 创建 ConfigAttribute 集合并返回它
    return SecurityConfig.createList("NOT_NEED_AUTH");
  }

  @Override
  public Collection<ConfigAttribute> getAllConfigAttributes() {
    return null;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return true;
  }

  public static void main(String[] args) {
    AntPathMatcher antPathMatcher = new AntPathMatcher();
    System.out.println(antPathMatcher.match("/manage/editExam.do**", "/manage/editExam.do?method=goExamSet&type=U"));
    System.out.println(antPathMatcher.match("/station/query/**", "/station/query/list/1/6"));
  }
}
