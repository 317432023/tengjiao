package com.tengjiao.part.jpa;

import com.tengjiao.tool.indep.BeanTool;
import com.tengjiao.tool.indep.DateTool;
import com.tengjiao.tool.indep.model.PageParam;
import com.tengjiao.tool.indep.model.PageVO;
import com.tengjiao.tool.indep.model.SystemException;
import com.tengjiao.tool.indep.sql.MysqlTool;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 基础 Service 与 JPA-Hibernate 强相关<br>
 *   <pre>严格意义上说该类是一个 DAO ，为了方便JPA项目服务层对象使用，直接继承它去使用 </pre>
 *
 * @author rise
 * @date 2020/06/06
 */
@Service
public class BaseService {

  private final Logger logger = LogManager.getLogger(BaseService.class);

  private EntityManagerFactory entityManagerFactory;
  @PersistenceContext protected EntityManager entityManager;

  public BaseService(EntityManagerFactory entityManagerFactory, EntityManager entityManager) {
    this.entityManagerFactory = entityManagerFactory;
    this.entityManager = entityManager;
  }

  /**
   * 取得hibernate session
   * <br>
   *     不推荐使用
   * @return
   */
  protected Session getSession() {
    return entityManager.unwrap(Session.class);
  }

  /**
   * 填充日期查询条件至predicates
   * @param propertyName
   * @param root
   * @param predicates
   * @param cb
   * @param startDate 开始日期 yyyy-MM-dd 格式
   * @param endDate 结束日期 yyyy-MM-dd 格式
   */
  protected void fillDateRangeCond(String propertyName, Root<?> root, List<Predicate> predicates, CriteriaBuilder cb, String startDate, String endDate) {
    if (StringUtils.isNotBlank(startDate)){
      predicates.add(cb.greaterThanOrEqualTo(root.get(propertyName), DateTool.parse(startDate, DateTool.Patterns.DATE)));
    }
    if (StringUtils.isNotBlank(endDate)){
      predicates.add(cb.lessThan(root.get(propertyName), DateUtils.addDays(DateTool.parse(endDate, DateTool.Patterns.DATE),1)));
    }
  }

  /**
   * 本地SQL查询
   * @param sql
   * @param params
   * @param clazz 转换为目标VO对象类型
   * @return
   */
  public List sqlDataVO(String sql, Map<String, Object> params, Class<?> clazz) {
    return sqlLimitVO(sql, params, clazz, -1, -1);
  }

  private NativeQuery _makeNativeQuery(String sql, Map<String, Object> params, ResultTransformer transformer) {
    NativeQuery query = getSession().createNativeQuery(sql);
    query = query.unwrap(NativeQueryImpl.class);

    if(transformer != null) {
      query.setResultTransformer(transformer);
    }

    for (Map.Entry<String, Object> entry : params.entrySet()) {
      query.setParameter(entry.getKey(), entry.getValue());
    }
    return query;
  }
  private NativeQuery makeNativeQuery(String sql, Map<String, Object> params) {
    return _makeNativeQuery(sql, params, null);
  }
  private NativeQuery makeNativeQuery(String sql, Map<String, Object> params, Class<?> clazz) {
    return _makeNativeQuery(sql, params, clazz != null ? Transformers.aliasToBean(clazz) : null);
  }
  private NativeQuery makeNativeQueryForMap(String sql, Map<String, Object> params) {
    return _makeNativeQuery(sql, params, Transformers.ALIAS_TO_ENTITY_MAP);
  }

  /**
   * 本地SQL查询
   * @param sql
   * @param params
   * @param clazz 转换为目标VO对象类型
   * @return
   */
  public Object sqlVO(String sql, Map<String, Object> params, Class<?> clazz) {

    logger.debug("Hibernate Native SQL: {}", sql);

    return makeNativeQuery(sql, params, clazz).uniqueResult();
  }
  /**
   * 本地SQL查询
   * @param sql
   * @param params
   * @return 返回的是一个Object[]数组，里面包含按顺序各个字段的值
   */
  public Object sqlVO(String sql, Map<String, Object> params) {

    logger.debug("Hibernate Native SQL: {}", sql);

    return makeNativeQuery(sql, params).uniqueResult();
  }
  /**
   * 本地SQL查询
   * @param sql
   * @param params
   * @return
   */
  public Object sqlMap(String sql, Map<String, Object> params) {

    logger.debug("Hibernate Native SQL: {}", sql);

    return makeNativeQueryForMap(sql, params).uniqueResult();
  }
  /**
   * 本地SQL查询
   * @param sql
   * @param params
   * @return
   */
  public Integer sqlCount(String sql, Map<String, Object> params) {
    String countSql = MysqlTool.countSql(sql);

    logger.debug("Hibernate Native SQL: {}", countSql);

    BigInteger bigInteger = (BigInteger) makeNativeQuery(countSql, params, null).uniqueResult();

    return bigInteger.intValue();
  }

  /**
   * 本地SQL查询
   * @param sql
   * @param params
   * @param clazz 转换为目标VO对象类型
   * @param page
   * @param limit
   * @return 未反序列化的结果集对象
   */
  public List sqlLimitVO(String sql, Map<String, Object> params, Class<?> clazz, int page, int limit) {

    sql = page > 0 && limit > 0?MysqlTool.limitSqlPage(sql, page, limit):sql;

    logger.debug("Hibernate Native SQL: {}", sql);

    return makeNativeQuery(sql, params, clazz).getResultList();
  }

  /**
   * 本地SQL分页查询
   * @param sql
   * @param params
   * @param clazz 转换为目标VO对象类型
   * @param page
   * @param limit
   * @return
   */
  public PageVO sqlPageVO(String sql, Map<String, Object> params, Class<?> clazz, int page, int limit) {
    List data = sqlLimitVO(sql, params, clazz, page, limit);
    Integer count = this.sqlCount(sql, params);
    PageVO<?> pageVO = new PageVO();

    return pageVO
      .setTotal(count)
      .setRecords(data)
      .setPage(page)
      .setLimit(limit);
  }

  /**
   * 本地SQL分页查询
   * @param sql
   * @param pageLimit
   * @param clazz 转换为目标VO对象类型
   * @return
   */
  public PageVO sqlPageVO(String sql, PageParam pageLimit, Class<?> clazz) {
    Map<String, Object> params;
    try {
      params = BeanTool.Introspect.objectToMap(pageLimit);
    } catch (Exception e) {
      throw SystemException.create("param convert error");
    }

    // 删除分页属性，该从pageLimit直接get取得
    Integer page = (Integer)params.remove("page");
    Integer limit = (Integer)params.remove("limit");

    return sqlPageVO(sql, params, clazz, page, limit);
  }

}
