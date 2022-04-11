package com.tengjiao.part.sample.ssj.service.test;

import com.alibaba.fastjson.JSON;
import com.tengjiao.part.sample.ssj.dao.jpa.SampleUserRepository;
import com.tengjiao.part.sample.ssj.entity.SampleUser;
import com.tengjiao.part.sample.ssj.service.SampleUserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.criteria.Predicate;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SampleUserServiceTests {

    @Before
    public void before() {}
    @After
    public void after() {}

    @Autowired
    SampleUserService sampleUserService;
    @Autowired
    SampleUserRepository sampleUserRepository;

    @Test
    public void testQuery() {
        System.out.println("【hib      】: " + JSON.toJSONString( sampleUserService.findByName_hib("jack")) );
        System.out.println("【jpa      】: " + JSON.toJSONString( sampleUserService.findByName_jpa("amanda")) );
    }

    @Test public void testPaginate() {
        // hibernate 分页
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(0, 2, sort);
        Page<SampleUser> demoUserPage = sampleUserRepository.findAll((root, query, cb)->query.where(new Predicate[0]).getRestriction(), pageable);
        System.out.println(JSON.toJSONString(demoUserPage));

    }

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
        Optional<SampleUser> opt1 = sampleUserRepository.findById(4L);
        Optional<SampleUser> opt2 = sampleUserRepository.findById(4L);
        opt1.ifPresent(user -> {
            user.setUsername("Amanda1");
            sampleUserRepository.save(user);
        });
        opt2.ifPresent(user -> {
            user.setUsername("Amanda2");
            sampleUserRepository.save(user);
        });
    }
}
