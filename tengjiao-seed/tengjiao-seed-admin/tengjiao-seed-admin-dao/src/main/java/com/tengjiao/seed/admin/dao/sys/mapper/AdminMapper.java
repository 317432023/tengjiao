package com.tengjiao.seed.admin.dao.sys.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.tengjiao.seed.admin.model.sys.entity.Admin;
import com.tengjiao.seed.admin.model.sys.pojo.AdminDo;
import com.tengjiao.seed.admin.model.sys.pojo.AdminQueryVo;

import java.util.List;

/**
 * <p>
 * 系统管理员表 Mapper 接口
 * </p>
 *
 * @author rise
 * @since 2020-11-13
 */
public interface AdminMapper extends BaseMapper<Admin> {

  IPage<AdminDo> page(Page page, @Param(Constants.WRAPPER) QueryWrapper<Admin> wrapper);
  List<AdminDo> list(@Param("dto") AdminQueryVo dto);
  List<AdminDo> listWithRoles(@Param("dto") AdminQueryVo dto);

  void deleteAdminRoleByAid(@Param("aid") Long aid);
  void insertAdminRoleIds(@Param("aid") Long aid, @Param("roleIds") List<Integer> roleIds);
}
