package com.tengjiao.part.wx.oa.util;

import com.tengjiao.tool.indep.HttpTool;

import java.util.Map;

/**
 * @author tengjiao
 * @description
 * @date 2021/10/15 17:29
 */
public class MediaUploadUtil {

    /**
     * 上传媒体文件到公众号平台
     * @param globToken 全局票据
     * @param type 多媒体类型 image|video|audio
     * @return
     */
    public static String upload(String globToken,
                                String type,
                                Map<String, String> textMap,
                                Map<String, String> fileMap) {
        final String requestUrl = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=" + globToken+ "&type="+type;
        return HttpTool.formUpload(requestUrl, textMap, fileMap, 5000, 5000);
    }
}
