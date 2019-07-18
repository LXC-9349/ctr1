package com.ctr.crm.controlers.sms;

import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.moduls.smsrecord.models.SmsRecord;
import com.ctr.crm.moduls.smsrecord.service.SmsRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ctr.crm.interceptors.CurrentWorkerAware;

/**
 * @author DoubleLi
 * @date 2019-04-28
 */
@Api(tags = "短信记录")
@RestController
@RequestMapping(value = "/api/sms")
@Secure(1)
public class SmsRecordController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(SmsRecordController.class);

    @Autowired
    private SmsRecordService smsRecordService;

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "0发送1接收", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "mobile", value = "手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "memberId", value = "收信客户Id", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "workerId", value = "收信系统用户Id", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "sendWorkerId", value = "系统发送用户Id", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "content", value = "内容", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "channelId", value = "渠道Id", dataType = "String", paramType = "query")
    })
    @GetMapping("search")
    public ResponseData search(HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            SmsRecord smsSendRecord = (SmsRecord) RequestObjectUtil.mapToBean(request, SmsRecord.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (smsSendRecord == null) {
                smsSendRecord = new SmsRecord();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            smsRecordService.searchPage(responseData, pageMode, smsSendRecord);
            responseData.setStatus(ResponseStatus.success);
        } catch (Exception e) {
            log.error("查询", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation("详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "Integer", paramType = "query")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段"), @ApiResponse(code = 100008, message = "非法请求")})
    @GetMapping(value = "info")
    public ResponseData info(@RequestParam(required = false) Integer id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.setStatus(ResponseStatus.null_param);
                return responseData;
            }
            SmsRecord smsSendRecord = smsRecordService.get(id);
            Map<String, Object> resMap = new HashMap<>(1);
            resMap.put("smsSendRecord", smsSendRecord);
            responseData.responseData(ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

  /*  @ApiOperation(value = "添加")
    @PostMapping(value = "insert")
    public ResponseData insert(@ModelAttribute(value = "SmsSendRecord") @ApiParam(value = "Created SmsSendRecord object", required = true) SmsSendRecord smsSendRecord) {
        ResponseData responseData = new ResponseData();
        if (StringUtils.isBlank(smsRecordService.insert(smsSendRecord))) {
            responseData.responseData(ResponseStatus.success, null, null);
        } else {
            responseData.responseData(ResponseStatus.failed, null, null);
        }
        return responseData;
    }*/

    @ApiOperation(value = "修改")
    @PostMapping(value = "update")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "Integer", paramType = "query")})
    public ResponseData update(@ModelAttribute(value = "SmsSendRecord") @ApiParam(value = "Created SmsSendRecord object", required = true) SmsRecord smsSendRecord) {
        ResponseData responseData = new ResponseData();
        smsSendRecord.setDeleted(null);
        if (smsSendRecord.getId()==null) {
            responseData.responseData(ResponseStatus.null_param, null, null);
        } else if (smsRecordService.update(smsSendRecord) > 0) {
            responseData.responseData(ResponseStatus.success, null, null);
        } else {
            responseData.responseData(ResponseStatus.failed, null, null);
        }
        return responseData;
    }

    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", dataType = "Integer")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段"), @ApiResponse(code = 100008, message = "非法请求")})
    @DeleteMapping(value = "delete")
    public ResponseData delete(@RequestParam(required = false) Integer id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.responseData(ResponseStatus.null_param, null, null);
            } else {
                smsRecordService.delete(id);
                responseData.responseData(ResponseStatus.success, null, null);
            }
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

}
