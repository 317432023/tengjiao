package com.tengjiao.part.sample.ssj.service;


import com.tengjiao.part.sample.ssj.entity.SampleUser;

import java.util.List;

public interface SampleUserService {
  List<SampleUser> findByName_jpa(String username);
  List<SampleUser> findByName_hib(String username);
  void insertUser1();
  void insertUser2();
}
