package com.tengjiao.seed.admin.service.sys.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tengjiao.part.mybatisplus.BaseDTO;
import com.tengjiao.part.mybatisplus.SqlUtil;
import com.tengjiao.seed.admin.dao.sys.mapper.StationMapper;
import com.tengjiao.seed.admin.service.sys.StationService;
import com.tengjiao.seed.admin.model.sys.entity.Station;
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
 * 系统站点 操作
 * </p>
 *
 * @author rise
 * @date 2021-02-02
 */
@Slf4j
@Service("stationService")
@Transactional(rollbackFor = Exception.class)
public class StationServiceImpl extends ServiceImpl<StationMapper, Station> implements StationService {

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
  public IPage<Station> page(Page<Station> page, BaseDTO params) {
    return SqlUtil
      .likeAll(Station.class, query(), params.getText())
      .orderBy(params.getSort().size() > 0, params.isAsc(), params.getSort().stream().map(StrUtil::toUnderlineCase).toArray(String[]::new))
      .page(page);
  }

  /**
   * 修改
   *
   * @param  station /
   * @return /
   */
  @Override
  public void mod(Station  station) {
    Station oldStation = super.getById(station.getId());
    BeanUtil.copyProperties(station, oldStation, CopyOptions.create().ignoreNullValue());
    super.updateById(oldStation);
    System.out.println("更新系统站点成功");
  }

  /**
   * 添加
   *
   * @param station /
   * @return /
   */
  @Override
  public void add(Station station) {
    super.save(station);
    System.out.println("添加系统站点成功");
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
    System.out.println("删除系统站点成功");
  }

  /**
   * 根据ID查询一个
   *
   * @param id /
   * @return /
   */
  @Transactional(readOnly = true)
  @Override
  public Station getOne(Serializable id) {
    return super.getById(id);
  }
}

