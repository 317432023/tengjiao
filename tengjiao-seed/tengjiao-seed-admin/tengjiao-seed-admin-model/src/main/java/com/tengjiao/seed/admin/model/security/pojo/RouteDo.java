package com.tengjiao.seed.admin.model.security.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * RouteDo
 * TODO
 * @author Administrator
 * @since 2020/11/22 12:04
 */
@Data
public class RouteDo implements Serializable {
  private static final long serialVersionUID = 5143318636549686108L;

  private Integer id;
  private Integer pid;
  private String name;
  private Integer type;
  private String path;
  private String pattern;
  private Integer open;
  private Integer disabled;
  private Integer hidden;
  private Integer sortNum;

  // 以下为元数据
  private String icon;
  private String title;
  private Integer breadcrumb,affix,noCache;
  private String activeMenu;
  private List<String> perm;
}
