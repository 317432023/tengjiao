package com.tengjiao.seed.admin.service.sys;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tengjiao.part.mybatisplus.BaseDTO;
import com.tengjiao.seed.admin.model.sys.entity.Admin;
import com.tengjiao.seed.admin.model.sys.pojo.AdminDo;
import com.tengjiao.seed.admin.model.sys.pojo.AdminQueryVo;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 系统管理员表 服务类
 * </p>
 *
 * @author rise
 * @since 2020-11-13
 */
public interface AdminService extends IService<Admin> {
  IPage<Admin> page(Page<Admin> page, BaseDTO params);

  IPage<AdminDo> page(Page page, QueryWrapper<Admin> wrapper);

  List<AdminDo> list(AdminQueryVo adminVo);

  List<AdminDo> listWithRoles(AdminQueryVo adminVo);

  void add(Admin entity);

  void del(Serializable id);

  void mod(Admin entity);

  Admin getOne(Serializable id);

  // 以上为套路代码

  // 以下为特定需求代码

  void saveRecord(AdminDo adminDo);

  // begin for 用户、用户拥有的角色、权限查询 required for SpringSecurity
  /**
   * 根据用户名查找用户
   * @param username
   * @return
   */
  Admin findByUsername(String username);

  /**
   * @deprecated
   * 查找用户的菜单权限标识集合
   * @param username
   * @return
   */
  Set<String> findPermissions(String username);
  // end for 用户、用户拥有的角色、权限查询 required for SpringSecurity

}
