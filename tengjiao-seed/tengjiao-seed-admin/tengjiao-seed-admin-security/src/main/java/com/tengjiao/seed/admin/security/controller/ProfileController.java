package com.tengjiao.seed.admin.security.controller;

import com.tengjiao.part.springmvc.ResponseResult;
import com.tengjiao.seed.admin.security.SecurityUtil;
import com.tengjiao.seed.admin.security.service.ProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ProfileController
 * @Description 个人相关操作_控制器
 * @Author Administrator
 * @Date 2021/3/17 22:06
 * @Version V1.0
 */
@RestController @ResponseResult
@RequestMapping("profile")
@Api(tags="sec-个人中心")
@AllArgsConstructor
public class ProfileController {

  private ProfileService profileService;

  @PutMapping("modifyPassword")
  @ApiOperation("修改密码")
  public void modifyPassword(@RequestParam String password, @RequestParam String newPassword) {

    // 取当前登录用户ID
    Long userId = SecurityUtil.getCurrentUser().getId();

    // 修改密码
    profileService.modifyPassword(userId, password, newPassword);

    // 重新登录
    //SecurityUtil.logout();

  }



}
