package com.tengjiao.comm.sms.service;

import com.tengjiao.comm.sms.model.CommSms;
import com.tengjiao.comm.sms.model.CommSmsDTO;
import com.tengjiao.comm.sms.model.CommSmsStatusDict;

import java.util.List;

/**
 * 短信操作
 * @author tengjiao
 * @description
 * @date 2021/9/7 16:44
 */
public interface ICommSmsService {

    /**
     * 推送一条短信信息到数据库或队列 用于发送
     * @param commSMSDTO
     */
    void pushOneForSend(CommSmsDTO commSMSDTO);

    /**
     * 从数据库或队列 提取最近的num条待发送的短信 去发送
     * @return
     */
    List<CommSms> fetchRecentlyToSend(Integer num);

    /**
     * 提交更新（主要是更新状态）
     * @param smsId
     * @param statusDict
     */
    void commit(Long smsId, CommSmsStatusDict statusDict);

    /**
     * 根据模板id取得 短信发送平台模板代码
     * @param templetId
     * @return
     */
    String getTempletCode(Integer templetId);
}
