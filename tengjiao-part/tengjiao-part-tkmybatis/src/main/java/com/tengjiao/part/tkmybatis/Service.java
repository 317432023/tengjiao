package com.tengjiao.part.tkmybatis;

import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Service 基础通用接口
 *
 */
public interface Service<T,E> {

    //
    // insert
    // ----------------------------------------------------------------------------------------------------
    /**
     * 保存一个实体，null的属性也会保存，不会使用数据库默认值
     */
    T insert(T record);

    /**
     * 批量插入，null的属性也会保存，不会使用数据库默认值
     */
    List<T> insert(List<T> recordList);

    /**
     * 保存一个实体，null的属性不会保存，会使用数据库默认值
     */
    T insertSelective(T record);

    /**
     * 批量插入，null的属性不会保存，会使用数据库默认值
     */
    List<T> insertSelective(List<T> recordList);

    //
    // update
    // ----------------------------------------------------------------------------------------------------
    /**
     * 根据主键更新实体全部字段，null值会被更新
     */
    T update(T record);

    /**
     * 批量更新，根据主键更新实体全部字段，null值会被更新
     */
    List<T> update(List<T> recordList);

    /**
     * 根据主键更新属性不为null的值
     */
    T updateSelective(T record);

    /**
     * 批量更新，根据主键更新属性不为null的值
     */
    List<T> updateSelective(List<T> recordList);

    /**
     * 通过条件更新
     */
    T updateByCondition(T record, Example condition);

    //
    // delete
    // ----------------------------------------------------------------------------------------------------
    /**
     * 根据主键逻辑删除
     *
     * @param id id不能为空
     */
    int deleteById(E id);

    /**
     * 根据主键逻辑删除多个实体，ID数组
     *
     * @param ids 类似[1,2,3]，不能为空
     */
    int delete(E[] ids);

    /**
     * 根据实体属性作为条件进行逻辑删除
     */
    int delete(T record);

    /**
     * 根据主键逻辑删除多个实体
     */
    int delete(List<T> recordList);

    //
    // select
    // ----------------------------------------------------------------------------------------------------
    /**
     * 根据主键查询
     *
     * @param id 不能为空
     */
    T selectById(E id);

    /**
     * 根据实体中的属性进行查询，只能有一个返回值，有多个结果是抛出异常
     *
     * @param record
     */
    T select(T record);

    /**
     * 根据字段和值查询 返回一个
     * 
     * @param key 不能为空
     * @param value 不能为空
     */
    T select(String key, Object value);


    /**
     * 根据主键字符串进行查询，！！！TODO 注意此方法有SQL注入的可能，待优化
     *
     * @param ids ID数组
     */
    List<T> selectList(E[] ids);

    /**
     * 根据实体中的属性值进行查询
     */
    List<T> selectList(T record);

    /**
     * 根据属性和值查询
     */
    List<T> selectList(String key, Object value);

    /**
     * 根据实体中的属性值进行分页查询
     */
    List<T> selectList(T record, int pageNum, int pageSize);

    /**
     * 查询全部结果
     */
    List<T> selectAll();

    /**
     * 根据条件查询
     * 
     * @param condition 条件
     */
    List<T> selectByCondition(Example condition);

    /**
     * 根据实体中的属性查询总数
     */
    int count(T record);

}
