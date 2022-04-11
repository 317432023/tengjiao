package com.tengjiao.seed.admin.service.sys;

import com.tengjiao.seed.admin.model.sys.entity.Job;
import com.baomidou.mybatisplus.extension.service.IService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tengjiao.part.mybatisplus.BaseDTO;

import java.io.Serializable;

/**
 * 定时任务操作
 *
 * @author tengjiao
 * @date 2021-06-27
 */
public interface JobService extends IService<Job> {

  /* 单表分页查询_[单列可排序NonCount] */
  IPage<Job> page(Page<Job> page, BaseDTO params);

  boolean add(Job job);

  boolean del(Serializable id);

  boolean mod(Job job);

  Job getOne(Serializable id);
}
