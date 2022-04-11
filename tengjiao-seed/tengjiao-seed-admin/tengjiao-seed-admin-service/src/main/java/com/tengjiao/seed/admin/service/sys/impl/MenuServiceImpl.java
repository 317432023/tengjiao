package com.tengjiao.seed.admin.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.tengjiao.part.redis.ModeDict;
import com.tengjiao.part.redis.RedisTool;
import com.tengjiao.seed.admin.model.security.pojo.RouteDo;
import com.tengjiao.seed.admin.service.sys.MenuService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tengjiao.seed.admin.dao.sys.mapper.MenuMapper;
import com.tengjiao.seed.admin.model.security.pojo.MenuVo;
import com.tengjiao.seed.admin.model.sys.entity.Menu;
import com.tengjiao.seed.admin.model.sys.pojo.MenuRolesDo;
import com.tengjiao.seed.admin.model.sys.pojo.MenuTreeVo;
import com.tengjiao.seed.admin.comm.TreeUtil;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import static com.tengjiao.seed.admin.comm.Constants.ADMINISTRATOR_ID;

/**
 * <p>
 * 系统菜单表 操作
 * </p>
 *
 * @author rise
 * @date 2020-11-14
 */
@Service("menuService")
@Transactional(rollbackFor = Exception.class)
//@CacheConfig(cacheNames = MenuServiceImpl.CACHE_NAME)
@AllArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
  public static final String CACHE_NAME = "menu";
  public static String getCacheKey(String method, String param) {
    final String cacheKey = CACHE_NAME + ":" + method + ((param != null && !"".equals(param)) ? "-"+param:"");
    return cacheKey;
  }

  private RedisTool redisTool;

  /**
   * 菜单列表
   * @return /
   */
  @Transactional(readOnly = true)
  @Override
  //@Cacheable(key = "#root.methodName")
  public List<Menu> listAll() {
    //return lambdaQuery().orderByAsc(Menu::getSortNum, Menu::getPid).list();
    final String cacheKey = getCacheKey(Thread.currentThread().getStackTrace()[1].getMethodName(), "");
    Object object = redisTool.get(cacheKey, ModeDict.APP);
    if(object != null) {
      return (List<Menu>)object;
    }
    List<Menu> menuList = lambdaQuery().orderByAsc(Menu::getSortNum, Menu::getPid).list();
    redisTool.set(cacheKey, menuList, ModeDict.APP);
    return menuList;
  }

  /**
   * 编辑
   * @param  menu /
   * @return /
   */
  @Override
  //@CacheEvict(allEntries = true)
  public void mod(Menu menu) {
    Menu oldMenu = super.getById(menu.getId());
    BeanUtil.copyProperties(menu, oldMenu, CopyOptions.create().ignoreNullValue());
    super.updateById(oldMenu);
    redisTool.deleteByPattern(CACHE_NAME+":*", ModeDict.APP);
  }

  /**
  * 添加
  *
  * @param menu /
  * @return /
  */
  @Override
  //@CacheEvict(allEntries = true)
  public void add(Menu menu) {
    super.save(menu);
    redisTool.deleteByPattern(CACHE_NAME+":*", ModeDict.APP);
  }

  /**
   * 删除
   *
   * @param id /
   * @return /
   */
  @Override
  //@CacheEvict(allEntries = true)
  public void del(Serializable id) {
    removeChildren(Lists.newArrayList(id));
    redisTool.deleteByPattern(CACHE_NAME+":*", ModeDict.APP);
  }

  /**
  * 根据ID查询一个
  *
  * @param id /
  * @return /
  */
  @Override
  //@Cacheable
  public Menu getOne(Serializable id) {
    //return super.getById(id);
    final String cacheKey = getCacheKey(Thread.currentThread().getStackTrace()[1].getMethodName(), id.toString());
    Object object = redisTool.get(cacheKey, ModeDict.APP);
    if(object != null) {
      return (Menu)object;
    }
    Menu menu = super.getById(id);
    redisTool.set(cacheKey, menu, ModeDict.APP);
    return menu;
  }


  /**
   * 列出所有菜单包含角色
   * @return
   */
  @Override
  //@Cacheable(key = "#root.methodName")
  public List<MenuRolesDo> selectAllMenuRoles() {
    //return baseMapper.selectAllMenuRoles();
    final String cacheKey = getCacheKey(Thread.currentThread().getStackTrace()[1].getMethodName(), null);
    Object object = redisTool.get(cacheKey, ModeDict.APP);
    if(object != null) {
      return (List<MenuRolesDo>)object;
    }
    List<MenuRolesDo> menuRolesDoList = baseMapper.selectAllMenuRoles();
    redisTool.set(cacheKey, menuRolesDoList, ModeDict.APP);
    return menuRolesDoList;
  }

  /**
   * 加载所有路由
   * @return
   */
  @Override
  //@Cacheable(key = "#root.methodName")
  public List<RouteDo> loadRoutes() {
    return baseMapper.loadRoutes();
  }

  /**
   * 根据用户ID加载路由
   * @param adminId
   * @return
   */
  @Override
  //@Cacheable(key = "#root.methodName+#root.args[0]")
  public List<RouteDo> loadRoutesByAdminId(Long adminId) {
    if(adminId.longValue() == ADMINISTRATOR_ID) {
      return this.loadRoutes();
    }
    return baseMapper.loadRoutesByAdminId(adminId);
  }

  /**
   * 根据角色IDs加载路由
   * @param roleIds
   * @return
   */
  @Override
  //@Cacheable(key = "#root.methodName+#root.args[0]")
  public List<RouteDo> loadRoutesByRoleIds(Integer[] roleIds) {
    return baseMapper.loadRoutesByRoleIds(roleIds);
  }

  /**
   * 根据角色IDs加载菜单
   * @param roleIds
   * @return
   */
  @Override
  //@Cacheable(key = "#root.methodName+#root.args[0]")
  public List<MenuVo> loadMenus(Integer[] roleIds) {
    return baseMapper.loadMenusByRoleIds(roleIds);
  }

  /**
   * 查询菜单树
   * @param excludesPerm 是否排除权限，默认不排除，也就是返回完整的树
   * @return
   */
  @Override
  //@Cacheable(key = "#root.methodName+#root.args[0]")
  public List<MenuTreeVo> menuTree(Boolean excludesPerm) {
    final String cacheKey = getCacheKey(Thread.currentThread().getStackTrace()[1].getMethodName(),
            excludesPerm != null ? excludesPerm.toString():"");
    Object object = redisTool.get(cacheKey, ModeDict.APP);
    if(object != null) {
      return (List<MenuTreeVo>)object;
    }
    List<MenuTreeVo> list = TreeUtil.getTree(super.baseMapper.menuTree(excludesPerm));
    redisTool.set(cacheKey, list, ModeDict.APP);
    return list;
  }

  private void removeChildren(List<Serializable> pids) {
    if (pids.size() == 0) {
      return;
    }
    removeByIds(pids);
    List<Serializable> ids = lambdaQuery().select(Menu::getId).in(Menu::getPid, pids).list().stream().map(Menu::getId).collect(Collectors.toList());
    removeChildren(ids);
  }
}

