package com.tengjiao.part.sample.ssm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import com.tengjiao.part.sample.ssm.dao.mybatis.SampleUserMapper;
import com.tengjiao.part.sample.ssm.entity.SampleUser;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class SampleUserServiceImpl implements SampleUserService {

    @Resource
    private SampleUserMapper sampleUserMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return sampleUserMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(SampleUser record) {
        return sampleUserMapper.insert(record);
    }

    @Override
    public int insertOrUpdate(SampleUser record) {
        return sampleUserMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(SampleUser record) {
        return sampleUserMapper.insertOrUpdateSelective(record);
    }

    @Override
    public int insertSelective(SampleUser record) {
        return sampleUserMapper.insertSelective(record);
    }

    @Transactional(readOnly = true)
    @Override
    public SampleUser selectByPrimaryKey(Long id) {
        return sampleUserMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(SampleUser record) {
        return sampleUserMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(SampleUser record) {
        return sampleUserMapper.updateByPrimaryKey(record);
    }

    @Override
    public int updateBatch(List<SampleUser> list) {
        return sampleUserMapper.updateBatch(list);
    }

    @Override
    public int batchInsert(List<SampleUser> list) {
        return sampleUserMapper.batchInsert(list);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SampleUser> findByUsername(String username) {
        return sampleUserMapper.findByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SampleUser> selectAll() {
        return sampleUserMapper.selectAll();
    }

    @Transactional(readOnly = true)
    @Override
    public PageInfo<SampleUser> selectAllwithPage(int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return new PageInfo<>(sampleUserMapper.selectAll());
    }
    /**
     * 测试事务异常
     * 由于在类名上已经标注 @Transactional ，此方法可省略事务标注。它的默认传播级别为 Propagation.REQUIRE
     */
    @Override
    public void insertUser1() {
        SampleUser user = createUser("niu",0,"1000000");
        this.insertSelective(user);

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
        this.insertSelective(user);
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

