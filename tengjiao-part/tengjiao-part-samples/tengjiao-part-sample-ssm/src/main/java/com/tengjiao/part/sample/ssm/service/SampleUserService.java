package com.tengjiao.part.sample.ssm.service;

import java.io.IOException;
import java.util.List;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tengjiao.part.sample.ssm.entity.SampleUser;

public interface SampleUserService {


    int deleteByPrimaryKey(Long id);

    int insert(SampleUser record);

    int insertOrUpdate(SampleUser record);

    int insertOrUpdateSelective(SampleUser record);

    int insertSelective(SampleUser record);

    SampleUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SampleUser record);

    int updateByPrimaryKey(SampleUser record);

    int updateBatch(List<SampleUser> list);

    int batchInsert(List<SampleUser> list);

    public List<SampleUser> findByUsername(String username);
    public List<SampleUser> selectAll();
    public PageInfo<SampleUser> selectAllwithPage(int page, int pageSize);

    void insertUser1();
    void insertUser2();
}

