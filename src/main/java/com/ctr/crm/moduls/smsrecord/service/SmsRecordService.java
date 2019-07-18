package com.ctr.crm.moduls.smsrecord.service;

import com.ctr.crm.moduls.smsrecord.models.SmsRecord;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;

/**
 * @author DoubleLi
 * @date  2019-04-28
 */
public interface SmsRecordService {

    void searchPage(ResponseData responseData, PageMode pageMode, SmsRecord smsSendRecord);

    SmsRecord get(Integer id);

    List<SmsRecord> findList(SmsRecord smsSendRecord);

    List<SmsRecord> findAllList(SmsRecord smsSendRecord);

    Integer insert(SmsRecord smsSendRecord);

    int insertBatch(List<SmsRecord> smsSendRecords);

    int update(SmsRecord smsSendRecord);

    boolean delete(Integer id);

    SmsRecord findSmsRecordBySmsId(String smsId);

    /**
     *
     * 功能描述:
     * 是否有发送中的短信
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/31 11:29
     */
    boolean isUndone();
}
