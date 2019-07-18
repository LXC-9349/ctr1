package com.ctr.crm.controlers.ucc;

import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.ucc.CallStatus;
import com.ctr.crm.commons.ucc.CallUtils;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.Id;
import com.ctr.crm.commons.utils.PojoUtils;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import com.ctr.crm.moduls.system.models.CallAddress;
import com.ctr.crm.moduls.system.service.CallAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;

/**
 * 说明：电话操作API 如拨打，签入/签出，获取队列等
 * @author eric
 * @date 2019年4月25日 上午10:42:35
 */
@Secure(0)
@Api(tags="呼叫中心")
@RequestMapping("/api/ucc/")
@RestController
public class CallControler implements CurrentWorkerAware {
	
	@Resource
	private WorkerService workerService;
	@Resource
	private CallAddressService callAddressService;

	@ApiOperation(value = "发起呼叫", notes = "发起呼叫,发起成功会返回procId唯一标识，这个唯一标识需要在保存小记时回传")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "phone", value = "被叫号码", required = true, dataTypeClass = String.class),
			@ApiImplicitParam(name = "memberId", value = "客户ID", dataTypeClass = Long.class) })
	@ApiResponses({ @ApiResponse(code = 100000, message = "请求成功"),
			@ApiResponse(code = 100001, message = "请求失败") })
	@RequestMapping(value = "makecall", method = { RequestMethod.POST })
	@Secure(value=1, actionName="拨打电话", actionUri="/api/ucc/makecall", actionNote="呼叫中心")
	public ResponseData makeCall(String phone, Long memberId) throws Exception {
		Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
		Long procId = Id.generateSaleProcID();
		String str = CallUtils.call(currentWorker, phone, memberId, null, procId);
		if (StringUtils.isNotBlank(str)) {
			return new ResponseData(ResponseStatus.failed, str);
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("procId", procId);
		return new ResponseData(ResponseStatus.success, data);
	}
	
	@ApiOperation(value="获取分机列表状态", notes="获取分机列表状态<br>状态码：1200(离线) 1201(空闲) 1202(振铃) 1203(摘机) 1204(通话中)")
	@ApiImplicitParams({
		@ApiImplicitParam(name="lineNums", value="分机号,多个用逗号分隔",required=true,paramType="query",dataTypeClass=String.class)
	})
	@ApiResponses({
		@ApiResponse(code=100000,message="",response= CallStatus.class, responseContainer="List")
	})
	@RequestMapping(value="getsipscallstatus", method={RequestMethod.GET})
	@ResponseBody
	public ResponseData getSipsCallStatus(String lineNums) throws Exception{
		Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
		if(StringUtils.isBlank(lineNums)){
			return new ResponseData(ResponseStatus.success, new ArrayList<CallStatus>());
		}
		List<CallStatus> list = CallUtils.getSipsCallStatus(currentWorker, lineNums);
		return new ResponseData(ResponseStatus.success, list);
	}
	
	@ApiOperation(value = "监听", notes = "监听")
    @ApiImplicitParams({@ApiImplicitParam(name = "lineNum", value = "被监听分机号", required = true, dataTypeClass=String.class)})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败")})
    @RequestMapping(value = "listen", method = {RequestMethod.POST})
    public ResponseData listen(String lineNum) throws Exception {
		Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
		String str = CallUtils.listen(currentWorker, lineNum, Constants.listen);
		if (StringUtils.isNotBlank(str)) {
			return new ResponseData(ResponseStatus.failed, str);
		}
		return new ResponseData(ResponseStatus.success);
	}
	
	@ApiOperation(value = "发起/取消耳语", notes = "监听切耳语或耳语恢复监听")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "lineNum", value = "分机号", required = true, dataTypeClass=String.class),
		@ApiImplicitParam(name = "whisperType", value = "2耳语 3取消耳语 ", paramType = "query", dataType = "Integer", allowableValues = "2,3")})
	@ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败")})
	@RequestMapping(value = "whisper", method = {RequestMethod.POST})
	/*@Secure(value=1, actionName="耳语", actionUri="/api/ucc/whisper", actionNote="呼叫中心", foundational=true)*/
	public ResponseData whisper(String lineNum, Integer whisperType) throws Exception {
		Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
		String str = CallUtils.listen(currentWorker, lineNum, whisperType);
		if (StringUtils.isNotBlank(str)) {
			return new ResponseData(ResponseStatus.failed, str);
		}
		return new ResponseData(ResponseStatus.success);
	}
	
	@ApiOperation(value = "三方通话", notes = "三方通话")
	@ApiImplicitParams({@ApiImplicitParam(name = "lineNum", value = "分机号", required = true, dataTypeClass=String.class)})
	@ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败")})
	@RequestMapping(value = "tps", method = {RequestMethod.POST})
	/*@Secure(value=1, actionName="三方通话", actionUri="/api/ucc/tps", actionNote="呼叫中心", foundational=true)*/
	public ResponseData threePartyServices(String lineNum) throws Exception {
		Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
		String str = CallUtils.listen(currentWorker, lineNum, Constants.three_party_call);
		if (StringUtils.isNotBlank(str)) {
			return new ResponseData(ResponseStatus.failed, str);
		}
		return new ResponseData(ResponseStatus.success);
	}
	
	@ApiOperation(value = "强插/强拆", notes = "强插/强拆")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lineNum", value = "被强插或强拆分机号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "forceType", value = "类型(1强插 2强拆)", paramType = "query", dataType = "Integer", allowableValues = "1,2")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败")})
    @RequestMapping(value = "forcecc", method = {RequestMethod.POST})
	/*@Secure(value=1, actionName="强插/强拆", actionUri="/api/ucc/forcecc", actionNote="呼叫中心", foundational=true)*/
    public ResponseData forcecc(String lineNum, Integer forceType) throws Exception {
    	Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
        String str = CallUtils.forcecc(currentWorker, lineNum, forceType);
        if (StringUtils.isNotBlank(str)) {
        	return new ResponseData(ResponseStatus.failed, str);
        }
        return new ResponseData(ResponseStatus.success);
    }

    @ApiOperation(value = "挂断通话", notes = "挂断通话")
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败")})
    @RequestMapping(value = "hangupcall", method = {RequestMethod.POST})
    public ResponseData hangupCall() throws Exception {
    	Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
        String str = CallUtils.hangupCall(currentWorker);
        if (StringUtils.isNotBlank(str)) {
        	return new ResponseData(ResponseStatus.failed, str);
        }
        return new ResponseData(ResponseStatus.success);
    }
    
    @ApiOperation(value = "分机注册信息", notes = "分机注册信息")
    @RequestMapping(value = "registerstatus", method = {RequestMethod.GET})
    public ResponseData registerStatus() throws Exception {
    	Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
    	Map<String, Object> data = CallUtils.registerStatus(currentWorker);
    	String errorMsg = (String)data.get("msg");
    	if (errorMsg != null) {
    		return new ResponseData(ResponseStatus.failed, errorMsg);
    	}
    	return new ResponseData(ResponseStatus.success, data);
    }

    @ApiOperation(value = "签入队列", notes = "签入队列")
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败")})
    @RequestMapping(value = "signin", method = {RequestMethod.POST})
    public ResponseData signIn() throws Exception {
    	Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
        Map<String, Object> resultMap = CallUtils.signIn(currentWorker);
        ResponseData result = new ResponseData();
        int code = (int) resultMap.get("code");
        if (code == 1) {
            result.setCode(ResponseStatus.success.getStatusCode());
        } else {
        	result.setCode(ResponseStatus.failed.getStatusCode());
        }
        result.setMsg((String)resultMap.get("text"));
        return result;
    }
    
    @ApiOperation(value = "签出队列", notes = "签出队列")
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败")})
    @RequestMapping(value = "signout", method = {RequestMethod.POST})
    public ResponseData signOut() throws Exception {
    	Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
        Map<String, Object> resultMap = CallUtils.signOut(currentWorker);
        ResponseData result = new ResponseData();
        int code = (int) resultMap.get("code");
        if (code == 1) {
            result.setCode(ResponseStatus.success.getStatusCode());
        } else {
        	result.setCode(ResponseStatus.failed.getStatusCode());
        }
        result.setMsg((String)resultMap.get("text"));
        return result;
    }
    
    @ApiOperation(value = "获取队列列表")
    @RequestMapping(value = "queues", method = {RequestMethod.GET})
    public ResponseData queues() throws Exception {
		return new ResponseData(ResponseStatus.success,
				CallUtils.acquireQueue(CurrentWorkerLocalCache.getCurrentWorker()));
	}
    
    @ApiOperation(value = "获取分机列表", notes="获取可用的分机列表，已经配置到员工的会过滤掉")
    @ApiImplicitParam(name="lineNum", value="分机号, 注：该分机不会被过滤掉", allowEmptyValue=true, dataTypeClass=Integer.class)
    @RequestMapping(value = "linenums", method = {RequestMethod.GET})
    public ResponseData lineNums(Integer lineNum) throws Exception {
    	List<Map<String, Object>> lineNumList = CallUtils.acquireLineNum(CurrentWorkerLocalCache.getCurrentWorker());
    	if(lineNumList == null) return new ResponseData(ResponseStatus.success,new ArrayList<>());
    	List<Worker> workers = workerService.selectAll();
    	// lineNums为已经被占用的分机号，排除了lineNum
    	// 之后再遍历电话系统的所有分机，如果在占用列表，则移除
    	String[] lineNums = PojoUtils.getArrayPropertis(workers, "lineNum", "lineNum", lineNum, PojoUtils.Sign.ne, false);
    	Integer[] is = CommonUtils.evalIntegerArray(lineNums);
    	for (Iterator<Map<String, Object>> it = lineNumList.iterator();it.hasNext();) {
    		Map<String, Object> num = it.next();
			if(!ArrayUtils.contains(is, num.get("value")))
				continue;
			it.remove();
		}
    	return new ResponseData(ResponseStatus.success,lineNumList);
    }
    
    @ApiOperation(value = "获取电话中间件地址")
    @RequestMapping(value = "proxy/info", method = {RequestMethod.GET})
    public ResponseData addressInfo() throws Exception {
    	CallAddress address = callAddressService.select();
    	Map<String, Object> data = new HashMap<>();
    	data.put("proxyip", address.getProxyIp());
    	data.put("proxyport", address.getProxyPort());
    	data.put("protocol", address.getProxyProtocol());
    	return new ResponseData(ResponseStatus.success, data);
    }
    
    @ApiOperation(value = "更新电话配置")
    @RequestMapping(value = "calladdress", method = {RequestMethod.POST})
    public ResponseData calladdress(@ModelAttribute CallAddress callAddress) throws Exception {
    	if(!callAddressService.update(callAddress)){
    		return new ResponseData(ResponseStatus.failed);
    	}
    	return new ResponseData(ResponseStatus.success);
    }
    
    @ApiOperation(value = "获取电话配置")
    @RequestMapping(value = "calladdress/info", method = {RequestMethod.GET})
    public ResponseData calladdressInfo() throws Exception {
    	return new ResponseData(ResponseStatus.success, callAddressService.select());
    }
	
}
