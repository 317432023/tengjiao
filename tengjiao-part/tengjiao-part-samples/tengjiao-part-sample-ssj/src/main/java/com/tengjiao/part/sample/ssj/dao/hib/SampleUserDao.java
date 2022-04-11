package com.tengjiao.part.sample.ssj.dao.hib;

import com.tengjiao.part.sample.ssj.entity.SampleUser;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author kangtengjiao
 */
@Repository("sampleUserDao")
public class SampleUserDao {

  private EntityManagerFactory entityManagerFactory;

  @PersistenceContext
  protected EntityManager entityManager;

  public SampleUserDao(EntityManagerFactory entityManagerFactory, EntityManager entityManager) {
    this.entityManagerFactory = entityManagerFactory;
    this.entityManager = entityManager;
  }

  protected Session getSession() {
    return entityManager.unwrap(Session.class);
  }

  public List<SampleUser> findByName(String username) {
    return getSession().createQuery("from SampleUser o where o.username = ?1").setParameter(1, username).list();
  }
  public List<SampleUser> findByName_new(String username) {
    return getSession().createQuery("from SampleUser o where o.username = :username").setParameter("username", username).list();
  }
}
