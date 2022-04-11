package com.tengjiao.part.wx.oa.util;

import cn.hutool.json.JSONObject;
import com.tengjiao.tool.indep.HttpTool;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 客服帐号管理<br>
 *     参考文档：https://developers.weixin.qq.com/doc/offiaccount/Customer_Service/Customer_Service_Management.html<br>
 *     https://developers.weixin.qq.com/doc/offiaccount/Customer_Service/Session_control.html<br>
 *     https://developers.weixin.qq.com/doc/offiaccount/Customer_Service/Obtain_chat_transcript.html
 * @author tengjiao
 * @description 必须先在公众平台官网为公众号设置微信号后才能使用该能力
 * @date 2021/10/15 19:50
 */
@Slf4j
public class CustomerServiceUtil {


    /**
     * 保存客服帐号 <br>
     *     POST https://api.weixin.qq.com/customservice/kfaccount/add?access_token=ACCESS_TOKEN
     * @param globToken 全局票据
     * @param kfJson 格式形如：{
     *      "kf_account" : "test1@test",
     *      "nickname" : "客服1",
     *      "password" : "pswmd5"
     * }
     * @param saveMode 0-添加, 1-更新
     * @return
     */
    public static int save(String globToken, String kfJson, int saveMode) {
        String requestUrl = "https://api.weixin.qq.com/customservice/kfaccount/add?access_token="+globToken;

        String reponseText = null;

        try {
            reponseText = HttpTool.postBody(requestUrl, kfJson, 3000);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return -1;
        }

        JSONObject map = new JSONObject(reponseText);

        int errorCode = map.getInt("errcode");
        String errorMsg = map.getStr("errmsg");
        log.error(errorMsg);

        return errorCode;
    }

    /**
     * 邀请绑定客服帐号<br>
     *     POST https://api.weixin.qq.com/customservice/kfaccount/inviteworker?access_token=ACCESS_TOKEN
     * @param globToken
     * @param kfAccount 完整客服帐号，格式为：帐号前缀@公众号微信号
     * @param wechatid 接收绑定邀请的客服微信号
     * @return
     */
    public static int inviteworker(String globToken, String kfAccount, String wechatid) {
        String requestUrl = "https://api.weixin.qq.com/customservice/kfaccount/inviteworker?access_token="+globToken;

        String reponseText = null;

        String inviteJson = "{\"kf_account\" : \""+kfAccount+"\",\"invite_wx\" : \""+wechatid+"\"}";

        try {
            reponseText = HttpTool.postBody(requestUrl, inviteJson, 3000);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return -1;
        }

        JSONObject map = new JSONObject(reponseText);

        int errorCode = map.getInt("errcode");
        String errorMsg = map.getStr("errmsg");
        log.error(errorMsg);

        return errorCode;
    }

    /**
     * 删除客服帐号 <br>
     *     POST https://api.weixin.qq.com/customservice/kfaccount/add?access_token=ACCESS_TOKEN
     * @param globToken 全局票据
     * @param kfAccount
     * @return
     */
    public static int del(String globToken, String kfAccount) {
        String requestUrl = "https://api.weixin.qq.com/customservice/kfaccount/del?access_token="+globToken;

        String reponseText = null;

        try {
            reponseText = HttpTool.postBody(requestUrl, "{\"kf_account\":\""+kfAccount+"\"}", 3000);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return -1;
        }

        JSONObject map = new JSONObject(reponseText);

        int errorCode = map.getInt("errcode");
        String errorMsg = map.getStr("errmsg");
        log.error(errorMsg);

        return errorCode;
    }

    /**
     * 设置公众号客服账号头像<br>
     *     POST/FORM http://api.weixin.qq.com/customservice/kfaccount/uploadheadimg?access_token=ACCESS_TOKEN&kf_account=KFACCOUNT 调用示例：使用curl命令，用FORM表单方式上传一个多媒体文件，curl命令的具体用法请自行了解
     * @param globToken 微信公众号全局票据
     * @param kfAccount 客户账号
     * @param filepath 文件完整路径
     * @return
     */
    public static int setAvatar(String globToken, String kfAccount, String filepath) {
        String requestUrl = "http://api.weixin.qq.com/customservice/kfaccount/uploadheadimg?access_token="+globToken+"&kf_account="+kfAccount;

        String reponseText = null;
        Map<String, String> fileMap = new HashMap<>();
        fileMap.put("media", filepath);
        try {
            // 相当于 curl -F "media=@<filepath>;filename=<从filepath中取得>" <requestUrl>
            reponseText = HttpTool.formUpload(requestUrl, null, fileMap, 3000, 5000);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return -1;
        }

        JSONObject map = new JSONObject(reponseText);

        int errorCode = map.getInt("errcode");
        String errorMsg = map.getStr("errmsg");
        log.error(errorMsg);


        return errorCode;
    }

    /**
     * 获取客服账号列表<br>
     *     GET https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token=ACCESS_TOKEN
     * @param globToken
     * @return 格式如：{
     *     "kf_list": [
     *         {
     *             "kf_account": "test1@test",
     *             "kf_nick": "ntest1",
     *             "kf_id": "1001",
     *             "kf_headimgurl": " http://mmbiz.qpic.cn/mmbiz/4whpV1VZl2iccsvYbHvnphkyGtnvjfUS8Ym0GSaLic0FD3vN0V8PILcibEGb2fPfEOmw/0"
     *         },
     *         {
     *             "kf_account": "test2@test",
     *             "kf_nick": "ntest2",
     *             "kf_id": "1002",
     *             "kf_headimgurl": " http://mmbiz.qpic.cn/mmbiz/4whpV1VZl2iccsvYbHvnphkyGtnvjfUS8Ym0GSaLic0FD3vN0V8PILcibEGb2fPfEOmw /0"
     *         },
     *         {
     *             "kf_account": "test3@test",
     *             "kf_nick": "ntest3",
     *             "kf_id": "1003",
     *             "kf_headimgurl": " http://mmbiz.qpic.cn/mmbiz/4whpV1VZl2iccsvYbHvnphkyGtnvjfUS8Ym0GSaLic0FD3vN0V8PILcibEGb2fPfEOmw /0"
     *         }
     *     ]
     * }
     */
    public static String getKflist(String globToken) {
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token="+globToken;

        return HttpTool.get(requestUrl ,3000, 5000);
    }
}
