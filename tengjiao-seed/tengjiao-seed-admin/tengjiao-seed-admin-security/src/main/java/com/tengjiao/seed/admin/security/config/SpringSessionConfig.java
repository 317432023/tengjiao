package com.tengjiao.seed.admin.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * 解决 google 76之后浏览器发送请求不会自动带cookie
 */
@Configuration
public class SpringSessionConfig {

	/**
	 * Chrome 浏览器 chrome://flags/ 禁用以下两项：
	 * SameSite by default cookies
	 * Cookies without SameSite must be secure
	 * @return
	 */
	@Bean
	public CookieSerializer httpSessionIdResolver() {
		DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
		cookieSerializer.setUseHttpOnlyCookie(false);
		cookieSerializer.setSameSite(null);
		cookieSerializer.setCookiePath("/");
		cookieSerializer.setUseSecureCookie(false);
		return cookieSerializer;
	}

	/**
	 * 解决Chrome新版本中cookie跨域携带和samesite的问题处理
	 *  Chrome版本67及以上
	 * @return
	 */
	/*@Bean
	public CookieSerializer httpSessionIdResolver() {
		DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
		cookieSerializer.setUseHttpOnlyCookie(false);
		cookieSerializer.setSameSite("None");
		cookieSerializer.setCookiePath("/");
		cookieSerializer.setUseSecureCookie(true); // Cookie 只能通过 HTTPS 协议发送
		return cookieSerializer;
	}*/

	/**
	 * Chrome版本(51~66)
	 * @return
	 */
	/*@Bean
	public CookieSerializer httpSessionIdResolver() {
		DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
		DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
		cookieSerializer.setUseHttpOnlyCookie(false);
		cookieSerializer.setSameSite(null);
		cookieSerializer.setCookiePath("/");
		cookieSerializer.setUseSecureCookie(true);
		return cookieSerializer;
	}
	然后在登录完成后的获取用户信息接口中,对session进行重新赋值:
	String userAgent = request.getHeader("user-agent");
Browser browser = UserAgent.parseUserAgentString(userAgent).getBrowser();
if(!CommonUtils.isEmpty(browser)&&browser.getName().contains("Chrome")){
    Version version = browser.getVersion(userAgent);
    if(!CommonUtils.isEmpty(version.getMajorVersion())){
        try{
            int majorVersion = Integer.parseInt(version.getMajorVersion());
            // 如果是谷歌并且版本大于等于67，则重赛COOKIE
            if(majorVersion>=67){
                List<String> cookieValues = CookieSetUtil.readCookieValues(request);
                cookieValues.forEach(cookieValueStr -> {
                    CookieSerializer.CookieValue cookieValue = new CookieSerializer.CookieValue(request, response, cookieValueStr);
                    CookieSetUtil.writeCookieValue(cookieValue);
                });
            }
        }catch (Exception e ){
            e.printStackTrace();
        }
    }
}
	*/
}