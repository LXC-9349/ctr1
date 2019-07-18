package com.ctr.crm.moduls.allocation.service.impl;

import com.ctr.crm.moduls.allocation.dao.AllocationRuleDao;
import com.ctr.crm.moduls.allocation.models.AllocationRule;
import com.ctr.crm.moduls.allocation.service.AllocationRuleService;
import com.ctr.crm.moduls.base.dao.BaseDao;
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
@Service("allocationRuleService")
public class AllocationRuleServiceImpl implements AllocationRuleService {

    @Autowired
    private AllocationRuleDao allocationRuleDao;
    @Autowired
    private BaseDao baseDao;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, AllocationRule allocationRule) {
        pageMode.setSqlFrom("select * from AllocationRule");
        pageMode.setSqlWhere("deleted=0");
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        pageMode.setOrderBy("order by priority asc");
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        if (data != null) {
            data.forEach(rule -> {
                String status = rule.get("status").toString();
                rule.put("status", status);
            });
        }
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public AllocationRule get(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return allocationRuleDao.get(id);
    }

    @Override
    public List<AllocationRule> findList(AllocationRule allocationRule) {
        return allocationRuleDao.findList(allocationRule);
    }

    @Override
    public List<AllocationRule> findAllList(AllocationRule allocationRule) {
        return allocationRuleDao.findAllList(allocationRule);
    }

    @Override
    public String insert(AllocationRule allocationRule) {
        if (StringUtils.isBlank(allocationRule.getId())) {
            allocationRule.setId(CommonUtils.getUUID());
        }
        if (allocationRule.getCreateTime() == null) {
            allocationRule.setCreateTime(new Date());
        }
        if (allocationRule.getPriority() == null) {
            allocationRule.setPriority(allocationRuleDao.getMaxPriority());
        }
        allocationRuleDao.insert(allocationRule);
        return allocationRule.getId();
    }

    @Override
    public int insertBatch(List<AllocationRule> allocationRules) {
        return allocationRuleDao.insertBatch(allocationRules);
    }

    @Override
    public int update(AllocationRule allocationRule) {
        return allocationRuleDao.update(allocationRule);
    }

    @Override
    public boolean delete(String id) {
        if (StringUtils.isBlank(id)) {
            return false;
        }
        return allocationRuleDao.delete(id);
    }

    @Override
    public List<Map<String, Object>> findMemberBaseInfoColumns() {
        return baseDao.select("select COLUMN_NAME name, (case when DATA_TYPE like '%int%' then 'Integer' when DATA_TYPE like '%date%' then 'date' else 'String' end) type, COLUMN_COMMENT showName" +
                " from INFORMATION_SCHEMA.COLUMNS" +
                " Where table_name = 'MemberBaseInfo' " +
                " AND table_schema = 'marry_crm' ");
    }

    @Override
    public String exchange(String beforeId, String afterId) {
        AllocationRule allocationRuleBefore = get(beforeId);
        AllocationRule allocationRuleAfter = get(afterId);
        if (allocationRuleAfter == null || allocationRuleBefore == null) {
            return "规则不存在";
        }
        int priority = allocationRuleBefore.getPriority();
        allocationRuleBefore.setPriority(allocationRuleAfter.getPriority());
        allocationRuleAfter.setPriority(priority);
        update(allocationRuleBefore);
        update(allocationRuleAfter);
        return null;
    }

    @Override
    public String status(String id, Integer status) {
        AllocationRule allocationRule = get(id);
        if (allocationRule == null) {
            return "规则不存在";
        }
        allocationRule.setStatus(status);
        update(allocationRule);
        return null;
    }
}
