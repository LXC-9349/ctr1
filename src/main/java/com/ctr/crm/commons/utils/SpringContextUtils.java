package com.ctr.crm.commons.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * spring上下文工具类
 * @author Administrator
 *
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {
	private static ApplicationContext context;
	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		SpringContextUtils.context = context;
	}
	
	public static Object getBean(String beanName){
		return context.getBean(beanName);
	}
	
	public static <T> T getBean(String beanName, Class<T> clazz){
		return context.getBean(beanName, clazz);
	}
	
	public static <T> T getBean(Class<T> clazz){
		return context.getBean(clazz);
	}

}
