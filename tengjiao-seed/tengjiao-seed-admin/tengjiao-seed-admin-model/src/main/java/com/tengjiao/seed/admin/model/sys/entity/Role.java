package com.tengjiao.seed.admin.model.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.tengjiao.part.mybatisplus.LikeIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 系统角色表
 * </p>
 *
 * @author rise
 * @date 2020-11-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_role")
@ApiModel(value="Role对象", description="角色")
public class Role implements Serializable {

    private static final long serialVersionUID = 7908411430115279818L;

    @LikeIgnore
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "角色代码(唯一)")
    private String code;

    @ApiModelProperty(value = "角色名")
    private String name;

    @ApiModelProperty(value = "角色备注")
    private String remark;

    @LikeIgnore
    @ApiModelProperty(value = "是否禁用（0-启用,1-禁用）")
    private Integer disabled;

    @LikeIgnore
    @ApiModelProperty(value = "站点ID")
    private Integer stationId;

    @LikeIgnore
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @LikeIgnore
    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
