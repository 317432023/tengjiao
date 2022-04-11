package com.tengjiao.part.sample.ssj.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tengjiao.tool.core.DateTool;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.OptimisticLocking;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @author kangtengjiao
 * 类标注 @OptimisticLocking 并且 字段标注 @version 支持乐观锁功能
 */
@ApiModel("示例用户")
@Entity
@Table(name = "sample_user")
@OptimisticLocking
public class SampleUser implements Serializable {
  /**
   * 用户ID
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @OrderBy("DESC")
  @ApiModelProperty(value = "用户ID")
  private Long id;

  /**
   * 用户名
   * @NotBlank // jsr 303
   */
  @NotBlank
  @ApiModelProperty(value = "用户名")
  private String username;
  /**
   * 密码
   * @NotBlank // jsr 303
   */
  @NotBlank
  @ApiModelProperty(value = "密码")
  private String password;

  /**
   * 昵称
   * @NotBlank // jsr 303
   */
  @NotBlank
  @ApiModelProperty(value = "昵称")
  private String nickname;

  /**
   * 生日
   * @JsonFormat // 序列化为字符串显示的格式
   * @DateTimeFormat // 反序列化为Date对象需要接收的字符串格式
   */
  @JsonFormat(timezone = "GMT+8", pattern = DateTool.Patterns.DATE)
  @DateTimeFormat(pattern = DateTool.Patterns.DATE)
  @ApiModelProperty(value = "生日")
  private Date birthday;

  /**
   * 性别：1-男/0-女
   */
  @ApiModelProperty(value = "性别：1-男/0-女/2-其他")
  private Integer sex;

  /**
   * 是否启用：1/0
   */
  @ApiModelProperty(value = "是否启用：1-true/0-false")
  private Boolean enabled;

  @Version
  @ApiModelProperty(value = "版本号")
  private Integer version;

  /**
   * 在新增数据之前设置version为0
   */
  @PrePersist
  public void prePersist() {
    version = 0;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }

  public Integer getSex() {
    return sex;
  }

  public void setSex(Integer sex) {
    this.sex = sex;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

}
