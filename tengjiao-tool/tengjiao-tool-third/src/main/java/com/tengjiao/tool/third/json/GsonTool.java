package com.tengjiao.tool.third.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tengjiao.tool.indep.DateTool;

import java.lang.reflect.Type;

/**
 * @author kangtengjiao
 */
public final class GsonTool {
  //
  // 定义json转换器
  // ----------------------------------------------------------------------------------------------------
  private static Gson filterNullGson;
  private static Gson nullableGson;

  static {
    nullableGson = new GsonBuilder()
      .disableHtmlEscaping()
      .enableComplexMapKeySerialization()
      .serializeNulls()
      .setDateFormat(DateTool.Patterns.DATETIME)
      .create();
    filterNullGson = new GsonBuilder()
      .disableHtmlEscaping()
      .enableComplexMapKeySerialization()
      .setDateFormat(DateTool.Patterns.DATETIME)
      .create();
  }

  /**
   * 根据对象返回json   不过滤空值字段
   */
  public static String toJsonWithNullField(Object obj) {
    return nullableGson.toJson(obj);
  }

  /**
   * 根据对象返回json  过滤空值字段
   */
  public static String toJsonFilterNullField(Object obj) {
    return filterNullGson.toJson(obj);
  }

  /**
   * 将json转化为对应的实体对象
   * new TypeToken<HashMap<String, Object>>(){}.getType()
   */
  public static <T> T parse(String json, Type type) {
    return nullableGson.fromJson(json, type);
  }

  /**
   * 将对象值赋值给目标对象
   *
   * @param source 源对象
   * @param <T>    目标对象类型
   * @return 目标对象实例
   */
  public static <T> T convert(Object source, Class<T> clz) {
    String json = GsonTool.toJsonFilterNullField(source);
    return parse(json, clz);
  }
}