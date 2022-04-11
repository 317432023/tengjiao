package com.tengjiao.tool.indep.web;

import com.tengjiao.tool.indep.StringTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author kangtengjiao
 */
public class PrintTool {

    private static transient Logger logger = LogManager.getLogger(PrintTool.class);

    /**
     * 输出流打印内容
     * @param response
     * @param content
     * @param contentType 例如："application/json;charset=UTF-8"
     */
    public static void print(HttpServletResponse response, String content, String contentType) {
        if( !StringTool.isBlank(contentType) ) {
            response.setContentType (contentType);
        }
        try {
            PrintWriter out = response.getWriter();
            out.print (content);
            out.flush ();
            out.close ();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static void printJson(HttpServletResponse response, String content) {

        print(response, content, "application/json;charset=UTF-8");

    }
}
