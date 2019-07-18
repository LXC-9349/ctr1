package com.ctr.crm.moduls.tag.service.impl;

import com.alibaba.fastjson.JSON;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.sysdict.service.SysDictService;
import com.ctr.crm.moduls.syslog.models.SysLog;
import com.ctr.crm.moduls.syslog.service.SysLogService;
import com.ctr.crm.moduls.tag.dao.TagDao;
import com.ctr.crm.moduls.tag.models.Tag;
import com.ctr.crm.moduls.tag.service.TagService;

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
@Service("tagService")
public class TagServiceImpl implements TagService {

    @Autowired
    private TagDao tagDao;
    @Autowired
    private BaseDao baseDao;
    @Autowired
    @Lazy
    private SysLogService sysLogService;
    @Autowired
    @Lazy
    private SysDictService sysDictService;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, Tag tag) {
        pageMode.setSqlFrom("select * from Tag");
        pageMode.setSqlWhere("deleted=0");
        if (StringUtils.isNotBlank(tag.getTagName())) {
            pageMode.setSqlWhere("tagName like '%" + pageMode.noSqlInjection(tag.getTagName()) + "%'");
        }
        if (StringUtils.isNotBlank(tag.getGroupId())) {
            pageMode.setSqlWhere("groupId = '" + pageMode.noSqlInjection(tag.getGroupId()) + "'");
        }
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        pageMode.setOrderBy("order by createTime desc");
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public Tag get(String id) {
        return tagDao.get(id);
    }

    @Override
    public List<Tag> findList(Tag tag) {
        return tagDao.findList(tag);
    }

    @Override
    public List<Tag> findAllList() {
        return tagDao.findAllList();
    }

    @Override
    public String insert(Tag tag) {
        if (StringUtils.isBlank(tag.getId())) {
        	tag.setId(CommonUtils.getUUID());
        }
        if (tag.getCreateTime() == null) {
        	tag.setCreateTime(new Date());
        }
        tagDao.insert(tag);
        sysLogService.insert(new SysLog(tag.getId(), null, null, null, JSON.toJSONString(tag), sysDictService.findValue("log_type", "客户标签设置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "新增")));
        return tag.getId();
    }

    @Override
    public int insertBatch(List<Tag> tags) {
        return tagDao.insertBatch(tags);
    }

    @Override
    public int update(Tag tag) {
        sysLogService.insert(new SysLog(tag.getId(), null, null, null, "修改前:" + JSON.toJSONString(get(tag.getId())) + "修改后:"+JSON.toJSONString(tag), sysDictService.findValue("log_type", "客户标签设置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "修改")));
        return tagDao.update(tag);
    }

    @Override
    public boolean delete(String id) {
        sysLogService.insert(new SysLog(id, null, null, null,  JSON.toJSONString(get(id)) , sysDictService.findValue("log_type", "客户标签设置"), CurrentWorkerLocalCache.getCurrentWorker().getWorkerId(), sysDictService.findValue("log_action", "删除")));
        return tagDao.delete(id);
    }

}
