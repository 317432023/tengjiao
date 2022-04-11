package com.tengjiao.seed.admin.model.sys.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tengjiao.seed.admin.comm.TreeNode;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Administrator
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuTreeVo implements Serializable, TreeNode<MenuTreeVo> {
    private static final long serialVersionUID = 345499360251948149L;
    private String label;
    private List<MenuTreeVo> children;
    private Integer id;
    private Integer pid;
}
