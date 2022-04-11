package com.tengjiao.seed.admin.security.config;

import com.google.common.collect.Lists;
import com.tengjiao.part.redis.RedisTool;
import com.tengjiao.part.springmvc.SpringContextHolder;
import com.tengjiao.seed.admin.security.LoginAuthenticationEntryPoint;
import com.tengjiao.seed.admin.security.PathMenuMetadataSource;
import com.tengjiao.seed.admin.security.UrlAccessDecisionManager;
import com.tengjiao.seed.admin.security.filter.NamePwdLoginFilter;
import com.tengjiao.seed.admin.security.filter.SessionAuthenticationFilter;
import com.tengjiao.seed.admin.security.filter.TokenAuthenticationFilter;
import com.tengjiao.seed.admin.security.handler.MyAccessDeniedHandler;
import com.tengjiao.seed.admin.security.handler.MyLogoutSuccessHandler;
import com.tengjiao.seed.admin.security.provider.CustomAuthenticationProvider;
import com.tengjiao.seed.admin.security.SecurityUtil;
import com.tengjiao.tool.indep.model.SystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import java.io.IOException;
import java.util.List;
import static com.tengjiao.comm.Constants.*;
import static com.tengjiao.seed.admin.comm.Constants.*;

/**
 * <p>
 * +EnableWebSecurity
 *  加载了WebSecurityConfiguration配置类, 配置安全认证策略。2: 加载了AuthenticationConfiguration, 配置了认证信息
 * </p>
 * <p>EnableGlobalMethodSecurity(prePostEnabled = true)
 *  开启spring方法级安全时，提供了prePostEnabled 、securedEnabled 和 jsr250Enabled 三种不同的机制来实现同一种功能
 * </p>
 * @author kangtengjiao
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 匿名可以访问的接口
     */
    public static final String[] PERMITS = new String[]{
            // 演示数据库访问地址
            "/h2-console/**",
            // 登录
            "/login",
            // 验证码
            "/captcha",
            // 拉取用户信息
            "/getInfo",
            // 对外 api，可用于公开的客户端，比如门户网站
            "/api/**",
    };
    /**
     * 不在系统菜单权限管理范围内，但是却需要认证(不允许匿名访问)的资源
     */
    public static final List<String> NEED_AUTH_URL = Lists.newArrayList(
            // 文档
            "/doc.html", "/swagger-ui.html",
            // 加载路由|菜单
            "/loadRoutes",
            // 加载菜单
            "/loadMenus"
    );
    @Autowired
    private SettingProperties settingProperties;
    @Autowired
    private RedisTool redisTool;

    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;// 登录加载用户信息(权限角色)

    @Autowired private TokenAuthenticationFilter tokenAuthenticationFilter;

    @Autowired
    private PathMenuMetadataSource pathMenuMetadataSource;// 取得URL权限对应的许可角色列表
    @Autowired
    private UrlAccessDecisionManager urlAccessDecisionManager;// 当前用户的角色列表与 URL权限对应的许可角色列表 交集 比较做 裁决即权限判断
    @Autowired
    private LoginAuthenticationEntryPoint loginAuthenticationEntryPoint;// 认证失败处理
    @Autowired
    private MyAccessDeniedHandler myAccessDeniedHandler;// 处理 已经登录无访问权限403 异常
    @Autowired
    private MyLogoutSuccessHandler myLogoutSuccessHandler;// 注销成功

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        // 使用自定义登录身份认证组件
        /** 以下相当于 auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder()); */
        auth.authenticationProvider(new CustomAuthenticationProvider(userDetailsService, passwordEncoder()));
    }

    /**
     * 放行静态资源，不走 Spring Security 过滤器链
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/**");
        web.ignoring().antMatchers("/css/**");
        web.ignoring().antMatchers("/js/**");
        web.ignoring().antMatchers("/img/**");
        web.ignoring().antMatchers("/fonts/**");
        web.ignoring().antMatchers("/favicon.ico");

        // swagger + knife4j
        if( !ProfileType.prod.name().equals(SpringContextHolder.getActiveProfile()) ) {
            // 非生产模式放行，不做是否登录判断，生产模式不能放行，防止接口文档外泄！！！
            web.ignoring()
                    .antMatchers("/doc.html","/swagger-ui.html");
        }

        web.ignoring()
                .antMatchers("/service-worker.js","/swagger**/**","/webjars/**","/v2/**");

        // web.debug(true); // 打印SpringSecurity 调试信息
        super.configure(web);
    }

    /**
     * 菜单权限拦截 器
     * @return
     */
    private ObjectPostProcessor<FilterSecurityInterceptor> objectPostProcessor() {
        return new ObjectPostProcessor<FilterSecurityInterceptor>() {
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                object.setSecurityMetadataSource(pathMenuMetadataSource);// 取得权限列表，该列表可以缓存在当前jvm进程内存或 redis 中
                object.setAccessDecisionManager(urlAccessDecisionManager); // 做出 Decision 裁决即权限判断
                return object;
            }
        };
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        SecurityStrategyType securityStrategyType = Enum.valueOf(SecurityStrategyType.class, settingProperties.getSecurityStrategy());
        LoginProcessorType loginProcessorType = Enum.valueOf(LoginProcessorType.class, settingProperties.getLoginProcessor());

        // 允许跨域
        httpSecurity.cors();

        // 关闭 CSRF(跨站请求伪造) 保护
        httpSecurity.csrf().disable();
        // 开启 CSRF(跨站请求伪造) 保护，仅允许 h2-console
        /* httpSecurity.csrf().ignoringAntMatchers("/h2-console/**"); */

        httpSecurity
                .authorizeRequests()
                // 允许所有由于跨域需要的OPTIONS预请求
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                // 允许所有OPTIONS请求
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 允许默认的认证请求（登录、拉取用户信息）
                .antMatchers(PERMITS).permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated()
                // 设置菜单权限拦截
                .withObjectPostProcessor(objectPostProcessor())
        ;

        httpSecurity
                .logout().logoutUrl("/logout")
                // 使会话失效
                .invalidateHttpSession(true)
                // 删除Cookie
                .deleteCookies("SESSION","JSESSIONID")
                .addLogoutHandler((request, resp, authentication) -> {
                    SecurityUtil.logout(request, redisTool, securityStrategyType, settingProperties.getTokenHeaderKey(), settingProperties.getTokenValPrefix());
                })
                .logoutSuccessHandler(myLogoutSuccessHandler)
        ;

        httpSecurity
                // 异常处理
                .exceptionHandling()
                // 401 处理
                .authenticationEntryPoint(loginAuthenticationEntryPoint)
                // 403 处理
                .accessDeniedHandler(myAccessDeniedHandler)
        ;

        if(LoginProcessorType.filter == loginProcessorType) {
            // 开启登录认证流程过滤器，如果使用LoginController的login接口, 修改loginProcessor=controller，根据使用习惯二选一即可
            httpSecurity.addFilterBefore(
                    new NamePwdLoginFilter(authenticationManagerBean(), securityStrategyType, redisTool,
                        settingProperties.getPrivateKey(), settingProperties.getEncryptPassword(), settingProperties.getDuration()),
                    UsernamePasswordAuthenticationFilter.class);
        }

        if(SecurityStrategyType.session == securityStrategyType) {
            // 基于session，会话状态检查过滤器
            httpSecurity.addFilterBefore(new SessionAuthenticationFilter(authenticationManagerBean()), UsernamePasswordAuthenticationFilter.class);
        }else if(SecurityStrategyType.token == securityStrategyType) {
            // 令牌状态检查过滤器
            httpSecurity.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            // 基于token，所以不需要session
            httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }else {
            throw SystemException.create("不支持的安全策略配置："+settingProperties.getSecurityStrategy());
        }

        // 仅允许同源使用 iframe
        httpSecurity.headers().frameOptions().sameOrigin();
        // 允许所有来源使用iframe
        /* httpSecurity.headers().frameOptions().disable(); */

        // 禁用缓存
        httpSecurity.headers().cacheControl();
    }

}
