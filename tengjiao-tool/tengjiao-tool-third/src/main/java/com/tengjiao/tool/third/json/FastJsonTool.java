package com.tengjiao.tool.third.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONLibDataFormatSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 阿里巴巴 FastJson 工具类
 *
 * @author kangtengjiao
 */
public final class FastJsonTool {

    private static final SerializeConfig config;

    public static final SerializerFeature[] features = {
      // 输出空置字段
      SerializerFeature.WriteMapNullValue,
      // list字段如果为null，输出为[]，而不是null
      SerializerFeature.WriteNullListAsEmpty,
      // 数值字段如果为null，输出为0，而不是null
      SerializerFeature.WriteNullNumberAsZero,
      // Boolean字段如果为null，输出为false，而不是null
      SerializerFeature.WriteNullBooleanAsFalse,
      // 字符类型字段如果为null，输出为""，而不是null
      SerializerFeature.WriteNullStringAsEmpty,
      // 避免循环引用
      SerializerFeature.DisableCircularReferenceDetect
    };

    static {
        config = new SerializeConfig();
        customConfig(config);
    }

    public static void customConfig(SerializeConfig config) {
        // 使用和json-lib兼容的日期输出格式
        config.put(java.util.Date.class, new JSONLibDataFormatSerializer());
        // 使用和json-lib兼容的日期输出格式
        config.put(java.sql.Date.class, new JSONLibDataFormatSerializer());
        // 序列换成json时,将所有的long变成string
        // 因为js中得数字类型不能包含所有的java long值
        // 另一种方法是在 每个long字段上面指定标注 @JSONField(serializeUsing= ToStringSerializer.class) @JsonSerialize(using=ToStringSerializer.class)
        config.put(BigInteger.class, com.alibaba.fastjson.serializer.ToStringSerializer.instance);
        config.put(Long.class, com.alibaba.fastjson.serializer.ToStringSerializer.instance);
        config.put(Long.TYPE, com.alibaba.fastjson.serializer.ToStringSerializer.instance);
    }


    /**
     * 将object转化为string
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        String s = JSONObject.toJSONString(obj);
        return s;
    }

    /**
     * 将object转化为string（使用自定义的序列化规则config+feature）
     *
     * @param object
     * @return
     */
    public static String toJsonByConfigAndFeatures(Object object) {
        return JSON.toJSONString(object, config, features);
    }

    /**
     * 将object转化为string（使用自定义的序列化规则config）
     *
     * @param object
     * @return
     */
    public static String toJSONNoFeatures(Object object) {
        return JSON.toJSONString(object, config);
    }

    /**
     * 转换为List
     *
     * @param text
     * @param clazz
     * @return
     */
    public static <T> List<T> parseList(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz);
    }

    /**
     * 将string转化为序列化的json字符串
     *
     * @param text
     * @return
     */
    public static Object parse(String text) {
        Object objectJson = JSON.parse(text);
        return objectJson;
    }

    /**
     * json字符串转化为map
     *
     * @param s
     * @return
     */
    public static Map parseMap(String s) {
        return JSONObject.parseObject(s);
    }

    /**
     * 转换JSON字符串为对象
     *
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T parse(String json, Class<T> clazz) {
        return JSONObject.parseObject(json, clazz);
    }

}