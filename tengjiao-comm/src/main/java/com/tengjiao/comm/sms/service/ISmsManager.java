package com.tengjiao.comm.sms.service;

import com.tengjiao.comm.sms.model.CommSms;

/**
 * 短信平台对接
 * @author tengjiao
 * @description
 * @date 2021/9/8 10:43
 */
public interface ISmsManager {

    /**
     * 对接(三方)短信平台发送短信
     * @param commSms 短信对象记录
     * @return
     * @throws Exception
     */
    String send(CommSms commSms) throws Exception;

}
