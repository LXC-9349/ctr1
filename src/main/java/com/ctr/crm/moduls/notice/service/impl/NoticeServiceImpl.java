package com.ctr.crm.moduls.notice.service.impl;

import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.commons.jdbc.YunhuJdbcOperations;
import com.ctr.crm.moduls.notice.dao.NoticeDao;
import com.ctr.crm.moduls.notice.models.Notice;
import com.ctr.crm.moduls.notice.service.NoticeService;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import com.yunhus.redisclient.RedisProxy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-05-10
 */
@Service("noticeService")
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeDao noticeDao;
    @Autowired
    private BaseDao baseDao;
    @Resource
    private YunhuJdbcOperations crmJdbc;
    private RedisProxy redisClient = RedisProxy.getInstance();

    private static final String NOTICE_KEY = "notice_key_";

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, Notice notice, String createTimeA, String createTimeB) {
        pageMode.setSqlFrom("select n.id,n.workerId,n.type,n.title,n.content,n.createTime,n.`status`,n.readTime,IFNULL(n.workerName,w.workerName) workerName from Notice n left join Worker w on n.workerId=w.workerId");
        pageMode.setSqlWhere("deleted=0");
        if (StringUtils.isNotBlank(createTimeA)) {
            pageMode.setSqlWhere("n.createTime >= '" + pageMode.noSqlInjection(createTimeA) + "'");
        }
        if (StringUtils.isNotBlank(createTimeB)) {
            pageMode.setSqlWhere("n.createTime <= '" + pageMode.noSqlInjection(createTimeB) + "'");
        }
        if (StringUtils.isNotBlank(notice.getWorkerName())) {
            pageMode.setSqlWhere("w.workerName like '%" + pageMode.noSqlInjection(notice.getWorkerName()) + "%'");
        }
        if (notice.getWorkerId() != null) {
            pageMode.setSqlWhere("n.workerId = '" + notice.getWorkerId() + "'");
        }
        if (notice.getStatus() != null) {
            pageMode.setSqlWhere("n.status = '" + notice.getStatus() + "'");
        }
        if (notice.getType() != null) {
            pageMode.setSqlWhere("n.type = '" + notice.getType() + "'");
        }
        if (StringUtils.isNotBlank(notice.getTitle())) {
            pageMode.setSqlWhere("n.title like '%" + pageMode.noSqlInjection(notice.getTitle()) + "%'");
        }
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        pageMode.setOrderBy("order by createTime desc");
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public Notice get(Integer id) {
        if (id == null) {
            return null;
        }
        return noticeDao.get(id);
    }

    @Override
    public List<Notice> findList(Notice notice) {
        return noticeDao.findList(notice);
    }

    @Override
    public List<Notice> findAllList(Notice notice) {
        return noticeDao.findAllList(notice);
    }

    @Override
    public Boolean insert(Notice notice) {
        if (notice.getWorkerId() != null) {
            String key = NOTICE_KEY + notice.getWorkerId();
            if (redisClient.checkKeyExisted(key)) {
                redisClient.delete(key);
            }
        }
        if (notice.getCreateTime() == null) {
            notice.setCreateTime(new Date());
        }
        return noticeDao.insert(notice);
    }

    @Override
    public int insertBatch(List<Notice> notices) {
        notices.forEach(notice -> {
            if (notice.getWorkerId() != null) {
                String key = NOTICE_KEY + notice.getWorkerId();
                if (redisClient.checkKeyExisted(key)) {
                    redisClient.delete(key);
                }
            }
        });
        return noticeDao.insertBatch(notices);
    }

    @Override
    public int update(Notice notice) {
        if (notice.getStatus() != null && notice.getStatus() == 2) {
            notice.setReadTime(new Date());
        }
        Notice noticeold= get(notice.getId());
        if (noticeold.getWorkerId() != null) {
            String key = NOTICE_KEY + noticeold.getWorkerId();
            if (redisClient.checkKeyExisted(key)) {
                redisClient.delete(key);
            }
        }
        return noticeDao.update(notice);
    }

    @Override
    public Boolean delete(Integer id) {
        if (id != null) {
            return false;
        }
        Notice notice = get(id);
        if (notice.getWorkerId() != null) {
            String key = NOTICE_KEY + notice.getWorkerId();
            if (redisClient.checkKeyExisted(key)) {
                redisClient.delete(key);
            }
        }
        return noticeDao.delete(id);
    }

    @Override
    public Map<String, Object> count(Worker currentWorker) {
        String key = NOTICE_KEY + currentWorker.getWorkerId();
        if (redisClient.checkKeyExisted(key)) {
            return (Map<String, Object>) redisClient.get(key);
        } else {
            Map<String, Object> map = crmJdbc.queryForMap("select count(case status when 1 then 1 else null end) `unread`,count(case status when 2 then 1 else null end) `read`,count(1) `all` from  Notice where workerId =?", currentWorker.getWorkerId());
            redisClient.set(key, map);
            return map;
        }
    }

    @Override
    public String read(Integer type, Worker worker) {
        if (worker == null) {
            return "登陆用户不能为空";
        }
        crmJdbc.update("update Notice set status=2 where type=? and workerId=?", type, worker.getWorkerId());
        String key = NOTICE_KEY + worker.getWorkerId();
        if (redisClient.checkKeyExisted(key)) {
            redisClient.delete(key);
        }
        return null;
    }
}
