package com.ctr.crm.commons.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Secure;

public class AnnontationUtils {

	public static <T extends Annotation> T getFromMethed(Class<T> annotationClazz, Object o,
			String methodName) {
		if (o == null) {
			return null;
		}
		Class<? extends Object> objectClass = o.getClass();
		try {
			Method method = objectClass.getDeclaredMethod(methodName);
			return method.getAnnotation(annotationClazz);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T extends Annotation> T getFromMethedOrType(Class<T> annotationClazz, Object o,
			String methodName, Class<?>[] parameterTypes) {
		if (o == null) {
			return null;
		}
		Class<? extends Object> objectClass = o.getClass();
		T annotation;
		try {
			Method method = objectClass.getDeclaredMethod(methodName, parameterTypes);
			annotation = method.getAnnotation(annotationClazz);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		if (annotation != null) {
			return annotation;
		}
		return objectClass.getAnnotation(annotationClazz);
	}

	public static Secure getSecureAnnontation(Object o, String methodName, Class<?>[] parameterTypes) {
		Secure secure = getFromMethedOrType(Secure.class, o, methodName, parameterTypes);
		if (secure != null) {
			return secure;
		}
		return null;
	}
	
	public static <T extends Annotation> List<T> getAnnotations(Class<T> annotationClazz, Object o){
		List<T> result = new ArrayList<T>();
		if(o == null) return result;
		Class<? extends Object> objectClass = o.getClass();
		T annotation = objectClass.getAnnotation(annotationClazz);
		if(annotation != null) result.add(annotation);
		Method[] methods = objectClass.getDeclaredMethods();
		if(methods == null || methods.length == 0) return result;
		for (Method method : methods) {
			annotation = method.getAnnotation(annotationClazz);
			if(annotation == null)
				continue;
			result.add(annotation);
		}
		return result;
	}
	
	public static <T extends Annotation> List<T> getAnnotationsFromFields(Class<T> annotationClazz, Class<?> clazz){
		List<T> result = new ArrayList<T>();
		if(clazz == null) return result;
		Field[] fields = clazz.getDeclaredFields();
		if(fields == null || fields.length == 0) return result;
		T annotation = null;
		for (Field field : fields) {
			annotation = field.getAnnotation(annotationClazz);
			if(annotation == null)
				continue;
			result.add(annotation);
		}
		return result;
	}
	
	public static <T extends Annotation> List<T> getAnnotations(Class<T> annotationClazz, Class<?> clazz){
		List<T> result = new ArrayList<T>();
		if(clazz == null) return result;
		T annotation = clazz.getAnnotation(annotationClazz);
		if(annotation != null) result.add(annotation);
		Method[] methods = clazz.getDeclaredMethods();
		if(methods == null || methods.length == 0) return result;
		for (Method method : methods) {
			annotation = method.getAnnotation(annotationClazz);
			if(annotation == null)
				continue;
			result.add(annotation);
		}
		return result;
	}
	
	public static Menu getMenuAnnontation(Object o, String methodName, Class<?>[] parameterTypes) {
		Menu menu = getFromMethedOrType(Menu.class, o, methodName, parameterTypes);
		if (menu != null) {
			return menu;
		}
		return null;
	}
}
