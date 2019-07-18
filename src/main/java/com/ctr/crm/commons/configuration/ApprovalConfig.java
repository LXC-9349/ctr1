package com.ctr.crm.commons.configuration;

import com.ctr.crm.interceptors.ApprovalInterceptor;
import org.aopalliance.aop.Advice;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 功能描述: 权限标签拦截配置
 *
 * @author: DoubleLi
 * @date: 2019/4/26 10:00
 */
@Configuration
public class ApprovalConfig {

    @Bean(name = "approvalInterceptor")
    public Advice ApprovalInterceptor() {
        return new ApprovalInterceptor();
    }

    /**
     * 功能描述: 代理创建
     *
     * @author: DoubleLi
     * @date: 2019/4/26 10:01
     */
    @Bean
    public BeanNameAutoProxyCreator transactionAutoProxy() {
        BeanNameAutoProxyCreator autoProxy = new BeanNameAutoProxyCreator();
        autoProxy.setProxyTargetClass(true);// 这个属性为true时，表示被代理的是目标类本身而不是目标类的接口
        String[] beanNames = {"contractOrderService","meetRecordService"};//"",""
        autoProxy.setBeanNames(beanNames);
        autoProxy.setInterceptorNames("approvalInterceptor");
        return autoProxy;
    }
}