package com.tengjiao.seed.admin.security.filter;

import com.alibaba.fastjson.JSON;
import com.tengjiao.part.redis.ModeDict;
import com.tengjiao.part.redis.RedisTool;
import com.tengjiao.seed.admin.security.SecurityUtil;
import com.tengjiao.seed.admin.security.domain.AdminUserDetails;
import com.tengjiao.seed.admin.security.domain.AuthenticationToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.tengjiao.comm.Constants.LOGIN_TOKEN_PREFIX;


/**
 * token filter bean.
 * 主要实现了对请求的拦截，获取http头上的Authorization元素，token码就在这个键里，我们的token都是采用通用的Bearer开头，当你的token没有过期时，会
 * 存储在redis里，key就是用户名的md5码，而value就是用户名，当拿到token之后去数据库或者缓存里拿用户信息进行授权即可。
 */
@PropertySource(value = {"file:${config.setting}"}, encoding = "utf-8", ignoreResourceNotFound = true)
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    private Environment env;
    @Autowired
    private RedisTool redisTool;
    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    //@Value("${tokenHeaderKey}")
    private String tokenHeaderKey = "Authorization";
    //@Value("${tokenValPrefix}")
    private String tokenValPrefix = "Bearer ";
    /**
     * 登录持续周期(分钟)
     */
    //@Value("${duration}")
    private Long duration = 30L;

    @PostConstruct
    private void init() {

        tokenHeaderKey = StringUtils.isNotBlank(env.getProperty("tokenHeaderKey")) ? env.getProperty("tokenHeaderKey") : tokenHeaderKey;
        tokenValPrefix = StringUtils.isNotBlank(env.getProperty("tokenValPrefix")) ? env.getProperty("tokenValPrefix") : tokenValPrefix;
        duration = StringUtils.isNotBlank(env.getProperty("duration")) ? Long.parseLong(env.getProperty("duration")) : duration;

    }


    /**
     * token filter.
     *
     * @param request
     * @param response
     * @param chain
     */
    @Override
    protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {

        String authHeader = request.getHeader(this.tokenHeaderKey);

        if (authHeader != null && authHeader.startsWith(tokenValPrefix)) {

            // The part after "Bearer "
            final String token = authHeader.substring(tokenValPrefix.length());

            if (redisTool.hasKey(LOGIN_TOKEN_PREFIX + token, ModeDict.APP)) {

                String stringAdminUserDetails = redisTool.getString(LOGIN_TOKEN_PREFIX + token, ModeDict.APP);

                if (stringAdminUserDetails != null) {

                    // 更新登录持续时间
                    redisTool.expire(LOGIN_TOKEN_PREFIX + token, ModeDict.APP, duration * 60);

                    if (!SecurityUtil.checkAuthentication()) {

                        AdminUserDetails adminUserDetails = JSON.parseObject(stringAdminUserDetails, AdminUserDetails.class);
                        String username = adminUserDetails.getUsername();
                        //UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                        // 可以校验token和username是否有效，目前由于token对应username存在redis，所以默认都是有效的
                        AuthenticationToken authentication = new AuthenticationToken(
                          adminUserDetails, null, adminUserDetails.getAuthorities(), token);

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                          request));

                        logger.debug("authenticated user " + username + ", setting security context");

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // 已登录
                        chain.doFilter(request, response);
                        return;
                    }
                }
            }
        }

        // 未登录
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(SecurityUtil.anonymousAuthenticationToken); // 设置匿名用户
        }

        chain.doFilter(request, response);

    }
}