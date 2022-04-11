package com.tengjiao.seed.admin.security;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 判断当前用户是否有权限访问（是否有当前请求URL需要的相关(任一)角色）
 * <p>
 * // TODO 用户拥有的角色变化时，强迫用户会话下线
 * @author Administrator
 */
@Component
public class UrlAccessDecisionManager implements AccessDecisionManager {
  /**
   * @param authentication   用户信息
   * @param object           /
   * @param configAttributes 需要角色列表， 已由 PathMenuMetadataSource#getAttributes 方法获得
   * @throws AccessDeniedException               /
   * @throws InsufficientAuthenticationException /
   */
  @Override
  public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {

    for (ConfigAttribute configAttribute : configAttributes) {

      // 取出对当前请求URL 拥有访问权限的其中一个角色
      String role = configAttribute.getAttribute();

      if ("NOT_NEED_AUTH".equals(role)) {
        return;
      }

      if (authentication instanceof AnonymousAuthenticationToken) {
        throw new InsufficientAuthenticationException("匿名用户无此权限(401 not Authenticated)");
      } else if ("NEED_AUTH".equals(role)) {
        return;
      }

      for (GrantedAuthority haveRole : authentication.getAuthorities()) {
        // 迭代当前用户拥有的所有角色，判断用户是否拥有指定的角色
        if (haveRole.getAuthority().equals(role)) {
          return;
        }
      }
    }

    // 注意：此处异常无法被 MyAccessDeniedHandler 捕获，只能被 LoginAuthenticationEntryPoint 处理
    throw new AccessDeniedException("403");
  }

  @Override
  public boolean supports(ConfigAttribute attribute) {
    return true;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return true;
  }
}
