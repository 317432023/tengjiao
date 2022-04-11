package com.tengjiao.part.springmvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tengjiao.tool.indep.model.R;
import com.tengjiao.tool.indep.model.SystemException;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author kangtengjiao
 */
@ControllerAdvice
public class ResponseResultHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> aClass) {
        // returnType.getGenericParameterType();
        java.lang.reflect.Type type = returnType.getParameterType();
        ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        ResponseResult responseResultAnn = (ResponseResult)sra.getRequest().getAttribute("RESPONSE-RESULT-ANN");
        // 非Result类型并且含有@ResponseResult标注的方法或类才需要包装成Result对象（见GlobalIntercepter，必须是JSON视图才能拿到@ResponseResult标注）
        return !type.equals(R.class) && responseResultAnn != null;
    }

    @Override
    public Object beforeBodyWrite(Object data, MethodParameter returnType, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest request, ServerHttpResponse response) {
        if (returnType.getGenericParameterType().equals(String.class)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.writeValueAsString(new R<>().setData(data));
            } catch (JsonProcessingException e) {
                throw SystemException.create("返回String类型错误");
            }
        }
        return new R<>().setData(data);
    }
}