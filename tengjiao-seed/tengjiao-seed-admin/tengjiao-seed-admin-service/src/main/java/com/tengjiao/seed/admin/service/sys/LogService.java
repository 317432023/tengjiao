package com.tengjiao.seed.admin.service.sys;

import com.tengjiao.part.mybatisplus.BaseDTO;
import com.tengjiao.seed.admin.model.sys.entity.Log;
import com.baomidou.mybatisplus.extension.service.IService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.io.Serializable;

/**
 * 系统日志操作
 *
 * @author rise
 * @date 2020-11-14
 */
public interface LogService extends IService<Log> {

  /** 单表分页查询_[单列可排序NonCount] */
  IPage<Log> page(Page<Log> page, BaseDTO params);

  void add(Log log);

  void del(Serializable id);

  void mod(Log log);

  Log getOne(Serializable id);
}
