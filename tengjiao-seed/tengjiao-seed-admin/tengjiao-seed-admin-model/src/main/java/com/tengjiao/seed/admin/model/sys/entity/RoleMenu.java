package com.tengjiao.seed.admin.model.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 角色菜单
 * </p>
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_role_menu")
@ApiModel(value="RoleMenu对象", description="角色菜单")
public class RoleMenu implements Serializable {

    private static final long serialVersionUID = -1732991075061917101L;

    @ApiModelProperty(value = "角色ID")
    private Integer rid;

    @ApiModelProperty(value = "菜单ID")
    private Integer mid;


}
