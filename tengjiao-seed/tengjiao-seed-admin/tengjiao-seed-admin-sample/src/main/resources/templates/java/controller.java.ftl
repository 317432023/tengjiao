package ${package.Controller};

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tengjiao.tool.core.model.Result;
import com.tengjiao.tool.core.model.ResultGenerator;
import ${package.Entity}.${entity};
import ${package.Service}.${table.serviceName};
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Api;
import com.tengjiao.part.springmvc.ResponseResult;
import org.springframework.validation.annotation.Validated;
import com.tengjiao.seed.admin.common.LogRequired;
import com.tengjiao.part.mybatisplus.BaseDTO;

import java.io.Serializable;

/**
* ${table.comment!}管理
*
* @author ${author}
* @date ${date}
*/
@RestController
@ResponseResult
@RequestMapping("/${entity?lower_case}")
@Api(tags = "${table.comment!} ")
public class ${table.controllerName} {
  @Autowired
  private ${entity}Service ${entity?lower_case}Service;

  /**
   * <p>
   * 单表分页查询_[单列可排序NonCount]
   * </p>
   * 用于记录较少的表，大表查询禁止使用该方法！！！
   * @param current /
   * @param size /
   * @param params /
   * @return /
   */
  @PostMapping("/query/list/{current}/{size}")
  @ApiOperation("分页查询${table.comment!}_[单表单列可排序NonCount]")
  public Result page${entity}(@PathVariable Integer current, @PathVariable Integer size, @RequestBody BaseDTO params) {
    IPage<${entity}> ${entity?lower_case}Page = ${entity?lower_case}Service.page(new Page<>(current, size), params);
    return ResultGenerator.success(Dict.create().set("total", ${entity?lower_case}Page.getTotal()).set("records", ${entity?lower_case}Page.getRecords()));
  }

  @PutMapping("/mod")
  @ApiOperation("修改${table.comment!}")
  @LogRequired("修改${table.comment!}")
  public void mod${entity}(@Validated@RequestBody ${entity} ${entity?lower_case}) {
    ${entity?lower_case}Service.mod(${entity?lower_case});
  }

  @PostMapping("/add")
  @ApiOperation("添加${table.comment!}")
  @LogRequired("添加${table.comment!}")
  public void add${entity}(@Validated @RequestBody ${entity} ${entity?lower_case}) {
    ${entity?lower_case}Service.add(${entity?lower_case});
  }

  @DeleteMapping("/del/{id}")
  @ApiOperation("删除${table.comment!}")
  @LogRequired("删除${table.comment!}")
  public void del${entity}(@PathVariable Serializable id) {
    ${entity?lower_case}Service.del(id);
  }

  @GetMapping("/query/one/{id}")
  @ApiOperation("查询一个${table.comment!}")
  public ${entity} query${entity}ById(@PathVariable Serializable id) {
    return ${entity?lower_case}Service.getOne(id);
  }
}

