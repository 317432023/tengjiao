package com.tengjiao.assembly.sms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tengjiao.comm.sms.mapper.CommSmsMapper;
import com.tengjiao.comm.sms.mapper.CommSmsTempletMapper;
import com.tengjiao.comm.sms.model.CommSms;
import com.tengjiao.comm.sms.model.CommSmsDTO;
import com.tengjiao.comm.sms.model.CommSmsStatusDict;
import com.tengjiao.comm.sms.model.CommSmsTemplet;
import com.tengjiao.comm.sms.service.ICommSmsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author tengjiao
 * @description
 * @date 2021/9/8 11:20
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommSmsService implements ICommSmsService {

    private CommSmsMapper commSmsMapper;
    private CommSmsTempletMapper commSmsTempletMapper;

    public CommSmsService(CommSmsMapper commSmsMapper, CommSmsTempletMapper commSmsTempletMapper) {
        this.commSmsMapper = commSmsMapper;
        this.commSmsTempletMapper = commSmsTempletMapper;
    }

    @Override
    public void pushOneForSend(CommSmsDTO commSmsDTO) {

        CommSms entity = new CommSms();
        entity.setContent(commSmsDTO.getContent());
        entity.setMobile(commSmsDTO.getMobile());
        entity.setReceiver(commSmsDTO.getReceiver());
        entity.setTempletId(commSmsDTO.getTempletId());
        commSmsMapper.insert(entity);
    }

    @Override
    public List<CommSms> fetchRecentlyToSend(Integer integer) {
        if( integer == null || integer <=0 ) {
            integer = 10;
        }

        QueryWrapper<CommSms> qw = new QueryWrapper<>();
        qw.lambda()
          .eq(CommSms::getStatus, CommSmsStatusDict.INIT)
          // expiretime = createtime+duration < now() 即 createtime < now() - duration
          //.lt(CommSms::getCreateTime, DateTool.addMinutes(new Date(), -smsProperties.getSmsCodeDuration()))
          .orderByAsc(CommSms::getCreateTime)
          .last("limit " + integer);
        return commSmsMapper.selectList(qw);
    }

    @Override
    public void commit(Long aLong, CommSmsStatusDict commSmsStatusDict) {
        // 更新特定的字段
        /*
        CommSms commSms = new CommSms();
        commSms.setId(aLong);
        commSms.setStatus(commSmsStatusDict.typeCode);
        commSmsMapper.updateById(commSms);
        */

        UpdateWrapper<CommSms> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(CommSms::getId, aLong)
          .set(CommSms::getStatus, commSmsStatusDict.typeCode)
        ;
        commSmsMapper.update(null, updateWrapper); // entity 设置为null是关键
    }

    @Override
    public String getTempletCode(Integer integer) {

        CommSmsTemplet commSmsTemplet = commSmsTempletMapper.selectById(integer);

        return commSmsTemplet != null ? commSmsTemplet.getCode() : "";

    }
}
