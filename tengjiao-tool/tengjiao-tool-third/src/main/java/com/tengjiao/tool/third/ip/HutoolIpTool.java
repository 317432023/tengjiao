package com.tengjiao.tool.third.ip;

import cn.hutool.core.net.NetUtil;
import cn.hutool.extra.servlet.ServletUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author tengjiao
 * @description
 * @date 2021/10/2 13:09
 */
public class HutoolIpTool {
    private static final String LOCAL_IP = "0:0:0:0:0:0:0:1";

    private static final String[] HEADERS_TO_TRY_1 = {
      "X-Forwarded-For",
      "X-Real-IP",
      "Proxy-Client-IP",
      "WL-Proxy-Client-IP",
      "HTTP_CLIENT_IP",
      "HTTP_X_FORWARDED_FOR",
    };

    private static final String[] HEADERS_TO_TRY_2 = {
      "HTTP_X_FORWARDED",
      "HTTP_X_CLUSTER_CLIENT_IP",
      "HTTP_FORWARDED_FOR",
      "HTTP_FORWARDED",
      "HTTP_VIA",
      "REMOTE_ADDR"
    };

    /**
     * 本机ip地址(局域网ip)
     * @return
     */
    public static String getLocalIpAddress() {
        return NetUtil.getLocalhost().getHostAddress();
    }

    /***
     * 获取请求的客户端ip地址(可以穿透代理)
     * @param request /
     * @return
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        String ip = ServletUtil.getClientIP(request, HEADERS_TO_TRY_2);
        return ip.equals(LOCAL_IP) ? "127.0.0.1" : ip;
    }
}
