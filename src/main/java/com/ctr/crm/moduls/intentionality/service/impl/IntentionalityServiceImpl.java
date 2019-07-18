package com.ctr.crm.moduls.intentionality.service.impl;

import com.alibaba.fastjson.JSON;
import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.sysdict.service.SysDictService;
import com.ctr.crm.moduls.syslog.models.SysLog;
import com.ctr.crm.moduls.syslog.service.SysLogService;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
import com.ctr.crm.moduls.intentionality.dao.IntentionalityDao;
import com.ctr.crm.moduls.intentionality.models.Intentionality;
import com.ctr.crm.moduls.intentionality.service.IntentionalityService;
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
 * @date 2019-04-19
 */
@Service("intentionalityService")
public class IntentionalityServiceImpl implements IntentionalityService {

    @Autowired
    private IntentionalityDao intentionalityDao;
    @Autowired
    private BaseDao baseDao;
    @Autowired
    @Lazy
    private SysLogService sysLogService;
    @Autowired
    @Lazy
    private SysDictService sysDictService;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, Intentionality intentionality) {
        pageMode.setSqlFrom("select * from Intentionality");
        pageMode.setSqlWhere("deleted=0");
        if (intentionality.getType() != null) {
            pageMode.setSqlWhere("type = '" + intentionality.getType() + "'");
        }
        pageMode.setOrderBy(" order by createTime asc");
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        pageMode.setOrderBy("order by caseClass asc,createTime");
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public Intentionality get(String id) {
        if(id==null)
            return null;
        return intentionalityDao.get(id);
    }

    /**
     * 功能描述:
     * 根据type获取一级成熟度
     *
     * @param: type 1销售2红娘3才俊佳丽
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/7 16:44
     */
    @Override
    public Intentionality getByTypeFirst(Integer type) {
        try {
            Intentionality intentionality = intentionalityDao.getByTypeFirst(type);
            if (intentionality == null)
                intentionality = new Intentionality(999);
            return intentionality;
        } catch (Exception e) {
            return new Intentionality(999);
        }
    }

    @Override
    public List<Intentionality> findList(Intentionality intentionality) {
        return intentionalityDao.findList(intentionality);
    }

    @Override
    public List<Intentionality> findAllList(Intentionality intentionality) {
        return intentionalityDao.findAllList(intentionality);
    }

    @Override
    public String insert(Intentionality intentionality) {
        if (StringUtils.isBlank(intentionality.getId())) {
            intentionality.setId(CommonUtils.getUUID());
        }
        if (intentionality.getCreateTime() == null) {
            intentionality.setCreateTime(new Date());
        }
        Integer count=intentionalityDao.getCaseClass(intentionality.getCaseClass());
        if(count>0){
            return "dup";
        }
        intentionalityDao.insert(intentionality);
        sysLogService.insert(new SysLog(intentionality.getId(), null, null, null, JSON.toJSONString(intentionality), sysDictService.findValue("log_type", "意向度设置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "新增")));
        return intentionality.getId();
    }

    @Override
    public int insertBatch(List<Intentionality> intentionalitys) {
        return intentionalityDao.insertBatch(intentionalitys);
    }

    @Override
    public int update(Intentionality intentionality) {
        sysLogService.insert(new SysLog(intentionality.getId(), null, null, null, "修改前：" + JSON.toJSONString(get(intentionality.getId())) + "修改后:" + JSON.toJSONString(intentionality), sysDictService.findValue("log_type", "意向度设置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "修改")));
        return intentionalityDao.update(intentionality);
    }

    @Override
    public boolean delete(String id) {
        return intentionalityDao.delete(id);
    }
    
    @Override
    public Intentionality select(Integer caseClass) {
    	if(caseClass == null) return null;
    	return intentionalityDao.select(caseClass);
    }
    
    @Override
    public String getStatisticsStr(Integer type, String targetAsAlias) {
    	if(type == null || StringUtils.isBlank(targetAsAlias)) return null;
    	return intentionalityDao.getStatisticsStr(type, targetAsAlias);
    }

}
