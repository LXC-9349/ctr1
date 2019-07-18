package com.ctr.crm.moduls.sysrecyclingmemberconfig.service.impl;

import com.alibaba.fastjson.JSON;
import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.sysdict.service.SysDictService;
import com.ctr.crm.moduls.syslog.models.SysLog;
import com.ctr.crm.moduls.syslog.service.SysLogService;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
import com.ctr.crm.moduls.sysrecyclingmemberconfig.dao.SysRecyclingMemberConfigDao;
import com.ctr.crm.moduls.sysrecyclingmemberconfig.models.SysRecyclingMemberConfig;
import com.ctr.crm.moduls.sysrecyclingmemberconfig.service.SysRecyclingMemberConfigService;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.PageMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-04-23
 */
@Service("sysRecyclingMemberConfigService")
public class SysRecyclingMemberConfigServiceImpl implements SysRecyclingMemberConfigService {

    @Autowired
    private SysRecyclingMemberConfigDao sysRecyclingMemberConfigDao;
    @Autowired
    private BaseDao baseDao;
    @Autowired
    @Lazy
    private SysLogService sysLogService;
    @Autowired
    @Lazy
    private SysDictService sysDictService;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, SysRecyclingMemberConfig sysRecyclingMemberConfig) {
        pageMode.setSqlFrom("select * from SysRecyclingMemberConfig");
        pageMode.setSqlWhere("deleted=0 order by createTime desc");
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public SysRecyclingMemberConfig get(String id) {
        return sysRecyclingMemberConfigDao.get(id);
    }

    @Override
    public List<SysRecyclingMemberConfig> findList(SysRecyclingMemberConfig sysRecyclingMemberConfig) {
        return sysRecyclingMemberConfigDao.findList(sysRecyclingMemberConfig);
    }

    @Override
    public List<SysRecyclingMemberConfig> findAllList(SysRecyclingMemberConfig sysRecyclingMemberConfig) {
        return sysRecyclingMemberConfigDao.findAllList(sysRecyclingMemberConfig);
    }

    @Override
    public String insert(SysRecyclingMemberConfig sysRecyclingMemberConfig) {
        if (StringUtils.isBlank(sysRecyclingMemberConfig.getId())) {
            sysRecyclingMemberConfig.setId(CommonUtils.getUUID());
        }
        if (sysRecyclingMemberConfig.getCreateTime() == null) {
            sysRecyclingMemberConfig.setCreateTime(new Date());
        }
        sysRecyclingMemberConfigDao.insert(sysRecyclingMemberConfig);
        sysLogService.insert(new SysLog(sysRecyclingMemberConfig.getId(), null, null, null, JSON.toJSONString(sysRecyclingMemberConfig), sysDictService.findValue("log_type", "客户回收策略配置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "新增")));
        return sysRecyclingMemberConfig.getId();
    }

    @Override
    public int insertBatch(List<SysRecyclingMemberConfig> sysRecyclingMemberConfigs) {
        return sysRecyclingMemberConfigDao.insertBatch(sysRecyclingMemberConfigs);
    }

    @Override
    public int update(SysRecyclingMemberConfig sysRecyclingMemberConfig) {
        sysLogService.insert(new SysLog(sysRecyclingMemberConfig.getId(), null, null, null, "修改前:" + JSON.toJSONString(get(sysRecyclingMemberConfig.getId())) + "修改后:" + JSON.toJSONString(sysRecyclingMemberConfig), sysDictService.findValue("log_type", "客户回收策略配置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "修改")));
        return sysRecyclingMemberConfigDao.update(sysRecyclingMemberConfig);
    }

    @Override
    public boolean delete(String id) {
        sysLogService.insert(new SysLog(id, null, null, null, JSON.toJSONString(get(id)), sysDictService.findValue("log_type", "客户回收策略配置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "删除")));
        return sysRecyclingMemberConfigDao.delete(id);
    }

}
