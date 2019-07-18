package com.ctr.crm.commons.annontations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明：菜单权限验证注解
 * @author eric
 * @date 2019年4月16日 上午10:40:59
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Menu {

	/**
	 * 是否菜单权限验证 如果为false 其他的属性可不填，为true，菜单信息必填
	 * @return
	 */
	boolean verify() default true;
	/**
	 * 菜单名称
	 */
	String menuName() default "";
	/**
	 * 菜单 可以是一串字符，一般为资源地址
	 */
	String menuUrl() default "";
	/**
	 * 上级菜单信息 
	 */
	Parent parent() default @Parent();
	/**菜单排序*/
	int orderNo() default 0;
	/**是否为基础功能菜单*/
	boolean foundational() default true;
}
