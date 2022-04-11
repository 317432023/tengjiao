package com.tengjiao.part.springmvc;

import com.tengjiao.tool.indep.StringTool;
import com.tengjiao.tool.indep.model.R;
import com.tengjiao.tool.indep.model.RC;
import com.tengjiao.tool.indep.model.SystemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器的使用<br>
 *   <p>
 *     1、springmvc：添加到控制器扫描路径后面<br>
 *     2、springboot：放置springboot扫码路径中，或添加到额外扫码路径中
 *   </p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {
  private final static Logger log = LogManager.getLogger(GlobalExceptionHandler.class);

  private final static String SYS_ERR_VIEW = "error/error";
  private final static String PAR_ERR_VIEW = "error/400";
  private final static String DEFAULT_VIEW = "error/500";

  /**
   * 将errors对象转换为Result输出,验证错误转为map
   */
  private static R<Map<String, String>> convertError(Exception e) {
    R<Map<String, String>> result = RC.PARAM_ERR.toR();
    Errors error;
    if(e instanceof MethodArgumentNotValidException) {
      MethodArgumentNotValidException e1 = (MethodArgumentNotValidException)e;
      error = e1.getBindingResult();
    }else if(e instanceof BindException) {
      BindException e2 = (BindException)e;
      error =  e2.getBindingResult();
    }else {
      return result;
    }

    StringBuilder errorMsg = new StringBuilder(200);

    if (error.hasErrors()) {


      Map<String, String> errMap = new HashMap<>();

      if (error.hasGlobalErrors()) {
        StringBuilder globalMsg = new StringBuilder();
        for (ObjectError ferr : error.getGlobalErrors()) {
          globalMsg.append(ferr.getDefaultMessage()).append(";");
        }

        errorMsg.append( globalMsg );

        errMap.put("_global_msg", globalMsg.toString());
      }

      if (error.hasFieldErrors()) {
        for (FieldError ferr : error.getFieldErrors()) {

          errMap.put(ferr.getField(), ferr.getDefaultMessage());

        }
      }
      result
        .setMessage(errorMsg.toString())
        .setData(errMap)
      ;

    }

    return result;
  }

  private static ModelAndView modelAndView(Exception e, boolean isJson, HttpServletRequest request, HttpServletResponse response) {

    final boolean isSystem = e instanceof SystemException;
    final boolean isParamError = e instanceof MethodArgumentTypeMismatchException;
    final boolean isValidError = e instanceof MethodArgumentNotValidException||e instanceof BindException;
    final boolean isNotFound = e instanceof NoHandlerFoundException;
    final int errorCode = isSystem?((SystemException)e).getCode():(isParamError||isValidError?400:(isNotFound ? 404:500));

    // begin for 打印异常日志
    log.error(errorCode +" error, message: " + e.getMessage(), e);
    if(log.isInfoEnabled()) {
      StringBuilder parametersBuf = new StringBuilder(128);
      for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
        String[] values = request.getParameterValues(entry.getKey());
        if(values != null) {
          for (String value : values) {
            if(entry.getKey().equals("password")) {
              value = "*";
            }
            parametersBuf.append(entry.getKey()).append('=').append(value).append(',');
          }
        }
      }
      final int len = parametersBuf.length();
      if (len > 0) {
        parametersBuf.deleteCharAt(len - 1);
      }
      log.info("Request Failed, URL["+request.getRequestURI()+"], Param(s){"+parametersBuf.toString()+"}");
    }
    // end for 打印异常日志

    ModelAndView mv = new ModelAndView();
    if(isJson){
      mv.setView(new MappingJackson2JsonView());
      Map<String,Object> jsonMap = new HashMap<>();
      if(isValidError) {
        R<Map<String, String>> result = convertError(e);
        jsonMap.put("code", result.getCode());
        jsonMap.put("message", result.getMessage());
        jsonMap.put("data", result.getData());
      }else {
        jsonMap.put("code", errorCode);
        jsonMap.put("message", isSystem?e.getMessage():"内部服务器错误");
        jsonMap.put("data", request.getRequestURL());
      }

      mv.addAllObjects(jsonMap);

      // 重新设定响应状态码 与 json code 保持一致
      //response.setStatus(errorCode/*HttpStatus.INTERNAL_SERVER_ERROR.value()*/);

    }else{
      mv.addObject("message", isParamError?"参数错误":e.getMessage());
      mv.addObject("url", request.getRequestURL());
      mv.addObject("exception", e);
      mv.setViewName(isSystem?SYS_ERR_VIEW:(isParamError?PAR_ERR_VIEW:DEFAULT_VIEW));
    }

    return mv;
  }

  /**
   * 全局异常处理,根据请求头决定是返回Json还是错误页面
   * @param e
   * @param request
   * @param response
   * @return
   */
  @ExceptionHandler(Exception.class)
  public ModelAndView resolveException(Exception e, HttpServletRequest request, HttpServletResponse response) {
    // 客户端要求接收json 或者 客户端发起的是一个ajax请求，则需要返回json
    final String acceptHeader = request.getHeader("accept");
    final String requestWithHeader = request.getHeader("X-Requested-With");
    boolean isJson = StringTool.isBlank(acceptHeader)
            || (acceptHeader.toLowerCase().contains("application/json")
            || "XMLHttpRequest".equalsIgnoreCase(requestWithHeader))
            ;

    if(!isJson) {
      Object o = request.getAttribute("TEMPLATE-VIEW");
      if (o != null) {
        boolean isView = (Boolean) o;
        isJson = !isView;
      }
    }
    return modelAndView(e, isJson, request, response);
  }

}
