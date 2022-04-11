package com.tengjiao.seed.admin.model.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.tengjiao.part.mybatisplus.LikeIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 系统菜单表
 * </p>
 *
 * @author rise
 * @date 2020-11-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_menu")
@ApiModel(value="Menu对象", description="系统菜单表")
public class Menu implements Serializable {

    private static final long serialVersionUID = 3520959926905679699L;
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "父菜单ID")
    private Integer pid;

    @ApiModelProperty(value = "路由名称")
    private String name;

    @ApiModelProperty(value = "类型(0-菜单项, 1-按钮, 2-菜单目录)")
    private Integer type;

    @ApiModelProperty(value = "URL兼路由路径")
    private String path;

    @ApiModelProperty(value = "接口URL规则，实现的功能相当于perm字段（比如/menu/query/**，一般用 /模块名/操作/** 的形式定义，用于AntPathMatcher）")
    private String pattern;

    @ApiModelProperty(value = "是否展开（0-收起,1-展开）")
    private Integer open;

    @ApiModelProperty(value = "是否禁用(1-禁用,0-可用)")
    private Integer disabled;
    @ApiModelProperty(value = "是否在sidebar菜单中展示")
    private Integer hidden;

    @ApiModelProperty(value = "排序号")
    private Integer sortNum;

    @ApiModelProperty(value = "菜单项标题")
    private String title;
    @ApiModelProperty(value = "图标")
    private String icon;
    @ApiModelProperty(value = "权限（一般是shiro或springsecurity资源格式，模块:操作 如user:add）")
    private String perm;
    @ApiModelProperty(value = "在面包屑导航中展示")
    private Integer breadcrumb;
    @ApiModelProperty(value = "固定到标签页")
    private Integer affix;
    @ApiModelProperty(value = "不缓存标签页")
    private Integer noCache;
    @ApiModelProperty(value = "打开页面时高亮的菜单项(路径)")
    private String activeMenu;
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
