package com.tengjiao.seed.member.security;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tengjiao.tool.indep.HttpTool;
import com.tengjiao.tool.indep.model.SystemException;
import com.tengjiao.tool.third.http.HttpClientTool;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Map;

import static com.tengjiao.seed.member.security.Constants.ServiceHeaders.*;

/**
 *
 * @description
 * @author tengjiao
 * @date 2020/6/9 19:22
 *
 * 如何使用：
 * 控制器测试类继承此类，然后在对应工程路径下建立config.json，内容格式如下：
 * {
 *   "test:{
 *     "loginAccount":{
 *       "username":"master",
 *       "password":"14e1b600b1fd579f47433b88e8d85291"
 *     },
 *     "defaultSalt":"rise",
 *     "baseUrl":"http://127.0.0.1:8080",
 *     "serverTimeUri":"/server_time"
 *   }
 * }
 */
public abstract class BaseControllerTest {
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  protected final String getUrl(String uri) throws IOException {return uri.startsWith("http")?uri: baseUrl(false) +uri;}

  /**
   * 测试配置文件
   */
  public static final String API_TESTING_CONFIG_FILE_NM = "config.json";

  public static final String
    LOGIN_TOKEN_FILE_NM = "loginToken.dat",
    USER_INFO_FILE_NM = "userInfo.dat";


  private JSONObject configMap = null;

  private void loadConfig() throws IOException {
    String config = FileUtils.readFileToString(new File(API_TESTING_CONFIG_FILE_NM), Charset.forName("UTF-8"));
    configMap = JSON.parseObject(config);
  }

  protected JSONObject readLoginAccount(boolean reload) throws IOException {
    if(configMap == null || reload) {
      loadConfig();
    }
    return configMap.getJSONObject("loginAccount");
  }
  protected JSONObject readLoginAccount() throws IOException {
    return readLoginAccount(false);
  }

  protected String getProperty(String propertyName, boolean reload) throws IOException {
    if(configMap == null || reload) {
      loadConfig();
    }
    return configMap.getString(propertyName);
  }

  /**
   * 默认盐
   * @param reload
   * @return
   * @throws IOException
   */
  protected String defaultSalt(boolean reload) throws IOException {

    return getProperty("defaultSalt", reload);

  }

  /**
   * api 基础URL
   * @param reload
   * @return
   * @throws IOException
   */
  protected String baseUrl(boolean reload) throws IOException {

    return getProperty("baseUrl", reload);
  }

  /**
   * api 时间服务URI
   * @param reload
   * @return
   * @throws IOException
   */
  protected String serverTimeUri(boolean reload) throws IOException {

    return getProperty("serverTimeUri", reload);
  }


  protected String readLoginToken() throws IOException {
    String loginToken = FileUtils.readFileToString(new File(LOGIN_TOKEN_FILE_NM), Charset.forName("UTF-8"));
    return loginToken;
  }
  protected void writeLoginToken(String loginToken) throws IOException {
    FileUtils.write(new File(LOGIN_TOKEN_FILE_NM), loginToken, Charset.forName("UTF-8"));
  }
  protected JSONObject readUserInfo() throws IOException {
    String userInfoJsonString = FileUtils.readFileToString(new File(USER_INFO_FILE_NM), Charset.forName("UTF-8"));
    return JSONObject.parseObject(userInfoJsonString);
  }
  protected void writeUserInfo(JSONObject jsonObject) throws IOException {
    FileUtils.write(new File(USER_INFO_FILE_NM), jsonObject.toJSONString(), Charset.forName("UTF-8"));
  }

  protected long getServerTime() {
    try {
      String string = HttpTool.get(getUrl(serverTimeUri(false)),2000, 2000);
      JSONObject object = JSON.parseObject(string);
      long serverTime = object.getLongValue("data");

      return serverTime;
    }catch (IOException e) {
      log.error(e.getMessage());
    }
    return 0L;
  }

  protected void fillHeaders(Map<String, String> headerParams) throws IOException {
    fillHeaders(headerParams, 0);
  }
  protected void fillHeaders(Map<String, String> headerParams, long timestamp) throws IOException {

    headerParams.put(TOKEN_KEY, readLoginToken());
    fillNHeaders(headerParams, timestamp);
  }

  protected void fillHeaders(Map<String, String> headerParams, String device, String terminal, String version, long timestamp) throws IOException {
    fillHeaders(headerParams, null, device, terminal, version, timestamp);
  }

  protected void fillHeaders(Map<String, String> headerParams, Integer stationId, String device, String terminal, String version, long timestamp) throws IOException {
    headerParams.put(TOKEN_KEY, readLoginToken());
    fillNHeaders(headerParams, stationId, device, terminal, version, timestamp);
  }

  protected void fillNHeaders(Map<String, String> headerParams) {
    fillNHeaders(headerParams, 0);
  }

  protected void fillNHeaders(Map<String, String> headerParams, long timestamp) {

    if(timestamp != 0) {
      headerParams.put(TIMESTAMP_KEY, String.valueOf(timestamp));
    }
  }

  protected void fillNHeaders(Map<String, String> headerParams, String device, String terminal, String version, long timestamp) throws IOException {
    fillNHeaders(headerParams, null, device, terminal, version, timestamp);
  }

  protected void fillNHeaders(Map<String, String> headerParams, Integer stationId, String device, String terminal, String version, long timestamp) throws IOException {
    if (stationId != null) {
      headerParams.put(SAAS_KEY, String.valueOf(stationId));
    }
    if (device != null) {
      headerParams.put(DEVICE_KEY, device);
    }
    if (terminal != null) {
      headerParams.put(TERMINAL_KEY, terminal);
    }
    if (version != null) {
      headerParams.put(VERSION_KEY, version);
    }
    if (timestamp != 0) {
      headerParams.put(TIMESTAMP_KEY, String.valueOf(timestamp));
    }
  }

  /**
   *
   * @param apiUri
   * @param paramMap
   * @param headerParams
   * @param mode 0-GET 1-POST 2-PUT 3-PATCH
   * @param salt
   * @return
   * @throws IOException
   * @throws URISyntaxException
   */
  protected String testTemplate(String apiUri, Map<String, String> paramMap, Map<String, String> headerParams, int mode, String salt) throws IOException, URISyntaxException {

    StringBuilder strBuf = new StringBuilder(128);

    for(Map.Entry<String, String> e: paramMap.entrySet()) {
      strBuf.append(e.getValue());
    }

    for(Map.Entry<String, String> e: headerParams.entrySet()) {
      strBuf.append(e.getValue());
    }

    // 消息加盐
    strBuf.append(StringUtils.isEmpty(salt)?defaultSalt(false):salt);

    // 消息摘要签名放进请求头
    String signature = DigestUtils.md5DigestAsHex(strBuf.toString().getBytes());
    headerParams.put(SIGNATURE_KEY, signature);

    // Content-Type 不参与消息摘要签名
    headerParams.put("Content-Type", "application/json;charset=utf-8");
    String string = null;
    switch (mode){
      case 0:
        string = HttpClientTool.getInstance().doGetForString(getUrl(apiUri), paramMap, headerParams);
        break;
      case 1:
        string = HttpClientTool.getInstance().doPostForString(getUrl(apiUri), paramMap, headerParams);
        break;
      case 2:
        string = HttpClientTool.getInstance().doPutForString(getUrl(apiUri), paramMap, headerParams);
        break;
      case 3:
        string = HttpClientTool.getInstance().doPatchForString(getUrl(apiUri), paramMap, headerParams);
        break;
      default :
        throw SystemException.create("illegal param error: mode");
    }
    System.out.println(string);
    return string;
  }

  protected String testTemplate(String apiUri, Map<String, String> paramMap, Map<String, String> headerParams, int mode) throws IOException, URISyntaxException {

    return testTemplate(apiUri, paramMap, headerParams, mode, defaultSalt(false));
  }

}
