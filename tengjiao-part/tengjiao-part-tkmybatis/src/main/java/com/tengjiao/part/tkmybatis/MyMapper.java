package com.tengjiao.part.tkmybatis;

import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.ConditionMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.special.InsertListMapper;
import tk.mybatis.mapper.entity.Example;

/**
 *
 * BaseMapper
 *
 * @author kangtengjiao
 */
public interface MyMapper<T> extends BaseMapper<T>, ConditionMapper<T>, IdsMapper<T>, InsertListMapper<T> {
  /*

     delete
     deleteByPrimaryKey
     updateByPrimaryKey
     updateByPrimaryKeySelective
     updateByCondition
     updateByConditionSelective

     仅有以上这些方法在执行时会更新乐观锁字段的值或者使用乐观锁的值作为查询条件
     在使用乐观锁时，由于通用 Mapper 是内置的实现，不是通过 拦截器 方式实现的，因此当执行上面支持的方法时，
     如果版本不一致，那么执行结果影响的行数可能就是 0。这种情况下也不会报错！
     所以在 Java6,7中使用时，你需要自己在调用方法后进行判断是否执行成功。
     在 Java8+ 中，可以通过默认方法来增加能够自动报错（抛异常）的方法：


   */

  static int check(int result, String message) {
    if(result == 0){
      throw new RuntimeException(message);
    }
    return result;
  }

  default int deleteWithVersion(T t){
    int result = delete(t);
    return check(result,"删除失败!");
  }
  default int deleteByPrimaryKeyWithVersion(T t){
    int result = deleteByPrimaryKey(t);
    return check(result,"删除失败!");
  }

  default int updateByPrimaryKeyWithVersion(T t){
    int result = updateByPrimaryKey(t);
    return check(result,"更新失败!");
  }
  default int updateByPrimaryKeySelectiveWithVersion(T t){
    int result = updateByPrimaryKeySelective(t);
    return check(result,"更新失败!");
  }
  default int updateByConditionWithVersion(T t, Example example){
    int result = updateByCondition(t, example);
    return check(result,"更新失败!");
  }
  default int updateByConditionSelectiveWithVersion(T t, Example example){
    int result = updateByConditionSelective(t, example);
    return check(result,"更新失败!");
  }
}
