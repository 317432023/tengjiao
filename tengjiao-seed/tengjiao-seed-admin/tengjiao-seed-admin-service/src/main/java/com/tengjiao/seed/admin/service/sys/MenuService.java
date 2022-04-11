package com.tengjiao.seed.admin.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tengjiao.seed.admin.model.sys.entity.Menu;
import com.tengjiao.seed.admin.model.sys.pojo.MenuRolesDo;
import com.tengjiao.seed.admin.model.sys.pojo.MenuTreeVo;
import com.tengjiao.seed.admin.model.security.pojo.MenuVo;
import com.tengjiao.seed.admin.model.security.pojo.RouteDo;

import java.io.Serializable;
import java.util.List;

/**
 * 系统菜单表操作
 *
 * @author rise
 * @date 2020-11-14
 */
public interface MenuService extends IService<Menu> {

  List<Menu> listAll();

  void add(Menu menu);

  void del(Serializable id);

  void mod(Menu menu);

  Menu getOne(Serializable id);

  /**
   * 列出所有菜单包含角色
   * @return
   */
  List<MenuRolesDo> selectAllMenuRoles();
  /**
   * 加载全部路由
   * @return
   */
  List<RouteDo> loadRoutes();
  /**
   * 根据用户ID加载路由
   * @param adminId
   * @return
   */
  List<RouteDo> loadRoutesByAdminId(Long adminId);
  /**
   * 根据角色IDs加载路由
   * @param roleIds
   * @return
   */
  List<RouteDo> loadRoutesByRoleIds(Integer[] roleIds);
  /**
   * 根据角色IDs加载菜单
   * @param roleIds
   * @return
   */
  List<MenuVo> loadMenus(Integer[] roleIds);

  List<MenuTreeVo> menuTree(Boolean excludesPerm);
}
