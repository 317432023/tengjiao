package com.tengjiao.seed.admin.model.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 系统日志
 * </p>
 *
 * @author rise
 * @date 2020-11-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_log")
@ApiModel(value="Log对象", description="系统日志")
public class Log implements Serializable {

    private static final long serialVersionUID = -4404532865649141177L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "ip地址")
    private String ip;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "操作")
    private String opName;

    @ApiModelProperty(value = "花费时间")
    private Integer spendTime;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "站点ID")
    private Integer stationId;


}
