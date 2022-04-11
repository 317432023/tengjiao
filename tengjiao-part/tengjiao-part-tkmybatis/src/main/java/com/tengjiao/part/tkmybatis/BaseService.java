package com.tengjiao.part.tkmybatis;

import com.github.pagehelper.PageHelper;
import com.tengjiao.tool.indep.ReflectionTool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础Service实现类
 * @author kangtengjiao
 */
public abstract class BaseService<T,E> implements Service<T,E> {

    @Autowired
    protected MyMapper<T> myMapper;

    private Class<T> entityClass;

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init() {
        this.entityClass = ReflectionTool.getClassGenericType(getClass());
    }

    //
    // insert
    // ----------------------------------------------------------------------------------------------------
    @Override
    @Transactional(rollbackFor = Exception.class)
    public T insert(T record) {
        //insertWhoField(record);
        myMapper.insert(record);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<T> insert(List<T> recordList) {
        /*for (T t : recordList) {
            insertWhoField(t);
        }*/
        myMapper.insertList(recordList);
        return recordList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public T insertSelective(T record) {
        //insertWhoField(record);
        myMapper.insertSelective(record);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<T> insertSelective(List<T> recordList) {
        /*for (T t : recordList) {
            insertWhoField(t);
        }*/
        myMapper.insertList(recordList);
        return recordList;
    }

    //
    // update
    // ----------------------------------------------------------------------------------------------------
    @Override
    @Transactional(rollbackFor = Exception.class)
    public T update(T record) {
        //updateWhoField(record);
        int count = myMapper.updateByPrimaryKey(record);
        checkUpdate(record, count);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<T> update(List<T> recordList) {
        // Mapper暂未提供批量更新，此处循实现
        for(T record : recordList){
            int count = myMapper.updateByPrimaryKey(record);
            checkUpdate(record, count);
        }
        return recordList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public T updateSelective(T record) {
        //updateWhoField(record);
        int count = myMapper.updateByPrimaryKeySelective(record);
        checkUpdate(record, count);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<T> updateSelective(List<T> recordList) {
        // Mapper暂未提供批量更新，此处循实现
        for(T record : recordList){
            //updateWhoField(record);
            int count = myMapper.updateByPrimaryKeySelective(record);
            checkUpdate(record, count);
        }
        return recordList;
    }

    @Override
    public T updateByCondition(T record, Example condition) {
        //updateWhoField(record);
        int count = myMapper.updateByCondition(record, condition);
        checkUpdate(record, count);
        return record;
    }

    //
    // delete
    // ----------------------------------------------------------------------------------------------------
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteById(E id) {
        return myMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(E[] ids) {
        return myMapper.deleteByIds(StringUtils.join(ids, ","));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(T record) {
        return myMapper.delete(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(List<T> recordList) {
        int count = 0;
        for(T record : recordList){
            myMapper.delete(record);
            count++;
        }
        return count;
    }

    //
    // select
    // ----------------------------------------------------------------------------------------------------
    @Override
    public T selectById(E id) {
        T entity = null;
        try {
            entity = entityClass.newInstance();
            Field idField = ReflectionTool.getFieldByAnnotation(entityClass, Id.class);
            idField.set(entity, id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return myMapper.selectByPrimaryKey(entity);
    }

    @Override
    public T select(T record) {
        return myMapper.selectOne(record);
    }

    @Override
    public T select(String key, Object value) {
        T entity = null;
        try {
            entity = entityClass.newInstance();
            Field field = ReflectionTool.getField(entityClass, key);
            field.set(entity, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myMapper.selectOne(entity);
    }

    @Override
    public List<T> selectList(E[] ids) {
        if(ids == null || ids.length == 0) {
            return new ArrayList<>(0);
        }

        // 字符串添加双引号
        if(ids[0] instanceof String) {
            StringBuilder buf = new StringBuilder(320);
            for(E id : ids) {
                buf.append('\'').append(id).append('\'').append(',');
            }
            final int len = buf.length();
            if(len > 0) {
                buf.deleteCharAt(len - 1);
            }
            return myMapper.selectByIds(buf.toString());
        }

        return myMapper.selectByIds(StringUtils.join(ids, ","));
    }

    @Override
    public List<T> selectList(T record) {
        return myMapper.select(record);
    }

    @Override
    public List<T> selectList(String key, Object value) {
        T entity = null;
        try {
            entity = entityClass.newInstance();
            Field field = ReflectionTool.getField(entityClass, key);
            field.set(entity, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myMapper.select(entity);
    }

    @Override
    public List<T> selectList(T record, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return myMapper.select(record);
    }

    @Override
    public List<T> selectAll() {
        return myMapper.selectAll();
    }

    @Override
    public List<T> selectByCondition(Example condition) {
        return myMapper.selectByCondition(condition);
    }

    @Override
    public int count(T record) {
        return myMapper.selectCount(record);
    }

    /**
     * 检查乐观锁<br>
     * 更新失败时，抛出 UpdateFailedException 异常
     *
     * @param updateCount update,delete 操作返回的值
     */
    private void checkUpdate(T record, int updateCount) {
        if (record instanceof BaseEntity && updateCount == 0) {
            throw new RuntimeException();
        }
    }

//    /**
//     * 插入标准字段
//     */
//    private void insertWhoField(T record) {
//        if (record instanceof BaseEntity) {
//            BaseEntity entity = (BaseEntity) record;
//            entity.setCreateDate(new Date());
//            CustomUserDetails details = DetailsHelper.getUserDetails();
//            Long userId = Optional.ofNullable(details).map(CustomUserDetails::getUserId).orElse(-1L);
//            entity.setCreateBy(userId);
//            entity.setVersion(1L);
//        }
//    }
//
//    /**
//     * 更新标准字段
//     */
//    private void updateWhoField(T record) {
//        if (record instanceof BaseEntity) {
//            BaseEntity entity = (BaseEntity) record;
//            entity.setUpdateDate(new Date());
//            CustomUserDetails details = DetailsHelper.getUserDetails();
//            Long userId = Optional.ofNullable(details).map(CustomUserDetails::getUserId).orElse(-1L);
//            entity.setUpdateBy(userId);
//        }
//    }

}
