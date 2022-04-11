package com.tengjiao.seed.admin.service.sys;

import com.tengjiao.part.mybatisplus.BaseDTO;
import com.tengjiao.seed.admin.model.sys.entity.Config;
import com.baomidou.mybatisplus.extension.service.IService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.io.Serializable;

/**
 * 系统设置操作
 *
 * @author rise
 * @date 2021-03-18
 */
public interface ConfigService extends IService<Config> {

  /* 单表分页查询_[单列可排序NonCount] */
  IPage<Config> page(Page<Config> page, BaseDTO params);

  void add(Config config);

  void del(Serializable id);

  void mod(Config config);

  Config getOne(Serializable id);
}
