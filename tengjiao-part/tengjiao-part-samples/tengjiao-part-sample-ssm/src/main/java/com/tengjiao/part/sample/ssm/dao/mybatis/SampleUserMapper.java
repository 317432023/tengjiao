package com.tengjiao.part.sample.ssm.dao.mybatis;

import com.tengjiao.part.sample.ssm.entity.SampleUser;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SampleUserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SampleUser record);

    int insertOrUpdate(SampleUser record);

    int insertOrUpdateSelective(SampleUser record);

    int insertSelective(SampleUser record);

    SampleUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SampleUser record);

    int updateByPrimaryKey(SampleUser record);

    int updateBatch(List<SampleUser> list);

    int batchInsert(@Param("list") List<SampleUser> list);

    List<SampleUser> findByUsername(@Param("username")String username);
    List<SampleUser> selectAll();



}