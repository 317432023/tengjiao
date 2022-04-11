package com.tengjiao.seed.admin.model.sys.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * BaseQueryVo
 * @author rise
 * @since 2020/12/30 18:53
 */
@Data
@ApiModel(value="BaseQueryVo 查询参数", description="基础查询参数[站点、日期...]")
public class BaseQueryVo {

  @ApiModelProperty(value = "站点ID")
  private Integer stationId;
}
