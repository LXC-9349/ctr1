package com.ctr.crm.moduls.allocation.service;

import com.ctr.crm.moduls.allocation.models.AllocationRule;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-04-29
 */
public interface AllocationRuleService {

    void searchPage(ResponseData responseData, PageMode pageMode, AllocationRule allocationRule);

    AllocationRule get(String id);

    List<AllocationRule> findList(AllocationRule allocationRule);

    List<AllocationRule> findAllList(AllocationRule allocationRule);

    String insert(AllocationRule allocationRule);

    int insertBatch(List<AllocationRule> allocationRules);

    int update(AllocationRule allocationRule);

    boolean delete(String id);

    /**
     * 功能描述: 查找客户表信息
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/17 10:33
     */
    List<Map<String, Object>> findMemberBaseInfoColumns();

    /**
     * 功能描述: 优先级顺序交换
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/17 10:33
     */
    String exchange(String beforeId, String afterId);

    String status(String id, Integer status);
}
