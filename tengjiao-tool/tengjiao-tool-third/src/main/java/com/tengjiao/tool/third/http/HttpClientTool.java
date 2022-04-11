package com.tengjiao.tool.third.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;

import com.tengjiao.tool.indep.StringTool;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BestMatchSpec;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class AnyTrustStrategy implements TrustStrategy {
    @Override
    public boolean isTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        return true;
    }
}

/**
 * @author Administrator
 */
public class HttpClientTool {

    private static final Logger log = LogManager.getLogger(HttpClientTool.class);

    private static int bufferSize = 1024;

    private static volatile HttpClientTool instance;

    private ConnectionConfig connConfig;

    private SocketConfig socketConfig;

    private RequestConfig requestConfig;

    private ConnectionSocketFactory plainSF;

    private KeyStore trustStore;

    private SSLContext sslContext;

    private LayeredConnectionSocketFactory sslSF;

    private Registry<ConnectionSocketFactory> registry;

    private PoolingHttpClientConnectionManager connManager;

    private volatile HttpClient client;

    private volatile BasicCookieStore cookieStore;

    public static String defaultEncoding = "utf-8";

    private static void wrapParams(URIBuilder builder, HttpRequestBase request, Map<String, String> queryParams, Map<String, String> headerParams) {
        // 填入查询参数
        if (queryParams != null && !queryParams.isEmpty()) {
            builder.setParameters(HttpClientTool.paramsConverter(queryParams));
        }
        // 填充头参数
        if(headerParams!=null){
            for(Entry<String, String> entry:headerParams.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }
    private static List<NameValuePair> paramsConverter(Map<String, String> params) {
        List<NameValuePair> nvps = new LinkedList<NameValuePair>();
        Set<Entry<String, String>> paramsSet = params.entrySet();
        for (Entry<String, String> paramEntry : paramsSet) {
            nvps.add(new BasicNameValuePair(paramEntry.getKey(), paramEntry
                    .getValue()));
        }
        return nvps;
    }

    public static String readStream(InputStream in, String encoding) {
        if (in == null) {
            return null;
        }
        try {
            InputStreamReader inReader = null;
            if (encoding == null) {
                inReader = new InputStreamReader(in, defaultEncoding);
            } else {
                inReader = new InputStreamReader(in, encoding);
            }
            char[] buffer = new char[bufferSize];
            int readLen = 0;
            StringBuffer sb = new StringBuffer();
            while ((readLen = inReader.read(buffer)) != -1) {
                sb.append(buffer, 0, readLen);
            }
            inReader.close();
            return sb.toString();
        } catch (IOException e) {
            log.error("读取返回内容出错", e);
        }
        return null;
    }

    private HttpClientTool() {
        // 设置连接参数

        connConfig = ConnectionConfig.custom()
                .setCharset(Charset.forName(defaultEncoding)).build();

        /**
         * Socket timeout in SocketConfig represents the default value applied to newly created connections. This value can be overwritten for individual requests by setting a non zero value of socket timeout in RequestConfig.
         */
        socketConfig = SocketConfig.custom().setSoTimeout(1000).build();//B 创建socket对象超时，使用SocketConfig . getSoTimeout设置socket的soTimeout.

        requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(1000)  //A 从连接池中获取可用连接超时（4.x新增）

                //B 创建socket对象超时，使用SocketConfig . getSoTimeout设置socket的soTimeout.

                .setConnectTimeout(5000)						//C 与服务器建立网络连接的超时时间：httpclient会创建一个异步线程用以创建socket连接，此处设置该socket的连接超时时间

                //D createLayeredSocket完成SSL握手处理，使用socket的超时时间（就是B设置的SocketConfig . getSoTimeout）

                .setSocketTimeout(5000).build();		//E 通讯过程中的超时时间或等待响应超时（读取数据超时），即建立url连接后，等待放回response的最大时间


        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder
                .<ConnectionSocketFactory> create();
        plainSF = new PlainConnectionSocketFactory();
        registryBuilder.register("http", plainSF);

        // 指定信任密钥存储对象和连接套接字工厂
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            sslContext = SSLContexts.custom().useTLS()
                    .loadTrustMaterial(trustStore, new AnyTrustStrategy()).build();
            sslSF = new SSLConnectionSocketFactory(sslContext,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            registryBuilder.register("https", sslSF);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        registry = registryBuilder.build();
        // 设置连接管理器
        connManager = new PoolingHttpClientConnectionManager(registry);
        connManager.setDefaultConnectionConfig(connConfig);
        connManager.setDefaultSocketConfig(socketConfig);

        // 指定cookie存储对象
        cookieStore = new BasicCookieStore();
        // 构建客户端
        client = HttpClientBuilder.create().setDefaultCookieStore(cookieStore)
                .setConnectionManager(connManager).build();
    }

    static {
        instance = new HttpClientTool();
    }
    public static HttpClientTool getInstance() {
        return instance;
    }

    public InputStream doGetForStream(String url) throws URISyntaxException,
            ClientProtocolException, IOException {
        return doGetForStream(url, null);
    }


    public InputStream doDeleteForStream(String url) throws URISyntaxException,
            ClientProtocolException, IOException {
        return doDeleteForStream(url, null);
    }

    public String doGetForString(String url) throws URISyntaxException,
            ClientProtocolException, IOException {
        return doGetForString(url, null);
    }


    public String doDeleteForString(String url) throws URISyntaxException,
            ClientProtocolException, IOException {
        return doDeleteForString(url, null);
    }

    public InputStream doGetForStream(String url, Map<String, String> queryParams)
            throws URISyntaxException, ClientProtocolException, IOException {
        HttpResponse response = this.doGet(url, queryParams, null);
        return response != null ? response.getEntity().getContent() : null;
    }


    public InputStream doDeleteForStream(String url, Map<String, String> queryParams)
            throws URISyntaxException, ClientProtocolException, IOException {
        HttpResponse response = this.doDelete(url, queryParams, null);
        return response != null ? response.getEntity().getContent() : null;
    }

    public String doGetForString(String url, Map<String, String> queryParams)
            throws URISyntaxException, ClientProtocolException, IOException {
        return doGetForString(url, queryParams, null);
    }


    public String doDeleteForString(String url, Map<String, String> queryParams)
            throws URISyntaxException, ClientProtocolException, IOException {
        return doDeleteForString(url, queryParams, null);
    }

    public String doGetForString(String url, Map<String, String> queryParams, Map<String, String> headerParams)
            throws URISyntaxException, ClientProtocolException, IOException {
        // return HttpClientUtil.readStream(this.doGetForStream(url, queryParams), null);
        String resultString = null;
        HttpResponse response = this.doGet(url, queryParams, headerParams);
        // 判断返回状态是否为200
        if (response.getStatusLine().getStatusCode() == 200) {
            resultString = EntityUtils.toString(response.getEntity(), defaultEncoding);
        }
        return resultString;
    }

    public String doDeleteForString(String url, Map<String, String> queryParams, Map<String, String> headerParams)
            throws URISyntaxException, ClientProtocolException, IOException {
        // return HttpClientUtil.readStream(this.doGetForStream(url, queryParams), null);
        String resultString = null;
        HttpResponse response = this.doDelete(url, queryParams, headerParams);
        // 判断返回状态是否为200
        if (response.getStatusLine().getStatusCode() == 200) {
            resultString = EntityUtils.toString(response.getEntity(), defaultEncoding);
        }
        return resultString;
    }

    /**
     * 基本的Get请求
     *
     * @param url
     *          请求url
     * @param queryParams
     *          请求头的查询参数
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public HttpResponse doGet(String url, Map<String, String> queryParams, Map<String, String> headerParams)
            throws URISyntaxException, ClientProtocolException, IOException {
        return this.doGetOrDelete(url, queryParams, headerParams, 0);
    }
    /**
     * 基本的Delete请求
     *
     * @param url
     *          请求url
     * @param queryParams
     *          请求头的查询参数
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public HttpResponse doDelete(String url, Map<String, String> queryParams, Map<String, String> headerParams)
            throws URISyntaxException, ClientProtocolException, IOException {
        return this.doGetOrDelete(url, queryParams, headerParams, 1);
    }

    /**
     * 基本的Get|Put请求
     *
     * @param url
     *          请求url
     * @param queryParams
     *          请求头的查询参数
     * @Param selectMethod 0-Get 1-Delete
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public HttpResponse doGetOrDelete(String url, Map<String, String> queryParams, Map<String, String> headerParams, int selectMethod)
            throws URISyntaxException, ClientProtocolException, IOException {
        HttpRequestBase method = selectMethod==0?new HttpGet():new HttpDelete();
        method.setConfig(requestConfig);
        URIBuilder builder = new URIBuilder(url);
        wrapParams(builder, method, queryParams, headerParams);
        method.setURI(builder.build());
        return client.execute(method);
    }

    public InputStream doPostForStream(String url, Map<String, String> queryParams)
            throws URISyntaxException, IOException {
        HttpResponse response = this.doPost(url, queryParams, null, null);
        return response != null ? response.getEntity().getContent() : null;
    }

    public InputStream doPutForStream(String url, Map<String, String> queryParams)
            throws URISyntaxException, IOException {
        HttpResponse response = this.doPut(url, queryParams, null, null);
        return response != null ? response.getEntity().getContent() : null;
    }

    public InputStream doPatchForStream(String url, Map<String, String> queryParams)
            throws URISyntaxException, IOException {
        HttpResponse response = this.doPatch(url, queryParams, null, null);
        return response != null ? response.getEntity().getContent() : null;
    }

    public String doPostForString(String url, Map<String, String> queryParams)
            throws URISyntaxException, IOException {
        return readStream(this.doPostForStream(url, queryParams), null);
    }

    public String doPutForString(String url, Map<String, String> queryParams)
            throws URISyntaxException, IOException {
        return readStream(this.doPutForStream(url, queryParams), null);
    }

    public String doPatchForString(String url, Map<String, String> queryParams)
            throws URISyntaxException, IOException {
        return readStream(this.doPatchForStream(url, queryParams), null);
    }

    public String doPostForString(String url, Map<String, String> queryParams, Map<String, String> headerParams)
            throws URISyntaxException, IOException {
        return readStream(this.doPostForStream(url, queryParams, null, headerParams), null);
    }

    public String doPutForString(String url, Map<String, String> queryParams, Map<String, String> headerParams)
            throws URISyntaxException, IOException {
        return readStream(this.doPutForStream(url, queryParams, null, headerParams), null);
    }

    public String doPatchForString(String url, Map<String, String> queryParams, Map<String, String> headerParams)
            throws URISyntaxException, IOException {
        return readStream(this.doPatchForStream(url, queryParams, null, headerParams), null);
    }

    public InputStream doPostForStream(String url,
                                       Map<String, String> queryParams, Map<String, String> formParams, Map<String, String> headerParams)
            throws URISyntaxException, IOException {
        HttpResponse response = this.doPost(url, queryParams, formParams, headerParams);
        return response != null ? response.getEntity().getContent() : null;
    }

    public InputStream doPutForStream(String url,
                                      Map<String, String> queryParams, Map<String, String> formParams, Map<String, String> headerParams)
            throws URISyntaxException, IOException {
        HttpResponse response = this.doPut(url, queryParams, formParams, headerParams);
        return response != null ? response.getEntity().getContent() : null;
    }

    public InputStream doPatchForStream(String url,
                                        Map<String, String> queryParams, Map<String, String> formParams, Map<String, String> headerParams)
            throws URISyntaxException, IOException {
        HttpResponse response = this.doPatch(url, queryParams, formParams, headerParams);
        return response != null ? response.getEntity().getContent() : null;
    }

    public String doPostForString(String url, Map<String, String> queryParams,
                                  Map<String, String> formParams, Map<String, String> headerParams) throws URISyntaxException, IOException {
		/*return readStream(
		    this.doPostForStream(url, queryParams, formParams), null);*/
        String resultString = null;
        HttpResponse response = doPost(url, queryParams, formParams, headerParams);
        // 判断返回状态是否为200
        if (response.getStatusLine().getStatusCode() == 200) {
            resultString = EntityUtils.toString(response.getEntity(), defaultEncoding);
        }
        return resultString;
    }

    public String doPutForString(String url, Map<String, String> queryParams,
                                 Map<String, String> formParams, Map<String, String> headerParams) throws URISyntaxException, IOException {
		/*return readStream(
		    this.doPostForStream(url, queryParams, formParams), null);*/
        String resultString = null;
        HttpResponse response = doPut(url, queryParams, formParams, headerParams);
        // 判断返回状态是否为200
        if (response.getStatusLine().getStatusCode() == 200) {
            resultString = EntityUtils.toString(response.getEntity(), defaultEncoding);
        }
        return resultString;
    }

    public String doPatchForString(String url, Map<String, String> queryParams,
                                   Map<String, String> formParams, Map<String, String> headerParams) throws URISyntaxException, IOException {
		/*return readStream(
		    this.doPostForStream(url, queryParams, formParams), null);*/
        String resultString = null;
        HttpResponse response = doPatch(url, queryParams, formParams, headerParams);
        // 判断返回状态是否为200
        if (response.getStatusLine().getStatusCode() == 200) {
            resultString = EntityUtils.toString(response.getEntity(), defaultEncoding);
        }
        return resultString;
    }

    /**
     * 基本的Post请求
     *
     * @param url
     *          请求url
     * @param queryParams
     *          请求头的查询参数
     * @param formParams
     *          post表单的参数
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public HttpResponse doPost(String url, Map<String, String> queryParams,
                               Map<String, String> formParams, Map<String, String> headerParams) throws URISyntaxException,
            ClientProtocolException, IOException {
        return doPost(url, queryParams, formParams, null, headerParams);
    }

    /**
     * 基本的Put请求
     *
     * @param url
     *          请求url
     * @param queryParams
     *          请求头的查询参数
     * @param formParams
     *          put表单的参数
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public HttpResponse doPut(String url, Map<String, String> queryParams,
                              Map<String, String> formParams, Map<String, String> headerParams) throws URISyntaxException,
            ClientProtocolException, IOException {
        return doPut(url, queryParams, formParams, null, headerParams);
    }

    /**
     * 基本的Patch请求
     *
     * @param url
     *          请求url
     * @param queryParams
     *          请求头的查询参数
     * @param formParams
     *          put表单的参数
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public HttpResponse doPatch(String url, Map<String, String> queryParams,
                                Map<String, String> formParams, Map<String, String> headerParams) throws URISyntaxException,
            ClientProtocolException, IOException {
        return doPatch(url, queryParams, formParams, null, headerParams);
    }

    /**
     * 基本的Post请求
     *
     * @param url
     *          请求url
     * @param queryParams
     *          请求头的查询参数
     * @param formParams
     *          post表单的参数
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public HttpResponse doPost(String url, Map<String, String> queryParams,
                               Map<String, String> formParams, RequestConfig config, Map<String, String> headerParams) throws URISyntaxException,
            ClientProtocolException, IOException {
        return this.doEnclosingRequest(url, queryParams, formParams, config, headerParams, 0);
    }

    /**
     * 基本的Put请求
     *
     * @param url
     *          请求url
     * @param queryParams
     *          请求头的查询参数
     * @param formParams
     *          post表单的参数
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public HttpResponse doPut(String url, Map<String, String> queryParams,
                              Map<String, String> formParams, RequestConfig config, Map<String, String> headerParams) throws URISyntaxException,
            ClientProtocolException, IOException {
        return this.doEnclosingRequest(url, queryParams, formParams, config, headerParams, 1);
    }

    /**
     * 基本的Patch请求
     *
     * @param url
     *          请求url
     * @param queryParams
     *          请求头的查询参数
     * @param formParams
     *          post表单的参数
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public HttpResponse doPatch(String url, Map<String, String> queryParams,
                                Map<String, String> formParams, RequestConfig config, Map<String, String> headerParams) throws URISyntaxException,
            ClientProtocolException, IOException {
        return this.doEnclosingRequest(url, queryParams, formParams, config, headerParams, 2);
    }
    /**
     * 基本的Post|Put|Patch请求
     *
     * @param url
     *          请求url
     * @param queryParams
     *          请求头的查询参数
     * @param formParams
     *          post表单的参数
     * @Param selectMethod 0-Post 1-Put 2-Patch
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public HttpResponse doEnclosingRequest(String url, Map<String, String> queryParams,
                                           Map<String, String> formParams, RequestConfig config, Map<String, String> headerParams, int selectMethod) throws URISyntaxException,
            ClientProtocolException, IOException {
        HttpEntityEnclosingRequestBase method =  selectMethod==0 ?new HttpPost(): selectMethod==1?new HttpPut():new HttpPatch();
        if(config == null) {
            config = this.requestConfig;
        }
        method.setConfig(config);
        URIBuilder builder = new URIBuilder(url);
        wrapParams(builder, method, queryParams, headerParams);
        method.setURI(builder.build());
        // 填入表单参数
        if (formParams != null && !formParams.isEmpty()) {
            method.setEntity(new UrlEncodedFormEntity(HttpClientTool
                    .paramsConverter(formParams)));
        }
        return client.execute(method);
    }
    /**
     * 基本Post JSON请求
     *
     * @param url
     *          请求url
     * @param queryParams
     *          请求头的查询参数
     * @param json
     *          直接放入post请求体中的文本(请使用JSON)
     * @Param selectMethod 0-Post 1-Put
     * @return
     * @throws URISyntaxException
     */
    public HttpResponse doPostOrPutJson(String url, Map<String, String> queryParams,
                                        String json, Map<String, String> headerParams, int selectMethod) throws URISyntaxException, ClientProtocolException,
            IOException {
        HttpEntityEnclosingRequestBase method = selectMethod==0?new HttpPost():new HttpPut();
        URIBuilder builder = new URIBuilder(url);
        wrapParams(builder, method, queryParams, headerParams);
        method.setURI(builder.build());
        // 填入post json数据
        if (!StringTool.isBlank(json)) {
            // 下面的ContentType完整类名为:org.apache.http.entity.ContentType
            method.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        }
        return client.execute(method);
    }
    /**
     * 多块Post请求
     *
     * @param url
     *          请求url
     * @param queryParams
     *          请求头的查询参数
     * @param formParts
     *          post表单的参数,支持字符串-文件(FilePart)和字符串-字符串(StringPart)形式的参数
     * @Param selectMethod 0-Post 1-Put
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws HttpException
     * @throws IOException
     */
    public HttpResponse multipartPostOrPut(String url,
                                           Map<String, String> queryParams, List<FormBodyPart> formParts, Map<String, String> headerParams, int selectMethod)
            throws URISyntaxException, ClientProtocolException, IOException {
        HttpEntityEnclosingRequestBase method = selectMethod == 0?new HttpPost():new HttpPut();

        method.setConfig(this.requestConfig);

        URIBuilder builder = new URIBuilder(url);
        wrapParams(builder, method, queryParams, headerParams);
        method.setURI(builder.build());
        // 填入表单参数
        if (formParts != null && !formParts.isEmpty()) {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder = entityBuilder
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            for (FormBodyPart formPart : formParts) {
                entityBuilder = entityBuilder.addPart(formPart.getName(),
                        formPart.getBody());
            }
            method.setEntity(entityBuilder.build());
        }
        return client.execute(method);
    }

    /**
     * 获取当前Http客户端状态中的Cookie
     *
     * @param domain
     *          作用域
     * @param port
     *          端口 传null 默认80
     * @param path
     *          Cookie路径 传null 默认"/"
     * @param useSecure
     *          Cookie是否采用安全机制 传null 默认false
     * @return
     */
    public Map<String, Cookie> getCookie(String domain, Integer port,
                                         String path, Boolean useSecure) {
        if (domain == null) {
            return null;
        }
        if (port == null) {
            port = 80;
        }
        if (path == null) {
            path = "/";
        }
        if (useSecure == null) {
            useSecure = false;
        }
        List<Cookie> cookies = cookieStore.getCookies();
        if (cookies == null || cookies.isEmpty()) {
            return null;
        }

        CookieOrigin origin = new CookieOrigin(domain, port, path, useSecure);
        BestMatchSpec cookieSpec = new BestMatchSpec();
        Map<String, Cookie> retVal = new HashMap<String, Cookie>();
        for (Cookie cookie : cookies) {
            if (cookieSpec.match(cookie, origin)) {
                retVal.put(cookie.getName(), cookie);
            }
        }
        return retVal;
    }

    /**
     * 批量设置Cookie
     *
     * @param cookies
     *          cookie键值对图
     * @param domain
     *          作用域 不可为空
     * @param path
     *          路径 传null默认为"/"
     * @param useSecure
     *          是否使用安全机制 传null 默认为false
     * @return 是否成功设置cookie
     */
    public boolean setCookie(Map<String, String> cookies, String domain,
                             String path, Boolean useSecure) {
        synchronized (cookieStore) {
            if (domain == null) {
                return false;
            }
            if (path == null) {
                path = "/";
            }
            if (useSecure == null) {
                useSecure = false;
            }
            if (cookies == null || cookies.isEmpty()) {
                return true;
            }
            Set<Entry<String, String>> set = cookies.entrySet();
            String key = null;
            String value = null;
            for (Entry<String, String> entry : set) {
                key = entry.getKey();
                if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
                    throw new IllegalArgumentException(
                            "cookies key and value both can not be empty");
                }
                BasicClientCookie cookie = new BasicClientCookie(key, value);
                cookie.setDomain(domain);
                cookie.setPath(path);
                cookie.setSecure(useSecure);
                cookieStore.addCookie(cookie);
            }
            return true;
        }
    }



    /**
     * 设置单个Cookie
     *
     * @param key
     *          Cookie键
     * @param value
     *          Cookie值
     * @param domain
     *          作用域 不可为空
     * @param path
     *          路径 传null默认为"/"
     * @param useSecure
     *          是否使用安全机制 传null 默认为false
     * @return 是否成功设置cookie
     */
    public boolean setCookie(String key, String value, String domain,
                             String path, Boolean useSecure) {
        Map<String, String> cookies = new HashMap<String, String>();
        cookies.put(key, value);
        return setCookie(cookies, domain, path, useSecure);
    }

}
