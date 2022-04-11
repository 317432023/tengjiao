package com.tengjiao.seed.admin.model.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * AdminEntity 管理员_实体模型
 * <p>
 * 对应于系统管理员表
 * </p>
 *
 * @author rise
 * @since 2020-11-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_admin")
@ApiModel(value="Admin 实体持久对象", description="管理员")
//@JsonIgnoreProperties({"password"})
public class Admin implements Serializable {

    private static final long serialVersionUID = -8447782983218820506L;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "盐")
    private String salt;

    @ApiModelProperty(value = "部门ID")
    private Long deptId;

    @ApiModelProperty(value = "站点ID")
    private Integer stationId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "性别（1-男，2-女，0-保密）")
    private Integer gender;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "生日")
    private Date birth;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "禁用(1-禁用,0-可用)")
    private Integer disabled;
    // 请移步至 AdminDO : Admin Domain Object
    //// begin 新增的属性
    //@ApiModelProperty(value = "站点名称")
    //@TableField(exist = false)
    //private String stationName;
    //
    //// end 新增的属性


}
