/**
 * 
 */
package com.ctr.crm.commons.annontations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 登录/权限验证, value值默认为0<br>
 * 若未配置该注解或value=-1，则直接通过<br>
 * 若配置了注解并且value>=0，则需要登录验证, 验证通过后，会注入登录人信息<br>
 * 若配置了注解，value=1并且uri不为空，则需要权限验证
 */
@Target( { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Secure {

	/**
	 * 验证标识值，默认为0
	 * @return
	 */
	int value() default 0;
	/**
	 * 权限标识，可以是一串简单字符，只要能标记唯一即可<br>
	 * 一般是该操作权限的URI地址
	 * @return
	 */
	String actionUri() default "";
	
	/**
	 * 权限名称
	 * @return
	 */
	String actionName() default "";
	
	/**
	 * 权限说明
	 * @return
	 */
	String actionNote() default "";
	
	/**
	 * 基础功能，一般系统设置的功能为高级功能
	 * @return
	 */
	boolean foundational() default true;
	
}
