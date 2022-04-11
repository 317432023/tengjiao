package com.tengjiao.part.springmvc;

import com.tengjiao.tool.indep.DateTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * 表单形式的全局时间类型转换器
 * <p>Title: CustomDateConverter</p>
 * <p>Description:日期转换器 </p>
 * @author rise
 * @usage 
 * <!-- 自定义参数绑定 -->
	<bean id="conversionService"
		class="org.springframework.format.support.FormattingConversionServiceFactoryBean">
		<!-- 转换器 -->
		<property name="converters">
			<list>
				<!-- 日期类型转换 -->
				<bean class="com.eliteams.quick4j.core.feature.springmvc.CustomDateConverter" />
			</list>
		</property>
	</bean>

<!-- 指定自己定义的validator ，同时指定转换器 -->
    <mvc:annotation-driven validator="validator" conversion-service="conversionService"/>

    <!-- 以下 validator ConversionService 在使用 mvc:annotation-driven 会 自动注册 -->
    <bean id="validator" class="org.springframework.validation.beanvalidation.LocalValidatorFactoryBean">
        <property name="providerClass" value="org.hibernate.validator.HibernateValidator"/>
        <!-- 如果不加默认到 使用classpath下的 ValidationMessages.properties -->
        <property name="validationMessageSource" ref="messageSource"/>
    </bean>
    
    其他：若要forward到jsp页面指定显示格式可以这样使用
    @DateTimeFormat(pattern="yyyy-MM")
    private Date date;
    
    <%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
    <spring:eval expression="contentModel.date"></spring:eval>
    
 */
public class CustomDateConverter implements Converter<String,Date>{

	private static final Logger log = LogManager.getLogger(CustomDateConverter.class);

	@Override
	public Date convert(String source) {
		String value = source.trim();
		if ("".equals(value)) {
			return null;
		}
		if (source.matches("^\\d{4}-\\d{1,2}$")) {
			return DateTool.parse(source, DateTool.Patterns.DATE_MM);
		} else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
			return DateTool.parse(source, DateTool.Patterns.DATE);
		} else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")) {
			return DateTool.parse(source, DateTool.Patterns.DATETIME_MM);
		} else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
			return DateTool.parse(source, DateTool.Patterns.DATETIME);
		} else {
			throw new IllegalArgumentException("Invalid boolean value '" + source + "'");
		}
	}

}
