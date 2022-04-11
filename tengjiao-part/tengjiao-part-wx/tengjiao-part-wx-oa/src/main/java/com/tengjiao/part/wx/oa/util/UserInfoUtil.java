package com.tengjiao.part.wx.oa.util;

import cn.hutool.json.JSONObject;
import com.tengjiao.part.wx.oa.model.AbsUserInfo;
import com.tengjiao.part.wx.oa.model.GlobUserInfo;
import com.tengjiao.part.wx.oa.model.SnsUserInfo;
import com.tengjiao.tool.indep.HttpTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取微信用户信息工具类
 * @author tengjiao
 * @description
 * @date 2021/10/16 18:18
 */
public class UserInfoUtil {

    private static Logger log = LoggerFactory.getLogger(UserInfoUtil.class);

    /**
     * 通过全局票据和openid获取微信用户信息<br>
     *     GET https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
     * @param globToken 全局票据
     * @param openid 微信用户openid（与本公众号对应）
     * @return
     */
    public static GlobUserInfo getGlobUserInfo(String globToken, String openid) {
        final String requestUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token="+globToken+"&openid="+openid+"&lang=zh_CN";
        String responseText = null;
        try {
            responseText = HttpTool.get(requestUrl, 2000, 3000);
        } catch (Exception e) {
            log.error("获取微信用户信息失败", e);
            return null;
        }
        log.info("获取用户信息 => ", responseText);
        JSONObject jsonObject = new JSONObject(responseText);
        if( jsonObject == null || jsonObject.isEmpty() ) {
            log.error("获取用户信息失败，返回结果不是有效的json格式");
            return null;
        }
        if( jsonObject.containsKey("errcode") ) {
            Integer errorCode = jsonObject.getInt("errcode");
            String errorMsg = jsonObject.getStr("errmsg");
            log.error("获取用户信息失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            return null;
        }

        GlobUserInfo globUserInfo = new GlobUserInfo();
        // 提取用户信息
        pickUserInfo(globUserInfo, jsonObject);
        globUserInfo.setSubscribe(jsonObject.getInt("subscribe"));
        globUserInfo.setSubscribe_time(jsonObject.getLong("subscribe_time"));
        globUserInfo.setRemark(jsonObject.getStr("remark"));

        return globUserInfo;
    }

    /**
     * 通过网页授权获取用户信息
     *     GET https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
     * @param snsToken 网页授权接口调用凭证
     * @param openid 微信用户标识
     * @return SnsUserInfo
     */
    public static SnsUserInfo getSnsUserInfo(String snsToken, String openid) {

        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/sns/userinfo?access_token="+snsToken+"&openid="+openid;

        // 通过网页授权获取用户信息
        String responseText = null;
        try{
            responseText = HttpTool.get(requestUrl, 3000, 5000);
        }catch(Exception e){
            log.error("请求用户信息失败 errmsg:{}", e.getMessage());
            return null;
        }
        SnsUserInfo snsUserInfo = null;
        JSONObject jsonObject = new JSONObject(responseText);
        if (null != jsonObject) {
            try {
                snsUserInfo = new SnsUserInfo();
                // 提取用户信息
                pickUserInfo(snsUserInfo, jsonObject);
                // 用户特权信息
                snsUserInfo.setPrivilege(
                  jsonObject.getJSONArray("privilege")
                    .toList(String.class));
            } catch (Exception e) {
                snsUserInfo = null;
                int errorCode = jsonObject.getInt("errcode");
                String errorMsg = jsonObject.getStr("errmsg");
                log.error("获取用户信息失败 errcode:{} errmsg:{}", errorCode, errorMsg);
                return null;
            }
        }
        return snsUserInfo;
    }

    /**
     * 从返回对象中提取微信用户信息
     * @param userInfo
     * @param jsonObject
     */
    private static void pickUserInfo(AbsUserInfo userInfo, JSONObject jsonObject) {
        // 用户的标识
        userInfo.setOpenid(jsonObject.getStr("openid"));
        userInfo.setUnionid(jsonObject.containsKey("unionid") ? jsonObject
          .getStr("unionid") : null);
        // 昵称
        userInfo.setNickname(jsonObject.getStr("nickname"));
        // 性别（1是男性，2是女性，0是未知）
        userInfo.setSex(jsonObject.getInt("sex"));
        // 用户所在国家
        userInfo.setCountry(jsonObject.getStr("country"));
        // 用户所在省份
        userInfo.setProvince(jsonObject.getStr("province"));
        // 用户所在城市
        userInfo.setCity(jsonObject.getStr("city"));
        // 用户头像
        userInfo.setHeadimgurl(jsonObject.getStr("headimgurl"));
        // 语言
        userInfo.setLanguage(jsonObject.getStr("language"));
    }



}
