
package com.tengjiao.comm.upload;

public interface IUploadService {

    /**
     * 单文件上传
     * @param localFile 本地文件完整路径
     * @param objectName eg. exampledir/exampleobject.txt
     */
    String upload(String localFile, String objectName);

    /**
     * 多文件上传
     * @param localFiles 本地文件完整路径s
     * @param objectNames eg. exampledir/exampleobject.txt
     */
    String[] upload(String[] localFiles, String[] objectNames);
}