package com.tengjiao.part.sample.sstkmybatis.service;

import com.tengjiao.part.tkmybatis.BaseService;
import com.tengjiao.part.sample.sstkmybatis.dao.tkmybatis.SampleUserMapper;
import com.tengjiao.part.sample.sstkmybatis.entity.SampleUser;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @ClassName SampleUserServiceImpl
 * @Description TODO
 * @Author Administrator
 * @Date 2021/5/22 16:26
 * @Version V1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SampleUserServiceImpl extends BaseService<SampleUser, Long> implements SampleUserService {

  private SampleUserMapper sampleUserMapper;

  public SampleUserServiceImpl(SampleUserMapper sampleUserMapper) {
    this.sampleUserMapper = sampleUserMapper;
  }

  @Transactional(readOnly = true)
  @Override
  public List<SampleUser> findByUsername(String username) {
    Condition condition = new Condition(SampleUser.class);
    Example.Criteria criteria = condition.createCriteria();
    criteria.orEqualTo("username", username);
    //return super.selectByCondition(condition);
    return sampleUserMapper.selectByCondition(condition);
  }


  /**
   * 测试事务异常
   * 由于在类名上已经标注 @Transactional ，此方法可省略事务标注。它的默认传播级别为 Propagation.REQUIRE
   */
  @Override
  public void insertUser1(){
    SampleUser user = createUser("niu",0,"1000000");
    super.insertSelective(user);

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
    super.insertSelective(user);
  }

  private SampleUser createUser(String username, Integer sex, String nickname) {
    SampleUser user = new SampleUser();
    user.setUsername(username);
    user.setSex(sex);
    user.setPassword("$"+username);
    user.setNickname(nickname);
    return user;
  }

  // 增加业务方法
  // ..



}

