package com.tengjiao.tool.indep.test;

import com.tengjiao.tool.indep.DateTool;

import java.util.Date;

public class DateToolTest {

    public static void main(String[] args) {

        Date nextYear = DateTool.addYears(new Date(), 1);
        String nextYearS = DateTool.format(nextYear, DateTool.Patterns.DATETIME);

        System.out.println("nextYear: " + nextYearS);

        nextYear = DateTool.parse(nextYearS, DateTool.Patterns.DATETIME);
        System.out.println("nextYear: " +nextYear);

    }
}
