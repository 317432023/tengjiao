package com.tengjiao.part.sample.ssm.service.test;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tengjiao.part.sample.ssm.dao.mybatis.SampleUserMapper;
import com.tengjiao.part.sample.ssm.entity.SampleUser;
import com.tengjiao.part.sample.ssm.service.SampleUserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SampleUserServiceTests {

    @Before
    public void before() {}
    @After
    public void after() {}

    @Autowired
    private SampleUserService sampleUserService;
    @Autowired
    private SampleUserMapper sampleUserMapper;
    @Test
    public void testQuery() {
        System.out.println("【mybatis】: " + JSON.toJSONString( sampleUserService.findByUsername("jack")) );
    }

    @Test public void testPaginate() {
        // mybatis pagehelper 分页
        PageHelper.startPage(1, 2, "id desc");
        List<SampleUser> list = sampleUserMapper.selectAll();
        PageInfo<SampleUser> pageInfo = PageInfo.of(list);
        System.out.println(JSON.toJSON(pageInfo));

    }


    /**
     * 内嵌事务测试<br>
     */
    @Test public void testInsert() {
        sampleUserService.insertUser1();
    }

    /**
     * 乐观锁测试<br>
     *     抛出ObjectOptimisticLockingFailureException，错误信息是:<br>
     *  Row was updated or deleted by another transaction
     */
    @Test
    public void testOptimisticLocking() {

        Optional<SampleUser> opt1 = Optional.of( sampleUserMapper.selectByPrimaryKey(4L) );
        Optional<SampleUser> opt2 = Optional.of( sampleUserMapper.selectByPrimaryKey(4L) );
        opt1.ifPresent(user -> {
            user.setUsername("Amanda1");
            sampleUserMapper.updateByPrimaryKey(user);
        });
        opt2.ifPresent(user -> {
            user.setUsername("Amanda2");
            sampleUserMapper.updateByPrimaryKey(user);
        });
    }
}
