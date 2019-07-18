package com.ctr.crm.api;

/**
 * 功能描述: vip客户定时任务
 *
 * @author: DoubleLi
 * @date: 2019/5/20 18:11
 */
public interface VipService {

    /**
     * 服务过期合同处理
     */
    void expired();
}