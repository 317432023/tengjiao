package com.tengjiao.part.wx.oa.model;

import java.util.List;

/**
 * 三方网站授权微信用户信息
 * @author tengjiao
 * @description
 * @date 2021/10/16 19:06
 */
public class SnsUserInfo extends AbsUserInfo {

    /** SNS 用户特权信息 */
    private List<String> privilege;

    public List<String> getPrivilege() {
        return privilege;
    }

    public void setPrivilege(List<String> privilege) {
        this.privilege = privilege;
    }

}
