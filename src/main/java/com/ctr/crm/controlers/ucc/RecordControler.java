package com.ctr.crm.controlers.ucc;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.configuration.Config;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponsePage;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.MemberUtils;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.record.models.CallRecord;
import com.ctr.crm.moduls.record.models.RecordSearch;
import com.ctr.crm.moduls.record.service.RecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;

@Api(tags="呼叫中心")
@RequestMapping("/api/ucc/")
@RestController
@Secure(0)
@Menu(menuName="通话记录", menuUrl="ucc_record")
public class RecordControler implements CurrentWorkerAware {

	@Resource
	private RecordService recordService;
	@Autowired
	private Config config;
	
	@ApiOperation(value="录音记录查询接口", notes="录音记录查询接口")
	@RequestMapping(value="record/query", method=RequestMethod.GET)
	public ResponseData query(@ModelAttribute RecordSearch search) throws Exception{
		Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
		ResponsePage<Map<String, Object>> result = recordService.query(search, currentWorker);
		return new ResponseData(ResponseStatus.success, result);
	}
	
	@ApiOperation(value="录音下载接口", notes="录音下载接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name="recordId", value="录音ID",required=true,dataTypeClass=Integer.class)
	})
	@Secure(value=1, actionName="录音下载", actionUri="/api/ucc/record/download", actionNote="呼叫中心", foundational=true)
	@RequestMapping(value="record/download", method={RequestMethod.GET})
	public ResponseData download(Integer recordId, HttpServletResponse response) throws Exception {
		if(recordId == null){			
			return new ResponseData(ResponseStatus.null_param);
		}
		CallRecord callRecord = recordService.selectByCallId(recordId);
		if(callRecord == null){
			return new ResponseData(ResponseStatus.failed, "录音记录不存在");
		}
		String fileName = callRecord.getRecordFileName();
		String address = callRecord.getDownFileIp();
		if(fileName == null){
			return new ResponseData(ResponseStatus.failed, "录音文件为空");
		}
		if(address == null){
			return new ResponseData(ResponseStatus.failed, "下载地址为空");
		}
		fileName = MemberUtils.decodeFileName(fileName);
		//String encrypted = URLEncoder.encode(AESencrp.AES_Encrypt(fileName+"|"+address+"|"+config.getDownloadPort()),"utf8");
		response.sendRedirect(recordService.getDownloadUrl(callRecord));
		return null;
	}

}
