package com.tengjiao.seed.admin.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tengjiao.part.mybatisplus.BaseDTO;
import com.tengjiao.part.mybatisplus.SqlUtil;
import com.tengjiao.seed.admin.dao.sys.mapper.LogMapper;
import com.tengjiao.seed.admin.model.sys.entity.Log;
import com.tengjiao.seed.admin.service.sys.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

import static java.util.stream.Collectors.toList;

/**
 * <p>
 * 系统日志 操作
 * </p>
 *
 * @author rise
 * @date 2020-11-14
 */
@Slf4j
@Service("logService")
@Transactional(rollbackFor = Exception.class)
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements LogService {

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
  public IPage<Log> page(Page<Log> page, BaseDTO params) {
    return SqlUtil
     .likeAll(Log.class, query(), params.getText())
     .orderBy(params.getSort().size() > 0, params.isAsc(), params.getSort().stream().map(StrUtil::toUnderlineCase).toArray(String[]::new))
     .page(page);
  }

  /**
   * 修改
   *
   * @param  log /
   * @return /
   */
  @Override
  public void mod(Log  log) {
    Log oldLog = super.getById(log.getId());
    BeanUtil.copyProperties(log, oldLog, CopyOptions.create().ignoreNullValue());
    super.updateById(oldLog);
    System.out.println("更新系统日志成功");
  }

  /**
  * 添加
  *
  * @param log /
  * @return /
  */
  @Override
  public void add(Log log){
    this.del(3);
    super.save(log);
    System.out.println("添加系统日志成功");
  }

  /**
   * 删除
   *
   * @param id /
   * @return /
   */
  @Override
  public void del(Serializable id) {
    super.removeById(id);
    System.out.println("删除系统日志成功");
  }

  /**
  * 根据ID查询一个
  *
  * @param id /
  * @return /
  */
  @Override
  public Log getOne(Serializable id) {
    return super.getById(id);
  }
}

