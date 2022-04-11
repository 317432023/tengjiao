package com.tengjiao.seed.admin.security.controller;

import com.tengjiao.part.springmvc.ResponseResult;
import com.tengjiao.seed.admin.security.domain.AdminUserDetails;
import com.tengjiao.seed.admin.model.security.pojo.RouteDo;
import com.tengjiao.seed.admin.service.sys.MenuService;
import com.tengjiao.seed.admin.security.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.tengjiao.seed.admin.model.security.pojo.MenuVo;

import java.util.List;

/**
 * RouteController
 *
 * @author Administrator
 * @since 2020/11/22 11:42
 */
@RestController
@ResponseResult
@AllArgsConstructor
@Api(value="sec-认证权限",tags="sec-认证权限")
public class RouteController {

  private MenuService menuService;

  /**
   * 加载路由
   * @param roleIds
   * @return
   */
  @PostMapping("/loadRoutes")
  @ApiOperation("前端加载路由")
  public List<RouteDo> loadRoutes(@RequestParam(required = false) Integer[] roleIds) {

    AdminUserDetails userDetails = SecurityUtil.getCurrentUser();

    return
      roleIds != null && roleIds.length > 0 ?
        menuService.loadRoutesByRoleIds(roleIds) :
        menuService.loadRoutesByAdminId(userDetails.getId());
  }

  /**
   * 加载菜单
   * TODO 将来废弃掉本方法直接使用loadRoutes返回的路由列表构造菜单
   * @param roleIds
   * @return
   */
  @PostMapping("/loadMenus")
  @ApiOperation("前端加载菜单")
  public List<MenuVo> loadMenus(@RequestParam Integer[] roleIds) {
    return menuService.loadMenus(roleIds);
  }

}
