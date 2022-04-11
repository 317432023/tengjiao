package com.tengjiao.part.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;

/**
 * 包含批量插入、更新的基本接口
 * @author kangtengjiao
 * @param <T> 实体类
 * @param <ID> 主键类型
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends
  JpaRepository<T, ID>,
  JpaSpecificationExecutor<T>,
  PagingAndSortingRepository<T, ID> {

    /**
     * 批量保存
     * @param itabl 集合对象
     * @param <S> 实体类
     * @return
     */
    <S extends T> Iterable<S> batchSave(Iterable<S> itabl);

    /**
     * 批量更新
     * @param itabl 集合对象
     * @param <S> 实体类
     * @return
     */
    <S extends T> Iterable<S> batchUpdate(Iterable<S> itabl);

}