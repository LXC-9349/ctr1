package com.ctr.crm.controlers.ucc;

import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.AESencrp;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.moduls.record.models.CallRecord;
import com.ctr.crm.moduls.record.service.RecordService;
import com.ctr.crm.moduls.system.models.CallAddress;
import com.ctr.crm.moduls.system.service.CallAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 呼叫数据推送接口
 * 
 * @author Administrator
 *
 */
@Api(tags="呼叫中心")
@RestController
@Secure(-1)
@RequestMapping("/api/ucc/")
public class RecordCallBackControler {

	private static final Log log = LogFactory.getLog("call");
	@Resource
	private RecordService recordService;
	@Resource
	private CallAddressService callAddressService;
	
	@ApiOperation(value="通话记录推送接口", notes="通话记录推送接口")
	@ApiResponses({ 
		@ApiResponse(code = 100000, message = "请求成功"),
		@ApiResponse(code = 100001, message = "请求失败"),
		@ApiResponse(code = 100002, message = "请求异常"), 
		@ApiResponse(code = 100003, message = "参数为空"),
		@ApiResponse(code = 100009, message = "参数不完整")
	})
	@RequestMapping(value="push/callrecord", method={RequestMethod.POST, RequestMethod.GET})
	public ResponseData pushCallRecord(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String jsonParams = StringUtils.trim(AESencrp.decrypt(request.getParameter("json_params")));
		String clientIp = CommonUtils.getRealRemoteIp(request);
		ResponseData result = new ResponseData();
		log.info("begin push callrecord. client ip:" + clientIp + ", data:"+jsonParams);
		if (StringUtils.isBlank(jsonParams)) {
			log.info("json_params is empty");
			result.setCode(ResponseStatus.null_param.getStatusCode());
			result.setMsg(ResponseStatus.null_param.getStatusDesc());
			return result;
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = JSON.parseObject(jsonParams);
		} catch (Exception e) {
			log.info("end push callrecord. occur exception. reason:" + e.getMessage());
			result.setCode(ResponseStatus.error.getStatusCode());
			result.setMsg("json转换对象异常");
			return result;
		}
		if (jsonObject == null) {
			log.info("end push callrecord. occur exception.");
			result.setCode(ResponseStatus.error.getStatusCode());
			result.setMsg("json转换对象失败");
			return result;
		}
		CallRecord callRecord = new CallRecord();
		try {
			jsonObject = CommonUtils.transToLowerObject(jsonObject);
			//json对象转换为bean对象
			jsonToObject(jsonObject, callRecord);
		} catch (Exception e) {
			log.info("jsonToObject. occur exception. reason:" + e.getMessage());
		}
		if (callRecord.getCallType() == null || callRecord.getStartTime() == null 
				|| callRecord.getHangupTime() == null) {
			log.info("end push callrecord. params is not full");
			result.setCode(ResponseStatus.imperfect.getStatusCode());
			result.setMsg(ResponseStatus.imperfect.getStatusDesc());
			return result;
		}
		//呼叫系统公司ID和CRM公司ID对应
		CallAddress callAddress = callAddressService.select();
		if(callAddress != null && callAddress.getCompanyId() != null){
			callRecord.setCompanyId(callAddress.getCompanyId());
		}else{
			//没有跟呼叫地址配置匹配上，默认存储在云呼下
			callRecord.setCompanyId("0");
		}
		boolean success = recordService.insertCallRecord(callRecord);
		if (!success) {
			log.info("end push callrecord. faild");
			result.setCode(ResponseStatus.failed.getStatusCode());
			result.setMsg(ResponseStatus.failed.getStatusDesc());
			return result;
		}
		log.info("end push callrecord. success");
		result.setCode(ResponseStatus.success.getStatusCode());
		result.setMsg(ResponseStatus.success.getStatusDesc());
		return result;
	}

	private void jsonToObject(JSONObject jsonObject, CallRecord callRecord) throws Exception{
		if(callRecord == null) callRecord = new CallRecord();
		if(jsonObject == null) return;
		callRecord.setCallId(jsonObject.getInteger("id"));
		callRecord.setMemberId(jsonObject.getLong("memberid"));
		callRecord.setPhone(jsonObject.getString("destnumber"));
		callRecord.setTelephone(jsonObject.getString("disnumber"));
		callRecord.setStartTime(jsonObject.getDate("starttime"));
		callRecord.setAnswerTime(jsonObject.getDate("answertime"));
		callRecord.setHangupTime(jsonObject.getDate("endtime"));
		callRecord.setCallTime(jsonObject.getInteger("billsec"));
		callRecord.setWaitTime(jsonObject.getInteger("duration"));
		callRecord.setRecordFileName(jsonObject.getString("recordfilename"));
		callRecord.setDownFileIp(jsonObject.getString("downloadip"));
		callRecord.setRemark(jsonObject.getString("hangupcause"));
		Integer caseClass = CommonUtils.evalInteger(jsonObject.getString("chengshudu"));
		if(caseClass != null && caseClass > 0)
		callRecord.setCaseClass(caseClass);
		callRecord.setWorkerId(jsonObject.getInteger("crmid"));
		callRecord.setLineNum(jsonObject.getInteger("extnumber"));
		callRecord.setCallType(StringUtils.equalsIgnoreCase("callout", jsonObject.getString("type"))?0:1);
		callRecord.setCallMode(jsonObject.getInteger("callmethod"));
		callRecord.setCompanyId(jsonObject.getString("companycode"));
		callRecord.setCustomuuid(jsonObject.getString("customuuid"));
		// 如果是手机号前有0，则把0去掉，座机不处理
		if(StringUtils.isNotBlank(callRecord.getPhone()) 
				&& CommonUtils.addZeroPhone(callRecord.getPhone())){
			callRecord.setPhone(callRecord.getPhone().substring(1, callRecord.getPhone().length()));
		}
	}

}
