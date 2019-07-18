package com.ctr.crm.moduls.allot.service.impl;

import com.ctr.crm.moduls.allot.dao.AllotCursorDao;
import com.ctr.crm.moduls.allot.models.AllotCursor;
import com.ctr.crm.moduls.allot.models.AllotWorker;
import com.ctr.crm.moduls.allot.service.AllotCursorService;
import com.ctr.crm.moduls.allot.service.AllotWorkerService;
import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import com.ctr.crm.commons.jdbc.YunhuJdbcOperations;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-05-05
 */
@Service("allotCursorService")
public class AllotCursorServiceImpl implements AllotCursorService {

    private static final Log log = LogFactory.getLog("allot");

    @Autowired
    private AllotCursorDao allotCursorDao;
    @Autowired
    private BaseDao baseDao;
    @Resource
    private WorkerService workerService;
    @Autowired
    private AllotWorkerService allotWorkerService;
    @Resource
    private YunhuJdbcOperations crmJdbc;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, AllotCursor allotCursor) {
        pageMode.setSqlFrom("select * from AllotCursor");
        pageMode.setSqlWhere("deleted=0");
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public AllotCursor get(Integer id) {
        if (id == null) {
            return null;
        }
        return allotCursorDao.get(id);
    }

    @Override
    public List<AllotCursor> findList(AllotCursor allotCursor) {
        return allotCursorDao.findList(allotCursor);
    }

    @Override
    public List<AllotCursor> findAllList(AllotCursor allotCursor) {
        return allotCursorDao.findAllList(allotCursor);
    }

    @Override
    public Integer insert(AllotCursor allotCursor) {
        if (allotCursor.getUpdateTime() == null) {
            allotCursor.setUpdateTime(System.currentTimeMillis());
        }
        if (allotCursor.getCreatetime() == null) {
            allotCursor.setCreatetime(new Date());
        }
        allotCursorDao.insert(allotCursor);
        return allotCursor.getId();
    }

    @Override
    public int insertBatch(List<AllotCursor> allotCursors) {
        return allotCursorDao.insertBatch(allotCursors);
    }

    @Override
    public int update(AllotCursor allotCursor) {
        return allotCursorDao.update(allotCursor);
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) {
            return false;
        }
        return allotCursorDao.delete(id);
    }

    @Override
    public List<AllotCursor> findByRuleId(String id) {
        return allotCursorDao.findByRuleId(id);
    }

    /**
     * 功能描述:
     * 选择合适的销售处理资源
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/6 16:11
     */
    @Override
    public Worker findNext(String ruleId, int size, int i) {
        try {
            if (i >= size) {
                log.warn("当前规则销售人员分配已满");
                return null;
            }
            AllotCursor allotCursot = allotCursorDao.findNext(ruleId);
            allotCursot.setUpdateTime(System.currentTimeMillis());
            update(allotCursot);
            Worker worker = workerService.select(allotCursot.getWorkerId());
            AllotWorker allotWorker = allotWorkerService.getByWorkerId(worker.getWorkerId());
            Integer hasAllot = allotWorker.getHasAllot();//已库存
            Integer alrAllotDay = allotWorker.getAlrAllotDay();//今天已分配
            if (hasAllot.intValue() >= allotWorker.getMaxAllot().intValue() && allotWorker.getMaxAllot() != 0) {
                log.warn(worker.getWorkerId() + "已达到最大库容,无法分配");
                i++;
                return findNext(ruleId, size, i);
            }
            if (alrAllotDay.intValue() >= allotWorker.getMaxAllotTheDay().intValue() && allotWorker.getMaxAllotTheDay() != 0) {
                log.warn(worker.getWorkerId() + "已达到今日最大库容,无法分配");
                i++;
                return findNext(ruleId, size, i);
            }
            //更新库容
            crmJdbc.update("update AllotWorker set hasAllot=hasAllot+1,alrAllotDay=alrAllotDay+1 where workerId=?", worker.getWorkerId());
            return worker;
        } catch (Exception e) {
            return null;
        }
    }
}
