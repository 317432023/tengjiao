package com.tengjiao.seed.admin.model.security.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Meta
 * TODO
 * @author Administrator
 * @since 2020/11/22 19:57
 */
@Data
public class MetaVo implements Serializable {
  private static final long serialVersionUID = -8178342841996211589L;
  private String title;
  private String icon;
  private List<String> perm;
  private Integer breadcrumb;
  private Integer affix;
  private String noCache;
  private String activeMenu;
}
