package com.tengjiao.seed.admin.security.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tengjiao.seed.admin.security.config.CustomGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * AdminUserDetails 安全管理员信息模型
 * @author Administrator
 * @since 2020/11/17 9:57
 */
public class AdminUserDetails/* extends User*/ implements UserDetails {

  private static final long serialVersionUID = 6994462468354433325L;

  private String username;
  private String password;
  private Set<CustomGrantedAuthority> authorities;

  public AdminUserDetails setUsername(String username) {
    this.username = username;
    return this;
  }

  public AdminUserDetails setPassword(String password) {
    this.password = password;
    return this;
  }

  public AdminUserDetails setAuthorities(Set<CustomGrantedAuthority> authorities) {
    this.authorities = authorities;
    return this;
  }

  public AdminUserDetails(){}
  /*public AdminUserDetails(String username, String password, Set<GrantedAuthority> authorities)
  {
    this.username = username;
    this.password = password;
    this.authorities = authorities;
  }*/
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }
  /**
   * 账户是否未过期,过期无法验证
   */
  @JsonIgnore
  @Override
  public boolean isAccountNonExpired()
  {
    return true;
  }

  /**
   * 指定用户是否解锁,锁定的用户无法进行身份验证
   *
   * @return
   */
  @JsonIgnore
  @Override
  public boolean isAccountNonLocked()
  {
    return true;
  }

  /**
   * 指示是否已过期的用户的凭据(密码),过期的凭据防止认证
   *
   * @return
   */
  @JsonIgnore
  @Override
  public boolean isCredentialsNonExpired()
  {
    return true;
  }

  /**
   * 是否可用 ,禁用的用户不能身份验证
   *
   * @return
   */
  @JsonIgnore
  @Override
  public boolean isEnabled()
  {
    return true;
  }

  // 以下自行添加其他用户属性
  /** 用户ID */
  private Long id;
  /** 昵称 */
  private String nickname;
  /** 头像 */
  private String avatar;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNickname() {
    return nickname;
  }

  public AdminUserDetails setNickname(String nickname) {
    this.nickname = nickname;
    return this;
  }

  public String getAvatar() {
    return avatar;
  }

  public AdminUserDetails setAvatar(String avatar) {
    this.avatar = avatar;
    return this;
  }
}
