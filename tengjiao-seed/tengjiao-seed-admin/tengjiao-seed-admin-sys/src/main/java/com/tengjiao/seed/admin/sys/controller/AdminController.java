package com.tengjiao.seed.admin.sys.controller;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tengjiao.part.mybatisplus.BaseDTO;
import com.tengjiao.part.springmvc.ResponseResult;
import com.tengjiao.seed.admin.model.sys.entity.Admin;
import com.tengjiao.seed.admin.model.sys.pojo.AdminDo;
import com.tengjiao.seed.admin.model.sys.pojo.AdminQueryVo;
import com.tengjiao.seed.admin.security.PermissionUtil;
import com.tengjiao.seed.admin.service.sys.AdminService;
import com.tengjiao.seed.admin.sys.comm.LogRequired;
import com.tengjiao.tool.indep.model.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 系统管理员 前端控制器
 * </p>
 *
 * @author rise
 * @since 2020-11-13
 */
@RestController @ResponseResult
@RequestMapping("system/admin")
@Api(tags = "sys-管理员管理")
@AllArgsConstructor
public class AdminController {

  private AdminService adminService;
  private PermissionUtil permissionUtil;

  /**@deprecated */
  @PostMapping("query/list/{current}/{size}")
  @ApiOperation("分页查询_MP_单表条件单列可排序")
  public R pageMPS_0(HttpServletRequest request,
                     @PathVariable Integer current, @PathVariable Integer size, @RequestBody BaseDTO params) {
    IPage<Admin> mpPage/*mybatis-plus page*/ = adminService.page(new Page<>(current, size), params);
    return new R(Dict.create().set("total", mpPage.getTotal()).set("records", mpPage.getRecords()));
  }
  /**@deprecated */
  @ApiOperation("分页查询_MP_单表")
  @PostMapping("query/list/MPS")
  public R pageMPS(HttpServletRequest request,
                   @RequestParam(required = false, defaultValue = "1") Integer current,
                   @RequestParam(required = false, defaultValue = "10") Integer size,
                   @RequestBody AdminQueryVo adminQueryVo) {

    Page<Admin> page = new Page<>(current, size);
    // 单表查询条件，这个方法是非常好用的
    LambdaQueryWrapper<Admin> lbdQueryWrapper = new LambdaQueryWrapper<>();

    if(adminQueryVo == null) {
      adminQueryVo = new AdminQueryVo();
    }
    permissionUtil.filterQuery(request, adminQueryVo);

    lbdQueryWrapper
      .like(!StringUtils.isEmpty(adminQueryVo.getUsername()), Admin::getUsername, adminQueryVo.getUsername() + "%")
      .like(!StringUtils.isEmpty(adminQueryVo.getNickname()), Admin::getNickname, adminQueryVo.getNickname() + "%")
      .like(!StringUtils.isEmpty(adminQueryVo.getEmail()), Admin::getEmail, adminQueryVo.getEmail() + "%")
      .like(!StringUtils.isEmpty(adminQueryVo.getPhone()), Admin::getPhone, adminQueryVo.getPhone() + "%");


    IPage<Admin> mpPage/*mybatis-plus page*/ = adminService.page(page, lbdQueryWrapper);

    return new R(Dict.create().set("total", mpPage.getTotal()).set("records", mpPage.getRecords()));

  }
  /**@deprecated */
  @ApiOperation("分页查询_MP_多表")
  @PostMapping("query/list/MPM")
  public R pageMPM(HttpServletRequest request,
                   @RequestParam(required = false, defaultValue = "1") Integer current,
                   @RequestParam(required = false, defaultValue = "10") Integer size,
                   @RequestBody AdminQueryVo adminQueryVo) {

    Page<AdminDo> page = new Page<>(current, size);
    QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();


    if(adminQueryVo == null) {
      adminQueryVo = new AdminQueryVo();
    }
    permissionUtil.filterQuery(request, adminQueryVo);

    // 封装查询条件(支持多表)
    queryWrapper
      .eq(!StringUtils.isEmpty(adminQueryVo.getUsername()), "username", adminQueryVo.getUsername())
      .eq(!StringUtils.isEmpty(adminQueryVo.getNickname()), "nickname", adminQueryVo.getNickname())
      //.eq(adminQueryVo.getDeptId() != null, "dept_id", adminQueryVo.getDeptId())
      .eq(adminQueryVo.getStationId() != null, "station_id", adminQueryVo.getStationId())
      .eq(!StringUtils.isEmpty(adminQueryVo.getEmail()), "email", adminQueryVo.getEmail())
      .eq(!StringUtils.isEmpty(adminQueryVo.getPhone()), "mobile", adminQueryVo.getPhone());

    IPage<AdminDo> mpPage/*mybatis-plus page*/ = adminService.page(page, queryWrapper);

    return new R(Dict.create().set("total", mpPage.getTotal()).set("records", mpPage.getRecords()));
  }

  @ApiOperation("分页查询_MB_多表")
  @PostMapping("query/list")
  public R pageMBM(HttpServletRequest request,
                   @RequestParam(required = false, defaultValue = "1") Integer current,
                   @RequestParam(required = false, defaultValue = "10") Integer size,
                   @RequestBody AdminQueryVo adminQueryVo) {

    // 第三个参数表示是否查询count总记录数
    PageHelper.startPage(current, size/*, false*/);

    if(adminQueryVo == null) {
      adminQueryVo = new AdminQueryVo();
    }
    permissionUtil.filterQuery(request, adminQueryVo);

    List<AdminDo> dataList = adminService.list(adminQueryVo);
    PageInfo<AdminDo> phPage/*PageHelper page*/ = new PageInfo<>(dataList);

    return new R(Dict.create().set("total", phPage.getTotal()).set("records", phPage.getList()));
  }

  @ApiOperation("添加管理员")
  @PostMapping("add")
  @LogRequired("添加管理员")
  public void addAdmin(HttpServletRequest request,
                       @Validated @RequestBody AdminDo adminDo) {
    permissionUtil.checkPermission(request, adminDo.getStationId());
    // 密码进行加密存储
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String encode = encoder.encode(adminDo.getPassword());
    adminDo.setPassword(encode);
    adminService.saveRecord(adminDo);
  }

  @ApiOperation("删除管理员")
  @DeleteMapping("/del/{id}")
  @LogRequired("删除管理员")
  public void delAdmin(HttpServletRequest request,
                       @PathVariable Long id) {
    permissionUtil.checkPermission(request, adminService.getById(id).getStationId());
    adminService.del(id);
  }

  @ApiOperation("修改管理员")
  @PutMapping("mod")
  @LogRequired("修改管理员")
  public void modAdmin(HttpServletRequest request,
                       @Validated @RequestBody AdminDo adminDo) {
    permissionUtil.checkPermission(request, adminDo.getStationId());
    adminService.saveRecord(adminDo);
  }

  @GetMapping("query/one/{id}")
  @ApiOperation("根据ID查询一个")
  public Admin queryAdminById(HttpServletRequest request,
                              @PathVariable Long id) {
    permissionUtil.checkPermission(request, adminService.getById(id).getStationId());
    AdminQueryVo adminQueryVo = new AdminQueryVo();
    adminQueryVo.setId(id);
    List<AdminDo> list = adminService.listWithRoles(adminQueryVo);
    return list.size() > 0 ? list.get(0) : null;
  }

}
