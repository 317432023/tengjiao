package com.tengjiao.tool.indep.sql;

/**
 * 仅适用MySQL
 * @author rise
 * @date 2020/06/10
 */
public final class MysqlTool {
  public static String countSql(String s) {
    s = s.replaceAll(" \n*+(?i)FROM\n* +", " FROM ");
    s = "SELECT COUNT(*) " + s.substring(s.indexOf("FROM"));
    int orderByIndex = s.lastIndexOf("order by");
    if(orderByIndex < 0 ) {
      orderByIndex = s.lastIndexOf("ORDER BY");
    }
    if(orderByIndex > 0 ) {
      int k = s.lastIndexOf(")");
      if(k < orderByIndex) {
        s = s.substring(0, orderByIndex);
      }
    }
    return s;
  }
  public static String limitSqlOffset(String s, int offset, int limit) {
    s += new StringBuffer(50).append(" limit ").append(offset).append(",").append(limit).toString();
    return s;
  }
  public static String limitSqlPage(String s, int page, int limit) {
    int offset = (page - 1) * limit;
    return limitSqlOffset(s, offset, limit);
  }
  public static String topSql(String s, int topNum) {
    return limitSqlOffset(s, 0, topNum);
  }
}
