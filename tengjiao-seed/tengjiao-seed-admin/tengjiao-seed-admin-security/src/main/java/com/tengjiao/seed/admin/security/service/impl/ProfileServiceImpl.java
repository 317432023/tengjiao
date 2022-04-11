package com.tengjiao.seed.admin.security.service.impl;

import com.tengjiao.part.springmvc.SpringContextHolder;
import com.tengjiao.seed.admin.dao.sys.mapper.AdminMapper;
import com.tengjiao.seed.admin.model.sys.entity.Admin;
import com.tengjiao.seed.admin.security.service.ProfileService;
import com.tengjiao.tool.indep.model.SystemException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * @ClassName ProfileServiceImpl
 * @Description 个人相关操作_业务方法
 * @Author Administrator
 * @Date 2021/3/17 22:25
 * @Version V1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
public class ProfileServiceImpl implements ProfileService {

  private AdminMapper adminMapper;

  @Override
  public void modifyPassword(Serializable id, String password, String newPassword) {

    BCryptPasswordEncoder encoder = SpringContextHolder.getBean(BCryptPasswordEncoder.class);

    // 写法一：旧的写法
    Admin admin = adminMapper.selectById(id);

    if( encoder.matches( password, admin.getPassword() ) ) {

      admin.setPassword(encoder.encode(newPassword));

      adminMapper.updateById(admin);

      return;
    }

    // 写法二：java8 最新写法
    /*
    Admin admin = adminService.lambdaQuery().select(Admin::getId, Admin::getPassword).eq(Admin::getId, id).one();

    //匹配旧密码
    if ( encoder.matches( password, admin.getPassword() ) ) {

      adminService.lambdaUpdate().eq(Admin::getId, admin.getId()).set(Admin::getPassword, encoder.encode(newPassword)).update();

      return;

    }
    */

    throw SystemException.create("原密码不正确");

  }



}
