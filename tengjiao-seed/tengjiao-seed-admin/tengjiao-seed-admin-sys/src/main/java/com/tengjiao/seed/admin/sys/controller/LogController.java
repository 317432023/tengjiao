package com.tengjiao.seed.admin.sys.controller;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tengjiao.part.mybatisplus.BaseDTO;
import com.tengjiao.part.springmvc.ResponseResult;
import com.tengjiao.tool.indep.model.R;
import lombok.AllArgsConstructor;
import com.tengjiao.seed.admin.service.sys.LogService;
import com.tengjiao.seed.admin.model.sys.entity.Log;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Api;

import java.io.Serializable;

/**
* 系统日志管理
*
* @author rise
* @since 2020-11-14
*/
@RestController @ResponseResult
@RequestMapping("system/log")
@Api(tags = "sys-系统日志 ")
@AllArgsConstructor
public class LogController {

  private LogService logService;

  /**
  * <p>
  * 单表分页查询_[单列可排序NonCount]
  * </p>
  * 用于记录较少的表，大表查询禁止使用该方法！！！
  * @param page   /
  * @param params /
  * @return /
  */
  @PostMapping("query/list/{page}/{size}")
  @ApiOperation("分页查询系统日志_[单表单列可排序NonCount]")
  public R pageLog(@PathVariable Integer page, @PathVariable Integer size, @RequestBody BaseDTO params) {
    IPage<Log> mpPage/*mybatis-plus page*/ = logService.page(new Page<>(page, size), params);
    return new R(Dict.create().set("total", mpPage.getTotal()).set("records", mpPage.getRecords()));
  }
  @DeleteMapping("del/{id}")
  public void delLog(@PathVariable Serializable id) {
    logService.del(id);
  }

}

