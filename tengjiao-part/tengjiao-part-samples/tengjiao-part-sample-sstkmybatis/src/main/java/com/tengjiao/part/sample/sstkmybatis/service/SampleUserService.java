package com.tengjiao.part.sample.sstkmybatis.service;

import com.tengjiao.part.tkmybatis.Service;
import com.tengjiao.part.sample.sstkmybatis.entity.SampleUser;

import java.util.List;

/**
 * @ClassName SampleUserService
 * @Description TODO
 * @Author Administrator
 * @Date 2021/5/22 16:24
 * @Version V1.0
 */
public interface SampleUserService extends Service<SampleUser, Long> {
  // 增加业务方法
  // ...
  List<SampleUser> findByUsername(String username);
  void insertUser1();
  void insertUser2();
}
