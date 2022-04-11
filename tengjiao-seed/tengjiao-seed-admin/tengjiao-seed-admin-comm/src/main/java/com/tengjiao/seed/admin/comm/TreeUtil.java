package com.tengjiao.seed.admin.comm;

import java.util.ArrayList;
import java.util.List;

/**
 * 构建树结构
 *
 */
public class TreeUtil {
    /**
     * 入口方法
     *
     * @param nodeList /
     * @param <T>      /
     * @return /
     */
    public static <T extends TreeNode> List<T> getTree(List<T> nodeList) {
        List<T> list = new ArrayList<>();
        // 遍历节点列表
        for (T node : nodeList) {
            if (node.getPid() == 0) {
                // parentID为0（根节点）作为入口
                node.setChildren(getChildrenNode(node.getId(), nodeList));
                list.add(node);
            }
        }
        return list;
    }

    /**
     * 获取子节点的递归方法
     *
     * @param id       /
     * @param nodeList /
     * @param <T>      /
     * @return /
     */
    private static <T extends TreeNode> List<T> getChildrenNode(Integer id, List<T> nodeList) {
        List<T> list = new ArrayList<>();
        for (T node : nodeList) {
            if (node.getPid().intValue() == id.intValue()) {
                // 递归获取子节点
                List<T> childrenNode = getChildrenNode(node.getId(), nodeList);
                if (childrenNode.size() > 0) {
                    node.setChildren(childrenNode);
                }
                list.add(node);
            }
        }
        return list;
    }

}
