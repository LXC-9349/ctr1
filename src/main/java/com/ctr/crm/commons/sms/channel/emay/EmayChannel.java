package com.ctr.crm.commons.sms.channel.emay;

import com.alibaba.fastjson.JSON;
import com.ctr.crm.commons.sms.channel.SuperChannel;
import com.ctr.crm.commons.sms.channel.emay.model.http.v1.dto.response.*;
import com.ctr.crm.commons.sms.channel.emay.util.JsonHelper;
import com.ctr.crm.commons.sms.pojo.SmsBody;
import com.ctr.crm.commons.sms.pojo.SmsBodys;
import com.ctr.crm.commons.sms.pojo.SmsConfigPojo;
import com.ctr.crm.moduls.sales.models.SaleCase;
import com.ctr.crm.moduls.sales.models.SaleCaseProc;
import com.ctr.crm.moduls.sales.service.SaleCaseProcService;
import com.ctr.crm.moduls.sales.service.SaleCaseService;
import com.ctr.crm.moduls.smsrecord.models.SmsRecord;
import com.ctr.crm.moduls.smsrecord.service.SmsRecordService;
import com.google.gson.reflect.TypeToken;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.Id;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 功能描述: 亿美软通通道
 *
 * @author: DoubleLi
 * @date: 2019/5/30 14:41
 */
@Component("emayChannel")
public class EmayChannel extends SuperChannel {

    private final static Log log = LogFactory.getLog("sms");
    /**
     * 加密算法
     */
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    /**
     * 编码
     */
    private static final String ENCODE = "UTF-8";
    /**
     * 是否压缩
     */
    private static final boolean IS_G_IZP = true;

    @Autowired
    private SmsRecordService smsRecordService;
    @Autowired
    private SaleCaseProcService saleCaseProcService;
    @Autowired
    private SaleCaseService saleCaseService;

    @Override
    public SmsBodys sendSms(SmsConfigPojo smsConfigPojo, List<SmsRecord> sendRecords, String templateid) {
        SmsBodys smsBodys = new SmsBodys();
        List<SmsBody> successList = new ArrayList<>();
        List<SmsBody> failList = new ArrayList<>();
        for (SmsRecord sendRecord : sendRecords) {
            String mobile = sendRecord.getMobile();
            ResultModel result = EmayUtils.setSingleSms(smsConfigPojo.getName(), smsConfigPojo.getPwd(), smsConfigPojo.getSendurl(), ALGORITHM, sendRecord.getContent(), null, null, mobile, IS_G_IZP, ENCODE);//短信内容请以商务约定的为准，如果已经在通道端绑定了签名，则无需在这里添加签名
            log.info("send emay sms. sendurl:" + smsConfigPojo.getSendurl() + ",mobile:" + mobile + ",content:" + sendRecord.getContent() + ",result:" + result);
            if (result == null) {
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
            if (EmayUtils.SUCCESS.equals(result.getCode())) {
                SmsResponse response = JSON.parseObject(result.getResult(), SmsResponse.class);
                if (response != null) {
                    System.out.println("data : " + response.getMobile() + "," + response.getSmsId() + "," + response.getCustomSmsId());
                }
                // 设置短信发送状态为发送中
                sendRecord.setStatus(2);
                sendRecord.setSmsId(response.getSmsId());
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
                sendRecord.setFailReason(result.getCode() + "-" + result.getResult());
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
        smsBodys.setRespCode(SmsBodys.Status.SUCCESS);
        smsBodys.setSuccessList(successList);
        smsBodys.setFailList(failList);
        return smsBodys;
    }

    @Override
    public String handlerSms(String jsonStr) {
        return null;
    }

    @Override
    public String handlerStatus(String jsonStr) {
        return null;
    }

    @Override
    public String status(SmsConfigPojo smsConfigPojo) {
        ResultModel result = EmayUtils.getReport(smsConfigPojo.getName(), smsConfigPojo.getPwd(), smsConfigPojo.getSendurl(), ALGORITHM, IS_G_IZP, ENCODE);
        if (result != null && EmayUtils.SUCCESS.equals(result.getCode())) {
            ReportResponse[] response = JSON.parseObject(result.getResult(), ReportResponse[].class);
            if (response != null) {
                log.info("emay status json:" + result.getResult());
                for (ReportResponse d : response) {
                    System.out.println("result data : " + d.getExtendedCode() + "," + d.getMobile() + "," + d.getCustomSmsId() + "," + d.getSmsId() + "," + d.getState() + "," + d.getDesc() + ","
                            + d.getSubmitTime() + "," + d.getReceiveTime());
                    SmsRecord sendRecord = smsRecordService.findSmsRecordBySmsId(d.getSmsId());
                    if (sendRecord != null) {
                        sendRecord.setSubmitTime(CommonUtils.parseDateFormStr(d.getSubmitTime(), "yyyy-MM-dd HH:mm:ss"));
                        sendRecord.setSendTime(CommonUtils.parseDateFormStr(d.getReceiveTime(), "yyyy-MM-dd HH:mm:ss"));
                        if (StringUtils.equalsIgnoreCase(d.getState(), "DELIVRD")) {
                            sendRecord.setStatus(1);
                        } else {
                            sendRecord.setStatus(0);
                            sendRecord.setFailReason(d.getState() + d.getDesc());
                        }
                        smsRecordService.update(sendRecord);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String up(SmsConfigPojo smsConfigPojo) {
        ResultModel result = EmayUtils.getMo(smsConfigPojo.getName(), smsConfigPojo.getPwd(), smsConfigPojo.getSendurl(), ALGORITHM, IS_G_IZP, ENCODE);
        if (result != null && EmayUtils.SUCCESS.equals(result.getCode())) {
            MoResponse[] response = JSON.parseObject(result.getResult(), MoResponse[].class);
            if (response != null) {
                log.info("emay up json:" + result.getResult());
                for (MoResponse d : response) {
                    SmsRecord sendRecord = new SmsRecord();
                    sendRecord.setContent(d.getContent());
                    sendRecord.setMobile(d.getMobile());
                    sendRecord.setSendTime(CommonUtils.parseDateFormStr(d.getMoTime(), "yyyy-MM-dd HH:mm:ss"));
                    // 设置上行短信类型
                    sendRecord.setType(1);
                    sendRecord.setStatus(1);
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
                }
            }
        }
        return null;
    }

    @Override
    public String balance(SmsConfigPojo smsConfigPojo) {
        try {
            ResultModel result = EmayUtils.getBalance(smsConfigPojo.getName(), smsConfigPojo.getPwd(), smsConfigPojo.getSendurl(), ALGORITHM, IS_G_IZP, ENCODE);
            if (result != null && EmayUtils.SUCCESS.equals(result.getCode())) {
                ResponseData<BalanceResponse> data = JsonHelper.fromJson(new TypeToken<ResponseData<BalanceResponse>>() {
                }, result.getResult());
                String code = data.getCode();
                if (EmayUtils.SUCCESS.equals(code)) {
                    return data.getData().getBalance()+"";
                }
            }
        } catch (Exception e) {
            log.error(e);
            return null;
        }
        return null;
    }
}