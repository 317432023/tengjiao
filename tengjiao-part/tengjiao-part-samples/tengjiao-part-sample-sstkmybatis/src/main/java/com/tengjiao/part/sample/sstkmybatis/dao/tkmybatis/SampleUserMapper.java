package com.tengjiao.part.sample.sstkmybatis.dao.tkmybatis;


import com.tengjiao.part.tkmybatis.MyMapper;
import com.tengjiao.part.sample.sstkmybatis.entity.SampleUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName SampleUserMapper
 * @Description SampleUser通用DAO
 * @Author Administrator
 * @Date 2021/5/22 15:46
 * @Version V1.0
 */
@Mapper
public interface SampleUserMapper extends MyMapper<SampleUser> {

}
