package com.tengjiao.seed.admin.service.sys.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tengjiao.part.mybatisplus.BaseDTO;
import com.tengjiao.part.mybatisplus.SqlUtil;
import com.tengjiao.seed.admin.model.sys.entity.Job;
import com.tengjiao.seed.admin.service.sys.JobService;
import com.tengjiao.seed.admin.dao.sys.mapper.JobMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * <p>
 * 定时任务 操作
 * </p>
 *
 * @author tengjiao
 * @date 2021-06-27
 */
@Slf4j
@Service("jobService")
@Transactional(rollbackFor = Exception.class)
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

  /**
   * <p>
   * 单表分页查询_[单列可排序NonCount]
   * </p>
   * 用于记录较少的表，大表查询禁止使用该方法！！！
   * @param page   /
   * @param params /
   * @return /
   */
  @Transactional(readOnly = true)
  @Override
  public IPage<Job> page(Page<Job> page, BaseDTO params) {
    IPage<Job> mpPage = SqlUtil
     .likeAll(Job.class, query(), params.getText())
     .orderBy(params.getSort().size() > 0, params.isAsc(), params.getSort().stream().map(StrUtil::toUnderlineCase).toArray(String[]::new))
     .page(page);
    return mpPage;
  }

  /**
   * 修改
   *
   * @param  job /
   * @return /
   */
  @Override
  public boolean mod(Job job) {
    Job oldJob = super.getById(job.getId());
    BeanUtil.copyProperties(job, oldJob, CopyOptions.create().ignoreNullValue());
    System.out.println("更新定时任务");
    return super.updateById(oldJob);
  }

  /**
  * 添加
  *
  * @param job /
  * @return /
  */
  @Override
  public boolean add(Job job) {
    System.out.println("添加定时任务");
    return super.save(job);
  }

  /**
   * 删除
   *
   * @param id /
   * @return /
   */
  @Override
  public boolean del(Serializable id) {
    System.out.println("删除定时任务");
    return super.removeById(id);
  }

  /**
  * 根据ID查询一个
  *
  * @param id /
  * @return /
  */
  @Transactional(readOnly = true)
  @Override
  public Job getOne(Serializable id) {
    return super.getById(id);
  }
}

