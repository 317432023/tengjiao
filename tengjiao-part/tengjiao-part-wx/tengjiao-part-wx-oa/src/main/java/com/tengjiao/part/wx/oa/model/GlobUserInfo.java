package com.tengjiao.part.wx.oa.model;

/**
 * 微信用户信息
 */
public class GlobUserInfo extends AbsUserInfo {
	/** 是否订阅 */
	private Integer subscribe;
	/** 订阅时间 */
	private Long subscribe_time;
	/** 备注*/
	private String remark;

	public Integer getSubscribe() {
		return subscribe;
	}

	public void setSubscribe(Integer subscribe) {
		this.subscribe = subscribe;
	}

	public Long getSubscribe_time() {
		return subscribe_time;
	}

	public void setSubscribe_time(Long subscribe_time) {
		this.subscribe_time = subscribe_time;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}