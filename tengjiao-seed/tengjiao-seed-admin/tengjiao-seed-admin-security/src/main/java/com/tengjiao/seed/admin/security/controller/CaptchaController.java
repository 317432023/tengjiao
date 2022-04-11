package com.tengjiao.seed.admin.security.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import com.alibaba.fastjson.JSON;
import com.tengjiao.part.redis.ModeDict;
import com.tengjiao.part.redis.RedisTool;
import com.tengjiao.seed.admin.security.config.SettingProperties;
import com.tengjiao.tool.indep.model.R;
import com.tengjiao.tool.indep.web.CaptchaTool;
import com.tengjiao.tool.indep.web.CookieTool;
import com.tengjiao.tool.indep.web.PrintTool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.tengjiao.comm.Constants.*;

/**
 * CaptchaController
 * <p>验证码</p>
 *
 * @author Administrator
 * @since 2020/11/15 19:48
 */
@Slf4j
@RestController
@Api(value="sec-认证权限",tags="sec-认证权限")
public class CaptchaController {
  private SettingProperties settingProperties;
  private RedisTool redisTool;

  public CaptchaController(SettingProperties settingProperties, RedisTool redisTool) {
    this.settingProperties = settingProperties;
    this.redisTool = redisTool;
  }

//  /** 图形验证码长度 */
//  @Value("${captchaSize}")
//  private Integer captchaSize;
//  /** 图形验证码有效期（分钟） */
//  @Value("${captchaDuration}")
//  private Integer captchaDuration;

  /**
   * 图形验证码JSON(返回json附带captcha令牌，建议只app端使用而不要用于h5端)
   * @param request
   * @param response
   * @param width
   * @param height
   * @param mode 0或null-打印base64图片json; 1-打印base64图片; 其他-输出图片文件流;
   */
  @GetMapping("/captcha")
  @ApiOperation("验证码")
  public void captcha(HttpServletRequest request, HttpServletResponse response,
                   Integer width, Integer height, Integer mode) {
    final int[] wh = CaptchaTool.getWh(width, height);

    // 验证码令牌
    String captchaToken = UUID.randomUUID().toString();

    ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(wh[0], wh[1]);
    captcha.setGenerator(new RandomGenerator(settingProperties.getCaptchaSize()));
    String code = captcha.getCode();

    // 验证码放进内存中
    redisTool.setString(CAPTCHA_TOKEN_PREFIX + captchaToken, code, ModeDict.APP, settingProperties.getCaptchaDuration()*60);

    // 写图片文本 或 写图片输出流
    if(mode == null || mode == 0) {
      final String base64 = BASE_64_IMG_PREFIX + captcha.getImageBase64();

      Map<String, Object> captchaMap = new HashMap<>(2);
      captchaMap.put(CAPTCHA_TOKEN_NAME, captchaToken);
      captchaMap.put("content", base64);
      R result = new R(captchaMap);
      String json = JSON.toJSONString(result);
      PrintTool.printJson(response, json);

    } else if(mode == 1) {

      final String base64 = BASE_64_IMG_PREFIX + captcha.getImageBase64();

      // 验证码令牌放进客户端cookie中
      CookieTool.setCookie(request, response, CAPTCHA_TOKEN_NAME, captchaToken);
      PrintTool.print(response, base64, "image/png");

    } else {

      response.setContentType("image/png");
      // 验证码令牌放进客户端cookie中
      CookieTool.setCookie(request, response, CAPTCHA_TOKEN_NAME, captchaToken);
      // 写图片输出流
      try {
        captcha.write(response.getOutputStream());
      } catch (IOException e) {
        log.error("generate captcha error.", e);
      }

    }

  }

}
