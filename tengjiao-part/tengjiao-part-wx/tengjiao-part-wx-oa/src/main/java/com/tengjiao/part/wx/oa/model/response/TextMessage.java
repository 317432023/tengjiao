package com.tengjiao.part.wx.oa.model.response;

/**
 * 文本消息消息体
 */
public class TextMessage extends BaseMessage {
    // 回复的消息内容
    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}