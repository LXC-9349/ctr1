package com.ctr.crm.moduls.smsrecord.service.impl;

import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.sales.service.SaleCaseService;
import com.ctr.crm.moduls.smsrecord.dao.SmsRecordDao;
import com.ctr.crm.moduls.smsrecord.models.SmsRecord;
import com.ctr.crm.moduls.smsrecord.service.SmsRecordService;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import com.yunhus.redisclient.RedisProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-04-28
 */
@Service("smsRecordService")
public class SmsRecordServiceImpl implements SmsRecordService {

    @Autowired
    private SmsRecordDao smsRecordDao;
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private SaleCaseService saleCaseService;
    private RedisProxy redisClient = RedisProxy.getInstance();
    private static final String SMS_UNDONE_KEY = "sms_undone";

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, SmsRecord smsSendRecord) {
        pageMode.setSqlFrom("select s.*,sc.name from SmsSendRecord s left join SmsChannel sc on sc.id=s.channelId");
        pageMode.setSqlWhere("deleted=0");
        if (StringUtils.isNotBlank(smsSendRecord.getContent())) {
            pageMode.setSqlWhere("s.content like '%" + pageMode.noSqlInjection(smsSendRecord.getContent()) + "%'");
        }
        if (StringUtils.isNotBlank(smsSendRecord.getChannelId())) {
            pageMode.setSqlWhere("s.channelId = '" + pageMode.noSqlInjection(smsSendRecord.getChannelId()) + "'");
        }
        if (smsSendRecord.getType() != null) {
            pageMode.setSqlWhere("s.type = '" + smsSendRecord.getType() + "'");
        }
        if (StringUtils.isNotBlank(smsSendRecord.getMobile())) {
            pageMode.setSqlWhere("s.mobile like '%" + pageMode.noSqlInjection(smsSendRecord.getMobile()) + "%'");
        }
        if (smsSendRecord.getMemberId() != null) {
            pageMode.setSqlWhere("s.memberId = '" + smsSendRecord.getMemberId() + "'");
        }
        if (smsSendRecord.getWorkerId() != null) {
            pageMode.setSqlWhere("s.workerId = '" + smsSendRecord.getWorkerId() + "'");
        }
        if (smsSendRecord.getSendWorkerId() != null) {
            pageMode.setSqlWhere("s.sendWorkerId = '" + smsSendRecord.getSendWorkerId() + "'");
        }
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        pageMode.setOrderBy("order by readStatus=0,crateTime desc");
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public SmsRecord get(Integer id) {
        if (id == null) {
            return null;
        }
        return smsRecordDao.get(id);
    }

    @Override
    public List<SmsRecord> findList(SmsRecord smsSendRecord) {
        return smsRecordDao.findList(smsSendRecord);
    }

    @Override
    public List<SmsRecord> findAllList(SmsRecord smsSendRecord) {
        return smsRecordDao.findAllList(smsSendRecord);
    }

    @Override
    public Integer insert(SmsRecord smsRecord) {
        if (smsRecord.getId() == null) {
            smsRecord.setId(smsRecordDao.getID());
        }
        if (smsRecord.getCreateTime() == null) {
            smsRecord.setCreateTime(new Date());
        }
        if (smsRecord.getMemberId() != null) {
            saleCaseService.updateLastSmsTime(smsRecord.getMemberId(), smsRecord.getSendTime());
        }
        redisClient.delete(SMS_UNDONE_KEY);
        smsRecordDao.insert(smsRecord);
        return smsRecord.getId();
    }

    @Override
    public int insertBatch(List<SmsRecord> smsSendRecords) {
        return smsRecordDao.insertBatch(smsSendRecords);
    }

    @Override
    public int update(SmsRecord smsSendRecord) {
        return smsRecordDao.update(smsSendRecord);
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) {
            return false;
        }
        return smsRecordDao.delete(id);
    }

    @Override
    public SmsRecord findSmsRecordBySmsId(String smsId) {
        return smsRecordDao.findSmsRecordBySmsId(smsId);
    }

    @Override
    public boolean isUndone() {
        Object o = redisClient.get(SMS_UNDONE_KEY);
        Long total;
        if (o == null) {
            total = baseDao.selectLong("select count(1) from SmsRecord where status=2");
            redisClient.set(SMS_UNDONE_KEY, total);
        } else {
            total = (Long) o;
        }
        if (total != null && total > 0) {
            return true;
        }
        return false;
    }
}
