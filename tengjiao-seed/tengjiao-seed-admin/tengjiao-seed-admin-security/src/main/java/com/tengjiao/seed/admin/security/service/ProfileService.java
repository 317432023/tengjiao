package com.tengjiao.seed.admin.security.service;

import java.io.Serializable;

/**
 * @ClassName ProfileService
 * @Description 个人相关操作_业务接口
 * @Author Administrator
 * @Date 2021/3/17 22:25
 * @Version V1.0
 */
public interface ProfileService {
  /**
   * 修改密码
   * @param id 用户id
   * @param password 当前密码
   * @param newPassword 新密码
   */
  void modifyPassword(Serializable id, String password, String newPassword);
}

