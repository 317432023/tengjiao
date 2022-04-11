package com.tengjiao.part.wx.mp.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 小程序无限码请求参数体
 */
@Data
@Accessors(chain = true)
public class UnlimitQRcodeRequest implements Serializable {

    private static final long serialVersionUID = 5795779398803171491L;
    @NotEmpty
    private String scene;

    private String page;

    private Boolean check_path = true;

    private String env_version = "release";

    private Integer width = 430;

    private Boolean auto_color = false;

    private LineColor line_color;

    private Boolean is_hyaline;

}
