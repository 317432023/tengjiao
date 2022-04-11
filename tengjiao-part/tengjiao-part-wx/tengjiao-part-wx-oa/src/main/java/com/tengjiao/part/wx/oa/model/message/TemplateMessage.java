package com.tengjiao.part.wx.oa.model.message;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class TemplateMessage {
    /**接收者openid*/
    private String touser;

    /**模板ID*/
    private String template_id;

    /**模板跳转链接（海外帐号没有跳转能力）*/
    private String url;

    /**跳小程序所需数据，不需跳小程序可不用传该数据*/
    private Miniprogram miniprogram;

    private Map<String, TemplateItem> data; // first | keyword(n) | remark
}
