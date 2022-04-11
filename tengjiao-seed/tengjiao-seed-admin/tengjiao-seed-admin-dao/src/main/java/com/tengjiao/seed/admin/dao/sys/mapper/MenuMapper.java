package com.tengjiao.seed.admin.dao.sys.mapper;

import com.tengjiao.seed.admin.model.security.pojo.MenuVo;
import com.tengjiao.seed.admin.model.security.pojo.RouteDo;
import com.tengjiao.seed.admin.model.sys.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tengjiao.seed.admin.model.sys.pojo.MenuRolesDo;
import com.tengjiao.seed.admin.model.sys.pojo.MenuTreeVo;

import java.util.List;

/**
 * <p>
 * 系统菜单表 Mapper 接口
 * </p>
 *
 * @author rise
 * @date 2020-11-14
 */
public interface MenuMapper extends BaseMapper<Menu> {
  List<MenuRolesDo> selectAllMenuRoles();
  List<RouteDo> loadRoutes();
  List<RouteDo> loadRoutesByAdminId(Long adminId);
  List<RouteDo> loadRoutesByRoleIds(Integer[] roleIds);
  List<MenuVo> loadMenusByRoleIds(Integer[] roleIds);
  List<MenuTreeVo> menuTree(Boolean excludesPerm);
}
