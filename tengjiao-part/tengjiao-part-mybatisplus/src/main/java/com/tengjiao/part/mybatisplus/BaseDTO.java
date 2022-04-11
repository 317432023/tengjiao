package com.tengjiao.part.mybatisplus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName BaseDto
 * @Description 通用DTO查询参数
 * @Author kangtengjiao
 * @Date 2020/11/14 12:58
 * @Version V1.0
 */
@Setter
@Getter
@NoArgsConstructor
@ApiModel(value="BaseDto查询参数对象", description="通用查询参数")
public class BaseDTO {
  /**
   * 排序字段列表
   */
  @ApiModelProperty("排序字段列表")
  List<String> sort= new ArrayList();
  /**
   * 模糊查询文本（全字段模糊，去除静态和标记有@LikeIgnore注解）
   */
  @ApiModelProperty(value="模糊查询文本",notes="全字段模糊，去除静态和标记有@LikeIgnore注解")
  String text;

  /**
   * 排序方式(true:asc,  false:desc)
   */
  @ApiModelProperty(value="排序方式",notes="true:asc,  false:desc")
  boolean asc;
}
