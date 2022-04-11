package com.tengjiao.seed.admin.model.sys.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * AdminQueryDTO 管理员 查询参数
 * @author Administrator
 * @since 2020/11/13 18:53
 */
@Data
@ApiModel(value="AdminQueryVo 查询参数", description="管理员查询参数")
public class AdminQueryVo extends BaseQueryVo {
  @ApiModelProperty(value = "用户ID")
  private Long id;
  @ApiModelProperty(value = "用户名")
  private String username;
  @ApiModelProperty(value = "昵称")
  private String nickname;
  @ApiModelProperty(value = "邮箱")
  private String email;
  @ApiModelProperty(value = "手机")
  private String phone;
  @ApiModelProperty(value = "部门ID")
  private Integer deptId;
  @ApiModelProperty(value = "是否禁用")
  private Integer disabled;
}
