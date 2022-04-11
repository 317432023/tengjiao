package com.tengjiao.tool.indep;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.activation.MimetypesFileTypeMap;
import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Http工具
 * <p>
 * java如何得到GET和POST请求URL和参数列表
 * 一 获取URL:
 * getRequestURL()
 *
 * 二 获取参数列表:
 *
 * 1.getQueryString()
 *
 * 只适用于GET,比如客户端发送http://localhost/testServlet?a=b&c=d&e=f,通过request.getQueryString()得到的是a=b&c=d&e=f.
 *
 *
 * 2.getParameter()
 * GET和POST都可以使用
 * 但如果是POST请求要根据<form>表单提交数据的编码方式来确定能否使用.
 * 当编码方式是(application/x-www-form-urlencoded)时才能使用.这种编码方式虽然简单，但对于传输大块的二进制数据显得力不从心.
 * 对于传输大块的二进制数这类数据，浏览器采用了另一种编码方式("multipart/form-data"),这时就需要使用下面的两种方法.
 *
 * 3.getInputStream()
 * 4.getReader()
 * 上面两种方法获取的是Http请求包的包体,因为GET方式请求一般不包含包体.所以上面两种方法一般用于POST请求获取参数.
 *
 * 需要注意的是：
 * request.getParameter()、 request.getInputStream()、request.getReader()这三种方法是有冲突的，因为流只能被读一次。
 * 比如：
 * 当form表单内容采用 enctype=application/x-www-form-urlencoded编码时，先通过调用request.getParameter()方法得到参数后,
 * 再调用request.getInputStream()或request.getReader()已经得不到流中的内容，
 * 因为在调用 request.getParameter()时系统可能对表单中提交的数据以流的形式读了一次,反之亦然。
 *
 * 当form表单内容采用 enctype=multipart/form-data编码时，即使先调用request.getParameter()也得不到数据，
 * 所以这时调用request.getParameter()方法对 request.getInputStream()或request.getReader()没有冲突，
 * 即使已经调用了 request.getParameter()方法也可以通过调用request.getInputStream()或request.getReader()得到表单中的数据,
 * 而request.getInputStream()和request.getReader()在同一个响应中是不能混合使用的,如果混合使用就会抛异常。
 * <p>
 * @author kangtengjiao
 */
public class HttpTool {
    private final static Logger logger = LogManager.getLogger(HttpTool.class);

    /**
     * post 默认超时设置（包含建立连接 和 读取 返回结果 的时间 ， 单位毫秒）
     */
    private final static int DEFAULT_connectTimeout = 2000;
    private final static int DEFAULT_readTimeout = 2000;

    private final static String DEFAULT_encoding = "UTF-8";
    private final static String DEFAULT_post_content_type = "application/x-www-form-urlencoded";
    private final static String DEFAULT_fileName = "myFileName";

    /**
     * 类名: MyX509TrustManager
     * </br> 描述: 信任管理器 </br>
     * 这个证书管理器的作用就是让它信任我们指定的证书，上面的代码意味着信任所有证书，不管是否权威机构颁发
     */
    public static class MyX509TrustManager implements X509TrustManager {

        // 检查客户端证书
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        // 检查服务器端证书
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        // 返回受信任的X509证书数组
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    /**
     * GET请求
     * @param urlStr 完整请求地址
     * @param connectTimeout 连接超时
     * @param readTimeout 读超时
     * @return
     */
    public static String get(String urlStr, int connectTimeout, int readTimeout) {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();

            // connection setting
            // 设置连接超时、读取响应超时，单位ms
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);

            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept-Charset", "application/json;charset=UTF-8");

            // 验证连接的有效性
            int statusCode = connection.getResponseCode();
            if (200 != statusCode) {
                throw new RuntimeException("Http Request StatusCode(" + statusCode + ") Invalid.");
            }

            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e2) {
                logger.error(e2.getMessage(), e2);
            }
        }
        return null;
    }

    public static String post(String reqUrl, Map<String, String> map) {
        return post(reqUrl, map, DEFAULT_connectTimeout, DEFAULT_readTimeout, DEFAULT_encoding);
    }

    public static String post(String reqUrl, String str, String contentType) {
        return post(reqUrl, str, DEFAULT_connectTimeout, DEFAULT_readTimeout, contentType, DEFAULT_encoding, null);
    }

    /**
     * POST原始数据
     * @param reqUrl
     * @param map
     * @param connectTimeout
     * @param readTimeout
     * @param encoding
     * @return
     */
    public static String post(String reqUrl, Map<String, String> map, int connectTimeout, int readTimeout, String encoding) {

        StringBuilder sbr = new StringBuilder();
        for (String k : map.keySet()) {
            sbr.append(k).append('=').append(map.get(k)).append('&');
        }
        if (sbr.length() > 0) {
            sbr.deleteCharAt(sbr.length() - 1);
        }

        final String str = sbr.toString();

        return post(reqUrl, str, connectTimeout, readTimeout, DEFAULT_post_content_type, encoding, null);
    }

    /**
     * POST原始数据
     * @param reqUrl
     * @param str
     * @param connectTimeout
     * @param readTimeout
     * @param contentType
     * @param encoding
     * @param filename
     * @return
     */
    public static String post(String reqUrl, String str, int connectTimeout, int readTimeout, String contentType, String encoding, String filename) {

        if (encoding == null) {
            encoding = DEFAULT_encoding;
        }

        logger.debug(str);

        BufferedReader bufferedReader = null;
        HttpURLConnection connection = null;
        try {
            // 创建连接
            URL url = null;

            if (reqUrl.startsWith("https://")) {
                url = new URL(null, reqUrl, new sun.net.www.protocol.https.Handler());
                connection = (HttpsURLConnection) url.openConnection();
                // 创建SSLContext对象，并使用我们指定的信任管理器初始化
                TrustManager[] tm = {new MyX509TrustManager()};
                SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
                sslContext.init(null, tm, new java.security.SecureRandom());
                // 从上述SSLContext对象中得到SSLSocketFactory对象
                SSLSocketFactory ssf = sslContext.getSocketFactory();
                ((HttpsURLConnection) connection).setSSLSocketFactory(ssf);
            } else {
                url = new URL(reqUrl);
                connection = (HttpURLConnection) url.openConnection();
            }

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Connection", "Keep-Alive");
            // 设置连接超时连接超时，单位ms
            connection.setConnectTimeout(connectTimeout);
            // 设置响应读取超时，单位ms
            connection.setReadTimeout(readTimeout);


            // 1、在Http 1.0及之前版本中，Content-Length字段可有可无。
            // 2、在http1.1及之后版本。如果是Keep-Alive，则Content-Length和chunk必然是二选一。若非Keep-Alive，则和http1.0一样。Content-Length可有可无。

            if (isPostParam(contentType) || isPostBody(contentType)) {
                byte[] strBytes = str.getBytes(encoding);
                connection.setRequestProperty("Content-Type", new StringBuilder(50).append(contentType).append(";charset=").append(encoding).toString());
                connection.setRequestProperty("Content-Length", String.valueOf(strBytes.length));

                connection.connect();


                if(isPostParam(contentType)) {
                    // 写字符串 方式1：PrintWriter写参数 name1=value1&name2=value2 的形式
                    PrintWriter printWriter = new PrintWriter(connection.getOutputStream());
                    printWriter.print(str);
                    printWriter.flush();
                    printWriter.close();

                    //// 写字符串 方式2： OutputStreamWriter写参数 name1=value1&name2=value2 的形式
                    //OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream(), encoding);
                    //outputStreamWriter.write(str);
                    //outputStreamWriter.flush();
                    //outputStreamWriter.close();
                } else {
                    // 写字节数组
                    DataOutputStream dosOut = new DataOutputStream(connection.getOutputStream());
                    dosOut.write(strBytes);
                    dosOut.flush();
                    dosOut.close();
                }

                // 验证连接有效性
                int statusCode = connection.getResponseCode();
                if (200 != statusCode) {
                    throw new RuntimeException("http request StatusCode("+ statusCode +") invalid. for url : " + url);
                }

                // 读取响应
                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    // line = new String(line.getBytes(), encoding);
                    sb.append(line);
                }

                return sb.toString();
            } else if (isPostFile(contentType)) {
                // 字段名
                if (filename == null || "".equals(filename)) {
                    filename = DEFAULT_fileName;
                }

                StringBuilder strBuf = new StringBuilder(str);
                // 分隔符
                String BOUNDARY = "---------------------------7d4a6d158c9";
                strBuf = strBuf.append("--");
                strBuf = strBuf.append(BOUNDARY);
                strBuf = strBuf.append("\r\n");
                strBuf = strBuf.append("Content-Disposition: form-data; name=\"" + filename + "\"\r\n\r\n");
                // 字段值
                strBuf = strBuf.append(URLEncoder.encode(str, encoding));
                strBuf = strBuf.append("\r\n");
                byte[] data = strBuf.toString().getBytes(encoding);
                byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes(encoding);
                // 设置HTTP头:
                connection.setRequestProperty("Content-Type", "multipart/form-data" + "; boundary=" + BOUNDARY);
                connection.setRequestProperty("Content-Length", String.valueOf(data.length + end_data.length));

                connection.connect();


                // 输出:
                OutputStream output = connection.getOutputStream();
                output.write(data);
                output.write(end_data);
                output.flush();
                output.close();


                // 验证连接有效性
                int statusCode = connection.getResponseCode();
                if (200 != statusCode) {
                    throw new RuntimeException("http request StatusCode("+ statusCode +") invalid. for url : " + url);
                }

                // 读取响应
                /*
                byte[] bRecByte = new byte[1024];
                DataInputStream dis = new DataInputStream(connection.getInputStream());
                BufferedInputStream bis = new BufferedInputStream(dis);
                int nByte = -1;
                ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
                nByte = bis.read(bRecByte, 0, 64); // nByte 只会小于或等于 64
                while (nByte != -1) {
                    baoStream.write(bRecByte, 0, nByte);
                    nByte = bis.read(bRecByte, 0, 64);
                }
                byte abyte[] = baoStream.toByteArray();
                String response = new String(abyte, encoding);
                return response;
                */

                // 读取响应
                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    // line = new String(line.getBytes(), encoding);
                    sb.append(line);
                }

                return sb.toString();
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e2) {
                logger.error(e2.getMessage(), e2);
            }
        }
        return null;
    }

    private static boolean isPostParam(String contentType) {
        return DEFAULT_post_content_type.equals(contentType);
    }
    private static boolean isPostBody(String contentType) {
        return "application/json".equals(contentType) || "text/html".equals(contentType);
    }
    private static boolean isPostFile(String contentType) {
        return "multipart/form-data".equals(contentType);
    }

    /**
     * post Body
     *
     * @param url
     * @param requestBody
     * @param readTimeoutSecond
     * @return
     */
    public static String postBody(String url, String requestBody, int readTimeoutSecond) {
        return post(url, requestBody, 3*1000, readTimeoutSecond*1000,
                "application/json", "UTF-8", null);
    }

    /**
     * 表单上传
     * @param requestUrl 上传地址
     * @param textMap 文本域
     * @param fileMap 文件域
     * @param connectTimeout 连接超时毫秒
     * @param readTimeout 读取超时毫秒
     * @return
     */
    public static String formUpload(String requestUrl, Map<String, String> textMap, Map<String, String> fileMap, int connectTimeout, int readTimeout) {
        String responseText =  "" ;
        HttpURLConnection conn =  null ;
        String BOUNDARY = "----------" + System.currentTimeMillis(); //boundary就是request头和上传文件内容的分隔符
        try  {
            URL url;

            if (requestUrl.startsWith("https://")) {
                url = new URL(null, requestUrl, new sun.net.www.protocol.https.Handler());
                HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
                // 创建SSLContext对象，并使用我们指定的信任管理器初始化
                TrustManager[] tm = {new MyX509TrustManager()};
                SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
                sslContext.init(null, tm, new java.security.SecureRandom());
                SSLSocketFactory ssf = sslContext.getSocketFactory();
                httpsConn.setSSLSocketFactory(ssf);
                conn = httpsConn;
            } else {
                url = new URL(requestUrl);
                conn = (HttpURLConnection) url.openConnection();
            }

            conn.setRequestMethod( "POST" );
            conn.setDoOutput( true );
            conn.setDoInput( true );
            conn.setUseCaches( false );// post方式不能使用缓存

            // 设置超时时间
            conn.setConnectTimeout( connectTimeout );
            conn.setReadTimeout( readTimeout );

            // 设置请求头信息
            // 1、在Http 1.0及之前版本中，Content-Length字段可有可无。
            // 2、在http1.1及之后版本。如果是Keep-Alive，则Content-Length和chunk必然是二选一。若非Keep-Alive，则和http1.0一样。Content-Length可有可无。

            //conn.setRequestProperty("charset", "UTF-8");
            //conn.setRequestProperty("accept", "application/json");           // 仅接收json响应
            //conn.setRequestProperty("Content-length", String.valueOf(file.length()));

            conn.setRequestProperty( "Connection" ,  "Keep-Alive" );
            conn.setRequestProperty( "User-Agent" ,  "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)" );
            conn.setRequestProperty( "Content-Type",  "multipart/form-data; boundary="  + BOUNDARY);  // 设置边界


            conn.connect();

            OutputStream out =  new  DataOutputStream(conn.getOutputStream());

            // 请求正文 文本域
            if  (textMap !=  null ) {
                StringBuilder strBuf =  new  StringBuilder();
                for (Map.Entry<String, String> entry : textMap.entrySet()) {
                    String inputName = entry.getKey();
                    String inputValue = entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    strBuf.append("\r\n")
                            .append("--")
                            .append(BOUNDARY)
                            .append("\r\n")
                            .append("Content-Disposition: form-data; name=\"").append(inputName).append("\"\r\n")
                            .append("\r\n")
                    ;
                    strBuf.append(inputValue);
                }
                out.write(strBuf.toString().getBytes());
            }

            // 请求正文 文件域
            if  (fileMap !=  null ) {
                for (Map.Entry<String, String> entry : fileMap.entrySet()) {
                    String inputName = entry.getKey();
                    String inputValue = entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    File file = new File(inputValue);
                    String filename = file.getName();
                    String contentType = new MimetypesFileTypeMap()
                            .getContentType(file);
                    if (filename.endsWith(".jpg")) {
                        contentType = "image/jpg";
                    }
                    if (contentType == null || contentType.equals("")) {
                        contentType = "application/octet-stream";
                    }

                    String string =
                            "\r\n" +
                            "--" +
                            BOUNDARY +
                            "\r\n" +
                            "Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename + "\"\r\n" +
                            "Content-Type:" + contentType + "\r\n" +
                            "\r\n";

                    out.write(string.getBytes());

                    DataInputStream in = new DataInputStream(new FileInputStream(file));
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    in.close();
                }
            }

            // 结尾部分
            byte [] endData = ( "\r\n--"  + BOUNDARY +  "--\r\n" ).getBytes();
            out.write(endData);
            out.flush();
            out.close();

            // 读取响应状态码
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("http request StatusCode("+ responseCode +") invalid. for url : " + requestUrl);
            }
            //String responseMessage = conn.getResponseMessage();

            // 读取响应
                /*
                byte[] bRecByte = new byte[1024];
                DataInputStream dis = new DataInputStream(connection.getInputStream());
                BufferedInputStream bis = new BufferedInputStream(dis);
                int nByte = -1;
                ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
                nByte = bis.read(bRecByte, 0, 64); // nByte 只会小于或等于 64
                while (nByte != -1) {
                    baoStream.write(bRecByte, 0, nByte);
                    nByte = bis.read(bRecByte, 0, 64);
                }
                byte abyte[] = baoStream.toByteArray();
                String response = new String(abyte, encoding);
                return response;
                */

            StringBuilder strBuf =  new  StringBuilder();
            BufferedReader reader =  new  BufferedReader( new  InputStreamReader(conn.getInputStream()));
            String line;
            while  ((line = reader.readLine()) !=  null ) {
                strBuf.append(line).append( "\n" );
            }
            responseText = strBuf.toString();
            reader.close();
        }  catch  (Exception e) {
            System.out.println( "发送POST请求出错。"  + requestUrl);
            e.printStackTrace();
        }  finally  {
            if  (conn !=  null ) {
                conn.disconnect();
            }
        }
        return  responseText;
    }

}