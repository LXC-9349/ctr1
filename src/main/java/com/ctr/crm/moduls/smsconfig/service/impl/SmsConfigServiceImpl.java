package com.ctr.crm.moduls.smsconfig.service.impl;

import com.alibaba.fastjson.JSON;
import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.smsconfig.dao.SmsConfigDao;
import com.ctr.crm.moduls.smsconfig.models.SmsConfig;
import com.ctr.crm.moduls.smsconfig.service.SmsConfigService;
import com.ctr.crm.moduls.sysdict.service.SysDictService;
import com.ctr.crm.moduls.syslog.models.SysLog;
import com.ctr.crm.moduls.syslog.service.SysLogService;
import com.ctr.crm.commons.jdbc.YunhuJdbcOperations;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.PageMode;

import com.yunhus.redisclient.RedisProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-04-28
 */
@Service("smsConfigService")
public class SmsConfigServiceImpl implements SmsConfigService {

    @Autowired
    private SmsConfigDao smsConfigDao;
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
    private RedisProxy redisClient = RedisProxy.getInstance();
    private static final String SMS_USER_CONFIG_KEY = "sms_user_config";

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, SmsConfig smsConfig) {
        pageMode.setSqlFrom("select * from SmsConfig");
        pageMode.setSqlWhere("deleted=0");
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public SmsConfig get(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return smsConfigDao.get(id);
    }

    @Override
    public List<SmsConfig> findList(SmsConfig smsConfig) {
        return smsConfigDao.findList(smsConfig);
    }

    @Override
    public List<SmsConfig> findAllList(SmsConfig smsConfig) {
        List<SmsConfig> smsConfigs;
        Object o = redisClient.get(SMS_USER_CONFIG_KEY);
        if (o == null) {
            smsConfigs = smsConfigDao.findAllList(smsConfig);
            redisClient.set(SMS_USER_CONFIG_KEY, smsConfigs);
        } else {
            smsConfigs = (List<SmsConfig>) o;
        }
        return smsConfigs;
    }

    @Override
    public String insert(SmsConfig smsConfig) {
        if (StringUtils.isBlank(smsConfig.getId())) {
            smsConfig.setId(CommonUtils.getUUID());
        }
        if (smsConfig.getCreateTime() == null) {
            smsConfig.setCreateTime(new Date());
        }
        smsConfigDao.insert(smsConfig);
        redisClient.delete(SMS_USER_CONFIG_KEY);
        sysLogService.insert(new SysLog(smsConfig.getId(), null, null, null, JSON.toJSONString(smsConfig), sysDictService.findValue("log_type", "短信用户配置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "新增")));
        return smsConfig.getId();
    }

    @Override
    public int insertBatch(List<SmsConfig> smsConfigs) {
        redisClient.delete(SMS_USER_CONFIG_KEY);
        return smsConfigDao.insertBatch(smsConfigs);
    }

    @Override
    public int update(SmsConfig smsConfig) {
        sysLogService.insert(new SysLog(smsConfig.getId(), null, null, null, "修改前:" + JSON.toJSONString(get(smsConfig.getId())) + "修改后:" + JSON.toJSONString(smsConfig), sysDictService.findValue("log_type", "短信用户配置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "修改")));
        if (smsConfig.getStatus() != null && smsConfig.getStatus() == 1) {
            crmJdbc.update("update SmsConfig set status=0");
        }
        redisClient.delete(SMS_USER_CONFIG_KEY);
        return smsConfigDao.update(smsConfig);
    }

    @Override
    public boolean delete(String id) {
        if (StringUtils.isBlank(id)) {
            return false;
        }
        redisClient.delete(SMS_USER_CONFIG_KEY);
        sysLogService.insert(new SysLog(id, null, null, null, JSON.toJSONString(get(id)), sysDictService.findValue("log_type", "短信用户配置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "删除")));
        return smsConfigDao.delete(id);
    }

    @Override
    public SmsConfig getEnable() {
        return smsConfigDao.getEnable();
    }
}
