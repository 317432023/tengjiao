package com.tengjiao.assembly.captcha.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import com.alibaba.fastjson.JSON;
import com.tengjiao.assembly.captcha.config.CaptchaProperties;
import com.tengjiao.part.redis.ModeDict;
import com.tengjiao.part.redis.RedisTool;
import com.tengjiao.tool.indep.model.R;
import com.tengjiao.tool.indep.web.CaptchaTool;
import com.tengjiao.tool.indep.web.CookieTool;
import com.tengjiao.tool.indep.web.PrintTool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static com.tengjiao.comm.Constants.*;

@Api(value = "pub-公共服务", tags = {"pub-公共服务"})
@CrossOrigin
@Slf4j
@RestController
public class CaptchaController {
    private CaptchaProperties captchaProperties;
    private RedisTool redisTool;

    public CaptchaController(CaptchaProperties captchaProperties, RedisTool redisTool) {
        this.captchaProperties = captchaProperties;
        this.redisTool = redisTool;
    }


    /**
     * 图形验证码JSON(返回json附带captcha令牌，建议只app端使用而不要用于h5端)
     *
     * @param request
     * @param response
     * @param width
     * @param height
     * @param mode     0或null-打印base64图片json; 1-打印base64图片; 其他-输出图片文件流;
     */
    @ApiOperation(value="获取图形验证码",/*tags={"获取图形验证码"},*/notes="注意开启参数签名验证的情况下，需要参数签名")
    @ApiImplicitParams({
      @ApiImplicitParam(name = "width", value = "宽度像素", dataType = "int", required = false, paramType = "query"),
      @ApiImplicitParam(name = "height", value = "高度像素", dataType = "int", required = false, paramType = "query"),
      @ApiImplicitParam(name = "mode", value = "图形验证码输出模式0-json带头令牌（默认），1-base64字符串带cookie令牌， 其他-文件流", dataType = "int", required = false, paramType = "query")
    })
    @GetMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response,
                        Integer width, Integer height, Integer mode) {
        final int[] wh = CaptchaTool.getWh(width, height);

        // 验证码令牌
        String captchaToken = UUID.randomUUID().toString();

        // 验证码
        //String code = CaptchaUtils.generateCaptcha(SIZE, SOURCES);
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(wh[0], wh[1]);
        captcha.setGenerator(new RandomGenerator(captchaProperties.getCaptchaSize()));
        String code = captcha.getCode();


        // 验证码放进内存中
        redisTool.setString(CAPTCHA_TOKEN_PREFIX + captchaToken, code, ModeDict.APP, captchaProperties.getCaptchaDuration() * 60);

        // 写图片文本 或 写图片输出流
        if (mode == null || mode == 0) {

            response.setHeader(CAPTCHA_TOKEN_NAME, captchaToken);

            // 验证码令牌放进客户端响应头中
            final String base64 = BASE_64_IMG_PREFIX + captcha.getImageBase64();

            R<String> result = new R<>(base64);
            String json = JSON.toJSONString(result);
            PrintTool.printJson(response, json);

        } else if (mode == 1) {

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