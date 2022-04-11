package com.tengjiao.seed.admin.security.domain;

import org.springframework.security.core.GrantedAuthority;

/**
 * 权限封装
 * @deprecated 目前请使用 SpringSecurity自带的 角色封装 SimpleGrantedAuthority
 * @author Administrator
 */
public class GrantedAuthorityImpl implements GrantedAuthority {

  private static final long serialVersionUID = -8973805578998039714L;

  private final String permission;

  public GrantedAuthorityImpl(String permission) {
    this.permission = permission;
  }

  @Override
  public String getAuthority() {
    return this.permission;
  }
}