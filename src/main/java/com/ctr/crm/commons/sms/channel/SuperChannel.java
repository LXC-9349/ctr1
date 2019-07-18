package com.ctr.crm.commons.sms.channel;

import com.ctr.crm.commons.sms.pojo.SmsConfigPojo;
import com.ctr.crm.moduls.smsrecord.models.SmsRecord;
import com.ctr.crm.commons.sms.pojo.SmsBodys;

import java.util.List;

/**
 * 功能描述: 短信渠道超类
 *
 * @author: DoubleLi
 * @date: 2019/4/28 11:02
 */
public abstract class SuperChannel {

    /**
     * 发送自定义短信
     *
     * @param smsConfigPojo 短信参数信息
     * @param sendRecords   发送短信记录
     * @return
     */
    public abstract SmsBodys sendSms(SmsConfigPojo smsConfigPojo, List<SmsRecord> sendRecords, String templateid);

    /**
     * 短信上行
     *
     * @param jsonStr 接收的json串
     * @return
     */
    public abstract String handlerSms(String jsonStr);

    /**
     * 短信状态上行
     *
     * @param jsonStr 接收的json串
     * @return
     */
    public abstract String handlerStatus(String jsonStr);

    /**
     * 功能描述:
     * 上行主动拉取
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/30 11:05
     */
    public abstract String up(SmsConfigPojo smsConfigPojo);

    /**
     * 功能描述:
     * 状态主动拉取
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/30 11:07
     */
    public abstract String status(SmsConfigPojo smsConfigPojo);

    /**
     * 功能描述:
     * 余额查询
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/6/4 15:36
     */
    public abstract String balance(SmsConfigPojo smsConfigPojo);
}
