package com.tengjiao.tool.indep;

/**
 * 字符串工具
 * @version 1.0.1
 * @author kangtengjiao
 * @since 2021-05-18
 */
public final class StringTool {

    private StringTool(){}

    /**
     * 判断字符串是否为 blank
     * <br>
     * 将 空值null、全空白字符串、空字符串均 视为 blank
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 判断字符串是否为 empty
     * <br>
     * 将 空值null、空字符串 视为 empty
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

}
