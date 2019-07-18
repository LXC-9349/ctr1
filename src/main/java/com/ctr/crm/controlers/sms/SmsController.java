package com.ctr.crm.controlers.sms;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.sms.SmsService;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.Encrypt;
import com.ctr.crm.commons.utils.Id;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;
import com.ctr.crm.moduls.member.service.MemberService;
import com.ctr.crm.moduls.sales.models.SaleCaseProc;
import com.ctr.crm.moduls.sales.service.SaleCaseProcService;
import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 功能描述: 短信发送
 *
 * @author: DoubleLi
 * @date: 2019/4/28 15:59
 */
@Api(tags = "短信发送")
@RequestMapping(value = "/api/sms")
@RestController
@Secure(1)
@Menu(menuName = "短信管理", menuUrl = "sms")
public class SmsController implements CurrentWorkerAware {

    private final static Log log = LogFactory.getLog("sms");

    @Autowired
    private SmsService smsService;
    @Resource
    private MemberService memberService;
    @Autowired
    private SaleCaseProcService saleCaseProcService;

    @ApiOperation("发送单条短信")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberId", value = "memberId", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "content", value = "内容", required = true, dataType = "String"),
            @ApiImplicitParam(name = "phone", value = "选择的手机号码", dataType = "String"),
            @ApiImplicitParam(name = "templateid", value = "模板Id", dataType = "String")
    })
    @PostMapping(value = "send_sms")
    @Secure(value = 1, actionName = "发送短信", actionUri = "/api/sms/send_sms", actionNote = "短信管理")
    public ResponseData send_sms(Long memberId, String phone, @RequestParam(required = false) String content, @RequestParam(required = false) String templateid, HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            if (memberId == null || StringUtils.isBlank(content)) {
                return new ResponseData(ResponseStatus.null_param);
            }
            MemberBaseInfo memberBaseInfo = memberService.get(MemberBaseInfo.class, memberId);
            if (memberBaseInfo == null) {
                return new ResponseData(ResponseStatus.null_param, "客户不存在");
            }
            List<MemberBaseInfo> list = new ArrayList<>();
            if (StringUtils.isNotBlank(phone)) {
                memberBaseInfo.setMobile(Encrypt.decrypt(phone));
            }
            list.add(memberBaseInfo);
            Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
            String msg = smsService.sendSms(currentWorker, list, content, templateid);
            if (StringUtils.isBlank(msg)) {
                responseData.responseData(ResponseStatus.success, null, null);
                SaleCaseProc caseProc = new SaleCaseProc();
                Long procId = Id.generateSaleProcID();
                caseProc.setProcId(procId);
                caseProc.setMemberId(memberId);
                caseProc.setWorkerId(currentWorker.getWorkerId());
                caseProc.setWorkerName(currentWorker.getWorkerName());
                caseProc.setProcTime(new Date());
                caseProc.setProcIp(CommonUtils.getRealRemoteIp(request));
                caseProc.setProcItem(content);
                caseProc.setNotesType(2);//短信下行小记
                saleCaseProcService.save(caseProc, null);
            } else {
                responseData.responseData(ResponseStatus.failed, msg, null);
            }

        } catch (Exception e) {
            log.error("发送短信", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation("群发短信")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberIds", value = "memberId ,隔开", required = true, dataType = "String"),
            @ApiImplicitParam(name = "content", value = "内容", required = true, dataType = "String"),
    })
    @PostMapping(value = "send_mass_sms")
    @Secure(value = 1, actionName = "群发短信", actionUri = "/api/sms/send_mass_sms", actionNote = "短信管理")
    public ResponseData send_mass_sms(String memberIds, String content,@RequestParam(required = false) String templateid, HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            if (StringUtils.isAnyBlank(content, memberIds)) {
                return new ResponseData(ResponseStatus.null_param);
            }
            List<MemberBaseInfo> list = memberService.searchByMemberIds(memberIds);
            if (list.isEmpty()) {
                return new ResponseData(ResponseStatus.null_param, "客户不存在");
            }
            Worker currentWorker=CurrentWorkerLocalCache.getCurrentWorker();
            String msg = smsService.sendSms(currentWorker, list, content, templateid);
            if (StringUtils.isBlank(msg)) {
                responseData.responseData(ResponseStatus.success, null, null);
                for (MemberBaseInfo memberBaseInfo : list) {
                    SaleCaseProc caseProc = new SaleCaseProc();
                    Long procId = Id.generateSaleProcID();
                    caseProc.setProcId(procId);
                    caseProc.setMemberId(memberBaseInfo.getMemberId());
                    caseProc.setWorkerId(currentWorker.getWorkerId());
                    caseProc.setWorkerName(currentWorker.getWorkerName());
                    caseProc.setProcTime(new Date());
                    caseProc.setProcIp(CommonUtils.getRealRemoteIp(request));
                    caseProc.setProcItem(content);
                    caseProc.setNotesType(2);//短信下行小记
                    saleCaseProcService.save(caseProc, null);
                }
            } else {
                responseData.responseData(ResponseStatus.failed, msg, null);
            }

        } catch (Exception e) {
            log.error("群发短信", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }
}
