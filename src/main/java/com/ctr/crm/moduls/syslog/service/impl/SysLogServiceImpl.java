package com.ctr.crm.moduls.syslog.service.impl;

import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.syslog.dao.SysLogDao;
import com.ctr.crm.moduls.syslog.models.SysLog;
import com.ctr.crm.moduls.syslog.service.SysLogService;
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
@Service("sysLogService")
public class SysLogServiceImpl implements SysLogService {

    @Autowired
    private SysLogDao sysLogDao;
    @Autowired
    private BaseDao baseDao;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, SysLog sysLog) {
        pageMode.setSqlFrom("select * from SysLog");
        pageMode.setSqlWhere("deleted=0");
        // 查询总条数
        if (StringUtils.isNotBlank(sysLog.getLogType())) {
            pageMode.setSqlWhere("logType = '" + pageMode.noSqlInjection(sysLog.getLogType()) + "'");
        }
        if (StringUtils.isNotBlank(sysLog.getLogAction())) {
            pageMode.setSqlWhere("logAction = '" + pageMode.noSqlInjection(sysLog.getLogAction()) + "'");
        }
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        pageMode.setOrderBy("order by createTime desc");
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public SysLog get(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return sysLogDao.get(id);
    }

    @Override
    public List<SysLog> findList(SysLog sysLog) {
        return sysLogDao.findList(sysLog);
    }

    @Override
    public List<SysLog> findAllList(SysLog sysLog) {
        return sysLogDao.findAllList(sysLog);
    }

    @Override
    public String insert(SysLog sysLog) {
        if (StringUtils.isBlank(sysLog.getId())) {
            sysLog.setId(CommonUtils.getUUID());
        }
        if (sysLog.getCreateTime() == null) {
            sysLog.setCreateTime(new Date());
        }
        try {
            sysLogDao.insert(sysLog);
        } catch (Exception e) {
            System.err.println(e);
        }
        return sysLog.getId();
    }

    @Override
    public int insertBatch(List<SysLog> sysLogs) {
        return sysLogDao.insertBatch(sysLogs);
    }

    @Override
    public int update(SysLog sysLog) {
        return sysLogDao.update(sysLog);
    }

    @Override
    public boolean delete(String id) {
        if (StringUtils.isBlank(id)) {
            return false;
        }
        return sysLogDao.delete(id);
    }

}
