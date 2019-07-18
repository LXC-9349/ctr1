package com.ctr.crm.commons.version;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明：API版本控制
 * @author eric
 * @date 2019年3月27日 下午2:21:28
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {

	/**
	 * 版本号
	 * @return
	 */
	int value();
}
