package com.tengjiao.seed.admin.model.sys.pojo;

import lombok.Getter;
import lombok.Setter;
import com.tengjiao.seed.admin.model.sys.entity.Role;

import java.io.Serializable;
import java.util.List;

/**
 * MenuRolesDO 菜单角色信息 领域对象
 * @author Administrator
 * @since 2020/11/18 0:24
 */
@Getter
@Setter
public class MenuRolesDo implements Serializable {
  private static final long serialVersionUID = -4142107374002400162L;
  /**
   * 菜单id
   */
  private Integer mid;
  /**
   * 菜单类型
   */
  private Integer type;
  /**
   * 权限，例如 user:view
   */
  private String perm;
  /**
   * 权限(匹配URL模式)，例如 访问url模式为/user/query/** 或 访问url模式为/user/query
   */
  private String pattern;
  /**
   * 角色列表
   */
  private List<Role> roles;
}
