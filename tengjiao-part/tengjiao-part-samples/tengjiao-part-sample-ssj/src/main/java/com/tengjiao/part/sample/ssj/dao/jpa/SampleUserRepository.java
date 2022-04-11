package com.tengjiao.part.sample.ssj.dao.jpa;

import com.tengjiao.part.jpa.BaseRepository;
import com.tengjiao.part.sample.ssj.entity.SampleUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author kangtengjiao
 */
public interface SampleUserRepository extends BaseRepository<SampleUser, Long> {

  /**
   * 返回列表全部
   * @return
   */
  @Override
  List<SampleUser> findAll();

  /**
   * sql带名称条件分页查询
   * @param username
   * @param pageable
   * @return
   */
  @Query(value = "SELECT * FROM sample_user WHERE username = ?1"
    , countQuery = "SELECT count(*) FROM sample_user WHERE username = ?1"
    , nativeQuery = true)
  Page<SampleUser> findByNamePageable(String username, Pageable pageable);

  /**
   * hql带条件列表查询
   * @param username
   * @return
   */
  @Query(value = "SELECT u FROM SampleUser u WHERE u.username like %:username%")
  List<SampleUser> findByName(@Param("username") String username);

}
