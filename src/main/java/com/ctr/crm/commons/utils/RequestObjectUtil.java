package com.ctr.crm.commons.utils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * 将request.getparm转换成各种类型
 * @author DoubleLi
 * @time 2018年8月9日
 *
 */
public class RequestObjectUtil {

	 /**
     * 从request中获得参数Map，并返回可读的Map
     *
     * @param request
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map<String,Object> getParameterMap(HttpServletRequest request) {
        // 参数Map
        Map properties = request.getParameterMap();
        // 返回值Map
        Map returnMap = new HashMap();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if(null == valueObj){
                value = "";
            }else if(valueObj instanceof String[]){
                String[] values = (String[])valueObj;
                for(int i=0;i<values.length;i++){
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length()-1);
            }else{
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }

	 /**
     * 将Map对象通过反射机制转换成Bean对象
     *
     * @param map 存放数据的map对象
     * @param clazz 待转换的class
     * @return 转换后的Bean对象
     * @throws Exception 异常
     */
    public static Object mapToBean(HttpServletRequest request, Class<?> clazz) throws Exception {

    	Map<String, Object> map=getParameterMap(request);

        Object obj = clazz.newInstance();
        if(map != null && map.size() > 0) {
            for(Map.Entry<String, Object> entry : map.entrySet()) {
                String propertyName = entry.getKey();       //属性名
                Object value = entry.getValue();
                String setMethodName = "set"
                        + propertyName.substring(0, 1).toUpperCase()
                        + propertyName.substring(1);
                Field field = getClassField(clazz, propertyName);
                if(field==null)
                    continue;
                Class<?> fieldTypeClass = field.getType();
                value = convertValType(value, fieldTypeClass);
                try{
                    clazz.getMethod(setMethodName, field.getType()).invoke(obj, value);
                }catch(NoSuchMethodException e){
                    e.printStackTrace();
                }
            }
        }else{
        	return null;
        }
        return obj;
    }

    /**
     * 获取指定字段名称查找在class中的对应的Field对象(包括查找父类)
     *
     * @param clazz 指定的class
     * @param fieldName 字段名称
     * @return Field对象
     */
    private static Field getClassField(Class<?> clazz, String fieldName) {
        if( Object.class.getName().equals(clazz.getName())) {
            return null;
        }
        Field []declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }

        Class<?> superClass = clazz.getSuperclass();
        if(superClass != null) {// 简单的递归一下
            return getClassField(superClass, fieldName);
        }
        return null;
    }

     /**
     * 将Object类型的值，转换成bean对象属性里对应的类型值
     *
     * @param value Object对象值
     * @param fieldTypeClass 属性的类型
     * @return 转换后的值
     */
    private static Object convertValType(Object value, Class<?> fieldTypeClass) {
        Object retVal = null;
        try {
            if(Long.class.getName().equals(fieldTypeClass.getName())
                    || long.class.getName().equals(fieldTypeClass.getName())) {
                retVal = Long.parseLong(value.toString());
            } else if(Integer.class.getName().equals(fieldTypeClass.getName())
                    || int.class.getName().equals(fieldTypeClass.getName())) {
                retVal = Integer.parseInt(value.toString());
            } else if(Float.class.getName().equals(fieldTypeClass.getName())
                    || float.class.getName().equals(fieldTypeClass.getName())) {
                retVal = Float.parseFloat(value.toString());
            } else if(Double.class.getName().equals(fieldTypeClass.getName())
                    || double.class.getName().equals(fieldTypeClass.getName())) {
                retVal = Double.parseDouble(value.toString());
            } else {
                retVal = value;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return retVal;
    }
}
