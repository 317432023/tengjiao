package com.tengjiao.seed.admin.sys.controller;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tengjiao.part.mybatisplus.BaseDTO;
import com.tengjiao.part.springmvc.ResponseResult;
import com.tengjiao.seed.admin.sys.comm.LogRequired;
import com.tengjiao.seed.admin.security.PermissionUtil;
import com.tengjiao.tool.indep.model.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.tengjiao.seed.admin.service.sys.MenuService;
import com.tengjiao.seed.admin.service.sys.RoleService;
import com.tengjiao.seed.admin.model.sys.entity.Role;
import com.tengjiao.seed.admin.model.sys.pojo.MenuTreeVo;
import com.tengjiao.seed.admin.model.sys.pojo.RoleMenuDto;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

/**
* 系统角色表
*
* @author rise
* @date 2020-11-14
*/
@RestController @ResponseResult
@RequestMapping("system/role")
@Api(tags = "sys-角色管理 ")
@AllArgsConstructor
public class RoleController {
  private RoleService roleService;
  private MenuService menuService;
  private PermissionUtil permissionUtil;

  @PostMapping("query/list/{current}/{size}")
  @ApiOperation("分页查询系统角色")
  public R pageRole(@PathVariable Integer current, @PathVariable Integer size, @RequestBody BaseDTO params) {
    IPage<Role> mpPage/*mybatis-plus page*/ = roleService.page(new Page<>(current, size), params);
    return new R(Dict.create().set("total", mpPage.getTotal()).set("records", mpPage.getRecords()));
  }

  @ApiOperation("分页查询_[MP][单表]")
  @PostMapping("query/list")
  public R pageSngTb(HttpServletRequest request,
                     @RequestParam(required = false, defaultValue = "1") Integer current,
                     @RequestParam(required = false, defaultValue = "6") Integer size,
                     @RequestParam(required = false) Integer stationId) {

    stationId = permissionUtil.checkPermission(request, stationId);

    // 单表查询条件，这个方法是非常好用的
    LambdaQueryWrapper<Role> lbdQryWrpr = new LambdaQueryWrapper<>();
    lbdQryWrpr
      .eq(stationId != null, Role::getStationId, stationId);
    IPage<Role> iPage = roleService.page(new Page<Role>(current, size), lbdQryWrpr);

    return new R(Dict.create().set("total", iPage.getTotal()).set("records", iPage.getRecords()));
  }

  @PutMapping("mod")
  @ApiOperation("修改系统角色")
  @LogRequired("修改系统角色")
  public void modRole(HttpServletRequest request, @Validated@RequestBody Role role) {
    permissionUtil.checkPermission(request, role.getStationId());
    roleService.mod(role);
  }

  @PostMapping("add")
  @ApiOperation("添加系统角色")
  @LogRequired("添加系统角色")
  public void addRole(HttpServletRequest request, @Validated @RequestBody Role role) {
    permissionUtil.checkPermission(request, role.getStationId());
    roleService.add(role);
  }

  @DeleteMapping("del/{id}")
  @ApiOperation("删除系统角色")
  @LogRequired("删除系统角色")
  public void delRole(HttpServletRequest request, @PathVariable Serializable id) {
    permissionUtil.checkPermission(request, roleService.getById(id).getStationId());
    roleService.del(id);
  }

  @GetMapping("query/one/{id}")
  @ApiOperation("查询一个系统角色")
  public Role queryRoleById(@PathVariable Serializable id) {
    return roleService.getOne(id);
  }

  @PutMapping("mod/menu")
  @LogRequired("更新角色菜单")
  public void modRoleMenus(HttpServletRequest request, @RequestBody RoleMenuDto dto) {
    permissionUtil.checkPermission(request, roleService.getById(dto.getRoleId()).getStationId());
    roleService.modRoleMenus(dto.getRoleId(), dto.getMenuIds());
  }

  // begin for 更新角色菜单 需要的额外请求
  /**
   * 查看菜单树
   * @return
   */
  @GetMapping("query/menuTree")
  public List<MenuTreeVo> getMenuTree() {
    return menuService.menuTree(false);
  }

  /**
   * 查看角色拥有的菜单项
   * @param roleId
   * @return
   */
  @GetMapping("query/menu")
  public List<Integer> getRoleMenus(HttpServletRequest request, Integer roleId) {
    permissionUtil.checkPermission(request, roleService.getById(roleId).getStationId());
    return roleService.getRoleMenus(roleId);
  }

  // end for 更新角色菜单 需要的额外请求
}

