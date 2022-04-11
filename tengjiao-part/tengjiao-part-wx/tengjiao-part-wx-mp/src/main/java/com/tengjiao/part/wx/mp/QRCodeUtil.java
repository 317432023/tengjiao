package com.tengjiao.part.wx.mp;

import cn.hutool.json.JSONUtil;
import com.tengjiao.part.wx.mp.model.QRcodeResponse;
import com.tengjiao.part.wx.mp.model.UnlimitQRcodeRequest;
import com.tengjiao.tool.indep.model.RC;
import com.tengjiao.tool.indep.model.SystemException;
import com.tengjiao.tool.third.http.HttpClientTool;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class QRCodeUtil {
    /**
     * 获取小程序二维码，适用于需要的码数量较少的业务场景。通过该接口生成的小程序码，永久有效，有数量限制
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/qr-code/wxacode.createQRCode.html
     * @param globToken
     * @param path
     * @param width
     * @return
     */
    public static QRcodeResponse createQRCode(String globToken, String path, Integer width) {
        if( path == null || "".equals(path.trim())) {
            throw SystemException.create("illegal param, path required").setCode(RC.PARAM_ERR);
        }
        if( width == null || width <= 0) {
            width = 430;
        }
        final String postUrl =  "https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode?access_token="+globToken;
        final String postContent = "{\n" +
                " \"path\":\"" + path + "\",\n" +
                " \"width\":" + width + "\n" +
                "}";
        return sendPostJson(postUrl, postContent );
    }

    /**
     * 获取小程序码，适用于需要的码数量极多的业务场景。通过该接口生成的小程序码，永久有效，数量暂无限制
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/qr-code/wxacode.getUnlimited.html
     * @param globToken
     * @param request
     * @return
     */
    public static QRcodeResponse getUnlimited(String globToken, UnlimitQRcodeRequest request) {

        final String postUrl =  "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+globToken;

        return sendPostJson(postUrl, JSONUtil.toJsonStr(request) );
    }

    /**
     * 获取小程序码，适用于需要的码数量极多的业务场景。通过该接口生成的小程序码，永久有效，数量暂无限制
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/qr-code/wxacode.getUnlimited.html
     * @param globToken
     * @param json
     * @return
     */
    public static QRcodeResponse getUnlimited(String globToken, String json) {

        final String postUrl =  "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+globToken;

        return sendPostJson(postUrl, json );
    }

    /**
     * 发送post请求 有参数 json
     * @param url
     * @param json
     * @return
     */
    public static QRcodeResponse sendPostJson(String url, String json) {
        QRcodeResponse qr = new QRcodeResponse();
        InputStream inputStream = null;
        byte[] data = null;
        // 创建默认的CloseableHttpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Content-type", "application/json; charset=utf-8");
        httppost.setHeader("Accept", "application/json");
        try {
            StringEntity s = new StringEntity(json, StandardCharsets.UTF_8);
            s.setContentEncoding("UTF-8");
            httppost.setEntity(s);
            try (CloseableHttpResponse response = httpclient.execute(httppost)) {
                // 获取相应实体
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    final String contentType = entity.getContentType().getValue();
                    inputStream = entity.getContent();
                    if( contentType.startsWith("application/json") ) {
                        return qr.setCode(1).setText( HttpClientTool.readStream(inputStream, "UTF-8") );
                    }
                    data = readInputStream(inputStream);
                    return qr.setBytes( data );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return qr.setBytes( data );
    }

    /**
     * 将流 保存为数据数组
     * @param inStream
     * @return
     * @throws Exception
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        // 创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        // 每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        // 使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            // 用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        // 关闭输入流
        inStream.close();
        // 把outStream里的数据写入内存
        return outStream.toByteArray();
    }
}
