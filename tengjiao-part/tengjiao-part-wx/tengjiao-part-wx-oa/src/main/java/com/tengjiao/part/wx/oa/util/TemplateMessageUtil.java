package com.tengjiao.part.wx.oa.util;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.tengjiao.part.wx.oa.model.message.TemplateMessage;
import com.tengjiao.tool.indep.HttpTool;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO 公众号主动推送模板消息<br>
 *     文档 https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Template_Message_Interface.html
 * @author tengjiao
 * @description
 * @date 2021/10/17 11:34
 */
@Slf4j
public class TemplateMessageUtil {

    /**
     * 发送模板消息<br>
     * 消息格式：<br>
     *  POST https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN
     *  {
     *            "touser":"OPENID",
     *            "template_id":"ngqIpbwh8bUfcSsECmogfXcV14J0tQlEpBO27izEYtY",
     *            "url":"http://weixin.qq.com/download",
     *            "miniprogram":{
     *              "appid":"xiaochengxuappid12345",
     *              "pagepath":"index?foo=bar"
     *            },
     *            "data":{
     *                    "first": {
     *                        "value":"恭喜你购买成功！",
     *                        "color":"#173177"
     *                    },
     *                    "keyword1":{
     *                        "value":"巧克力",
     *                        "color":"#173177"
     *                    },
     *                    "keyword2": {
     *                        "value":"39.8元",
     *                        "color":"#173177"
     *                    },
     *                    "keyword3": {
     *                        "value":"2014年9月22日",
     *                        "color":"#173177"
     *                    },
     *                    "remark":{
     *                        "value":"欢迎再次购买！",
     *                        "color":"#173177"
     *                    }
     *            }
     *        }
     * 消息响应格式<br>
     * {
     *     "errcode":0,
     *      "errmsg":"ok",
     *      "msgid":200228332
     *   }
     */
    public static boolean sendMessage(String globToken, String requestBody) {
        final String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+globToken;
        final String respTxt = HttpTool.postBody(url, requestBody, 3);
        if( !JSONUtil.isJsonObj(respTxt) ) {
            log.error("网络异常");
            return false;
        }
        JSON json = JSONUtil.parse(respTxt);
        Integer errcode = (Integer)json.getByPath("errcode");
        return errcode == 0;
    }

    public static boolean sendMessage(String globToken, TemplateMessage message) {
        return sendMessage(globToken, JSONUtil.toJsonStr(message));
    }
}
