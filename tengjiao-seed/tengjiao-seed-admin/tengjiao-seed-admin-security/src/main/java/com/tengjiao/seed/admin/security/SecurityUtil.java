package com.tengjiao.seed.admin.security;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tengjiao.part.redis.ModeDict;
import com.tengjiao.part.redis.RedisTool;
import com.tengjiao.part.springmvc.SpringContextHolder;
import com.tengjiao.seed.admin.security.config.CustomGrantedAuthority;
import com.tengjiao.seed.admin.security.domain.AdminUserDetails;
import com.tengjiao.seed.admin.security.domain.AuthenticationToken;
import com.tengjiao.tool.indep.web.body.HttpBodyTool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import static com.tengjiao.comm.Constants.*;
import static com.tengjiao.seed.admin.comm.Constants.*;

/**
 * 安全登录
 *
 * @author Administrator
 */
@Slf4j
public class SecurityUtil {
    /**
     * 登录前组装 UsernamePasswordAuthenticationToken
     * @param request
     * @param response
     * @param redisTool
     * @param privateKey
     * @return
     * @throws AuthenticationServiceException
     */
    public static AuthenticationToken getAuthentication(HttpServletRequest request, HttpServletResponse response, RedisTool redisTool, String privateKey)
        throws AuthenticationServiceException {
        String body = null;
        try {
            body = HttpBodyTool.getBodyString(request);
        } catch (IOException e) {
            log.error(e.getMessage());
            body = "";
        }
        JSONObject params = JSON.parseObject(body);

        // 验证码校验
        String userInputCode = (String)params.get("captcha");
        String captchaToken = request.getHeader(CAPTCHA_TOKEN_NAME);
        String captcha = Optional.ofNullable(redisTool.getString(CAPTCHA_TOKEN_PREFIX + captchaToken, ModeDict.APP)).orElse("");
        if (StrUtil.isEmpty(userInputCode)) {
            throw new AuthenticationServiceException("验证码不能为空");
        } else if (StrUtil.isEmpty(captcha)) {
            throw new AuthenticationServiceException("验证码已失效");
        }

        // 正式开始验证之前，先立即删除内存中的验证码
        redisTool.del(ModeDict.APP, CAPTCHA_TOKEN_PREFIX + captchaToken);

        if(!captcha.equals(userInputCode)) {
            throw new AuthenticationServiceException("验证码错误");
        }

        String username = params.getString(PAR_USER_NAME);
        String password = params.getString(PAR_PASSWORD);

        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        } else if( StringUtils.isNotBlank(privateKey) ) {
            // 公钥加密 私钥解密
            RSA rsa = new RSA(privateKey, null);
            password = rsa.decryptStr(password, KeyType.PrivateKey);
        }

        username = username.trim();

        return new AuthenticationToken(username, password);
    }

    /**
     * 登录成功后生成登录会话或令牌
     * @param request
     * @param authentication
     * @param securityStrategyType
     * @param redisTool
     * @param duration 仅对令牌有效，单位分钟
     * @return
     */
    public static String generateToken(HttpServletRequest request, Authentication authentication,
                                       SecurityStrategyType securityStrategyType, RedisTool redisTool, long duration) {
        final AdminUserDetails userDetails = (AdminUserDetails)authentication.getPrincipal();

        final String token;
        if(SecurityStrategyType.session == securityStrategyType) {
            token = request.getSession().getId();
            log.debug("生成会话id：" + token);
        }else {
            // 生成令牌 TODO 令牌与用户信息建立关联
            token = UUID.randomUUID().toString();
            // 持久化到redis
            redisTool.setString(LOGIN_TOKEN_PREFIX + token, JSON.toJSONString(userDetails)/*userDetails.getUsername()*/, ModeDict.APP, duration*60);
        }

        AuthenticationToken authenticationToken = new AuthenticationToken(
          userDetails, null, userDetails.getAuthorities(), token);

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 存储登录认证信息到上下文 Note that : 过滤器执行完毕SpringSecurity会自动调用SecurityContextHolder.clearContext()
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        return token;
    }

    /**
     * 登录
     * @param request
     * @param authenticationManager
     * @param userDetailsService
     * @param securityStrategyType
     * @param redisTool
     * @param duration 登录持续时间（分钟）
     * @return
     */
    public static String login(HttpServletRequest request, HttpServletResponse response,
                               AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
                               SecurityStrategyType securityStrategyType, RedisTool redisTool, String privateKey, long duration) {

        AuthenticationToken authToken = getAuthentication(request, response, redisTool, privateKey);

        // Perform the security
        // 该方法会调用 loadUserByUsername 根据用户名查出用户进行密码比对 （可选：如果是单体应用的话，loadUserByUsername 应该同时将拥有的 权限列表 放进 User implements UserDetails 对象 中）
        final Authentication authentication = authenticationManager.authenticate(authToken);

        return generateToken(request, authentication, securityStrategyType, redisTool, duration*60);
    }

    /**
     * 获取当前用户信息
     * @return
     */
    public static AdminUserDetails getCurrentUser() {
        AdminUserDetails userDetails;
        try {
            userDetails = (AdminUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new InsufficientAuthenticationException("匿名用户无此权限(401 not Authenticated)");
        }
        return userDetails;
    }

    public static String loginToken(HttpServletRequest request, SecurityStrategyType securityStrategyType, String tokenHeaderKey, String tokenValPrefix) {
        if(SecurityStrategyType.session == securityStrategyType) {
            return request.getRequestedSessionId();
        }else if(SecurityStrategyType.token == securityStrategyType) {
            String authHeader = request.getHeader(tokenHeaderKey);
            if (authHeader != null && authHeader.startsWith(tokenValPrefix)) {

                // The part after "Bearer "
                final String token = authHeader.substring(tokenValPrefix.length());
                return token;
            }

            return authHeader;
        }
        return "";
    }

    /**
     * 注销登陆
     */
    public static void logout(HttpServletRequest request, RedisTool redisTool, SecurityStrategyType securityStrategyType, String tokenHeaderKey, String tokenValPrefix) {
        if(SecurityStrategyType.session == securityStrategyType) {
            // AuthenticationToken authToken = (AuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
            SecurityContextHolder.getContext().setAuthentication(null);
            SecurityContextHolder.clearContext();
        }else if(SecurityStrategyType.token == securityStrategyType) {
            String authHeader = request.getHeader(tokenHeaderKey);
            if (authHeader != null && authHeader.startsWith(tokenValPrefix)) {

                // The part after "Bearer "
                final String token = authHeader.substring(tokenValPrefix.length());

                if (redisTool.hasKey(LOGIN_TOKEN_PREFIX + token, ModeDict.APP)) {
                    // 删除登录会话信息
                    redisTool.del(ModeDict.APP, LOGIN_TOKEN_PREFIX + token);
                }
            }
        }
    }

    /**
     * 判断是否已登录
     * @return
     */
    public static boolean checkAuthentication() {
        // 检查登录状态
        SecurityContext ctx = SecurityContextHolder.getContext();
        Authentication auth = ctx.getAuthentication();
        if(auth == null) {
            return false;
        }
        if("anonymousUser".equals(auth.getName()) || auth instanceof AnonymousAuthenticationToken) {
            // 用户未登录
            return false;
        }
        return true;
    }

    /**
     * 获取在线用户信息
     * @deprecated
     */
    public static List<AdminUserDetails> onlineUsers() {
        SessionRegistry registry = SpringContextHolder.getBean(SessionRegistry.class);
        return registry.getAllPrincipals().stream().map(p -> (AdminUserDetails) p).collect(Collectors.toList());
    }

    /**
     * 匿名用户
     */
    public final static AnonymousAuthenticationToken anonymousAuthenticationToken;
    static {
        Set<String> roles = new HashSet<String>(1);
        roles.add("0");
        List<GrantedAuthority> grantedAuthorities = roles.stream().map(CustomGrantedAuthority::new).collect(Collectors.toList());
        anonymousAuthenticationToken = new AnonymousAuthenticationToken("anonymousUser", "anonymousUser", grantedAuthorities);
    }
}