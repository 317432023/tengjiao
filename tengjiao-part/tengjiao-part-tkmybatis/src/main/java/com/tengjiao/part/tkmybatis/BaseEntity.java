package com.tengjiao.part.tkmybatis;

import com.fasterxml.jackson.annotation.*;
import com.tengjiao.tool.indep.DateTool;
import tk.mybatis.mapper.annotation.Version;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础实体类. 统一定义创建时间，版本等.
 * @author kangtengjiao
 */
public abstract class BaseEntity implements Serializable {

    /**
     * 数据版本号,每发生update则自增,用于实现乐观锁.
     */
    @Version
    protected Integer version = 0;

    /**
     * 创建时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(timezone = "GMT+8", pattern = DateTool.Patterns.DATETIME)
    @Column(updatable = false)
    protected Date createTime;

    /**
     * 更新时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(timezone = "GMT+8", pattern = DateTool.Patterns.DATETIME)
    protected Date updateTime;

    /**
     * 其它属性
     */
    @JsonIgnore
    @Transient
    protected Map<String, Object> innerMap = new HashMap<>();


    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 序列化的时候就会将Map里面的值也相当于实体类里面的字段给显示出来
     * @param key
     * @return
     */
    @JsonAnyGetter
    public Object getAttribute(String key) {
        return innerMap.get(key);
    }

    /**
     * 反序列化的时候将对应不上的字段全部放到Map里面
     * @param key
     * @param obj
     */
    @JsonAnySetter
    public void setAttribute(String key, Object obj) {
        innerMap.put(key, obj);
    }

}
