package com.tengjiao.part.springmvc;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author kangtengjiao
 */
@Component
public class GlobalInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod hm = (HandlerMethod) handler;
        Method method = hm.getMethod();
        Class<?> beanType = hm.getBeanType();
        Class<?> returnType = method.getReturnType();

        boolean isPage = returnType.equals(String.class) || returnType.equals(ModelAndView.class);
        boolean isNotRespBodyMethod = !method.isAnnotationPresent(ResponseBody.class);
        boolean isController = (!beanType.isAnnotationPresent(RestController.class)
                && !beanType.isAnnotationPresent(ResponseBody.class)
                && beanType.isAnnotationPresent(Controller.class));

        boolean isTemplateView = isPage && isNotRespBodyMethod && isController;

        request.setAttribute("TEMPLATE-VIEW", isTemplateView);

        // 如果是 JSON 视图
        if(!isTemplateView) {
            if(beanType.isAnnotationPresent(ResponseResult.class)) {
                request.setAttribute("RESPONSE-RESULT-ANN",beanType.getAnnotation(ResponseResult.class));
            }else if(method.isAnnotationPresent(ResponseResult.class)) {
                request.setAttribute("RESPONSE-RESULT-ANN",method.getAnnotation(ResponseResult.class));
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //ignore
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //ignore
    }

}