package com.tengjiao.tool.indep.model;

/**
 * @ClassName SystemException
 * @Description 系统异常
 * @Author rise
 * @Date 2020/7/10 14:28
 * @Version V1.0
 */
public class SystemException extends RuntimeException {

	/** 异常代码*/
	private int code = RC.FAILURE.code;
	
	private transient Object[] values;

	private SystemException(){}
	private SystemException(String message) {
		super(message);
	}
	private SystemException(String message, Throwable throwable) {
		super(message, throwable);
	}
	private SystemException(Throwable throwable) {
		super(throwable);
	}
	public static SystemException create() {
		return new SystemException();
	}
	public static SystemException create(String message) {
		return new SystemException(message);
	}
	public static SystemException create(String message, Throwable throwable) {
		return new SystemException(message, throwable);
	}
	public static SystemException create(Throwable throwable) {
		return new SystemException(throwable);
	}

	public int getCode() {
		return code;
	}
	public SystemException setCode(int code) {
		this.code = code;
		return this;
	}
	public SystemException setCode(RC error) {
		this.code = error.code;
		return this;
	}

	public Object[] getValues() {
		return values;
	}
  public SystemException setValues(Object[] values) {
		this.values = values;
		return this;
	}

}
