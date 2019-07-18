package com.ctr.crm.commons.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.ctr.crm.commons.es.MemberBean;
import org.apache.commons.lang3.StringUtils;

/**
 * bean 操作相关工具类
 * 
 * @author Administrator
 *
 */
public class BeanUtils {

	public static Map<String, Object> transBeanToMap(Object obj) {
		if (obj == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();

				// 过滤class属性
				if (!key.equals("class")) {
					// 得到property对应的getter方法
					Method getter = property.getReadMethod();
					Object value = getter.invoke(obj);

					map.put(key, value);
				}

			}
		} catch (Exception e) {
			System.out.println("transBeanToMap Error " + e);
		}
		return map;
	}
	
	/**
	 * 把dest对象的字段值更新至src对象，对于null的字段会丢去
	 * 
	 * @param bean
	 * @return
	 */
	public static void toUpate(MemberBean src, MemberBean dest) {
		Class<?> type = dest.getClass();
		try {
			Field[] fields = type.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				String fieldName = field.getName();
				if (!fieldName.equals("serialVersionUID")) {
					Method m = type.getMethod("get" + StringUtils.capitalize(fieldName));
					Object val = m.invoke(dest);
					if (val != null) {
						PojoUtils.setProperties(src, fieldName, val);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("toUpate Error " + e);
		}
	}
	
}