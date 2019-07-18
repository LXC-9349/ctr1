package com.ctr.crm.moduls.allot.service.impl;

import com.ctr.crm.moduls.allot.dao.AllotHistoryDao;
import com.ctr.crm.moduls.allot.models.AllotHistory;
import com.ctr.crm.moduls.allot.service.AllotHistoryService;
import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-05-07
 */
@Service("allotHistoryService")
public class AllotHistoryServiceImpl implements AllotHistoryService {

    @Autowired
    private AllotHistoryDao allotHistoryDao;
    @Autowired
    private BaseDao baseDao;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, AllotHistory allotHistory, String depts) {
        pageMode.setSqlFrom("select * from AllotHistory");
        // 查询总条数
        if (allotHistory.getMemberId() != null) {
            pageMode.setSqlWhere("memberId = '" + allotHistory.getMemberId() + "'");
        }
        if (allotHistory.getWorkerId() != null) {
            pageMode.setSqlWhere("workerId = '" + allotHistory.getWorkerId() + "'");
        }
        if (StringUtils.isNotBlank(allotHistory.getWorkerName())) {
            pageMode.setSqlWhere("workerName like '%" + pageMode.noSqlInjection(allotHistory.getWorkerName()) + "%'");
        }
        if (StringUtils.isNotBlank(depts)) {//部门
            pageMode.setSqlWhere("workerDept  in (" + depts+ ")");
        }
        if(StringUtils.isNotBlank(allotHistory.getAllotType())){
        pageMode.setSqlWhere("allotType = '"+pageMode.noSqlInjection(allotHistory.getAllotType())+"'");
        }
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        pageMode.setOrderBy("order by allotTime desc");
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public AllotHistory get(Integer id) {
        if (id==null) {
            return null;
        }
        return allotHistoryDao.get(id);
    }

    @Override
    public List<AllotHistory> findList(AllotHistory allotHistory) {
        return allotHistoryDao.findList(allotHistory);
    }

    @Override
    public List<AllotHistory> findAllList() {
        return allotHistoryDao.findAllList();
    }

    @Override
    public Boolean insert(AllotHistory allotHistory) {
        try {
            return allotHistoryDao.insert(allotHistory)>0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int insertBatch(List<AllotHistory> allotHistorys) {
        return allotHistoryDao.insertBatch(allotHistorys);
    }

    @Override
    public int update(AllotHistory allotHistory) {
        return allotHistoryDao.update(allotHistory);
    }

    @Override
    public boolean delete(Integer autoId) {
        if (autoId == null) {
            return false;
        }
        return allotHistoryDao.delete(autoId);
    }

}
