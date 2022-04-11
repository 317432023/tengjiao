package com.tengjiao.seed.admin.security.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tengjiao.seed.admin.security.config.CustomGrantedAuthority;
import com.tengjiao.seed.admin.security.domain.AdminUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tengjiao.seed.admin.service.sys.AdminService;
import com.tengjiao.seed.admin.service.sys.RoleService;
import com.tengjiao.seed.admin.model.sys.entity.Admin;
import com.tengjiao.seed.admin.model.sys.entity.Role;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 管理员登录认证信息查询
 * AllArgsConstructor 自动生成构造器。当有构造器时，Spring容器将使用构造器实例化该Bean，所以属性不再需要标注Autowired
 * @author Administrator
 * @version 2020/11/17 10:02
 */
@Service
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private AdminService adminService;

  private RoleService roleService;

  @Transactional(readOnly = true)
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Admin admin = adminService.getOne(Wrappers.<Admin>lambdaQuery().eq(Admin::getUsername, username));
    if (admin == null) {
      throw new UsernameNotFoundException("用户不存在");
    }

    /*
    方式一、用户权限菜单列表，可以根据用户拥有的权限标识与如 @PreAuthorize("hasAuthority('sys:menu:view')") 标注的接口对比，决定是否可以调用接口
      Set<String> permissions = userService.findPermissions(username);
      List<GrantedAuthority> grantedAuthorities = permissions.stream().map(GrantedAuthorityImpl::new).collect(Collectors.toList());
    */

    /* 方式二、加载角色（利用角色控制权限），配合 http.withObjectPostProcessor 决定是否可以调用接口 */
    List<Role> roles = roleService.selectRoleByAdminId(admin.getId());
    Set<CustomGrantedAuthority> grantedAuthorities = roles.stream().map(r -> new CustomGrantedAuthority(r.getId().toString())).collect(Collectors.toSet());

    /* 取得UserDetails方式： UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); */
    AdminUserDetails userDetails = new AdminUserDetails();
    userDetails.setUsername(username);
    userDetails.setPassword(admin.getPassword());
    userDetails.setAuthorities(grantedAuthorities);

    userDetails.setId(admin.getId());
    userDetails.setAvatar(admin.getAvatar());
    userDetails.setNickname(admin.getNickname());
    return userDetails;
  }
}
