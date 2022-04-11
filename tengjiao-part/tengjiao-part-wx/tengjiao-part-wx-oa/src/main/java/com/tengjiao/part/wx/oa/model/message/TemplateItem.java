package com.tengjiao.part.wx.oa.model.message;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TemplateItem {
    /**模板数据*/
    private String value; // eg. "恭喜你购买成功！"

    /**模板内容字体颜色，不填默认为黑色*/
    private String color; // eg. "#173177"
}
