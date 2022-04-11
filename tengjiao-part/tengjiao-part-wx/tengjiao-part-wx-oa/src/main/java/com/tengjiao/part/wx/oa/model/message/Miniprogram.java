package com.tengjiao.part.wx.oa.model.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Miniprogram {
    /**所需跳转到的小程序appid（该小程序appid必须与发模板消息的公众号是绑定关联关系，暂不支持小游戏）*/
    private String appid; //eg. "xiaochengxuappid12345"

    /**所需跳转到小程序的具体页面路径，支持带参数,（示例index?foo=bar），要求该小程序已发布，暂不支持小游戏*/
    private String pagepath; // eg. "index?foo=bar"
}
