package com.ctr.crm.commons.annontations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 用来标记审批条件 （一般标注在数据集上，如数组 LIST，用来判断满足一定数据量时才进入审批）
 *
 * @author DoubleLi
 * @time 2019年4月25日
 */
@Target(ElementType.PARAMETER)//注解的作用目标：参数
@Retention(RetentionPolicy.RUNTIME)//注解会在class字节码文件中存在，在运行时可以通过反射获取到
public @interface ApprovalVerify {

}

