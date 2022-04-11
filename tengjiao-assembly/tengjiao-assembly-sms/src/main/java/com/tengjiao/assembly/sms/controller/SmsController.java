package com.tengjiao.assembly.sms.controller;

import com.tengjiao.assembly.sms.config.SmsProperties;
import com.tengjiao.comm.sms.model.CommSmsDTO;
import com.tengjiao.comm.sms.service.ICommSmsService;
import com.tengjiao.part.redis.ModeDict;
import com.tengjiao.part.redis.RedisTool;
import com.tengjiao.tool.indep.RegexTool;
import com.tengjiao.tool.indep.model.R;
import com.tengjiao.tool.indep.model.SystemException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;

import static com.tengjiao.comm.Constants.SMS_CODE_KEY_PREFIX;

/**
 * @author tengjiao
 * @description
 * @date 2021/9/7 19:31
 */
@Api(value = "pub-公共服务", tags = {"pub-公共服务"})
@CrossOrigin
@RestController
@RequestMapping("/sms")
public class SmsController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private RedisTool redisTool;
    private SmsProperties smsProperties;
    private ICommSmsService commSmsService;

    public SmsController(RedisTool redisTool, SmsProperties smsProperties, ICommSmsService commSmsService) {
        this.redisTool = redisTool;
        this.smsProperties = smsProperties;
        this.commSmsService = commSmsService;
    }

    /**
     * 生成短信验证码
     * @param smsCodeSource
     * @param smsCodeSize
     * @return
     */
    private static String generateCode( String smsCodeSource, int smsCodeSize ) {
        SecureRandom random = new SecureRandom();
        final int len = smsCodeSource.length();
        char[] chars = new char[smsCodeSize];
        for( int i=0; i<smsCodeSize; i++) {
            final int index = random.nextInt(len);
            chars[i] = smsCodeSource.charAt(index);
        }
        return String.valueOf(chars);
    }

    /**
     * 发送短信验证码
     * @description 验证码在有效时间内未使用禁止重复获取
     * @param mobile
     * @return
     */
    @ApiOperation(value="发送短信验证码",/*tags={"发送短信验证码"},*/notes="注意开启参数签名验证的情况下，需要参数签名(文件域不需要签名)")
    @ApiImplicitParams({
      @ApiImplicitParam(name = "mobile", value = "手机号码", dataType = "string", required = true, paramType = "query"),
    })
    @PostMapping("/sendCode")
    public R sendCode(@RequestParam String mobile ) {

        if( !RegexTool.isMobile(mobile) ) {
            throw SystemException.create(mobile + " 不是合法的手机号码");
        }

        if(!StringUtils.isEmpty( redisTool.getString( SMS_CODE_KEY_PREFIX + mobile, ModeDict.APP_GROUP ) ) ) {
            throw SystemException.create("频繁获取手机验证码，请使用上次获取到的验证码，如未收到请隔一段时间后重试");
        }

        final String smsCode = generateCode(smsProperties.getSmsCodeSource(), smsProperties.getSmsCodeSize());
        CommSmsDTO dto = new CommSmsDTO();
        dto.setMobile( mobile );
        dto.setContent( "{\"code\":\"" + smsCode + "\"}" );
        dto.setTempletId( smsProperties.getSmsCodeTempletId() );
        commSmsService.pushOneForSend(dto);

        redisTool.setString( SMS_CODE_KEY_PREFIX + mobile, smsCode, ModeDict.APP_GROUP, smsProperties.getSmsCodeDuration()*60);

        return new R<Void>().setMessage("短信验证码已发送");
    }

}
