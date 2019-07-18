package com.ctr.crm.moduls.tag.service.impl;

import com.alibaba.fastjson.JSON;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.sysdict.service.SysDictService;
import com.ctr.crm.moduls.syslog.models.SysLog;
import com.ctr.crm.moduls.syslog.service.SysLogService;
import com.ctr.crm.moduls.tag.dao.TagGroupDao;
import com.ctr.crm.moduls.tag.models.TagGroup;
import com.ctr.crm.moduls.tag.service.TagGroupService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-04-19
 */
@Service("tagGroupService")
public class TagGroupServiceImpl implements TagGroupService {

    @Autowired
    private TagGroupDao tagGroupDao;
    @Autowired
    private BaseDao baseDao;
    @Autowired
    @Lazy
    private SysLogService sysLogService;
    @Autowired
    @Lazy
    private SysDictService sysDictService;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, TagGroup tagGroup) {
        pageMode.setSqlFrom("select *,(select count(1) from Tag mt where mt.groupId=mg.id) total from TagGroup mg");
        pageMode.setSqlWhere("deleted=0");
        if (StringUtils.isNotBlank(tagGroup.getGroupName())) {
            pageMode.setSqlWhere("groupName like '%" + pageMode.noSqlInjection(tagGroup.getGroupName()) + "%'");
        }
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        pageMode.setOrderBy("order by createTime asc");
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public TagGroup get(String id) {
        return tagGroupDao.get(id);
    }

    @Override
    public List<TagGroup> findList(TagGroup tagGroup) {
        return tagGroupDao.findList(tagGroup);
    }

    @Override
    public List<TagGroup> findAllList(TagGroup tagGroup) {
        return tagGroupDao.findAllList(tagGroup);
    }

    @Override
    public String insert(TagGroup tagGroup) {
        if (StringUtils.isBlank(tagGroup.getId())) {
            tagGroup.setId(CommonUtils.getUUID());
        }
        if (tagGroup.getCreateTime() == null) {
            tagGroup.setCreateTime(new Date());
        }
        tagGroupDao.insert(tagGroup);
        sysLogService.insert(new SysLog(tagGroup.getId(), null, null, null, JSON.toJSONString(tagGroup), sysDictService.findValue("log_type", "客户标签组设置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "新增")));
        return tagGroup.getId();
    }

    @Override
    public int insertBatch(List<TagGroup> tagGroups) {
        return tagGroupDao.insertBatch(tagGroups);
    }

    @Override
    public int update(TagGroup tagGroup) {
        sysLogService.insert(new SysLog(tagGroup.getId(), null, null, null, "修改前:" + JSON.toJSONString(get(tagGroup.getId())) + "修改后:" + JSON.toJSONString(tagGroup), sysDictService.findValue("log_type", "客户标签组设置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "修改")));

        return tagGroupDao.update(tagGroup);
    }

    @Override
    public boolean delete(String id) {
        sysLogService.insert(new SysLog(id, null, null, null, JSON.toJSONString(get(id)), sysDictService.findValue("log_type", "客户标签组设置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "删除")));
        return tagGroupDao.delete(id);
    }

}
