package com.ctr.crm.controlers.report;

import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.PojoUtils;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.intentionality.models.Intentionality;
import com.ctr.crm.moduls.intentionality.service.IntentionalityService;
import com.ctr.crm.moduls.record.service.CallStatService;
import com.ctr.crm.moduls.report.service.HomepageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;

/**
 * 说明：
 * @author eric
 * @date 2019年5月29日 下午2:44:07
 */
@Api(tags = "报表")
@RestController
@RequestMapping(value = "/api/report")
@Secure(1)
public class HomepageControler implements CurrentWorkerAware {
	
	@Resource
	private IntentionalityService intentionalityService;
	@Resource
	private HomepageService homepageService;
	@Resource
	private CallStatService callStatService;

	@ApiOperation(value = "/首页/机会库意向度分布")
	@ApiResponses({ @ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
		@ApiResponse(code = 100004, message = "未登录") })
	@RequestMapping(value = "homepage/distribution", method = { RequestMethod.GET })
	@ResponseBody
	public ResponseData caseClassChart(HttpServletRequest request) {
		Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
		// 意向度[A类,B类,C类,D类,...]
		Intentionality intentionality = new Intentionality();
		intentionality.setType(1);
		List<Intentionality> list = intentionalityService.findList(intentionality);
		List<String> legend = null;
		if(list != null){
			legend = Arrays.asList(PojoUtils.getArrayPropertis(list, "name", null, null));
		}
		// 饼状图数据
		List<Map<String, Object>> series = homepageService.chanceHierarchy(currentWorker);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("legend", legend);
		resultMap.put("series", series);
		return new ResponseData(ResponseStatus.success, resultMap);
	}
	
	@ApiOperation(value = "/首页/机会库跟进情况")
	@ApiResponses({ @ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
		@ApiResponse(code = 100004, message = "未登录") })
	@RequestMapping(value = "homepage/followup", method = { RequestMethod.GET })
	public ResponseData followupChart(HttpServletRequest request) {
		Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
		Map<String, Object> todayAllot = homepageService.todayAllot(currentWorker);
		Map<String, Object> todayCommunicate = homepageService.todayCommunicate(currentWorker);
		Map<String, Object> overdueEstranged = homepageService.overdueEstranged(currentWorker);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.putAll(todayAllot);
		resultMap.putAll(todayCommunicate);
		resultMap.putAll(overdueEstranged);
		return new ResponseData(ResponseStatus.success, resultMap);
	}
	
	@ApiOperation(value = "/首页/本月话务统计")
	@ApiImplicitParams({ 
		@ApiImplicitParam(name = "range", value = "范围 0呼出 1呼入 -1全部", required = true, paramType = "query")
	})
	@ApiResponses({ @ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
			@ApiResponse(code = 100004, message = "未登录") })
	@RequestMapping(value = "homepage/month/call", method = { RequestMethod.GET })
	public ResponseData monthCallChart(Integer range) throws Exception{
		Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
		// x轴(1号,2号,3号...)
		List<String> xAxis = CommonUtils.getMonthStr(new Date());
		// y轴(数据)
		Map<String, Object> yAxis = callStatService.monthCallStat(currentWorker, range);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("xAxis", xAxis);
		resultMap.putAll(yAxis);
		return new ResponseData(ResponseStatus.success, resultMap);
	}
	
	@ApiOperation(value = "/首页/今日话务")
	@ApiResponses({ @ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
		@ApiResponse(code = 100004, message = "未登录") })
	@RequestMapping(value = "homepage/today/call", method = { RequestMethod.GET })
	@ResponseBody
	public ResponseData todayCallChart() throws Exception {
		Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
		Map<String, Object> resultMap = callStatService.todayCallStat(currentWorker);
		return new ResponseData(ResponseStatus.success, resultMap);
	}
	
}
