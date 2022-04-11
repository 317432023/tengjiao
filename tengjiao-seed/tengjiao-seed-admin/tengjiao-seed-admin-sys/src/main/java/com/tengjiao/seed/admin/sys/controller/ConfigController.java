package com.tengjiao.seed.admin.sys.controller;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tengjiao.part.mybatisplus.BaseDTO;
import com.tengjiao.part.redis.ModeDict;
import com.tengjiao.part.redis.RedisTool;
import com.tengjiao.part.springmvc.ResponseResult;
import com.tengjiao.seed.admin.comm.Constants;
import com.tengjiao.seed.admin.security.PermissionUtil;
import com.tengjiao.seed.admin.service.sys.ConfigService;
import com.tengjiao.seed.admin.sys.comm.LogRequired;
import com.tengjiao.tool.indep.model.R;
import com.tengjiao.tool.indep.model.SystemException;
import lombok.AllArgsConstructor;
import com.tengjiao.seed.admin.model.sys.entity.Config;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
* 系统设置管理
*
* @author rise
* @date 2021-03-18
*/
@RestController
@ResponseResult
@RequestMapping("system/config")
@Api(tags = "sys-参数设置 ")
@AllArgsConstructor
public class ConfigController {

  private ConfigService configService;
  private PermissionUtil permissionUtil;
  private RedisTool redisTool;
  /**
  * <p>
  * 单表分页查询
  * </p>
  * 用于记录较少的表，大表查询禁止使用该方法！！！
  * @param current   /
  * @param size /
  * @param params /
  * @return /
  */
  @PostMapping("query/list/{current}/{size}")
  @ApiOperation("分页查询系统设置")
  public R pageConfig(@PathVariable Integer current, @PathVariable Integer size, @RequestBody BaseDTO params) {
    IPage<Config> configPage = configService.page(new Page<>(current, size), params);
    return new R(Dict.create().set("total", configPage.getTotal()).set("records", configPage.getRecords()));
  }

  @PutMapping("mod")
  @ApiOperation("修改系统设置")
  @LogRequired("修改系统设置")
  public void modConfig(HttpServletRequest request, @Validated@RequestBody Config config) {
    if(!permissionUtil.isRoot(request)) {
      throw SystemException.create("403 权限不足，仅限总站用户操作");
    }
    configService.mod(config);
    redisTool.hset(Constants.SYS_CONFIG_KEY, config.getName(), config.getValue(), ModeDict.APP_GROUP);
  }

  @PostMapping("add")
  @ApiOperation("添加系统设置")
  @LogRequired("添加系统设置")
  public void addConfig(HttpServletRequest request, @Validated @RequestBody Config config) {
    if(!permissionUtil.isRoot(request)) {
      throw SystemException.create("403 权限不足，仅限总站用户操作");
    }
    configService.add(config);
    redisTool.hset(Constants.SYS_CONFIG_KEY, config.getName(), config.getValue(), ModeDict.APP_GROUP);
  }

  @DeleteMapping("del/{id}")
  @ApiOperation("删除系统设置")
  @LogRequired("删除系统设置")
  public void delConfig(HttpServletRequest request, @PathVariable Serializable id) {
    if(!permissionUtil.isRoot(request)) {
      throw SystemException.create("403 权限不足，仅限总站用户操作");
    }
    Config config = configService.getOne(id);

    if(config != null) {
      configService.del(id);
      redisTool.hdel(Constants.SYS_CONFIG_KEY, ModeDict.APP_GROUP, config.getName());
    }

  }

  @GetMapping("query/one/{id}")
  @ApiOperation("查询一个系统设置")
  public Config queryConfigById(@PathVariable Serializable id) {
    return configService.getOne(id);
  }
}

