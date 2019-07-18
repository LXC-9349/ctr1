package com.ctr.crm.moduls.allot.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ctr.crm.moduls.allot.dao.AllotWorkerDao;
import com.ctr.crm.moduls.allot.models.AllotWorker;
import com.ctr.crm.moduls.allot.service.AllotWorkerService;
import com.ctr.crm.moduls.base.dao.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

/**
 * @author DoubleLi
 * @date 2019-05-05
 */
@Service("allotWorkerService")
public class AllotWorkerServiceImpl implements AllotWorkerService {

    @Autowired
    private AllotWorkerDao allotWorkerDao;
    @Autowired
    private BaseDao baseDao;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, AllotWorker allotWorker) {
        pageMode.setSqlFrom("select * from AllotWorker");
        pageMode.setSqlWhere("deleted=0");
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public AllotWorker get(Integer id) {
        if (id == null) {
            return null;
        }
        return allotWorkerDao.get(id);
    }

    @Override
    public List<AllotWorker> findList(AllotWorker allotWorker) {
        return allotWorkerDao.findList(allotWorker);
    }

    @Override
    public List<AllotWorker> findAllList(AllotWorker allotWorker) {
        return allotWorkerDao.findAllList(allotWorker);
    }

    @Override
    public Integer insert(AllotWorker allotWorker) {
        if (allotWorker.getCreateTime() == null) {
            allotWorker.setCreateTime(new Date());
        }
        allotWorkerDao.insert(allotWorker);
        return allotWorker.getId();
    }

    @Override
    public int insertBatch(List<AllotWorker> allotWorkers) {
        return allotWorkerDao.insertBatch(allotWorkers);
    }

    @Override
    public int update(AllotWorker allotWorker) {
        return allotWorkerDao.update(allotWorker);
    }

    @Override
    public boolean delete(Integer id) {
        if (null == id) {
            return false;
        }
        return allotWorkerDao.delete(id);
    }

    @Override
    public AllotWorker getByWorkerId(Integer workerId) {
        if (null == workerId) {
            return null;
        }
        return allotWorkerDao.getByWorkerId(workerId);
    }
}
