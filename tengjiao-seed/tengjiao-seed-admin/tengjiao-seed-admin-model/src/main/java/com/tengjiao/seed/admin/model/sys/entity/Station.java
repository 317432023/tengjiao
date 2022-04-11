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
 * 系统站点
 * </p>
 *
 * @author rise
 * @date 2021-02-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_station")
@ApiModel(value="Station对象", description="系统站点")
public class Station implements Serializable {

    private static final long serialVersionUID = -5728015077918513676L;

    @LikeIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "分站名称")
    private String name;

    @ApiModelProperty(value = "分站域名")
    private String domain;

    @ApiModelProperty(value = "客服QQ")
    private String kfQq;

    @LikeIgnore
    @ApiModelProperty(value = "区域码")
    private Integer regionId;

    @ApiModelProperty(value = "地名")
    private String regionName;

    @LikeIgnore
    @ApiModelProperty(value = "是否禁用")
    private Integer disabled;

    @LikeIgnore
    @ApiModelProperty(value = "过期时间")
    private Date expireTime;

    @LikeIgnore
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


}
