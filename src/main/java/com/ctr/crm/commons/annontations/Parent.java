package com.ctr.crm.commons.annontations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明：
 * @author eric
 * @date 2019年4月16日 上午10:40:59
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Parent {

	String menuName() default "";
	String menuUrl() default "";
	/**菜单排序*/
	int orderNo() default 0;
	/**是否为基础功能菜单*/
	boolean foundational() default true;
}
