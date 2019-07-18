package com.ctr.crm.moduls.allocation.service;

import com.ctr.crm.moduls.allocation.models.AllocationCondition;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;

/**
 * @author DoubleLi
 * @date  2019-04-29
 */
public interface  AllocationConditionService {

    void searchPage(ResponseData responseData, PageMode pageMode, AllocationCondition allocationCondition);

    AllocationCondition get(String id);

    List<AllocationCondition> findList(AllocationCondition allocationCondition);

    List<AllocationCondition> findAllList(AllocationCondition allocationCondition);

    String insert(AllocationCondition allocationCondition);

    int insertBatch(List<AllocationCondition> allocationConditions);

    int update(AllocationCondition allocationCondition);

    boolean delete(String id);

    void deleteByAllotId(String id);
}
