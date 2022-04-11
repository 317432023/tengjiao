package com.tengjiao.part.sample.ssj.controller;

import com.tengjiao.tool.core.model.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
//import starter.core.aspect.ParamSecurityRequired;

import javax.servlet.http.HttpServletRequest;

/**
 * @author kangtengjiao
 */
@Controller
public class SampleUserController {

  @GetMapping("/get")@ResponseBody
  public Object get(HttpServletRequest request, String y) {
    return request.getQueryString();
  }

  @PostMapping("/post")
  @ResponseBody
  //@ParamSecurityRequired(expires=0)
  public Object post(HttpServletRequest request, String y) {
    return ResultGenerator.success(request.getParameter("bt"));
  }

}
