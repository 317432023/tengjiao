package com.tengjiao.tool.indep.model;

/**
 * 返回码定义
 * <ul><b>一、业务成功与失败</b>
 *   <li>0   - 成功</li>
 *   <li>1   - 代表任意失败但又不想返回有意义的错误码</li>
 *   <li>200 - 成功2</li>
 *   <li>3   - 系统做了限流，用户频繁请求</li>
 * </ul>
 * <ul><b>二、非业务异常导致的失败（未进入业务层时）</b>
 *   <li>404 - 访问地址不存在</li>
 *   <li>400 - 参数不合法</li>
 *   <li>401 - 未认证</li>
 *   <li>403 - 权限不足</li>
 * </ul>
 * <ul><b>三、业务异常归类到某个区间内（执行业务层逻辑过程中）</b>
 *   <li>1000～1999 区间表示业务参数错误</li>
 *   <li>2000～2999 区间表示用户错误</li>
 *   <li>3000～3999 区间表示接口异常</li>
 * </ul>
 * <ul><b>四、未定义异常</b>
 *   <li>500 - 其他错误导致的失败或服务器内部错误</li>
 * </ul>
 *
 * @author Administrator
 */
public enum RC {
	// 成功
	SUCCESS(0),
	// 失败
	FAILURE(1),
	// 频繁请求
	LIMIT(3),
	// 成功2
	SUCCESS2(200),
	// 接口或页面不存在
	NOT_FOUND(404),
	// 参数不合法
	PARAM_ERR(400),
	// 未登录
	NOT_AUTHENTICATED(401),
	// 权限不足
	PERMISSION_DENIED(403),
	// 内部服务器错误
	INTERNAL_SERVER_ERROR(500),

	// 账号被锁定
	USER_ACCOUNT_LOCKED(2006),
	// 账号已过期
	USER_ACCOUNT_EXPIRED(2002),
	// 账号或密码错误
	USER_CREDENTIALS_ERROR(2003),
	// 密码过期
	USER_CREDENTIALS_EXPIRED(2004),
	// 账号禁用
	USER_ACCOUNT_DISABLE(2005),
	// 账号不存在
	USER_ACCOUNT_NOT_EXIST(2007),
	// 账号已存在
	USER_ACCOUNT_ALREADY_EXIST(2008),
	// 账号被动下线
	USER_ACCOUNT_USE_BY_OTHERS(2009),
	
;

	public final int code;
	RC(int code){
		this.code = code;
	}
	public R toR() {
		return new R().setCode(code);
	}
}
