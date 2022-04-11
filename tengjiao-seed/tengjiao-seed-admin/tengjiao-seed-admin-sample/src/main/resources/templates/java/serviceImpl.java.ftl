package ${package.ServiceImpl};

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tengjiao.part.mybatisplus.BaseDTO;
import com.tengjiao.part.mybatisplus.SqlUtil;
import ${package.Entity}.${entity};
import ${package.Service}.${table.serviceName};
import ${package.Mapper}.${table.mapperName};
import ${superServiceImplClassPackage};
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * <p>
 * ${table.comment!} 操作
 * </p>
 *
 * @author ${author}
 * @date ${date}
 */
@Slf4j
@Service("${entity?lower_case}Service")
@Transactional(rollbackFor = Exception.class)
public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}> implements ${table.serviceName} {

  /**
   * <p>
   * 单表分页查询_[单列可排序NonCount]
   * </p>
   * 用于记录较少的表，大表查询禁止使用该方法！！！
   * @param page   /
   * @param params /
   * @return /
   */
  @Transactional(readOnly = true)
  @Override
  public IPage<${entity}> page(Page<${entity}> page, BaseDTO params) {
    IPage<${entity}> mpPage = SqlUtil
     .likeAll(${entity}.class, query(), params.getText())
     .orderBy(params.getSort().size() > 0, params.isAsc(), params.getSort().stream().map(StrUtil::toUnderlineCase).toArray(String[]::new))
     .page(page);
    return mpPage;
  }

  /**
   * 修改
   *
   * @param  ${entity?lower_case} /
   * @return /
   */
  @Override
  public boolean mod(${entity}  ${entity?lower_case}) {
    ${entity} old${entity} = super.getById(${entity?lower_case}.getId());
    BeanUtil.copyProperties(${entity?lower_case}, old${entity}, CopyOptions.create().ignoreNullValue());
    return super.updateById(old${entity});
  }

  /**
   * 添加
   *
   * @param ${entity?lower_case} /
   * @return /
   */
  @Override
  public boolean add(${entity} ${entity?lower_case}) {
    //System.out.println("添加${table.comment!}成功");
    return super.save(${entity?lower_case});
  }

  /**
   * 删除
   *
   * @param id /
   * @return /
   */
  @Override
  public boolean del(Serializable id) {
    //System.out.println("删除${table.comment!}成功");
    return super.removeById(id);
  }

  /**
   * 根据ID查询一个
   *
   * @param id /
   * @return /
   */
  @Transactional(readOnly = true)
  @Override
  public ${entity} getOne(Serializable id) {
    return super.getById(id);
  }
}

