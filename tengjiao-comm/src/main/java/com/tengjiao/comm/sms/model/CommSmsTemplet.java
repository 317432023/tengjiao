package com.tengjiao.comm.sms.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author tengjiao
 * @description
 * @date 2021/11/18 12:45
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("comm_sms_templet")
@ApiModel(value = "CommSMSTemplet对象", description = "短信模板表")
public class CommSmsTemplet implements Serializable {
    private static final long serialVersionUID = 5134896237611149949L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("模版类型0-通知 1-验证码 2-推广")
    private Integer type;

    @ApiModelProperty("模版CODE")
    private String code;

    @ApiModelProperty("对应平台1-阿里 2-腾讯")
    private Integer platform;

    @ApiModelProperty("备注")
    private String remark;

}
