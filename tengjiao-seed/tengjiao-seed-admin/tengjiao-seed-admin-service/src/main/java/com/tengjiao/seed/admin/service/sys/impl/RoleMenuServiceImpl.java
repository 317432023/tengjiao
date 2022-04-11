package com.tengjiao.seed.admin.service.sys.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tengjiao.seed.admin.dao.sys.mapper.RoleMenuMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tengjiao.seed.admin.service.sys.RoleMenuService;
import com.tengjiao.seed.admin.model.sys.entity.RoleMenu;

/**
 * <p>
 * 角色菜单关系表 操作
 * </p>
 *
 * @author rise
 * @date 2021-02-02
 */
@Service("roleMenuService")
@Transactional(rollbackFor = Exception.class)
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

}

