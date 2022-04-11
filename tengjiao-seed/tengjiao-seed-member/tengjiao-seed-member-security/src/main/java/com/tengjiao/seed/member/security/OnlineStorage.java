package com.tengjiao.seed.member.security;

import com.alibaba.fastjson.JSON;
import com.tengjiao.part.redis.ModeDict;
import com.tengjiao.part.redis.RedisTool;
import com.tengjiao.tool.indep.model.RC;
import com.tengjiao.tool.indep.model.SystemException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 在线信息 存取
 * @author tengjiao
 * @description
 * @date 2021/8/25 1:30
 */
@Component
@AllArgsConstructor
public class OnlineStorage {
    private RedisTool redisTool;
    private HttpServletRequest request;

    public boolean put(String clientToken, OnlineInfo onlineInfo) {

        Assert.isTrue(!StringUtils.isEmpty(clientToken), "400, not allowed empty key of clientToken");

        final String storeToken = SessionUtils.getStoreToken(clientToken);
        final String storeString = JSON.toJSONString(onlineInfo);

        SessionModel model = SessionUtils.getSession();
        model.setLoginStore(onlineInfo);

        long duration = TerminalType.getByTypeCode( model.getTerminal()==null?0:model.getTerminal() ).duration;
        if( duration == 0 ) {
            duration = onlineInfo.getPlatform() > 0 ? TerminalType.WeChat_Mini.duration : TerminalType.PC.duration;
        }

        // 存储登录信息 关联 该令牌
        redisTool.setString(storeToken, storeString, ModeDict.APP_GROUP, duration);
        return true;
    }
    public boolean put(OnlineInfo onlineInfo) {

        final String clientToken = getClientToken(request);

        Assert.isTrue(!StringUtils.isEmpty(clientToken), "401, not authenticated.");

        return put(clientToken, onlineInfo);
    }

    public OnlineInfo get(String clientToken, boolean force) {

        final String storeToken = SessionUtils.getStoreToken(clientToken);
        final String storeString = redisTool.getString(storeToken, ModeDict.APP_GROUP);

        if(!StringUtils.isEmpty(storeString)) {
            return JSON.parseObject(storeString, OnlineInfo.class);
        }
        if(!force) {
            return null;
        }
        throw SystemException.create().setCode(RC.NOT_AUTHENTICATED);
    }

    public OnlineInfo get(boolean force) {
        OnlineInfo onlineInfo = SessionUtils.getOnlineInfo();
        if(onlineInfo != null) {
            return onlineInfo;
        }
        final String clientToken = request.getHeader(Constants.ServiceHeaders.TOKEN_KEY);
        return get(clientToken, force);
    }


    public void remove(String clientToken) {
        final String storeToken = SessionUtils.getStoreToken(clientToken);
        redisTool.del(ModeDict.APP_GROUP, storeToken);
    }

    public void remove() {
        String clientToken = request.getHeader(Constants.ServiceHeaders.TOKEN_KEY);
        this.remove(clientToken);
    }

    public String getClientToken(HttpServletRequest request) {
        String clientToken = SessionUtils.getToken();
        if(StringUtils.isEmpty(clientToken)) {
            clientToken = request.getHeader(Constants.ServiceHeaders.TOKEN_KEY);
        }
        return clientToken;
    }
}
