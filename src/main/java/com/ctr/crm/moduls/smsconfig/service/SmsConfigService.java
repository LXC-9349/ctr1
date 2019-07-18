package com.ctr.crm.moduls.smsconfig.service;

import com.ctr.crm.moduls.smsconfig.models.SmsConfig;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;

/**
 * @author DoubleLi
 * @date 2019-04-28
 */
public interface SmsConfigService {

    void searchPage(ResponseData responseData, PageMode pageMode, SmsConfig smsConfig);

    SmsConfig get(String id);

    List<SmsConfig> findList(SmsConfig smsConfig);

    List<SmsConfig> findAllList(SmsConfig smsConfig);

    String insert(SmsConfig smsConfig);

    int insertBatch(List<SmsConfig> smsConfigs);

    int update(SmsConfig smsConfig);

    boolean delete(String id);

    /**
     * 功能描述:
     * 获取启用的配置
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/28 17:50
     */
    SmsConfig getEnable();
}
