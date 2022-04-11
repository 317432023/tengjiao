package ${package.Service};

import ${package.Entity}.${entity};
import ${superServiceClassPackage};

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tengjiao.part.mybatisplus.BaseDTO;

import java.io.Serializable;

/**
 * ${table.comment!}操作
 *
 * @author ${author}
 * @date ${date}
 */
public interface ${table.serviceName} extends ${superServiceClass}<${entity}> {

  /* 单表分页查询_[单列可排序NonCount] */
  IPage<${entity}> page(Page<${entity}> page, BaseDTO params);

  boolean add(${entity} ${entity?lower_case});

  boolean del(Serializable id);

  boolean mod(${entity} ${entity?lower_case});

  ${entity} getOne(Serializable id);
}
