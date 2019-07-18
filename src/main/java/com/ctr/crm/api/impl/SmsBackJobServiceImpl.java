package com.ctr.crm.api.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.ctr.crm.api.SmsBackJobService;
import com.ctr.crm.commons.sms.channel.SuperChannel;
import com.ctr.crm.commons.sms.pojo.SmsConfigPojo;
import com.ctr.crm.commons.utils.SpringContextUtils;
import com.ctr.crm.moduls.smschannel.models.SmsChannel;
import com.ctr.crm.moduls.smschannel.service.SmsChannelService;
import com.ctr.crm.moduls.smsconfig.models.SmsConfig;
import com.ctr.crm.moduls.smsconfig.service.SmsConfigService;
import com.ctr.crm.moduls.smsrecord.service.SmsRecordService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * /**
 *
 * @author: DoubleLi
 * @date: 2019/5/30 14:26
 * @description:
 */
@Component
@Service
@EnableAsync
public class SmsBackJobServiceImpl implements SmsBackJobService {

    private final static Log log = LogFactory.getLog("sms");

    @Autowired
    private SmsConfigService smsConfigService;
    @Autowired
    private SmsChannelService smsChannelService;
    @Autowired
    private SmsRecordService smsRecordService;

    /**
     * 功能描述: 短信主动拉取
     * 每2分钟拉取
     * 采用异步调用防止执行方法超时
     *
     * @param:
     * @return:
     * @author: DoubleLi
     */
    @Async
    @Override
    public void backSms() {
        try {
            List<SmsChannel> smsChannelList = smsChannelService.findAllList(new SmsChannel());
            if (smsChannelList.size() == 0) {
                return;
            }
            List<SmsConfig> smsConfigs = smsConfigService.findAllList(new SmsConfig());
            if (smsConfigs.size() == 0) {
                return;
            }
            /** 查询状态是否有发送中的短信*/
            boolean isUndone = smsRecordService.isUndone();
            for (SmsChannel smsChannel : smsChannelList) {
                /** 主动拉取*/
                if (smsChannel.getBackType() == 2) {
                    SuperChannel channel = null;
                    try {
                        channel = (SuperChannel) SpringContextUtils.getBean(smsChannel.getServiceName());
                    } catch (Exception e) {
                        log.error("短信未找到渠道" + smsChannel.getServiceName());
                    }
                    for (SmsConfig smsConfig : smsConfigs) {
                        SmsConfigPojo smsConfigPojo = new SmsConfigPojo(smsChannel.getSendUrl(), smsChannel.getCallbackUrl(), smsConfig.getName(), smsConfig.getPwd());
                        if (isUndone){
                            channel.status(smsConfigPojo);
                            channel.balance(smsConfigPojo);
                        }
                        channel.up(smsConfigPojo);

                    }
                }
            }
        } catch (Exception e) {
            log.error("短信主动拉取", e);
        }
    }
}
