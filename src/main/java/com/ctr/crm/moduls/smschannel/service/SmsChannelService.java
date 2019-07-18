package com.ctr.crm.moduls.smschannel.service;

import com.ctr.crm.moduls.smschannel.models.SmsChannel;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;

/**
 * @author DoubleLi
 * @date  2019-04-28
 */
public interface  SmsChannelService {

    void searchPage(ResponseData responseData, PageMode pageMode, SmsChannel smsChannel);

    SmsChannel get(String id);

    List<SmsChannel> findList(SmsChannel smsChannel);

    List<SmsChannel> findAllList(SmsChannel smsChannel);

    String insert(SmsChannel smsChannel);

    int insertBatch(List<SmsChannel> smsChannels);

    int update(SmsChannel smsChannel);

    boolean delete(String id);

}
