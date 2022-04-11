package com.tengjiao.seed.admin.model.security.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * MenuVo
 * 完全打平的菜单
 * @author Administrator
 * @since 2020/12/14 21:28
 */
@Data
public class MenuVo implements Serializable {
  private static final long serialVersionUID = 2191440456977506636L;

  private Integer id;
  private Integer pid;
  private String title;
  private String url;
  private String icon;
  private Integer sortNum;
  private Integer open;
  private Boolean disabled;
  private Integer type;
  private List<MenuVo> children = new ArrayList<>();

}
