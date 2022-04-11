package com.tengjiao.part.wx.oa.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.tengjiao.part.wx.oa.model.menu.ClickButton;
import com.tengjiao.part.wx.oa.model.menu.ViewButton;
import com.tengjiao.tool.indep.HttpTool;

/**
 * @author tengjiao
 * @description 自定义菜单创建<br>
 *     网页调试工具
 *     https://mp.weixin.qq.com/debug/cgi-bin/apiinfo?t=index&type=%E8%87%AA%E5%AE%9A%E4%B9%89%E8%8F%9C%E5%8D%95&form=%E8%87%AA%E5%AE%9A%E4%B9%89%E8%8F%9C%E5%8D%95%E5%88%9B%E5%BB%BA%E6%8E%A5%E5%8F%A3%20/menu/create
 *     {
 *      "button": [
 *          {
 *              "name": "博客",
 *              "type": "view",
 *              "url": "http://www.cuiyongzhi.com"
 *          },
 *          {
 *              "name": "菜单",
 *              "sub_button": [
 *                  {
 *                      "key": "text",
 *                      "name": "回复图文",
 *                      "type": "click"
 *                  },
 *                  {
 *                      "name": "博客",
 *                      "type": "view",
 *                      "url": "http://www.cuiyongzhi.com"
 *                  }
 *              ]
 *          },
 *          {
 *              "key": "text",
 *              "name": "回复图文",
 *              "type": "click"
 *          }
 *      ]
 * }
 * @date 2021/10/15 21:21
 */
public class MenuUtil {

    /**
     * 代码实现菜单的生成
     * @param globToken 全局票據
     */
    public static void createMenu(String globToken, JSONArray button) {

        JSONObject menujson = new JSONObject();
        menujson.set( "button" , button);

        String jsonString = menujson.toJSONString(2);

        String url= "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + globToken;

        try {
            String rs = HttpTool.postBody(url, jsonString, 3000);

        } catch (Exception e){
            System.out.println( "请求错误！" );
        }

    }

    public static void main(String[] args) {
        String accessToken = "xxxx-xxxx-xxxx";

        ClickButton cbt = new  ClickButton();
        cbt.setKey( "image" );
        cbt.setName( "回复图片" );
        cbt.setType( "click" );


        ViewButton vbt= new  ViewButton();
        vbt.setUrl( "http://www.cuiyongzhi.com" );
        vbt.setName( "博客" );
        vbt.setType( "view" );

        JSONArray sub_button= new JSONArray();
        sub_button.add(cbt);
        sub_button.add(vbt);


        JSONObject buttonOne= new  JSONObject();
        buttonOne.set( "name" ,  "菜单" );
        buttonOne.set( "sub_button" , sub_button);

        JSONArray button= new JSONArray();
        button.add(vbt);
        button.add(buttonOne);
        button.add(cbt);

        createMenu(accessToken, button);
    }

}
