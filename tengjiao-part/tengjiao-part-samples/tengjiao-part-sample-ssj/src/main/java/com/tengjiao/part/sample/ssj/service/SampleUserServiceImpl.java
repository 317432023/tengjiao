package com.tengjiao.part.sample.ssj.service;

import com.tengjiao.part.sample.ssj.dao.hib.SampleUserDao;
import com.tengjiao.part.sample.ssj.dao.jpa.SampleUserRepository;
import com.tengjiao.part.sample.ssj.entity.SampleUser;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @author kangtengjiao
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SampleUserServiceImpl implements SampleUserService {
    @Autowired private SampleUserRepository sampleUserRepository;
    @Autowired private SampleUserDao sampleUserDao;

    @Transactional(readOnly = true)
    @Override
    public List<SampleUser> findByName_jpa(String username) {
        return sampleUserRepository.findByName(username);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SampleUser> findByName_hib(String username) {
        return sampleUserDao.findByName_new(username);
    }

    /**
     * 测试事务异常
     * 由于在类名上已经标注 @Transactional ，此方法可省略事务标注。它的默认传播级别为 Propagation.REQUIRE
     */
    @Override
    public void insertUser1(){
        SampleUser user = createUser("niu",0,"1000000");
        sampleUserRepository.save(user);

        //this.insertUser2(); // this 指向非代理对象，所以 insertUser2 开启新事务不会生效，因为必须是代理对象才会重新识别另一个方法标注

        SampleUserService selfObject = ((SampleUserService) AopContext.currentProxy());
        // 会将当前事务挂起，重新开启另一个新的事务来执行insertUser2
        selfObject.insertUser2();

        // true：是代理类, false：不是代理类
        boolean isProxy = AopUtils.isAopProxy(selfObject);
        // true
        System.out.println(isProxy);

        //异常
        int a = 10/0;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void insertUser2(){
        SampleUser user = createUser("xing",1,"1111111");
        sampleUserRepository.save(user);
    }

    private SampleUser createUser(String username, Integer sex, String nickname) {
        SampleUser user = new SampleUser();
        user.setUsername(username);
        user.setSex(sex);
        user.setPassword("$"+username);
        user.setNickname(nickname);
        return user;
    }


}
