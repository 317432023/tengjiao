package com.tengjiao.seed.admin.sys.controller;

import cn.hutool.core.lang.Dict;
import com.tengjiao.part.mybatisplus.BaseDTO;
import com.tengjiao.part.springmvc.ResponseResult;
import com.tengjiao.seed.admin.security.PermissionUtil;
import com.tengjiao.seed.admin.service.sys.MenuService;
import com.tengjiao.seed.admin.sys.comm.LogRequired;
import com.tengjiao.tool.indep.model.R;
import com.tengjiao.tool.indep.model.RC;
import com.tengjiao.tool.indep.model.SystemException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.tengjiao.seed.admin.model.sys.entity.Menu;
import com.tengjiao.seed.admin.model.sys.pojo.MenuTreeVo;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

/**
* 系统菜单表
*
* @author rise
* @date 2020-11-14
*/
@RestController @ResponseResult
@RequestMapping("system/menu")
@Api(tags = "sys-菜单管理 ")
@AllArgsConstructor
public class MenuController {

  private MenuService menuService;
  private PermissionUtil permissionUtil;

  @PostMapping("query/list/{current}/{size}")
  @ApiOperation("[单表]分页查询")
  public R pageMenu(@PathVariable Integer current, @PathVariable Integer size, @RequestBody BaseDTO params) {
    //IPage<Menu> mpPage/*mybatis-plus page*/ = menuService.page(new Page<>(current, size), params);
    //return Results.success(Dict.create().set("total", mpPage.getTotal()).set("records", mpPage.getRecords()));

    // 单表查询更直接的方式
    List<Menu> list = menuService.listAll();
    return new R(Dict.create().set("total", list.size()).set("records", list));

  }

  @PutMapping("mod")
  @ApiOperation("修改系统菜单")
  @LogRequired("修改系统菜单")
  public void modMenu(HttpServletRequest request, @Validated @RequestBody Menu menu) {
    if(!permissionUtil.isRoot(request)) {
      throw SystemException.create("403 权限不足").setCode(RC.PERMISSION_DENIED);
    }
    menuService.mod(menu);
  }

  @PostMapping("add")
  @ApiOperation("添加系统菜单")
  @LogRequired("添加系统菜单")
  public void addMenu(HttpServletRequest request, @Validated @RequestBody Menu menu) {
    if(!permissionUtil.isRoot(request)) {
      throw SystemException.create("403 权限不足").setCode(RC.PERMISSION_DENIED);
    }
    menuService.add(menu);
  }

  @DeleteMapping("/del/{id}")
  @ApiOperation("删除系统菜单")
  @LogRequired("删除系统菜单")
  public void delMenu(HttpServletRequest request, @PathVariable Serializable id) {
    if(!permissionUtil.isRoot(request)) {
      throw SystemException.create("403 权限不足");
    }
    menuService.del(id);
  }

  @GetMapping("query/one/{id}")
  @ApiOperation("查询一个系统菜单")
  public Menu queryMenuById(@PathVariable Serializable id) {
    return menuService.getOne(id);
  }

  @GetMapping("query/tree")
  @ApiOperation("查询菜单树")
  public List<MenuTreeVo> queryMenuTree() {
    return menuService.menuTree(true);
  }
}

