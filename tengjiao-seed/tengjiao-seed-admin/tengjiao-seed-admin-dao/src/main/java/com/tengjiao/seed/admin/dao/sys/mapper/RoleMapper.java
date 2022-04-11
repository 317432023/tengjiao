package com.tengjiao.seed.admin.dao.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.tengjiao.seed.admin.model.sys.entity.Role;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 系统角色表 Mapper 接口
 * </p>
 *
 * @author rise
 * @date 2020-11-14
 */
public interface RoleMapper extends BaseMapper<Role> {
  /**
   * 查询管理员角色
   * @param aid 管理员ID
   * @return /
   */
  List<Role> selectRoleByAdminId(@Param("aid") Serializable aid);

  List<Integer> getRoleMenuIds(@Param("rid") Serializable roleId);
}
