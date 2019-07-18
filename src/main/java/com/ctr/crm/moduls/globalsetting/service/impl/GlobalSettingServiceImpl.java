package com.ctr.crm.moduls.globalsetting.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.globalsetting.dao.GlobalSettingDao;
import com.ctr.crm.moduls.globalsetting.models.GlobalSetting;
import com.ctr.crm.moduls.globalsetting.service.GlobalSettingService;
import com.ctr.crm.moduls.sysdict.service.SysDictService;
import com.ctr.crm.moduls.syslog.models.SysLog;
import com.ctr.crm.moduls.syslog.service.SysLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
import com.yunhus.redisclient.RedisProxy;

/**
 * @author DoubleLi
 * @date 2019-04-19
 */
@Service("globalSettingService")
public class GlobalSettingServiceImpl implements GlobalSettingService {

    @Autowired
    private GlobalSettingDao globalSettingDao;
    @Autowired
    private BaseDao baseDao;
    @Autowired
    @Lazy
    private SysLogService sysLogService;
    @Autowired
    @Lazy
    private SysDictService sysDictService;
    private RedisProxy redis = RedisProxy.getInstance();

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, GlobalSetting globalSetting) {
        pageMode.setSqlFrom("select * from GlobalSetting");
        pageMode.setSqlWhere("deleted=0");
        if (StringUtils.isNotBlank(globalSetting.getType())) {
            pageMode.setSqlWhere("type like '%" + pageMode.noSqlInjection(globalSetting.getType()) + "%'");
        }
        if (StringUtils.isNotBlank(globalSetting.getName())) {
            pageMode.setSqlWhere("type name '%" + pageMode.noSqlInjection(globalSetting.getName()) + "%'");
        }
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        pageMode.setOrderBy("order by createTime asc");
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    /**
     * 功能描述:
     * 获取排重配置
     *
     * @param:
     * @return: 1无须排重2排重
     * @author: DoubleLi
     * @date: 2019/5/6 17:34
     */
    @Override
    public boolean isDuplicate() {
    	String key = "global_duplicate_off";
    	Object v = redis.get(key);
    	if(v != null){
    		int value = CommonUtils.evalInt(v, 1);
    		return value==2;
    	}
        try {
            GlobalSetting globalSetting = new GlobalSetting();
            globalSetting.setType("'duplicate_off'");
            List<GlobalSetting> list = findList(globalSetting);
            if(list == null || list.isEmpty()) return false;
            int value = CommonUtils.evalInt(list.get(0).getValue(), 1);
            redis.set(key, value, Constants.exp_24hours);
            return value == 2;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 功能描述:
     * 获取手机号加密配置
     *
     * @param:
     * @return: 1加密0不加密
     * @author: DoubleLi
     * @date: 2019/5/6 17:34
     */
    @Override
    public boolean isPhoneEncrypt() {
    	String key = "global_phoneencrypt";
    	Object v = redis.get(key);
    	if(v != null){
    		int value = CommonUtils.evalInt(v, 0);
    		return value==1;
    	}
        try {
            GlobalSetting globalSetting = new GlobalSetting();
            globalSetting.setType("'phoneencrypt'");
            List<GlobalSetting> list = findList(globalSetting);
            if(list == null || list.isEmpty()) return false;
            int value = CommonUtils.evalInt(list.get(0).getValue(), 0);
            redis.set(key, value, Constants.exp_24hours);
            return value == 1;
        } catch (Exception e) {
            return false;
        }
    }
    /**
     *
     * 功能描述: 获取每人每天允许捞取
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/28 11:28
     */
    @Override
    public Integer gainNum() {
        String key = "gain_total";
        Object v = redis.get(key);
        if(v != null){
            int value = CommonUtils.evalInt(v, 0);
            return value;
        }
        try {
            GlobalSetting globalSetting = new GlobalSetting();
            globalSetting.setType("'gain_total'");
            List<GlobalSetting> list = findList(globalSetting);
            if(list == null || list.isEmpty()) return 0;
            int value = CommonUtils.evalInt(list.get(0).getValue(), 0);
            redis.set(key, value, Constants.exp_24hours);
            return value;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public GlobalSetting get(String id) {
    	if(id == null) return null;
        return globalSettingDao.get(id);
    }

    @Override
    public List<GlobalSetting> findList(GlobalSetting globalSetting) {
        return globalSettingDao.findList(globalSetting);
    }

    @Override
    public List<GlobalSetting> findAllList(GlobalSetting globalSetting) {
        return globalSettingDao.findAllList(globalSetting);
    }

    @Override
    public String insert(GlobalSetting globalSetting) {
        if (StringUtils.isBlank(globalSetting.getId())) {
            globalSetting.setId(CommonUtils.getUUID());
        }
        if (globalSetting.getCreateTime() == null) {
            globalSetting.setCreateTime(new Date());
        }
        globalSettingDao.insert(globalSetting);
        sysLogService.insert(new SysLog(globalSetting.getId(), null, null, null, JSON.toJSONString(globalSetting), sysDictService.findValue("log_type", "系统全局配置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "新增")));
        return globalSetting.getId();
    }

    @Override
    public int insertBatch(List<GlobalSetting> globalSettings) {
        return globalSettingDao.insertBatch(globalSettings);
    }

    @Override
    public int update(GlobalSetting globalSetting) {
        sysLogService.insert(new SysLog(globalSetting.getId(), null, null, null, "修改前:" + JSON.toJSONString(get(globalSetting.getId())) + "修改后:" + JSON.toJSONString(globalSetting), sysDictService.findValue("log_type", "系统全局配置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "修改")));
        String type = globalSetting.getType();
        if(StringUtils.isBlank(type)){
        	GlobalSetting src = get(globalSetting.getId());
        	if(src != null) type = src.getType();
        }
        if(StringUtils.equalsAny(type, "phoneencrypt", "duplicate_off","gain_total")){
        	redis.delete("global_"+globalSetting.getType());
        }
        return globalSettingDao.update(globalSetting);
    }

    @Override
    public boolean delete(String id) {
        sysLogService.insert(new SysLog(id, null, null, null, JSON.toJSONString(get(id)), sysDictService.findValue("log_type", "系统全局配置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "删除")));
        return globalSettingDao.delete(id);
    }

}
