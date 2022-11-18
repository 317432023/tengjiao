package com.tengjiao.tool.third.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.tengjiao.tool.indep.DateTool;
import com.tengjiao.tool.indep.StringTool;

import java.io.IOException;
import java.math.BigInteger;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author kangtengjiao
 */
public class JackJsonTool {
    /** 定义json转换器 */
    private static final ObjectMapper objectMapper;
    static {
        objectMapper = makeCustomObjectMapper();
    }

    /**
     * JSON形式的全局时间类型转换器
     */
    public static class CustomDateFormat extends StdDateFormat {
        public static final CustomDateFormat instance = new CustomDateFormat();

        /**
         * 只要覆盖parse(String)这个方法即可
         */
        @Override
        public Date parse(String dateStr, ParsePosition pos) {
            return getDate(dateStr, pos);
        }

        @Override
        public Date parse(String dateStr) {
            ParsePosition pos = new ParsePosition(0);
            return getDate(dateStr, pos);
        }

        private Date getDate(String dateStr, ParsePosition pos) {
            SimpleDateFormat sdf = null;
            if (StringTool.isBlank(dateStr)) {
                return null;
            } else if (dateStr.matches("^\\d{4}-\\d{1,2}$")) {
                sdf = new SimpleDateFormat(DateTool.Patterns.DATE_MM);
                return sdf.parse(dateStr, pos);
            } else if (dateStr.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
                sdf = new SimpleDateFormat(DateTool.Patterns.DATE);
                return sdf.parse(dateStr, pos);
            } else if (dateStr.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")) {
                sdf = new SimpleDateFormat(DateTool.Patterns.DATETIME_MM);
                return sdf.parse(dateStr, pos);
            } else if (dateStr.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
                sdf = new SimpleDateFormat(DateTool.Patterns.DATETIME);
                return sdf.parse(dateStr, pos);
            } else if (dateStr.length() == 23) {
                sdf = new SimpleDateFormat(DateTool.Patterns.DATETIME_SSS);
                return sdf.parse(dateStr, pos);
            }
            return super.parse(dateStr, pos);
        }

        @Override
        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(date, toAppendTo, fieldPosition);
        }

        @Override
        public CustomDateFormat clone() {
            return new CustomDateFormat();
        }
    }
    public static ObjectMapper makeCustomObjectMapper() {
        // 空数组或集合序列化器
        JsonSerializer<Object> nullArrayJsonSerializer = new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                if (value == null) {
                    jgen.writeStartArray();
                    jgen.writeEndArray();
                } else {
                    jgen.writeObject(value);
                }
            }
        }, nullStringJsonSerializer = new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                if (value == null) {
                    jgen.writeString("");
                } else {
                    jgen.writeObject(value);
                }
            }
        }, nullNumberJsonSerializer = new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                if (value == null) {
                    jgen.writeNumber(0);
                } else {
                    jgen.writeObject(value);
                }
            }
        }, nullBooleanJsonSerializer = new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                if (value == null) {
                    jgen.writeBoolean(false);
                } else {
                    jgen.writeObject(value);
                }
            }
        };

        // JSON 转化器
        ObjectMapper objectMapper = new ObjectMapper();

        // 忽略无法转换的对象
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // PrettyPrinter 格式化输出
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        // 忽略json字符串中不识别的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // NULL不参与序列化   ：JsonInclude.Include.NON_NULL
        // NULL总是参与序列化 : JsonInclude.Include.ALWAYS
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // 设定日期序列化 使用的对象 自定义的 JSON全局日期转换器 CustomDateFormat
        objectMapper.setDateFormat(CustomDateFormat.instance);
        // 取消timestamps形式 spring.jackson.serialization.write-dates-as-timestamps=false，一般指定了 DateFormat 就可以了
        //objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 指定时区
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        // java8 日期处理
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));

        // 为mapper注册一个带有SerializerModifier的Factory，此modifier主要做的事情为：当序列化类型为array，list、set时，当值为空时，序列化成[]
        objectMapper.setSerializerFactory(objectMapper.getSerializerFactory().withSerializerModifier(new BeanSerializerModifier(){
            @Override
            public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
                                                             List<BeanPropertyWriter> beanProperties) {
                // 循环所有的beanPropertyWriter
                for (BeanPropertyWriter writer : beanProperties) {
                    // 判断字段的类型，如果是array 或 collection则注册nullSerializer
                    Class<?> clazz = writer.getType().getRawClass();
                    if ( clazz.isArray() || Collection.class.isAssignableFrom(clazz) ) { // 数组或集合
                        writer.assignNullSerializer(nullArrayJsonSerializer);
                    } else if( Number.class.isAssignableFrom(clazz)) { // 数值
                        writer.assignNullSerializer(nullNumberJsonSerializer);
                    } else if(CharSequence.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz) ) { // 字符或字符串
                        writer.assignNullSerializer(nullStringJsonSerializer);
                    } else if(clazz.equals(Boolean.class) ) { // 布尔值
                        writer.assignNullSerializer(nullBooleanJsonSerializer);
                    }
                }
                return beanProperties;
            }
        }));

        /*
         * 序列换成json时,将所有的long变成string
         * 因为js中得数字类型不能包含所有的java long值
         * 另一种方法是在 每个long字段上面指定标注
         * albaba fastjson 使用 => @JSONField(serializeUsing= ToStringSerializer.class)
         * jackjson        使用 => @JsonSerialize(using=ToStringSerializer.class) 或 @JsonFormat(shape = JsonFormat.Shape.STRING)
         */
        objectMapper.registerModule(
          new SimpleModule()
            .addSerializer(BigInteger.class, com.fasterxml.jackson.databind.ser.std.ToStringSerializer.instance)
            .addSerializer(Long.class, com.fasterxml.jackson.databind.ser.std.ToStringSerializer.instance)
            .addSerializer(Long.TYPE, com.fasterxml.jackson.databind.ser.std.ToStringSerializer.instance)
        );

        return objectMapper;
    }

    /**
     * 将对象转换成json字符串
     * @param data
     * @return
     */
    public static String toJson(Object data) {
        try {
            String string = objectMapper.writeValueAsString(data);
            return string;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json结果集转化为对象
     *
     * @param jsonData json数据
     * @param beanType 对象中的object类型
     * @return
     */
    public static <T> T parse(String jsonData, Class<T> beanType) {
        try {
            T t = objectMapper.readValue(jsonData, beanType);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json数据转换成list对象
     * <p>Title: jsonToList</p>
     * <p>Description: </p>
     *
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T> List<T> parseList(String jsonData, Class<T> beanType) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            List<T> list = objectMapper.readValue(jsonData, javaType);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 提取json属性值放进map，最终返回
     *
     * @param json
     * @param attributeNames
     * @return
     */
    public static Map<String, String> getAttributeValues(String json, List<String> attributeNames) {
        Map<String, String> results = new HashMap<>(2^4);
        try {
            JsonNode root = objectMapper.readTree(json);
            for (String attributeName : attributeNames) {
                results.put(attributeName, root.findValue(attributeName).asText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

}
