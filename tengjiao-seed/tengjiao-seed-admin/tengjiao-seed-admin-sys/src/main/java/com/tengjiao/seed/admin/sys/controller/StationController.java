package com.tengjiao.seed.admin.sys.controller;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tengjiao.part.mybatisplus.BaseDTO;
import com.tengjiao.part.springmvc.ResponseResult;
import com.tengjiao.seed.admin.sys.comm.LogRequired;
import com.tengjiao.seed.admin.model.sys.entity.Admin;
import com.tengjiao.seed.admin.model.sys.entity.Role;
import com.tengjiao.seed.admin.model.sys.entity.Station;
import com.tengjiao.seed.admin.service.sys.AdminService;
import com.tengjiao.seed.admin.service.sys.RoleService;
import com.tengjiao.seed.admin.service.sys.StationService;
import com.tengjiao.seed.admin.security.PermissionUtil;
import com.tengjiao.tool.indep.model.R;
import com.tengjiao.tool.indep.model.RC;
import com.tengjiao.tool.indep.model.SystemException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 系统站点管理
 *
 * @author rise
 * @date 2021-02-02
 */
//@AdminLoginRequired(token = LOGIN_TOKEN_NAME)
//@ParamSecurityRequired(saas = false, token = LOGIN_TOKEN_NAME)
@RestController @ResponseResult
@RequestMapping("system/station")
@Api(tags = "sys-站点管理")
@AllArgsConstructor
public class StationController {
  private final StationService stationService;
  private final PermissionUtil permissionUtil;

  private final AdminService adminService;
  private final RoleService roleService;

  /**
   * <p>
   * 单表分页查询
   * </p>
   * 用于记录较少的表，大表查询禁止使用该方法！！！
   * @param current   /
   * @param params /
   * @return /
   */
  @ApiOperation("分页查询")
  @PostMapping("query/list/{current}/{size}")
  public R queryPage(@PathVariable Integer current, @PathVariable Integer size, @RequestBody BaseDTO params) {
    IPage<Station> mpPage/*mybatis-plus page*/ = stationService.page(new Page<>(current, size), params);
    return new R(Dict.create().set("total", mpPage.getTotal()).set("records", mpPage.getRecords()));
  }

  @PutMapping("mod")
  @ApiOperation("修改系统站点")
  @LogRequired("修改系统站点")
  public void modStation(HttpServletRequest request, @Validated @RequestBody Station station) {
    permissionUtil.checkPermission(request, station.getId());
    stationService.mod(station);
  }

  @PostMapping("add")
  @ApiOperation("添加系统站点")
  @LogRequired("添加系统站点")
  public void addStation(HttpServletRequest request, @Validated @RequestBody Station station) {
    if(!permissionUtil.isRoot(request)) {
      throw SystemException.create("403 权限不足").setCode(RC.PERMISSION_DENIED);
    }
    stationService.add(station);
  }

  @DeleteMapping("del/{id}")
  @ApiOperation("删除系统站点")
  @LogRequired("删除系统站点")
  public void delStation(HttpServletRequest request, @PathVariable Integer id) {
    if(!permissionUtil.isRoot(request)) {
      throw SystemException.create("403 权限不足").setCode(RC.PERMISSION_DENIED);
    }
    // 不可 删除 已经存在数据(用户、角色...)的站点 或 自身站点
    QueryWrapper<Admin> qw = new QueryWrapper<>();
    qw.eq("station_id", id);
    if( adminService.count(qw) > 1) {
      throw SystemException.create("无法删除站点，原因：该站点下面存在用户");

    }
    QueryWrapper<Role> qw1 = new QueryWrapper<>();
    qw1.eq("station_id", id);
    if( roleService.count(qw1) > 1) {
      throw SystemException.create("无法删除站点，原因：该站点下面存在角色");
    }
    stationService.del(id);
  }

  @GetMapping("query/one/{id}")
  @ApiOperation("查询一个系统站点")
  public Station queryStationById(@PathVariable Integer id) {
    return stationService.getOne(id);
  }


}
