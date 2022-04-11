package com.tengjiao.tool.indep;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author kangtengjiao
 */
public class ExceptionTool {

	/**
	 * 获取异常的堆栈信息
	 * 
	 * @param t
	 * @return
	 */
	public static String getStackTrace(Throwable t) {

		try( StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw) ) {

			t.printStackTrace(pw);
			pw.flush();
			return sw.toString();

		} catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
}
