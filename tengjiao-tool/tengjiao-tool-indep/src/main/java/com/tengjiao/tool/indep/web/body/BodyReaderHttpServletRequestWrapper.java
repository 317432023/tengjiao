package com.tengjiao.tool.indep.web.body;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;


/**
 * 绕开原生的request.getInputStream只能读取一次的问题，避免Resolved [org.springframework.http.converter.HttpMessageNotReadableException: I/O error while reading input message; nested exception is java.io.IOException: Stream closed]
 * 使用方法：
 * springboot:
 * 1、springboot app主程序注解：@ServletComponentScan("com.tengjiao.tool.indep.web.body")
 * 或者
 * @author kangtengjiao
 * @Configuration
 * public class FilterConfig {
 *
 *     @Bean
 *     public FilterRegistrationBean registFilter() {
 *         FilterRegistrationBean registration = new FilterRegistrationBean();
 *         registration.setFilter(new LogCostFilter());
 *         registration.addUrlPatterns("/*");
 *         registration.setName("LogCostFilter");
 *         registration.setOrder(1);
 *         return registration;
 *     }
 *
 * }
 * 2、HttpBodyTool.getBodyString(request)
 * 原理：
 * 0、流的本质其实是只能读取一次，即被关闭
 * 1、把request中的body保存在HttpServletRequest包装类的属性body中，
 * 2、包装类中重写了getInputStream方法,每次通过传入保存的body返回一个新的流
 * 限制：
 * 意味着过滤器一旦设置了此Wrapper，其他地方无法再次使用request.getParameter
 */
public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] body;
    //private Map<String, List<String>> headers = new HashMap<>();
    public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        //提取头信息
        //Enumeration<String> headerNames = request.getHeaderNames();
        //while(headerNames.hasMoreElements()) {
        //    String headerName = headerNames.nextElement();
        //    if(!headers.containsKey(headerName)) headers.put(headerName, new ArrayList<>());
        //    Enumeration<String> headerValues = request.getHeaders(headerName);
        //    while(headerValues.hasMoreElements()){
        //        String headerValue = headerValues.nextElement();
        //        headers.get(headerName).add(headerValue);
        //    }
        //}
        String bodyString = HttpBodyTool.getBodyString(request);
        body = bodyString.getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

}