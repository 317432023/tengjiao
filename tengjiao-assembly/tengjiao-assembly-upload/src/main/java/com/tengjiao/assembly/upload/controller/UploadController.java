package com.tengjiao.assembly.upload.controller;

import com.tengjiao.assembly.upload.config.UploadProperties;
import com.tengjiao.assembly.upload.pojo.UploadResponse;
import com.tengjiao.assembly.upload.util.FileTypeJudge;
import com.tengjiao.comm.upload.IUploadService;
import com.tengjiao.tool.indep.model.R;
import com.tengjiao.tool.indep.model.SystemException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Api(value = "pub-公共服务", tags = {"pub-公共服务"})
@CrossOrigin
@Controller
public class UploadController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private UploadProperties uploadProperties;

    /** 见pub.properties */
    @Value("${pub.staticPrefix:unknown}")
    private String staticPrefix;

    @Autowired
    private HttpServletRequest request;

    public UploadController(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    private IUploadService getUploader() {

        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext( request.getServletContext() );
        try {
            return applicationContext.getBean(uploadProperties.getUploader(), IUploadService.class);
        }catch(Exception e) {
            log.warn(uploadProperties.getUploader() + " not exists in applicationContext");
            return null;
        }
    }

    /**
     * 保存文件
     *
     * @param bytes       字节数组
     * @param renamed     重命名后的文件名
     * @return            文件url路径
     * @throws IOException
     */
    private String saveFile(String fileType, byte[] bytes, String renamed) throws IOException, SystemException {

        // 文件存储的pattern路径
        String patternPath = uploadProperties.getPatternPath();

        // 文件存储的路径
        String fileSavePath = uploadProperties.getFileSavePath(fileType, patternPath);
        // 文件全路径名称
        String fullFileSavePath = fileSavePath + File.separator + renamed;

        log.info("准备上传文件到：" + fullFileSavePath);

        File saveFileDir = new File(fileSavePath);
        if (!saveFileDir.exists() && !saveFileDir.mkdirs()) {
            throw SystemException.create("创建目录失败："+ fileSavePath);
        }

        File temp = new File(fullFileSavePath);
        if (!temp.exists() && !temp.createNewFile()) {
            throw SystemException.create("创建文件失败: " + fullFileSavePath);
        }

        BufferedOutputStream bos = null;
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(temp);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
            bos.flush();
        } catch (IOException e) {
            throw e;
        } finally {
            if (bos != null) {
                bos.close();
            }
        }

        return uploadProperties.getUrlAccessPath(fileType, patternPath, renamed);
    }

    /**
     * multipart上传单个文件
     *
     * @param bytes 文件字节数组
     * @param renamed
     * @return
     */
    private String responseUpload(String fileType, byte[] bytes, String renamed) {

        String urlAccessPath;

        try {

            urlAccessPath = saveFile(fileType, bytes, renamed);

        } catch (IOException | SystemException e) {
            log.error(e.getMessage());
            return null;
        }

        log.info("文件访问路径：" + urlAccessPath);

        return urlAccessPath;
    }

    /**
     * multipart上传单个文件
     * @param file 文件
     * @return
     */
    private String responseUpload(MultipartFile file) {
        // 原文件名
        String origFileName = file.getOriginalFilename();
        // 重命名文件
        String renamed = uploadProperties.getRenamed(origFileName);

        // 文件字节数组
        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }

        // 获取文件类型
        String fileType = FileTypeJudge.getTypeName(fileBytes);

        return responseUpload(fileType, fileBytes, renamed);
    }

    /**
     * 多文件上传
     *
     * @param request
     */
    @ApiOperation(value="文件上传",/*tags={"文件上传"},*/notes="注意开启参数签名验证的情况下，需要参数签名(文件域不需要签名)")
    @ApiImplicitParams({
      @ApiImplicitParam(name = "myFileName", value = "文件域", dataType = "byte", required = true, paramType = "form"),
    })
    @ResponseBody
    @PostMapping("/upload")
    public R<UploadResponse> upload(HttpServletRequest request) {
        //MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        //MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);

        R<UploadResponse> result = new R<>();

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files = multipartRequest.getFiles(uploadProperties.getFileParam());

        final int len = files.size();
        if(len == 0) {
            return result.setCode(1).setMessage("文件为空");
        }
        if(len > uploadProperties.getMaxFileNum()) {
            return result.setCode(1).setMessage("文件不能超过个数: " + uploadProperties.getMaxFileNum());
        }
        for (MultipartFile multipartFile : files) {
            if (multipartFile.getSize() > uploadProperties.getMaxFileSize()) {
                return result.setCode(1).setMessage("每个文件不能超过大小：" + uploadProperties.getMaxFileSize());
            }
        }
        List<String> data = new ArrayList<>(len);

        for (MultipartFile file : files) {

            // 资源访问的Url全路径
            String urlAccessPath = null;

            boolean success = (urlAccessPath = responseUpload(file)) != null;

            if (!success) {
                return result.setCode(500).setMessage("internal server error.");
            }


            data.add(urlAccessPath);
        }

        UploadResponse uploadResponse = new UploadResponse();
        uploadResponse.setRes(data);

        // 三方平台文件上传
        if( uploadProperties.isSaveTo3rdPlatform() ) {
            IUploadService uploadService = getUploader();
            if(uploadService != null) {

                List<String> localFileList = new ArrayList<>(len);
                List<String> objectNameList = new ArrayList<>(len);

                data.forEach(e->{
                    final String localFile = uploadProperties.getResPath() + e,
                      objectName = e.startsWith("/")?e.substring(1):e;

                    localFileList.add(localFile);
                    objectNameList.add(objectName);

                });

                String[] thirdUrlAccessPaths = uploadService.upload(localFileList.toArray(new String[len]),
                  objectNameList.toArray(new String[len]));

                // 使用三方上传后的文件访问地址
                if( uploadProperties.isReturn3rdAccessUrl() && uploadResponse.getResPrefix()==null ) {
                    final String resPrefix = thirdUrlAccessPaths[0].substring(0, thirdUrlAccessPaths[0].length() - data.get(0).length());
                    uploadResponse.setResPrefix( resPrefix );
                }
            }
        }

        if( useOwnResPrefix() ) {
            uploadResponse.setResPrefix(staticPrefix);
        }

        return result.setData(uploadResponse);

    }

    /**
     * 单文件上传(base64图片)
     *
     * @param base64Data
     */
    @ApiOperation(value="图像上传",/*tags={"获取图形验证码"},*/notes="注意开启参数签名验证的情况下，需要参数签名")
    @ApiImplicitParams({
      @ApiImplicitParam(name = "base64Data", value = "图像base64字符串", dataType = "string", required = true, paramType = "query"),
      @ApiImplicitParam(name = "fileName", value = "文件名", dataType = "string", paramType = "query"),
    })
    @ResponseBody
    @PostMapping("/upload_base64_img")
    public R<UploadResponse> base64UpLoad(@RequestParam String base64Data, @RequestParam(required = false) String fileName) {

        R<UploadResponse> result = new R<>();

        if (base64Data == null || "".equals(base64Data.trim())) {
            return result.setCode(1).setMessage("base64 string not specified");
        }
        String dataPrefix;
        String data;
        String[] d = base64Data.split("base64,");
        if (d.length == 2) {
            dataPrefix = d[0];
            data = d[1];
        } else {
            final String err = "base64文件内容格式不正确";
            log.error(err);
            return result.setCode(1).setMessage("err");
        }

        String suffix;
        if ("data:image/jpeg;".equalsIgnoreCase(dataPrefix) || "data:image/jpg;".equalsIgnoreCase(dataPrefix)) {//data:image/jpeg;base64,base64编码的jpeg图片数据
            suffix = ".jpg";
        } else if ("data:image/x-icon;".equalsIgnoreCase(dataPrefix)) {//data:image/x-icon;base64,base64编码的icon图片数据
            suffix = ".ico";
        } else if ("data:image/gif;".equalsIgnoreCase(dataPrefix)) {//data:image/gif;base64,base64编码的gif图片数据
            suffix = ".gif";
        } else if ("data:image/png;".equalsIgnoreCase(dataPrefix)) {//data:image/png;base64,base64编码的png图片数据
            suffix = ".png";
        } else {
            final String err = dataPrefix + " 文件头不是图片";
            log.error(err);
            return result.setCode(1).setMessage("err");
        }
        // 重命名后的文件名
        String renamed = StringUtils.isEmpty(fileName)?UUID.randomUUID().toString().replace("-", ""):fileName;
        renamed += suffix;

        // 因为BASE64Decoder的jar问题，此处使用spring框架提供的工具包
        byte[] bytes = Base64Utils.decodeFromString(data);
        return upload(bytes, renamed);

    }

    /**
     * 文件字节数组上传
     * @param bytes 文件字节数组
     * @param renamed 文件名，不带路径和后缀
     * @return
     */
    public R<UploadResponse> upload(byte[] bytes, String renamed) {


        // 文件类型
        String fileType = FileTypeJudge.getTypeName(bytes);

        // 资源访问的Url全路径
        String urlAccessPath = responseUpload(fileType, bytes, renamed);

        if (urlAccessPath != null) {

            UploadResponse uploadResponse = new UploadResponse();
            List<String> res = new ArrayList<>(1);
            res.add(urlAccessPath);
            uploadResponse.setRes(res);

            // 三方平台文件上传
            if( uploadProperties.isSaveTo3rdPlatform() ) {
                IUploadService uploadService = getUploader();
                if(uploadService != null) {

                    final String localFile = uploadProperties.getResPath() + urlAccessPath,
                      objectName = urlAccessPath.startsWith("/")?urlAccessPath.substring(1):urlAccessPath;

                    final String thirdUrlAccessPath = uploadService.upload(localFile, objectName);

                    // 使用三方上传后的文件访问地址
                    if(uploadProperties.isReturn3rdAccessUrl()) {
                        final String resPrefix = thirdUrlAccessPath.substring(0, thirdUrlAccessPath.length() - urlAccessPath.length());
                        uploadResponse.setResPrefix( resPrefix );
                    }
                }
            }

            if( useOwnResPrefix() ) {
                uploadResponse.setResPrefix(staticPrefix);
            }

            return new R<UploadResponse>().setData(uploadResponse);

        }

        return new R<UploadResponse>().setCode(1);
    }

    private boolean useOwnResPrefix() {
        // 是否有效的静态资源前缀或域名
        boolean hasValidStaticPrefix = !StringUtils.isEmpty(staticPrefix)
          && !staticPrefix.equals("unknown")
          && staticPrefix.startsWith("http");

        // 是否上传并返回三方的资源路径
        boolean use3rd = uploadProperties.isReturn3rdAccessUrl()
          && uploadProperties.isSaveTo3rdPlatform();

        return !use3rd && hasValidStaticPrefix;
    }
}
