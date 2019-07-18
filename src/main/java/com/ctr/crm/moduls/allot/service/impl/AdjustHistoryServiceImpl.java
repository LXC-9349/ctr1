package com.ctr.crm.moduls.allot.service.impl;

import com.ctr.crm.moduls.allot.dao.AdjustHistoryDao;
import com.ctr.crm.moduls.allot.models.AdjustHistory;
import com.ctr.crm.moduls.allot.service.AdjustHistoryService;
import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 调配历史
 *
 * @author DoubleLi
 * @date 2019-05-08
 */
@Service("adjustHistoryService")
public class AdjustHistoryServiceImpl implements AdjustHistoryService {

    @Autowired
    private AdjustHistoryDao adjustHistoryDao;
    @Autowired
    private BaseDao baseDao;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, AdjustHistory adjustHistory) {
        pageMode.setSqlFrom("select * from AdjustHistory");
        if (StringUtils.isNotBlank(adjustHistory.getAdjustWorker())) {
            pageMode.setSqlWhere("adjustWorker like '%" + pageMode.noSqlInjection(adjustHistory.getAdjustWorker()) + "%'");
        }
        if (adjustHistory.getFromWorkerId() == null) {
            pageMode.setSqlWhere("fromWorkerId = '"+adjustHistory.getFromWorkerId()+"'");
        }
        if (adjustHistory.getToWorkerId() == null) {
            pageMode.setSqlWhere("tomWorkerId = '"+adjustHistory.getToWorkerId()+"'");
        }
        if (adjustHistory.getCaseClass()== null) {
            pageMode.setSqlWhere("caseClass = '"+adjustHistory.getCaseClass()+"'");
        }
        if (adjustHistory.getMemberId()== null) {
            pageMode.setSqlWhere("memberId = '"+adjustHistory.getMemberId()+"'");
        }
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public AdjustHistory get(Integer id) {
        if (id == null) {
            return null;
        }
        return adjustHistoryDao.get(id);
    }

    @Override
    public List<AdjustHistory> findList(AdjustHistory adjustHistory) {
        return adjustHistoryDao.findList(adjustHistory);
    }

    @Override
    public List<AdjustHistory> findAllList(AdjustHistory adjustHistory) {
        return adjustHistoryDao.findAllList(adjustHistory);
    }

    @Override
    public Boolean insert(AdjustHistory adjustHistory) {
        return adjustHistoryDao.insert(adjustHistory);
    }

    @Override
    public int insertBatch(List<AdjustHistory> adjustHistorys) {
        return adjustHistoryDao.insertBatch(adjustHistorys);
    }

    @Override
    public int update(AdjustHistory adjustHistory) {
        return adjustHistoryDao.update(adjustHistory);
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) {
            return false;
        }
        return adjustHistoryDao.delete(id);
    }

}
