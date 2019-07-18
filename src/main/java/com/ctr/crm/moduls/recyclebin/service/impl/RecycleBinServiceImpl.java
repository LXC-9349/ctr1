package com.ctr.crm.moduls.recyclebin.service.impl;

import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.recyclebin.dao.RecycleBinDao;
import com.ctr.crm.moduls.recyclebin.models.RecycleBin;
import com.ctr.crm.moduls.recyclebin.service.RecycleBinService;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-05-10
 */
@Service("recycleBinService")
public class RecycleBinServiceImpl implements RecycleBinService {

    @Autowired
    private RecycleBinDao recycleBinDao;
    @Autowired
    private BaseDao baseDao;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, RecycleBin recycleBin, String recycleTimeA, String recycleTimeB, String memberName) {
        pageMode.setSqlFrom("select rb.id,rb.memberId,rb.recycleTime,rb.recycleWorker,w.workerName recycleWorkerName,rb.recycleReason,rb.allotTime,rb.workerId,rb.workerName,rb.caseClass,m.trueName,m.birthday,m.sex,m.salary,m.education,m.marriage,m.height,m.workCity,m.homeTown from RecycleBin rb left join Worker w on rb.recycleWorker=w.workerId join MemberBaseInfo m on rb.memberId=m.memberId");
        if (StringUtils.isNotBlank(recycleTimeA)) {
            pageMode.setSqlWhere("rb.recycleTime >= '" + pageMode.noSqlInjection(recycleTimeA) + "'");
        }
        if (StringUtils.isNotBlank(recycleTimeB)) {
            pageMode.setSqlWhere("rb.recycleTime <=DATE_ADD( '" + pageMode.noSqlInjection(recycleTimeB) + "',INTERVAL 1 DAY)");
        }
        if (recycleBin.getMemberId() != null) {
            pageMode.setSqlWhere("rb.memberId = " + recycleBin.getMemberId());
        }
        if (recycleBin.getWorkerId() != null) {
            pageMode.setSqlWhere("rb.recycleWorker = " + recycleBin.getWorkerId());
        }
        if(StringUtils.isNotBlank(recycleBin.getRecycleReason())){
        	pageMode.setSqlWhere("rb.recycleReason like '%"+recycleBin.getRecycleReason()+"%'");
        }
        if(StringUtils.isNotBlank(memberName)){
            pageMode.setSqlWhere("m.trueName like '%"+pageMode.noSqlInjection(memberName)+"%'");
        }
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        pageMode.setOrderBy("order by rb.recycleTime desc");
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public RecycleBin get(Integer id) {
        if (id == null) {
            return null;
        }
        return recycleBinDao.get(id);
    }

    @Override
    public List<RecycleBin> findList(RecycleBin recycleBin) {
        return recycleBinDao.findList(recycleBin);
    }

    @Override
    public List<RecycleBin> findAllList(RecycleBin recycleBin) {
        return recycleBinDao.findAllList(recycleBin);
    }

    @Override
    public Boolean insert(RecycleBin recycleBin) {
        if (recycleBin.getRecycleTime() == null) {
            recycleBin.setRecycleTime(new Date());
        }
        return recycleBinDao.insert(recycleBin);
    }

    @Override
    public int insertBatch(List<RecycleBin> recycleBins) {
        return recycleBinDao.insertBatch(recycleBins);
    }

    @Override
    public int update(RecycleBin recycleBin) {
        return recycleBinDao.update(recycleBin);
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) {
            return false;
        }
        return recycleBinDao.delete(id);
    }
}
