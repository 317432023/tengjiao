package com.tengjiao.tool.indep.model;

import java.io.Serializable;

/**
 * 封装返回数据
 */
public class R<T> implements Serializable {
  private static final long serialVersionUID = 1430633339880116031L;
  protected int code;
  protected String message;
  protected T data;

  public R() {}

  public R(T data) {
    this.data = data;
  }

  public boolean isSuccess() {
    return code == RC.SUCCESS.code || code == RC.SUCCESS2.code;
  }

  public String getMessage() {
    return message;
  }

  public R<T> setMessage(String message) {
    this.message = message;
    return this;
  }

  public T getData() {
    return data;
  }

  public R<T> setData(T data) {
    this.data = data;
    return this;
  }

  public int getCode() {
    return code;
  }

  public R<T> setCode(int code) {
    this.code = code;
    return this;
  }

  @Override
  public String toString() {
    return "Result{" +
      "code=" + code +
      ", msg='" + message + '\'' +
      ", data=" + data +
      '}';
  }

}
