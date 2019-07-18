package com.ctr.crm.moduls.approvalgroup.service.impl;

import com.alibaba.fastjson.JSON;
import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.sysdict.service.SysDictService;
import com.ctr.crm.moduls.syslog.models.SysLog;
import com.ctr.crm.moduls.syslog.service.SysLogService;
import com.ctr.crm.commons.jdbc.YunhuJdbcOperations;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
import com.ctr.crm.moduls.approvalgroup.dao.ApprovalGroupDao;
import com.ctr.crm.moduls.approvalgroup.models.ApprovalGroup;
import com.ctr.crm.moduls.approvalgroup.service.ApprovalGroupService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-04-24
 */
@Service("approvalGroupService")
public class ApprovalGroupServiceImpl implements ApprovalGroupService {

    @Autowired
    private ApprovalGroupDao approvalGroupDao;
    @Autowired
    private BaseDao baseDao;
    @Autowired
    @Lazy
    private SysLogService sysLogService;
    @Autowired
    @Lazy
    private SysDictService sysDictService;
    @Resource
    private YunhuJdbcOperations crmJdbc;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, ApprovalGroup approvalGroup) {
        pageMode.setSqlFrom("select * from ApprovalGroup");
        pageMode.setSqlWhere("deleted=0");
        if (StringUtils.isNotBlank(approvalGroup.getName())) {
            pageMode.setSqlWhere("name like '%" + pageMode.noSqlInjection(approvalGroup.getName()) + "%'");
        }
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        for (Map<String, Object> list : data) {
            if (list.get("workerIds") != null) {
                String w_ids = "'" + list.get("workerIds").toString().replaceAll(",", "','") + "'";
                list.put("workerList", baseDao.select("select workerId,workerName from Worker where workerId in(" + w_ids + ")"));
            }
        }
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public ApprovalGroup get(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return approvalGroupDao.get(id);
    }

    @Override
    public List<Map<String, Object>> findWorker(String workerIds) {
        String w_ids = "'" + workerIds.replaceAll(",", "','") + "'";
        return baseDao.select("select * from Worker where workerId in(" + w_ids + ")");
    }


    @Override
    public List<ApprovalGroup> findList(ApprovalGroup approvalGroup) {
        return approvalGroupDao.findList(approvalGroup);
    }

    @Override
    public List<ApprovalGroup> findAllList(ApprovalGroup approvalGroup) {
        return approvalGroupDao.findAllList(approvalGroup);
    }

    @Override
    public String insert(ApprovalGroup approvalGroup) {
        if (StringUtils.isBlank(approvalGroup.getId())) {
            approvalGroup.setId(CommonUtils.getUUID());
        }
        if (approvalGroup.getCreateTime() == null) {
            approvalGroup.setCreateTime(new Date());
        }
        approvalGroupDao.insert(approvalGroup);
        sysLogService.insert(new SysLog(approvalGroup.getId(), null, null, null, JSON.toJSONString(approvalGroup), sysDictService.findValue("log_type", "流程操作"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "新增")));
        return approvalGroup.getId();
    }

    @Override
    public int insertBatch(List<ApprovalGroup> approvalGroups) {
        return approvalGroupDao.insertBatch(approvalGroups);
    }

    @Override
    public int update(ApprovalGroup approvalGroup) {
        sysLogService.insert(new SysLog(approvalGroup.getId(), null, null, null, "修改前:"+JSON.toJSONString(get(approvalGroup.getId()))+"修改后:"+JSON.toJSONString(approvalGroup), sysDictService.findValue("log_type", "流程操作"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "修改")));
        return approvalGroupDao.update(approvalGroup);
    }

    @Override
    public boolean delete(String id) {
        Long count=crmJdbc.queryForObject("select ifnull((" +
                "select (count(1)+(select  count(1) from  ApprovalFlow af where af.proId =?)) from ApprovalFlowSetting afs  where afs.groupId=?" +
                "),0) count from dual", Long.class,id,id);
        if(count>0){
            return false;
        }
        sysLogService.insert(new SysLog(id, null, null, null, JSON.toJSONString(get(id)), sysDictService.findValue("log_type", "流程操作"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "删除")));
        return approvalGroupDao.delete(id);
    }

    @Override
    public List<String> getGroupIdByWorkerId(Integer workerId) {
        return approvalGroupDao.getGroupIdByWorkerId(workerId);
    }

    @Override
    public List<ApprovalGroup> findListByIds(List<String> groupIds) {
        if (groupIds != null && groupIds.size() > 0) {
            List<ApprovalGroup> las = new ArrayList<>();
            for (String groupId : groupIds) {
                las.add(approvalGroupDao.get(groupId));
            }
            return las;
        }
        return null;
    }
}
