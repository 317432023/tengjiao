package com.tengjiao.comm.sms.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author tengjiao
 * @description
 * @date 2021/9/7 16:46
 */
@TableName(value = "comm_sms")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CommSMS对象", description = "短信表")
public class CommSms implements Serializable {
    private static final long serialVersionUID = -6705782665752049375L;

    private Long id;

    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "短信内容")
    private String content;
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @ApiModelProperty(value = "接收者")
    private Long receiver;
    @ApiModelProperty(value = "状态 0待发送 1发送中 2发送成功 3发送失败 4取消发送")
    private Integer status;
    @ApiModelProperty("模板id")
    private Integer templetId;
}
