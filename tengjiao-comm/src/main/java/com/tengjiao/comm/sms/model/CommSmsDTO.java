package com.tengjiao.comm.sms.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author tengjiao
 * @description
 * @date 2021/9/7 16:46
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CommSMSDTO对象", description = "短信信息")
public class CommSmsDTO implements Serializable {

    private static final long serialVersionUID = 3777555827704864714L;

    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "短信内容")
    private String content;
    @ApiModelProperty(value = "接收者")
    private Long receiver;
    @ApiModelProperty("模板id")
    private Integer templetId;

}
