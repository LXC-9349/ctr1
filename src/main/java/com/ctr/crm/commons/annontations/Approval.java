package com.ctr.crm.commons.annontations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来标记操作方法是否要审批
 *
 * @author DoubleLi
 * @time 2019年04月25日
 */
@Target({ElementType.METHOD})//注解的作用目标：方法
@Retention(RetentionPolicy.RUNTIME)//注解会在class字节码文件中存在，在运行时可以通过反射获取到
public @interface Approval {
    String value();//即审批类型标识值

    String name();//即审批类型名称 流程配置需要针对具体审批来定义

    String action();//执行动作

    String beanName();//即service的bean名称

    String method();//实际执行的方法

    boolean submit() default false;//true先执行添加数据，如知识库新增功能，先新增，得到的ID作为审批数据的参数

    String delmethod() default "";//处理驳回执行删除的方法

    String viewUrl();//数据展示方法
}

