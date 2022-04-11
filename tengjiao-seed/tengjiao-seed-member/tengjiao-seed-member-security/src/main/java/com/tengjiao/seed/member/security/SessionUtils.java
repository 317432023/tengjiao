package com.tengjiao.seed.member.security;

import com.tengjiao.tool.indep.model.RC;
import com.tengjiao.tool.indep.model.SystemContext;
import com.tengjiao.tool.indep.model.SystemException;

/**
 * @author tengjiao
 * @create 2021/8/23 15:01
 */
public class SessionUtils {
    public static SessionModel getSession() {
        return (SessionModel) SystemContext.get(SessionModel.class);
    }
    public static String getToken() {
        return getSession().getToken();
    }
    public static String getStoreToken(String clientToken) {
        return Constants.ServiceHeaders.TOKEN_KEY + ":" + clientToken;
    }
    public static String getStoreToken() {
        return Constants.ServiceHeaders.TOKEN_KEY + ":" + getToken();
    }
    public static String getStationId() {
        return getSession().getStationId();
    }
    public static OnlineInfo getOnlineInfo() {
        SessionModel model = getSession();
        return model != null ? model.getLoginStore() : null;
    }
    public static Long getMemberId() {
        OnlineInfo onlineInfo = getOnlineInfo();
        return onlineInfo == null ? null : onlineInfo.getMemberId();
    }
    public static void checkAuthentication(Long currentMemberId) {
        if(currentMemberId == null) {
            throw SystemException.create().setCode(RC.PERMISSION_DENIED);
        }
    }
    public static Long checkAuthentication() {
        Long currentMemberId = SessionUtils.getMemberId();
        checkAuthentication(currentMemberId);
        return currentMemberId;
    }
    public static void checkRecordPerm(Long recordMemberId, Long currentMemberId) {
        checkAuthentication(currentMemberId);
        if(recordMemberId.intValue() != currentMemberId) {
            throw SystemException.create().setCode(RC.PERMISSION_DENIED);
        }
    }
    public static void checkRecordPerm(Long recordMemberId) {
        Long currentMemberId = checkAuthentication();
        checkRecordPerm(recordMemberId, currentMemberId);
    }

}
