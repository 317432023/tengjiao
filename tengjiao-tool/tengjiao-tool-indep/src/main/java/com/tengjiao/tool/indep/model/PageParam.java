package com.tengjiao.tool.indep.model;

/**
 * @author kangtengjiao
 * @date 2022-04-10
 */
public class PageParam {

  private Integer page = 1;

  private Integer limit = 10;

  public Integer getPage() {
    return page;
  }

  public PageParam setPage(Integer page) {
    this.page = page;
    return this;
  }

  public Integer getLimit() {
    return limit;
  }

  public PageParam setLimit(Integer limit) {
    this.limit = limit;
    return this;
  }

}
