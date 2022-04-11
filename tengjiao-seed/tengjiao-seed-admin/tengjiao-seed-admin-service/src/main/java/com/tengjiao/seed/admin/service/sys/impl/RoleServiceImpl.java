package com.tengjiao.seed.admin.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tengjiao.part.mybatisplus.BaseDTO;
import com.tengjiao.part.mybatisplus.SqlUtil;
import com.tengjiao.part.redis.ModeDict;
import com.tengjiao.part.redis.RedisTool;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tengjiao.seed.admin.service.sys.RoleMenuService;
import com.tengjiao.seed.admin.service.sys.RoleService;
import com.tengjiao.seed.admin.model.sys.entity.Role;
import com.tengjiao.seed.admin.model.sys.entity.RoleMenu;
import com.tengjiao.seed.admin.dao.sys.mapper.RoleMapper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统角色
 * </p>
 *
 * @author rise
 * @date 2020-11-14
 */
@Slf4j
@Service("roleService")
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

  private RoleMenuService roleMenuService;
  private RedisTool redisTool;
  /**
   * 单表分页查询
   *
   * @param page   /
   * @param params /
   * @return /
   */
  @Transactional(readOnly = true)
  @Override
  public Page<Role> page(Page<Role> page, BaseDTO params) {
    return SqlUtil
     .likeAll(Role.class, query(), params.getText())
     .orderBy(params.getSort().size() > 0, params.isAsc(), params.getSort().stream().map(StrUtil::toUnderlineCase).toArray(String[]::new))
     .page(page);
  }


  /**
   * 编辑
   *
   * @param  role /
   * @return /
   */
  @Override
  public void mod(Role role) {
    Role oldRole = super.getById(role.getId());
    BeanUtil.copyProperties(role, oldRole, CopyOptions.create().ignoreNullValue());
    super.updateById(oldRole);
    System.out.println("更新系统角色表成功");
  }

  /**
  * 添加
  *
  * @param role /
  * @return /
  */
  @Override
  public void add(Role role) {
    super.save(role);
    System.out.println("添加系统角色表成功");
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
    System.out.println("删除系统角色表成功");
  }

  /**
  * 根据ID查询一个
  *
  * @param id /
  * @return /
  */
  @Override
  @Transactional(readOnly = true)
  public Role getOne(Serializable id) {
    return super.getById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Role> selectRoleByAdminId(Serializable adminId) {
    return baseMapper.selectRoleByAdminId(adminId);
  }


  /**
   * 根据角色ID查询角色有的菜单ID列表
   *
   * @param roleId 角色ID
   * @return 菜单IDl列表
   */
  @Override
  public List<Integer> getRoleMenus(Integer roleId) {
    return super.baseMapper.getRoleMenuIds(roleId);
  }

  /**
   * 根据角色ID更新角色有的菜单ID列表
   * 当角色拥有的权限菜单变化时，强制 menuService.selectAllMenuRoles() 存放的缓存做一次失效
   * @param menuIds 菜单ID列表
   * @param roleId  角色ID
   * @return /
   */
  @Override
  //@CacheEvict(allEntries = false, cacheNames = {"menu"}, key="'selectAllMenuRoles'")
  @Transactional(rollbackFor = Exception.class)
  public void modRoleMenus(Integer roleId, List<Integer> menuIds) {

    // 删除指定角色所有的权限
    Map<String,Object> map = new HashMap<>(1);
    map.put("rid", roleId);
    roleMenuService.removeByMap(map);

    // 批量插入
    roleMenuService.saveBatch(menuIds.stream().map(mid -> new RoleMenu(roleId, mid)).collect(Collectors.toList()));
    redisTool.deleteByPattern(MenuServiceImpl.CACHE_NAME+":*", ModeDict.APP);
  }
}

