package com.tengjiao.assembly.upload.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tengjiao.tool.indep.DateTool;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * @author tengjiao
 */
@Component
@PropertySource(value = {"classpath:config/upload.properties", "file:${config.upload}"}, encoding = "utf-8", ignoreResourceNotFound = true)
@ConfigurationProperties(prefix = "upload", ignoreInvalidFields = true)
public class UploadProperties {
    private String fileParam = "myFileName";
    private String resPath = "/c:/files";
    public String uploadFolder = "/upload";
    private boolean judgeFileType = true;
    private String savePathPattern = "yyyy/MM/dd";
    private int maxFileSize = 1048576;
    private int maxFileNum = 3;
    private boolean saveTo3rdPlatform = false;
    private String uploader = "aliUploadService";
    private boolean return3rdAccessUrl = false;

    public String getFileParam() {
        return fileParam;
    }

    public void setFileParam(String fileParam) {
        this.fileParam = fileParam;
    }

    public String getResPath() {
        return resPath;
    }

    public void setResPath(String resPath) {
        this.resPath = resPath;
    }

    public String getUploadFolder() {
        return uploadFolder;
    }

    public void setUploadFolder(String uploadFolder) {
        this.uploadFolder = uploadFolder;
    }

    public boolean isJudgeFileType() {
        return judgeFileType;
    }

    public void setJudgeFileType(boolean judgeFileType) {
        this.judgeFileType = judgeFileType;
    }

    public String getSavePathPattern() {
        return savePathPattern;
    }

    public void setSavePathPattern(String savePathPattern) {
        this.savePathPattern = savePathPattern;
    }

    public int getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public int getMaxFileNum() {
        return maxFileNum;
    }

    public void setMaxFileNum(int maxFileNum) {
        this.maxFileNum = maxFileNum;
    }

    public boolean isSaveTo3rdPlatform() {
        return saveTo3rdPlatform;
    }

    public void setSaveTo3rdPlatform(boolean saveTo3rdPlatform) {
        this.saveTo3rdPlatform = saveTo3rdPlatform;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getUploader() {
        return this.uploader;
    }

    public boolean isReturn3rdAccessUrl() {
        return return3rdAccessUrl;
    }

    public void setReturn3rdAccessUrl(boolean return3rdAccessUrl) {
        this.return3rdAccessUrl = return3rdAccessUrl;
    }

    //======================================= 拼装字符串 ==============================
    @JsonIgnore
    public String getPatternPath() {
        String rawPatternPath = DateTool.format(new Date(), this.savePathPattern);
        return rawPatternPath.replace('/', File.separatorChar);
    }
    /**
     * 文件存储的路径
     */
    public String getFileSavePath(String fileType, String patternPath) {
        return resPath + uploadFolder + File.separator + fileType + File.separator + patternPath;
    }

    /**
     * 资源访问的Url全路径
     */
    public String getUrlAccessPath(String fileType, String patternPath, String renamed) {
        return uploadFolder + '/' + fileType + '/' + patternPath.replace(File.separatorChar, '/') + '/' + renamed;
    }

    /**
     * 重命名文件名
     * @param origFileName
     * @return
     */
    public String getRenamed(String origFileName) {
        int lastIndexDot = origFileName.lastIndexOf(".");
        String filePrefixName = lastIndexDot > 0 ? origFileName.substring(0, lastIndexDot) : "",
                fileSuffixName = lastIndexDot > 0 ? origFileName.substring(lastIndexDot) : "";
        return filePrefixName + "-" + UUID.randomUUID().toString() + fileSuffixName;
    }
}
