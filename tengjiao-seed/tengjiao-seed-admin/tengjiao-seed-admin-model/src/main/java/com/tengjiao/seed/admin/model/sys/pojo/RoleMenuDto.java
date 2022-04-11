package com.tengjiao.seed.admin.model.sys.pojo;

import lombok.Data;

import java.util.List;

/**
 * 角色菜单更新时的对象
 */
@Data
public class RoleMenuDto {
    /**
     * 角色ID
     */
    private Integer roleId;
    /**
     * 菜单ID列表
     */
    private List<Integer> menuIds;
}