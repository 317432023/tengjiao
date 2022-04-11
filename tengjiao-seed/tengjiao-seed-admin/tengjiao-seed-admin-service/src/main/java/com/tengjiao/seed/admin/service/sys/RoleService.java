package com.tengjiao.seed.admin.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tengjiao.part.mybatisplus.BaseDTO;
import com.tengjiao.seed.admin.model.sys.entity.Role;

import java.io.Serializable;
import java.util.List;

/**
 * 系统角色
 *
 * @author rise
 * @date 2020-11-14
 */
public interface RoleService extends IService<Role> {

  /** 单表分页查询 Non-Count */
  Page<Role> page(Page<Role> page, BaseDTO params);

  void add(Role role);

  void del(Serializable id);

  void mod(Role role);

  Role getOne(Serializable id);


  // begin for 用户、用户拥有的角色、权限查询 required for SpringSecurity
  /**
   * 查询管理员相关的角色
   * @return
   */
  List<Role> selectRoleByAdminId(Serializable adminId);

  // end for 用户、用户拥有的角色、权限查询 required for SpringSecurity

  List<Integer> getRoleMenus(Integer roleId);

  void modRoleMenus(Integer roleId, List<Integer> menuIds);
}
