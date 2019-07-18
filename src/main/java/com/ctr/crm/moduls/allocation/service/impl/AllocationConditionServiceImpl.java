package com.ctr.crm.moduls.allocation.service.impl;

import com.ctr.crm.moduls.allocation.dao.AllocationConditionDao;
import com.ctr.crm.moduls.allocation.models.AllocationCondition;
import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.allocation.service.AllocationConditionService;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.PageMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-04-29
 */
@Service("allocationConditionService")
public class AllocationConditionServiceImpl implements AllocationConditionService {

    @Autowired
    private AllocationConditionDao allocationConditionDao;
    @Autowired
    private BaseDao baseDao;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, AllocationCondition allocationCondition) {
        pageMode.setSqlFrom("select * from AllocationCondition");
        pageMode.setSqlWhere("deleted=0");
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public AllocationCondition get(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return allocationConditionDao.get(id);
    }

    @Override
    public List<AllocationCondition> findList(AllocationCondition allocationCondition) {
        return allocationConditionDao.findList(allocationCondition);
    }

    @Override
    public List<AllocationCondition> findAllList(AllocationCondition allocationCondition) {
        return allocationConditionDao.findAllList(allocationCondition);
    }

    @Override
    public String insert(AllocationCondition allocationCondition) {
        if (StringUtils.isBlank(allocationCondition.getId())) {
            allocationCondition.setId(CommonUtils.getUUID());
        }
        if (allocationCondition.getCreateTime() == null) {
            allocationCondition.setCreateTime(new Date());
        }
        allocationConditionDao.insert(allocationCondition);
        return allocationCondition.getId();
    }

    @Override
    public int insertBatch(List<AllocationCondition> allocationConditions) {
        allocationConditions.forEach(ac->{
            if (StringUtils.isBlank(ac.getId())) {
                ac.setId(CommonUtils.getUUID());
            }
            if (ac.getCreateTime() == null) {
                ac.setCreateTime(new Date());
            }
        });
        return allocationConditionDao.insertBatch(allocationConditions);
    }

    @Override
    public int update(AllocationCondition allocationCondition) {
        return allocationConditionDao.update(allocationCondition);
    }

    @Override
    public boolean delete(String id) {
        if (StringUtils.isBlank(id)) {
            return false;
        }
        return allocationConditionDao.delete(id);
    }

    @Override
    public void deleteByAllotId(String id) {
        allocationConditionDao.deleteByAllotId(id);
    }
}
