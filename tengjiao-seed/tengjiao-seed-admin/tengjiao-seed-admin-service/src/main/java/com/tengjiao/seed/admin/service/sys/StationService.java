package com.tengjiao.seed.admin.service.sys;

import com.tengjiao.part.mybatisplus.BaseDTO;
import com.tengjiao.seed.admin.model.sys.entity.Station;
import com.baomidou.mybatisplus.extension.service.IService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.io.Serializable;

/**
 * 系统站点操作
 *
 * @author rise
 * @date 2021-02-02
 */
public interface StationService extends IService<Station> {

  /* 单表分页查询_[单列可排序NonCount] */
  IPage<Station> page(Page<Station> page, BaseDTO params);

  void add(Station station);

  void del(Serializable id);

  void mod(Station station);

  Station getOne(Serializable id);
}
