package com.ctr.crm.commons.sms.channel.miaodi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ctr.crm.commons.sms.channel.SuperChannel;
import com.ctr.crm.commons.sms.pojo.SmsBody;
import com.ctr.crm.commons.sms.pojo.SmsBodys;
import com.ctr.crm.commons.sms.pojo.SmsConfigPojo;
import com.ctr.crm.moduls.sales.models.SaleCase;
import com.ctr.crm.moduls.sales.models.SaleCaseProc;
import com.ctr.crm.moduls.sales.service.SaleCaseProcService;
import com.ctr.crm.moduls.sales.service.SaleCaseService;
import com.ctr.crm.moduls.smsrecord.models.SmsRecord;
import com.ctr.crm.moduls.smsrecord.service.SmsRecordService;
import com.ctr.crm.commons.sms.channel.miaodi.model.ReceiveStatus;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.HttpUtils;
import com.ctr.crm.commons.utils.Id;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 功能描述: 秒嘀通道
 *
 * @author: DoubleLi
 * @date: 2019/4/28 14:41
 */
@Component("miaodiChannel")
public class MiaodiChannel extends SuperChannel {

    private final static Log log = LogFactory.getLog("sms");

    @Autowired
    private SmsRecordService smsRecordService;
    @Autowired
    private SaleCaseProcService saleCaseProcService;
    @Autowired
    private SaleCaseService saleCaseService;

    @Override
    public SmsBodys sendSms(SmsConfigPojo smsConfigPojo, List<SmsRecord> sendRecords, String templateid) {
        SmsBodys result = new SmsBodys();
        List<SmsBody> successList = new ArrayList<>();
        List<SmsBody> failList = new ArrayList<>();
        for (SmsRecord sendRecord : sendRecords) {
            String mobile = sendRecord.getMobile();
            long timestamp = System.currentTimeMillis();
            String sig = DigestUtils.md5Hex(smsConfigPojo.getName() + smsConfigPojo.getPwd() + timestamp);
            String body = null;
            if (StringUtils.isBlank(templateid)) {
                body = "accountSid=" + smsConfigPojo.getName() + "&to=" + mobile + "&smsContent=" + sendRecord.getContent()
                        + "&timestamp=" + timestamp + "&sig=" + sig + "&respDataType=" + "JSON";
            } else {
                body = "accountSid=" + smsConfigPojo.getName() + "&to=" + mobile + "&param=" + sendRecord.getContent()
                        + "&templateid=" + templateid + "&timestamp=" + timestamp + "&sig=" + sig + "&respDataType=" + "JSON";
            }
            String res = HttpUtils.getInstance().post(smsConfigPojo.getSendurl(), body);
            log.info("send miaodi sms. sendurl:" + smsConfigPojo.getSendurl() + ",body:" + body + ",result:" + res);
            if (res == null) {
                SmsBody smsBody = new SmsBody();
                smsBody.setMobile(mobile);
                smsBody.setSuccess("false");
                smsBody.setFailReason("postJson exception");
                failList.add(smsBody);
                // 设置短信发送状态为失败
                sendRecord.setStatus(0);
                sendRecord.setFailReason("(请求返回空)");
                smsRecordService.insert(sendRecord);
                smsBody.setUid(sendRecord.getId().toString());
                continue;
            }
            JSONObject json = JSONObject.parseObject(res);
            String respCode = json.getString("respCode");
            if (StringUtils.equals(respCode, "0000")) {// 0000表示发送成功
                // 设置短信发送状态为发送中
                sendRecord.setStatus(2);
                sendRecord.setSmsId(json.getString("smsId"));
                // 新增加一条发送记录
                smsRecordService.insert(sendRecord);
                SmsBody smsBody = new SmsBody();
                smsBody.setMobile(mobile);
                smsBody.setUid(sendRecord.getId().toString());
                smsBody.setSuccess("true");
                smsBody.setFailReason("");
                successList.add(smsBody);
            } else {
                // 设置短信发送状态为失败
                sendRecord.setStatus(0);
                sendRecord.setFailReason(res);
                // 新增加一条发送记录
                smsRecordService.insert(sendRecord);
                SmsBody smsBody = new SmsBody();
                smsBody.setMobile(mobile);
                smsBody.setSuccess("false");
                smsBody.setUid(sendRecord.getId().toString());
                smsBody.setFailReason("(" + sendRecord.getFailReason() + ")");
                failList.add(smsBody);
            }
        }
        result.setRespCode(SmsBodys.Status.SUCCESS);
        result.setSuccessList(successList);
        result.setFailList(failList);
        return result;
    }

    @Override
    public String handlerSms(String jsonStr) {
        log.info("handler miaodi sms. body:" + jsonStr);
        Map<String, String> resbody = new HashMap<>(1);
        //秒滴约定需响应的字段信息
        resbody.put("respCode", "00000");
        if (jsonStr == null) {
            return JSON.toJSONString(resbody);
        }
        JSONObject receiveBody = null;
        try {
            receiveBody = JSON.parseObject(jsonStr);
        } catch (Exception e) {
            log.info("handler miaodi sms. exception:" + e.getMessage());
        }
        if (receiveBody == null) {
            return JSON.toJSONString(resbody);
        }
        //通过手机号查询最近发送记录
        SmsRecord sendRecord = new SmsRecord();
        sendRecord.setContent(receiveBody.getString("content"));
        sendRecord.setMobile(receiveBody.getString("phone"));
        sendRecord.setSendTime(CommonUtils.parseDateFormStr(receiveBody.getString("MOTime"), "yyyy-MM-dd HH:mm:ss"));
        // 设置上行短信类型
        sendRecord.setType(1);
        sendRecord.setStatus(1);
        sendRecord.setSmsId(receiveBody.getString("MOPort"));
        smsRecordService.insert(sendRecord);
        //添加小计
        SaleCase saleCase = saleCaseService.getByMobile(sendRecord.getMobile());
        if (saleCase != null) {
            SaleCaseProc caseProc = new SaleCaseProc();
            Long procId = Id.generateSaleProcID();
            caseProc.setProcId(procId);
            caseProc.setMemberId(saleCase.getMemberId());
            caseProc.setWorkerId(saleCase.getWorkerId());
            caseProc.setWorkerName(saleCase.getWorkerName());
            caseProc.setProcTime(new Date());
            caseProc.setProcItem(sendRecord.getContent());
            caseProc.setNotesType(3);//短信上行小记
            saleCaseProcService.save(caseProc, null);
        }
        resbody.put("respCode", "00000");
        return JSON.toJSONString(resbody);
    }

    @Override
    public String handlerStatus(String jsonStr) {
        log.info("handler miaodi status. body:" + jsonStr);
        Map<String, String> resbody = new HashMap<>();
        resbody.put("respCode", "00000");
        if (jsonStr == null) {
            return JSON.toJSONString(resbody);
        }
        List<ReceiveStatus> statusBody = null;
        try {
            JSONObject json = JSONObject.parseObject(jsonStr);
            String smsResult = json.getString("smsResult");
            statusBody = JSON.parseArray(smsResult, ReceiveStatus.class);
        } catch (Exception e) {
            log.info("handler miaodi status. exception:" + e.getMessage());
        }
        if (statusBody == null) {
            return JSON.toJSONString(resbody);
        }
        for (ReceiveStatus receiveStatus : statusBody) {
            SmsRecord sendRecord = smsRecordService.findSmsRecordBySmsId(receiveStatus.getSmsId());
            if (sendRecord != null) {
                sendRecord.setSendTime(CommonUtils.parseDateFormStr(receiveStatus.getReceiveTime(), "yyyy-MM-dd HH:mm:ss"));
                if (StringUtils.equalsIgnoreCase(receiveStatus.getStatus(), "0")) {
                    sendRecord.setStatus(1);
                } else {
                    sendRecord.setStatus(0);
                    sendRecord.setFailReason(receiveStatus.getRespMessage());
                }
                smsRecordService.update(sendRecord);
            }
        }
        resbody.put("respCode", "00000");
        return JSON.toJSONString(resbody);
    }

    @Override
    public String status(SmsConfigPojo smsConfigPojo) {
        return null;
    }

    @Override
    public String up(SmsConfigPojo smsConfigPojo) {
        return null;
    }

    @Override
    public String balance(SmsConfigPojo smsConfigPojo) {
        return null;
    }
}