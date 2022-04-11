package com.tengjiao.tool.indep.model;

import java.util.List;

/**
 * 分页结果
 * @author rise
 * @param <T>
 */
public class PageVO<T> extends PageParam {
  private Integer total;
  private List<T> records;

  public PageVO<T> setTotal(Integer total) {
    this.total = total;
    return this;
  }
  public PageVO<T> setRecords(List<T> records) {
    this.records = records;
    return this;
  }
  public Integer getTotal() {
    return total;
  }

  public List<T> getRecords() {
    return records;
  }


  @Override
  public PageVO<T> setLimit(Integer limit) {
    super.setLimit(limit);
    return this;
  }

  @Override
  public PageVO<T> setPage(Integer page) {
    super.setPage(page);
    return this;
  }

}
