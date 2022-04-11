package com.tengjiao.seed.admin.service.sys.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tengjiao.part.mybatisplus.BaseDTO;
import com.tengjiao.part.mybatisplus.SqlUtil;
import com.tengjiao.seed.admin.model.sys.entity.Config;
import com.tengjiao.seed.admin.dao.sys.mapper.ConfigMapper;
import com.tengjiao.seed.admin.service.sys.ConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统设置 操作
 * </p>
 *
 * @author rise
 * @date 2021-03-18
 */
@Slf4j
@Service("configService")
@Transactional(rollbackFor = Exception.class)
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {

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
  public IPage<Config> page(Page<Config> page, BaseDTO params) {
    return SqlUtil
     .likeAll(Config.class, query(), params.getText())
     .orderBy(params.getSort().size() > 0, params.isAsc(), params.getSort().stream().map(StrUtil::toUnderlineCase).toArray(String[]::new))
     .page(page);
  }

  /**
   * 修改
   *
   * @param  config /
   * @return /
   */
  @Override
  public void mod(Config  config) {
    Config oldConfig = super.getById(config.getId());
    BeanUtil.copyProperties(config, oldConfig, CopyOptions.create().ignoreNullValue());
    super.updateById(oldConfig);
    System.out.println("更新系统设置成功");
  }

  /**
  * 添加
  *
  * @param config /
  * @return /
  */
  @Override
  public void add(Config config) {
    super.save(config);
    System.out.println("添加系统设置成功");
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
    System.out.println("删除系统设置成功");
  }

  /**
  * 根据ID查询一个
  *
  * @param id /
  * @return /
  */
  @Transactional(readOnly = true)
  @Override
  public Config getOne(Serializable id) {
    return super.getById(id);
  }
}

