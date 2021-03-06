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
     * ??????????????????
     * ?????????????????????????????? @Transactional ??????????????????????????????????????????????????????????????? Propagation.REQUIRE
     */
    @Override
    public void insertUser1() {
        SampleUser user = createUser("niu",0,"1000000");
        this.insertSelective(user);

        //this.insertUser2(); // this ?????????????????????????????? insertUser2 ????????????????????????????????????????????????????????????????????????????????????????????????

        SampleUserService selfObject = ((SampleUserService) AopContext.currentProxy());
        // ?????????????????????????????????????????????????????????????????????insertUser2
        selfObject.insertUser2();

        // true???????????????, false??????????????????
        boolean isProxy = AopUtils.isAopProxy(selfObject);
        // true
        System.out.println(isProxy);

        //??????
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

