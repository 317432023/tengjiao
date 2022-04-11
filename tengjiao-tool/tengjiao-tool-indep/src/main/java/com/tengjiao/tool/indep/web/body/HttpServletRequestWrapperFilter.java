package com.tengjiao.tool.indep.web.body;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author kangtengjiao
 */
@WebFilter(filterName = "httpServletRequestWrapperFilter", urlPatterns = { "/*" })
public class HttpServletRequestWrapperFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

        ServletRequest requestWrapper = null;

        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            String contentType = httpRequest.getHeader("Content-Type"),methodType = httpRequest.getMethod();

            // 遇到post|put|patch  方法带 application/json 请求头才对request进行包装
            if ( ("POST".equals(methodType) || "PATCH".equals(methodType) || "PUT".equals(methodType))
              && (contentType!=null && contentType.startsWith("application/json")) ) { // "application/json;charset=UTF-8"

                requestWrapper = new BodyReaderHttpServletRequestWrapper(httpRequest);

            }

        }

        if (null == requestWrapper) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }

    }

    @Override
    public void destroy() {

    }
}