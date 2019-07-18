package com.ctr.crm.api;

/**
 * 功能描述: 客户回收
 *
 * @author: DoubleLi
 * @date: 2019/5/9 15:43
 */
public interface RecyclingMemberJobService {
    /**
     * 根据回收规则每天进行客户自动回收
     */
    void recyclingMember();
}