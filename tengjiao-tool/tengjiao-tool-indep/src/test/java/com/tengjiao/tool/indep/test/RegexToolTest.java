package com.tengjiao.tool.indep.test;

import com.tengjiao.tool.indep.RegexTool;

public class RegexToolTest {

  public static void main(String[] args) {

    System.out.println(RegexTool.isChinese("中文"));
    System.out.println(RegexTool.containChinese("ab"));
    System.out.println(RegexTool.is("0", RegexTool.Patterns.INTEGER));

  }
}
