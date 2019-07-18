package com.ctr.crm.controlers.ucc;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.record.service.CallStatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;

@Api(tags="呼叫中心")
@RequestMapping("/api/ucc")
@RestController
@Secure(0)
@Menu(menuName="话务统计报表", menuUrl="ucc_call_stat", parent=@Parent(menuName="报表中心", menuUrl="report_center"))
public class CallStatControler implements CurrentWorkerAware {

	@Resource
	private CallStatService callStatService;
	
	@ApiOperation(value="话务工作统计查询接口", notes="话务工作统计查询接口")
	@ApiResponses({
		@ApiResponse(code=100000, message="请求成功"),
		@ApiResponse(code=100003, message="参数为空"),
		@ApiResponse(code=100004, message="未登录")
	})
	@RequestMapping(value="callstat/query", method=RequestMethod.GET)
	public ResponseData query(@RequestParam Map<String, String> params) throws Exception{
		Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
		if(params == null) params = new HashMap<String, String>();
		List<Map<String, Object>> result = callStatService.query(params, currentWorker);
		return new ResponseData(ResponseStatus.success, result);
	}
	
}
