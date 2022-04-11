package com.tengjiao.part.wx.mp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author tengjiao
 * @description
 * @date 2021/12/8 19:50
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
class LineColor implements Serializable {
    private static final long serialVersionUID = 2421549892694408278L;
    private Integer r,g,b;
}
