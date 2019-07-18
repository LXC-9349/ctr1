package com.ctr.crm.commons.sms;

import com.ctr.crm.commons.sms.pojo.SmsConfigPojo;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;
import com.ctr.crm.moduls.smschannel.models.SmsChannel;
import com.ctr.crm.moduls.smschannel.service.SmsChannelService;
import com.ctr.crm.moduls.smsconfig.models.SmsConfig;
import com.ctr.crm.moduls.smsconfig.service.SmsConfigService;
import com.ctr.crm.moduls.smsrecord.models.SmsRecord;
import com.ctr.crm.commons.sms.channel.SuperChannel;
import com.ctr.crm.commons.sms.pojo.SmsBodys;
import com.ctr.crm.commons.utils.SpringContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 发送短信
 *
 * @author: DoubleLi
 * @date: 2019/4/28 11:01
 * @description:
 */
@Component
public class SmsService {
    private final static Log log = LogFactory.getLog("sms");
    @Autowired
    private SmsConfigService smsConfigService;
    @Autowired
    private SmsChannelService smsChannelService;

    /**
     * 功能描述: 发送文本短信给客户
     *
     * @param: worker登陆用户
     * @param: memberBaseInfos 发送手机号码,隔开
     * @param: content 发送内容
     * @return:
     * @author: DoubleLi
     * @date: 2019/4/28 14:46
     */
    public String sendSms(Worker worker, List<MemberBaseInfo> memberBaseInfos, String content, String templateid) {
        if (worker == null) {
            return "用户未登录";
        }
        if (StringUtils.isBlank(content)) {
            return "手机号和短信内容不能为空";
        }
        if (memberBaseInfos == null || memberBaseInfos.size() == 0) {
            return "客户信息不能为空";
        }
        List<SmsConfig> configList = smsConfigService.findList(new SmsConfig(1));
        if (configList.size() == 0) {
            return "没有短信配置";
        }
        /**init*/
        List<SmsRecord> ssrs = new ArrayList<>();
        /*短信配置*/
        SmsConfig sc = configList.get(0);
        SmsChannel smsChannel = smsChannelService.get(sc.getChannelId());
        String sign=StringUtils.isBlank(templateid)?smsChannel.getSign():"";
        for (MemberBaseInfo memberBaseInfo : memberBaseInfos) {
            ssrs.add(new SmsRecord(sc.getChannelId(), sc.getId(), 0, sign+content, memberBaseInfo.getMobile(), memberBaseInfo.getMemberId(), null, worker.getWorkerId()));
        }
        SuperChannel channel = (SuperChannel) SpringContextUtils.getBean(smsChannel.getServiceName());
        SmsBodys sb = channel.sendSms(new SmsConfigPojo(smsChannel.getSendUrl(), smsChannel.getCallbackUrl(), sc.getName(), sc.getPwd()), ssrs, templateid);
        if (sb.getFailList().size() > 0) {
            log.error(sb.getFailList());
        }
        return sb.getRespCode().equals(SmsBodys.Status.SUCCESS) ? "" : "发送失败";
    }

}
