package com.tengjiao.seed.admin.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tengjiao.part.mybatisplus.BaseDTO;
import com.tengjiao.part.mybatisplus.SqlUtil;
import com.tengjiao.seed.admin.comm.IdGeneratorSnowflake;
import com.tengjiao.seed.admin.dao.sys.mapper.AdminMapper;
import com.tengjiao.seed.admin.model.sys.entity.Admin;
import com.tengjiao.seed.admin.model.sys.pojo.AdminDo;
import com.tengjiao.seed.admin.model.sys.pojo.AdminQueryVo;
import com.tengjiao.seed.admin.service.sys.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * <p>
 * 系统管理员表 服务实现类
 * </p>
 *
 * @author rise
 * @since 2020-11-13
 */
@Service("adminService")
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

  private IdGeneratorSnowflake idGeneratorSnowflake;

  private AdminMapper adminMapper;

  /**
   * 单表分页查询[单列可排序]
   *
   * @param page   /
   * @param params /
   * @return /
   */
  @Transactional(readOnly = true)
  @Override
  public IPage<Admin> page(Page<Admin> page, BaseDTO params) {
    return SqlUtil
      .likeAll(Admin.class, query(), params.getText())
      .orderBy(params.getSort().size() > 0, params.isAsc(), params.getSort().stream().map(StrUtil::toUnderlineCase).toArray(String[]::new))
      .page(page);
  }

  /**
   * 支持多表分页查询[多列不排序]
   * @param page
   * @param wrapper
   * @return
   */
  @Transactional(readOnly = true)
  @Override
  public IPage<AdminDo> page(Page page, QueryWrapper<Admin> wrapper) {
    return adminMapper.page(page, wrapper);
  }

  /**
   * 支持多表列表查询
   * @param adminVo
   * @return
   */
  @Transactional(readOnly = true)
  @Override
  public List<AdminDo> list(AdminQueryVo adminVo) {
    return adminMapper.list(adminVo);
  }

  /**
   * 多表列表查询(不支持分页)
   * @param adminVo
   * @return
   */
  @Transactional(readOnly = true)
  @Override
  public List<AdminDo> listWithRoles(AdminQueryVo adminVo) {
    return baseMapper.listWithRoles(adminVo);
  }

  @Override
  public void add(Admin entity) {
    entity.setId(idGeneratorSnowflake.snowflakeId());
    baseMapper.insert(entity);
  }

  @Override
  public void del(Serializable id) {
    baseMapper.deleteById(id);
  }

  @Override
  public void mod(Admin entity) {
    entity.setId(null);
    Admin oldEntity = baseMapper.selectById(entity.getId());
    BeanUtil.copyProperties(entity, oldEntity, CopyOptions.create().ignoreNullValue());
    baseMapper.updateById(oldEntity);
  }

  @Transactional(readOnly = true)
  @Override
  public Admin getOne(Serializable id) {
    Admin entity = baseMapper.selectById(id);
    entity.setPassword(null);
    return entity;
  }

  // 以上为套路代码

  // 以下为特定需求代码

  @Override
  public void saveRecord(AdminDo adminDo) {

    Long aid = adminDo.getId();
    if(aid == null) { // 新增用户
      Admin admin = new Admin();
      BeanUtil.copyProperties(adminDo, admin);
      // 雪花算法生成ID
      aid = idGeneratorSnowflake.snowflakeId();
      admin.setId(aid);
      baseMapper.insert(admin);
    }else { // 更新用户
      Admin admin = baseMapper.selectById(aid);
      BeanUtil.copyProperties(adminDo, admin);
      baseMapper.updateById(admin);
    }

    // 删除所有角色信息
    List<Integer> rids = adminDo.getRoleIds();
    adminMapper.deleteAdminRoleByAid(aid);
    // 重新入库角色信息
    if(rids != null && !rids.isEmpty())
    {
      adminMapper.insertAdminRoleIds(aid, rids);
    }
  }

  @Transactional(readOnly = true)
  @Override
  public Admin findByUsername(String username) {
    return super.getOne(Wrappers.<Admin>lambdaQuery().eq(Admin::getUsername, username));
  }

  @Transactional(readOnly = true)
  @Override
  public Set<String> findPermissions(String username) {
    return null;
  }

}
