package com.tengjiao.tool.indep.web.body;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * 流读取工具类
 * 通过流（inputStream）获取字符串的工具类
 *
 * @author kangtengjiao
 */
public class HttpBodyTool {
    public static String getBodyString(HttpServletRequest request) throws IOException {
        StringBuilder strBuf = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = request.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

            char[] bodyCharBuffer = new char[1024];
            int len;
            while ((len = reader.read(bodyCharBuffer)) != -1) {
                strBuf.append(new String(bodyCharBuffer, 0, len));
            }
            //String line = null;
            //while ((line = reader.readLine()) != null) {
            //    sb.append(line);
            //}
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return strBuf.toString();
    }
}