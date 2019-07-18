package com.ctr.crm.moduls.smschannel.service.impl;

import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.smschannel.dao.SmsChannelDao;
import com.ctr.crm.moduls.smschannel.models.SmsChannel;
import com.ctr.crm.moduls.smschannel.service.SmsChannelService;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.CommonUtils;
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
@Service("smsChannelService")
public class SmsChannelServiceImpl implements SmsChannelService {

    @Autowired
    private SmsChannelDao smsChannelDao;
    @Autowired
    private BaseDao baseDao;
    private RedisProxy redisClient = RedisProxy.getInstance();

    private static final String SMS_CHANNEL_KEY = "sms_channel";

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, SmsChannel smsChannel) {
        pageMode.setSqlFrom("select * from SmsChannel");
        pageMode.setSqlWhere("deleted=0");
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public SmsChannel get(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return smsChannelDao.get(id);
    }

    @Override
    public List<SmsChannel> findList(SmsChannel smsChannel) {
        return smsChannelDao.findList(smsChannel);
    }

    @Override
    public List<SmsChannel> findAllList(SmsChannel smsChannel) {
        List<SmsChannel> smsChannels;
        Object o = redisClient.get(SMS_CHANNEL_KEY);
        if (o == null) {
            smsChannels = smsChannelDao.findAllList(smsChannel);
            redisClient.set(SMS_CHANNEL_KEY, smsChannels);
        } else {
            smsChannels = (List<SmsChannel>) o;
        }
        return smsChannels;
    }

    @Override
    public String insert(SmsChannel smsChannel) {
        if (StringUtils.isBlank(smsChannel.getId())) {
            smsChannel.setId(CommonUtils.getUUID());
        }
        if (smsChannel.getCreateTime() == null) {
            smsChannel.setCreateTime(new Date());
        }
        smsChannelDao.insert(smsChannel);
        redisClient.delete(SMS_CHANNEL_KEY);
        return smsChannel.getId();
    }

    @Override
    public int insertBatch(List<SmsChannel> smsChannels) {
        redisClient.delete(SMS_CHANNEL_KEY);
        return smsChannelDao.insertBatch(smsChannels);
    }

    @Override
    public int update(SmsChannel smsChannel) {
        redisClient.delete(SMS_CHANNEL_KEY);
        return smsChannelDao.update(smsChannel);
    }

    @Override
    public boolean delete(String id) {
        if (StringUtils.isBlank(id)) {
            return false;
        }
        redisClient.delete(SMS_CHANNEL_KEY);
        return smsChannelDao.delete(id);
    }

}
