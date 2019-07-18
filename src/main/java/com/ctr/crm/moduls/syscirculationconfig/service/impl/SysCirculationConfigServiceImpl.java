package com.ctr.crm.moduls.syscirculationconfig.service.impl;

import com.alibaba.fastjson.JSON;
import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.syscirculationconfig.dao.SysCirculationConfigDao;
import com.ctr.crm.moduls.syscirculationconfig.models.SysCirculationConfig;
import com.ctr.crm.moduls.syscirculationconfig.service.SysCirculationConfigService;
import com.ctr.crm.moduls.sysdict.service.SysDictService;
import com.ctr.crm.moduls.syslog.models.SysLog;
import com.ctr.crm.moduls.syslog.service.SysLogService;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
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
@Service("sysCirculationConfigService")
public class SysCirculationConfigServiceImpl implements SysCirculationConfigService {

    @Autowired
    private SysCirculationConfigDao sysCirculationConfigDao;
    @Autowired
    private BaseDao baseDao;
    @Autowired
    @Lazy
    private SysLogService sysLogService;
    @Autowired
    @Lazy
    private SysDictService sysDictService;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, SysCirculationConfig sysCirculationConfig, String type) {
        pageMode.setSqlFrom("select *,(case when deleteMember=1 then 1 when transferMember=1 then 2 when quitMember then 3 else 0 end) type from SysCirculationConfig");
        pageMode.setSqlWhere("deleted=0");
        if (StringUtils.isNotBlank(type)) {
            switch (type) {
                case "1":
                    pageMode.setSqlWhere("deleteMember=1");
                    break;
                case "2":
                    pageMode.setSqlWhere("transferMember=1");
                    break;
                case "3":
                    pageMode.setSqlWhere("quitMember=1");
                    break;
                default:
                    System.err.println("type错误");
            }
        }
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        pageMode.setOrderBy("order by createTime asc");
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        for (Map<String, Object> datum : data) {
            String reason = datum.get("reason").toString();
            if (reason.equals("无法联系") || reason.equals("无意向客户") || reason.equals("其它")) {
                datum.put("isDelete", false);
            } else {
                datum.put("isDelete", true);
            }
        }
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public SysCirculationConfig get(String id) {
    	if(StringUtils.isBlank(id)) return null;
        return sysCirculationConfigDao.get(id);
    }

    @Override
    public List<SysCirculationConfig> findList(SysCirculationConfig sysCirculationConfig) {
        return sysCirculationConfigDao.findList(sysCirculationConfig);
    }

    @Override
    public List<SysCirculationConfig> findAllList(SysCirculationConfig sysCirculationConfig) {
        return sysCirculationConfigDao.findAllList(sysCirculationConfig);
    }
    
    @Override
    public List<SysCirculationConfig> getCirculationList(Integer circulationType) {
    	return sysCirculationConfigDao.getCirculationList(circulationType);
    }

    @Override
    public String insert(SysCirculationConfig sysCirculationConfig) {
        if (StringUtils.isBlank(sysCirculationConfig.getId())) {
            sysCirculationConfig.setId(CommonUtils.getUUID());
        }
        if (sysCirculationConfig.getCreateTime() == null) {
            sysCirculationConfig.setCreateTime(new Date());
        }
        sysCirculationConfigDao.insert(sysCirculationConfig);
        sysLogService.insert(new SysLog(sysCirculationConfig.getId(), null, null, null, JSON.toJSONString(sysCirculationConfig), sysDictService.findValue("log_type", "客户流转原因配置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "新增")));
        return sysCirculationConfig.getId();
    }

    @Override
    public int insertBatch(List<SysCirculationConfig> sysCirculationConfigs) {
        return sysCirculationConfigDao.insertBatch(sysCirculationConfigs);
    }

    @Override
    public int update(SysCirculationConfig sysCirculationConfig) {
        sysLogService.insert(new SysLog(sysCirculationConfig.getId(), null, null, null, "修改前:" + JSON.toJSONString(get(sysCirculationConfig.getId())) + "修改后:" + JSON.toJSONString(sysCirculationConfig), sysDictService.findValue("log_type", "客户流转原因配置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "修改")));
        return sysCirculationConfigDao.update(sysCirculationConfig);
    }

    @Override
    public boolean delete(String id) {
        sysLogService.insert(new SysLog(id, null, null, null, JSON.toJSONString(get(id)), sysDictService.findValue("log_type", "客户流转原因配置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "删除")));
        return sysCirculationConfigDao.delete(id);
    }

}
