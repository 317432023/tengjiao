package com.tengjiao.seed.admin.model.sys.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.tengjiao.seed.admin.model.sys.entity.Admin;

import java.util.List;

/**
 * AdminDO: Admin Domain Object 管理员 领域对象
 * @author Administrator
 * @since 2020/11/13 22:43
 */
@Data
@ApiModel(value="AdminDO 领域对象", description="系统管理员领域对象")
public class AdminDo extends Admin {
  private static final long serialVersionUID = -7046568959063567421L;

  // begin 新增的属性
  @ApiModelProperty(value = "站点名称")
  private String stationName;

  @ApiModelProperty(value = "角色ID列表")
  private List<Integer> roleIds;

  // end 新增的属性

}
