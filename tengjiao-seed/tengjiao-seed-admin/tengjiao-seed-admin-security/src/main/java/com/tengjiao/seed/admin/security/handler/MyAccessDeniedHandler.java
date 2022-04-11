package com.tengjiao.seed.admin.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tengjiao.tool.indep.model.R;
import com.tengjiao.tool.indep.model.RC;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 用来解决 认证过 的用户访问 无权限 资源时的异常
 * 这个时候会throw AccessDeniedException
 * 注意：SpringSecurity标注@PreAuthorize控制层判定无操作权限时在此捕获，
 * 但是最好在spring全局异常捕获也处理一下@ExceptionHandler(AccessDeniedException.class)避免异常没有被系统捕获
 */
@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse resp,
                       AccessDeniedException e) throws IOException {
        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        R result = RC.FAILURE.toR().setMessage(e.getMessage()).setCode(resp.getStatus());
        out.write(new ObjectMapper().writeValueAsString(result));
        out.flush();
        out.close();
    }
}