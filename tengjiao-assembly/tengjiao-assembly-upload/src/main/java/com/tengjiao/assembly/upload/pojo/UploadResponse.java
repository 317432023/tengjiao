package com.tengjiao.assembly.upload.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author tengjiao
 * @description
 * @date 2021/10/1 22:51
 */
@ApiModel("上传结果")
public class UploadResponse {
    @ApiModelProperty("静态资源URL路径")
    private String resPrefix;
    @ApiModelProperty("资源文件URI列表")
    private List<String> res;

    public String getResPrefix() {
        return resPrefix;
    }

    public UploadResponse setResPrefix(String resPrefix) {
        this.resPrefix = resPrefix;
        return this;
    }

    public List<String> getRes() {
        return res;
    }

    public UploadResponse setRes(List<String> res) {
        this.res = res;
        return this;
    }

    @Override
    public String toString() {
        return "UploadResponse{" +
          "resPrefix='" + resPrefix + '\'' +
          ", res=" + res +
          '}';
    }
}
