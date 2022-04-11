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
 * 系统设置
 * </p>
 *
 * @author rise
 * @date 2021-03-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_config")
@ApiModel(value="Config对象", description="系统设置")
public class Config implements Serializable {

    private static final long serialVersionUID = -5465652333140055363L;

    @LikeIgnore
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @LikeIgnore
    @ApiModelProperty(value = "类别0:系统,9其他")
    private Integer type;

    @ApiModelProperty(value = "参数名称（中文）")
    private String title;

    @ApiModelProperty(value = "配置编码（唯一）")
    private String name;

    @ApiModelProperty(value = "参数值")
    private String value;

    @ApiModelProperty(value = "备注")
    private String remark;

    @LikeIgnore
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @LikeIgnore
    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
