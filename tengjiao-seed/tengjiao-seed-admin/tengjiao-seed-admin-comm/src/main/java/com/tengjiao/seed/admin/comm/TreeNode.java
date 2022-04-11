package com.tengjiao.seed.admin.comm;

import java.util.List;

/**
 * 树节点
 * @author Administrator
 */
public interface TreeNode<T> {
    /**
     * 获取父节点ID
     * @return
     */
    Integer getPid();

    /**
     * 获取自己的ID
     * @return
     */
    Integer getId();

    /**
     * 添加子节点列表
     * @param node /
     */
    void setChildren(List<T> node);
}