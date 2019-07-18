package com.ctr.crm.commons.ucc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ctr.crm.commons.utils.HttpUtils;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import com.ctr.crm.moduls.system.models.CallAddress;
import com.ctr.crm.moduls.system.service.CallAddressService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.Encrypt;
import com.ctr.crm.commons.utils.MemberUtils;
import com.ctr.crm.commons.utils.SpringContextUtils;

/**
 * 封装与SIP API交互的工具类<br>
 * 200 请求成功<br>
 * 400:非法请求<br>
 * 500:服务器错误<br>
 * 600 token失效
 *
 * @author Administrator
 */
public class CallUtils {

	private final static Log log = LogFactory.getLog("call");
	private static CallAddressService callAddressService = SpringContextUtils.getBean("callAddressService", CallAddressService.class);
	private static WorkerService workerService = SpringContextUtils.getBean("workerService", WorkerService.class);
	private static String token = null;

	public static String acquireToken(Worker currentWorker) {
		if (currentWorker == null) {
			log.info("acquireToken failed. not login");
			return null;
		}
		CallAddress callAddress = callAddressService.select();
		if (callAddress == null) {
			log.info("acquireToken(workerId=" + currentWorker.getWorkerId() + ", companyId="+ currentWorker.getCompanyId()+") failed. not config CallAddress");
			return null;
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", callAddress.getAppid());
		params.put("accesskey", callAddress.getAccessKey());
		params.put("service", "App.Sip_Auth.Login");
		String result = HttpUtils.getInstance().doPost(callAddress.getAddress(), params);
		if (result == null) {
			log.info("acquireToken error. result is null");
			return null;
		}
		JSONObject jsonObject = JSONObject.parseObject(result);
		if (jsonObject.getInteger("ret") == 200) {// 响应成功
			jsonObject = jsonObject.getJSONObject("data");
			// 业务响应成功
			if (jsonObject != null && jsonObject.getInteger("status") == 0) {
				jsonObject = jsonObject.getJSONObject("result");
				if (jsonObject != null) {
					token = jsonObject.getString("token");
					return token;
				}
			}
		}
		log.info("acquireToken. result:" + result);
		return null;
	}

	/**
	 * 注销token
	 *
	 * @param currentWorker
	 * @return
	 */
	public static String logoutToken(Worker currentWorker) {
		if (currentWorker == null) {
			log.info("acquireToken failed. not login");
			return null;
		}
		CallAddress callAddress = callAddressService.select();
		if (callAddress == null) {
			log.info("acquireToken(workerId=" + currentWorker.getWorkerId() + ", companyId="+ currentWorker.getCompanyId()+") failed. not config CallAddress");
			return null;
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", callAddress.getAppid());
		params.put("accesskey", callAddress.getAccessKey());
		params.put("service", "App.Sip_Auth.Logout");
		String result = HttpUtils.getInstance().doPost(callAddress.getAddress(), params);
		if (result == null) {
			log.info("acquireToken error. result is null");
			return null;
		}
		JSONObject jsonObject = JSONObject.parseObject(result);
		if (jsonObject.getInteger("ret") == 200) {// 响应成功
			jsonObject = jsonObject.getJSONObject("data");
			// 业务响应成功
			if (jsonObject != null && jsonObject.getInteger("status") == 0) {
				jsonObject = jsonObject.getJSONObject("result");
				if (jsonObject != null) {
					return jsonObject.getString("token");
				}
			}
		}
		log.info("acquireToken. result:" + result);
		return null;
	}

	/**
	 * 判断分机号是否在线
	 *
	 * @param currentWorker
	 * @param extNumber
	 * @return
	 */
	public static boolean isOnline(Worker currentWorker, String extNumber) {
		if (currentWorker == null || StringUtils.isBlank(extNumber))
			return false;
		Integer lineNum = CommonUtils.evalInteger(extNumber);
		Worker w = workerService.selectByExtNumber(lineNum);
		if (w == null || w.getOnline() == null)
			return false;
		return w.getOnline() == 1;
	}
	
	public static String call(Worker currentWorker, String phone, Long memberId, Integer caseClass, Long procId){
		CallAddress callAddress = callAddressService.select();
		if (callAddress == null) {
			log.info("sendSyncRequest[workerId=" + currentWorker.getWorkerId() + ", companyId="+ currentWorker.getCompanyId()+", action=makecall] failed. not config CallAddress");
			return "未配置呼叫参数，无法拨打";
		}
		int callMode = CommonUtils.evalInt(callAddress.getCallMode(), 1);
		if(callMode == 1){
			return makeCall(currentWorker, phone, memberId, caseClass, procId);
		}else if(callMode == 2){
			return makePloyCall(currentWorker, phone, memberId, caseClass, procId);
		}
		return makeCall(currentWorker, phone, memberId, caseClass, procId);
	}

	/**
	 * 发起呼叫
	 *
	 * @param token
	 * @param currentWorker
	 * @param phone 客户号码
	 * @param memberId 客户ID
	 * @param caseClass 客户意向度
	 * @param procId 小记ID
	 * @return 是否发起成功
	 */
	private static String makeCall(Worker currentWorker, String phone, Long memberId, Integer caseClass, Long procId) {
		if (currentWorker == null) {
			log.info("App.Sip_Call.MakeCall failed. logout");
			return "未登录或员工不存在";
		}
		if (currentWorker.getLineNum() == null) {
			log.info("App.Sip_Call.MakeCall failed. workerId:" + currentWorker.getWorkerId()
					+ "; reason:lineNum is empty");
			return "分机号未设置";
		}
		if (StringUtils.isBlank(phone)) {
			log.info("App.Sip_Call.MakeCall failed. workerId:" + currentWorker.getWorkerId()
					+ "; reason:phone is empty");
			return "电话号码为空";
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("service", "App.Sip_Call.MakeCall");
		params.put("extnumber", currentWorker.getLineNum().toString());
		params.put("destnumber", Encrypt.decrypt(phone));
		params.put("userid", currentWorker.getWorkerId().toString());
		params.put("memberid", CommonUtils.evalString(memberId));
		params.put("chengshudu", CommonUtils.evalString(caseClass));
		params.put("customuuid", CommonUtils.evalString(procId));
		JSONObject result = sendSyncRequest(currentWorker, "App.Sip_Call.MakeCall", params);
		if (result != null) {
			Integer status = result.getInteger("status");
			if (status == 1) {
				result = result.getJSONObject("errors");
				if (result != null) {
					log.info("App.Sip_Call.MakeCall error. msg:" + result.toJSONString());
					return result.getString("codemsg") + "(" + result.getString("code") + ")";
				}
			}
			log.info("App.Sip_Call.MakeCall Status:" + status + ". params:" + params.toString());
			return StringUtils.EMPTY;
		}
		return "请求异常";
	}

	/**
	 * 发起策略呼叫
	 *
	 * @param token
	 * @param currentWorker
	 * @param phone
	 * @return 是否发起成功
	 */
	private static String makePloyCall(Worker currentWorker, String phone, Long memberId, Integer caseClass, Long procId) {
		if (currentWorker == null) {
			log.info("App.Sip_Call.MakePloyCall failed. logout");
			return "未登录或员工不存在";
		}
		if (currentWorker.getLineNum() == null) {
			log.info("App.Sip_Call.MakePloyCall failed. workerId:" + currentWorker.getWorkerId()
					+ "; reason:lineNum is empty");
			return "分机号未设置";
		}
		if (StringUtils.isBlank(phone)) {
			log.info("App.Sip_Call.MakePloyCall failed. workerId:" + currentWorker.getWorkerId()
					+ "; reason:phone is empty");
			return "电话号码为空";
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("service", "App.Sip_Call.MakePloyCall");
		params.put("extnumber", currentWorker.getLineNum().toString());
		params.put("destnumber", Encrypt.decrypt(phone));
		params.put("userid", currentWorker.getWorkerId().toString());
		params.put("memberid", CommonUtils.evalString(memberId));
		params.put("chengshudu", CommonUtils.evalString(caseClass));
		params.put("customuuid", CommonUtils.evalString(procId));
		JSONObject result = sendSyncRequest(currentWorker, "App.Sip_Call.MakePloyCall", params);
		if (result != null) {
			Integer status = result.getInteger("status");
			if (status == 1) {
				result = result.getJSONObject("errors");
				if (result != null) {
					log.info("App.Sip_Call.MakePloyCall error. msg:" + result.toJSONString());
					return result.getString("codemsg") + "(" + result.getString("code") + ")";
				}
			}
			log.info("App.Sip_Call.MakePloyCall Status:" + status + ". params:" + params.toString());
			return StringUtils.EMPTY;
		}
		return "请求异常";
	}

	/**
	 * 挂断通话
	 *
	 * @param token
	 * @param currentWorker
	 * @param phone
	 * @return 是否发起成功
	 */
	public static String hangupCall(Worker currentWorker) {
		if (currentWorker == null) {
			log.info("App.Sip_Call.HangupCall failed. logout");
			return "未登录或员工不存在";
		}
		if (currentWorker.getLineNum() == null) {
			log.info("App.Sip_Call.HangupCall failed. workerId:" + currentWorker.getWorkerId()
					+ "; reason:lineNum is empty");
			return "分机号未设置";
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("extnumber", currentWorker.getLineNum().toString());
		params.put("service", "App.Sip_Call.HangupCall");
		JSONObject result = sendSyncRequest(currentWorker, "App.Sip_Call.HangupCall", params);
		if (result != null) {
			Integer status = result.getInteger("status");
			if (status == 1) {
				result = result.getJSONObject("errors");
				if (result != null) {
					log.info("App.Sip_Call.HangupCall error. msg:" + result.toJSONString());
					return result.getString("codemsg") + "(" + result.getString("code") + ")";
				}
			}
			log.info("App.Sip_Call.HangupCall Status:" + status + ". params:" + params.toString());
			return StringUtils.EMPTY;
		}
		return "请求异常";
	}

	/**
	 * 发起监听/耳语/三方通话
	 *
	 * @param currentWorker
	 *            当前操作人
	 * @param destLineNum
	 *            被监听分机号
	 * @param listenType
	 *            监听类型 1监听分机 2耳语 3取消耳语 4三方通话
	 * @return
	 */
	public static String listen(Worker currentWorker, String destLineNum, int listenType) {
		if (currentWorker == null) {
			log.info("App.Sip_Call.SpyCall failed. logout");
			return "未登录或员工不存在";
		}
		if (currentWorker.getLineNum() == null) {
			log.info("App.Sip_Call.SpyCall failed. workerId:" + currentWorker.getWorkerId()
					+ "; reason:lineNum is empty");
			return "分机号未设置";
		}
		if (StringUtils.isBlank(destLineNum)) {
			log.info("App.Sip_Call.SpyCall failed. workerId:" + currentWorker.getWorkerId()
					+ "; reason:destLineNum is empty");
			return "对方分机号为空";
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("service", "App.Sip_Call.SpyCall");
		params.put("extnumber", currentWorker.getLineNum().toString());
		params.put("destnumber", destLineNum);
		params.put("monitortype", String.valueOf(listenType));
		JSONObject result = sendSyncRequest(currentWorker, "App.Sip_Call.SpyCall", params);
		if (result != null) {
			Integer status = result.getInteger("status");
			if (status == 1) {
				result = result.getJSONObject("errors");
				if (result != null) {
					log.info("App.Sip_Call.SpyCall error. msg:" + result.toJSONString());
					return result.getString("codemsg") + "(" + result.getString("code") + ")";
				}
			}
			log.info("App.Sip_Call.SpyCall Status:" + status + ". params:" + params.toString());
			return StringUtils.EMPTY;
		}
		return "请求异常";
	}

	/**
	 * 强插/强拆
	 *
	 * @param currentWorker
	 *            当前操作人
	 * @param destLineNum
	 *            被强插/强拆分机号
	 * @param forceType
	 *            类型 1强插 2强拆
	 * @return
	 */
	public static String forcecc(Worker currentWorker, String destLineNum, int forceType) {
		if (currentWorker == null) {
			log.info("App.Sip_Call.ForceCall failed. logout");
			return "未登录或员工不存在";
		}
		if (currentWorker.getLineNum() == null) {
			log.info("App.Sip_Call.ForceCall failed. workerId:" + currentWorker.getWorkerId()
					+ "; reason:lineNum is empty");
			return "分机号未设置";
		}
		if (StringUtils.isBlank(destLineNum)) {
			log.info("App.Sip_Call.ForceCall failed. workerId:" + currentWorker.getWorkerId()
					+ "; reason:destLineNum is empty");
			return "对方分机号为空";
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("service", "App.Sip_Call.ForceCall");
		params.put("extnumber", currentWorker.getLineNum().toString());
		params.put("destnumber", destLineNum);
		params.put("forcetype", String.valueOf(forceType));
		JSONObject result = sendSyncRequest(currentWorker, "App.Sip_Call.ForceCall", params);
		if (result != null) {
			Integer status = result.getInteger("status");
			if (status == 1) {
				result = result.getJSONObject("errors");
				if (result != null) {
					log.info("App.Sip_Call.ForceCall error. msg:" + result.toJSONString());
					return result.getString("codemsg") + "(" + result.getString("code") + ")";
				}
			}
			log.info("App.Sip_Call.ForceCall Status:" + status + ". params:" + params.toString());
			return StringUtils.EMPTY;
		}
		return "请求异常";
	}
	
	/**
	 * 分机注册状态
	 *
	 * @param currentWorker 当前操作人
	 * @return
	 */
	public static Map<String, Object> registerStatus(Worker currentWorker) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (currentWorker == null) {
			log.info("App.Sip_Status.GetSipRegisterStatus failed. logout");
			map.put("msg", "未登录系统");
			return map;
		}
		if (currentWorker.getLineNum() == null) {
			log.info("App.Sip_Status.GetSipRegisterStatus failed. workerId:" + currentWorker.getWorkerId()
					+ "; reason:lineNum is empty");
			map.put("msg", "分机号未设置");
			return map;
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("service", "App.Sip_Status.GetSipRegisterStatus");
		params.put("extnumber", currentWorker.getLineNum().toString());
		JSONObject result = sendSyncRequest(currentWorker, "App.Sip_Status.GetSipRegisterStatus", params);
		// 业务响应成功
		if (result != null && result.getInteger("status") == 0) {
			JSONArray jsonArray = result.getJSONArray("result");
			if (jsonArray != null) {
				result = jsonArray.getJSONObject(0);
				map.put("clientip", result.getString("clientip"));
				map.put("password", acquireLineNumPassword(currentWorker));
				map.put("extnumber", currentWorker.getLineNum());
				CallAddress address = callAddressService.select();
				if(address != null && address.getAddress() != null){
					map.put("serverip", address.getAddress().replaceFirst("^(http://)(.*?)(:\\d+)$", "$2"));
				}
				return map;
			}
		}
		map.put("msg", "请求失败");
		return map;
	}

	/**
	 * 获取队列列表(人员管理的增加和修改用
	 *
	 * @param currentWorker
	 * @return
	 */
	public static List<Map<String, Object>> acquireQueue(Worker currentWorker) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("service", "App.Sip_Queue.GetQueueList");
		JSONObject result = sendSyncRequest(currentWorker, "App.Sip_Queue.GetQueueList", params);
		if (result != null) {
			Integer status = result.getInteger("status");
			if (status == 1) {
				log.info("App.Sip_Queue.GetQueueList error. msg:" + result.getString("errors"));
				return null;
			}
			String resStr = result.getString("result");
			JSONArray jsonArray = JSONArray.parseArray(resStr);
			// 队列数据无法获取 result
			if (jsonArray == null)
				return null;

			List<Map<String, Object>> strList = new ArrayList<>();
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject job = jsonArray.getJSONObject(i); // 遍历 jsonarray 数组，把每一个对象转成 json 对象
				strList.add(CommonUtils.createMap("value", job.getString("queuename")));
			}

			return strList;
		}
		return null;
	}
	
	/**
	 * 获取分机列表(人员管理的增加和修改用
	 *
	 * @param currentWorker
	 * @return
	 */
	public static List<Map<String, Object>> acquireLineNum(Worker currentWorker) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("service", "App.Sip_Sipnum.GetSipnumberInfo");
		params.put("status", "1");
		JSONObject result = sendSyncRequest(currentWorker, "App.Sip_Sipnum.GetSipnumberInfo", params);
		if (result != null) {
			Integer status = result.getInteger("status");
			if (status == 1) {
				log.info("App.Sip_Sipnum.GetSipnumberInfo error. msg:" + result.getString("errors"));
				return null;
			}
			String resStr = result.getString("result");
			JSONArray jsonArray = JSONArray.parseArray(resStr);
			// 队列数据无法获取 result
			if (jsonArray == null)
				return null;
			
			List<Map<String, Object>> strList = new ArrayList<>();
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				strList.add(CommonUtils.createMap("value", jsonObject.getInteger("extnumber")));
			}
			
			return strList;
		}
		return null;
	}
	
	/**
	 * 获取分机号密码
	 *
	 * @param currentWorker
	 * @return
	 */
	public static String acquireLineNumPassword(Worker currentWorker) {
		if(currentWorker == null 
				|| currentWorker.getLineNum() == null)
			return null;
		Map<String, String> params = new HashMap<String, String>();
		params.put("service", "App.Sip_Sipnum.GetSipnumberInfo");
		params.put("status", "1");
		params.put("extnumber", currentWorker.getLineNum().toString());
		JSONObject result = sendSyncRequest(currentWorker, "App.Sip_Sipnum.GetSipnumberInfo", params);
		if (result != null) {
			Integer status = result.getInteger("status");
			if (status == 1) {
				log.info("App.Sip_Sipnum.GetSipnumberInfo error. msg:" + result.getString("errors"));
				return null;
			}
			String resStr = result.getString("result");
			JSONArray jsonArray = JSONArray.parseArray(resStr);
			// 队列数据无法获取 result
			if (jsonArray == null)
				return null;
			
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			return jsonObject.getString("password");
			
		}
		return null;
	}
	
	

	/**
	 * 获取分机通话状态 <br>
	 *
	 * @param currentWorker
	 * @param extNumbers
	 *            多个分机号用逗号分隔
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<CallStatus> getSipsCallStatus(Worker currentWorker, String extNumbers) {
		List<CallStatus> list = new ArrayList<CallStatus>();
		// TODO 一定记得删除代码
		//----状态列表
		/*String[] extNumberArray = StringUtils.split(extNumbers, ",");
		if(extNumberArray != null){
			List<Integer> statuss = Arrays.asList(1200,1201,1202,1203,1204);
			Random random = new Random();
			for (String extnumber : extNumberArray) {
				CallStatus callStatus = new CallStatus();
				callStatus.setExtnumber(extnumber);
				int status = statuss.get(random.nextInt(statuss.size()));
				callStatus.setStatus(status);
				callStatus.setStatusDesc(getCallStatusDesc(status));
				if(status == 1204){
					callStatus.setCallduration(random.nextInt(100));
					callStatus.setPhone("13800138000");
				}else if(status == 1201){
					callStatus.setIdleduration(random.nextInt(30));
				}
				list.add(callStatus);
			}
			return list;
		}*/
		//----测试后删除
		Map<String, String> params = new HashMap<String, String>();
		params.put("extnumber", extNumbers);
		params.put("service", "App.Sip_Status.GetSipCallStatus");
		JSONObject result = sendSyncRequest(currentWorker, "App.Sip_Status.GetSipCallStatus", params);
		if (result != null && result.getInteger("status") == 0) {
			String v = result.getString("result");
			if (v == null)
				return list;
			List<Map> mapList = JSONArray.parseArray(v, Map.class);
			CallStatus callStatus = null;
			for (Map<String, Object> map : mapList) {
				callStatus = new CallStatus();
				int status = CommonUtils.evalInt(map.get("status"), -1);
				callStatus.setExtnumber(CommonUtils.evalString(map.get("extnumber")));
				// 是否离线
				//boolean online = CallUtils.isOnline(currentWorker, CommonUtils.evalString(map.get("extnumber")));
				//if(!online) status = 1200;
				callStatus.setStatus(status);
				callStatus.setStatusDesc(getCallStatusDesc(status));
				callStatus.setIdleduration(CommonUtils.evalInteger(map.get("idleduration")));
				callStatus.setCallduration(CommonUtils.evalInteger(map.get("callduration")));
				String direction = CommonUtils.evalString(map.get("direction"));
				if (StringUtils.equalsIgnoreCase(direction, "callout")) {
					callStatus.setPhone(CommonUtils.evalString(map.get("callee")));
				} else {
					callStatus.setPhone(CommonUtils.evalString(map.get("caller")));
				}
				callStatus.setPhone(MemberUtils.maskPhone(callStatus.getPhone(), currentWorker));
				list.add(callStatus);
			}
		}
		return list;
	}

	/**
	 * 1012 分机不存在 <br>
	 * 1013 分机已停用 <br>
	 * 1014 分机未注册 <br>
	 * 1015 分机不在通话中 <br>
	 * 1016 分机已启用 <br>
	 * 1017 分机已注册 1200 离线 <br>
	 * 1201 空闲 <br>
	 * 1202 振铃 <br>
	 * 1203 摘机 <br>
	 * 1204 通话中
	 *
	 * @param status
	 * @return 返回状态描述
	 */
	private static String getCallStatusDesc(int status) {
		switch (status) {
		case -1:
			return "未知";
		case 1012:
			return "分机不存在";
		case 1013:
			return "分机已停用";
		case 1014:
			return "分机未注册";
		case 1015:
		case 1016:
		case 1017:
		case 1201:
			return "空闲";
		case 1202:
			return "振铃";
		case 1203:
			return "摘机";
		case 1204:
			return "通话中";
		default:
			break;
		}
		return "离线";
	}

	/**
	 * 语音评分
	 *
	 * @param currentWorker
	 * @param memberId
	 *            客户ID
	 * @return
	 */
	public static String voiceScore(Worker currentWorker, Long memberId) {
		// TODO 语音评分接口待定
		if (currentWorker == null) {
			log.info("Action.PingFen failed. logout");
			return "未登录或员工不存在";
		}
		if (currentWorker.getLineNum() == null) {
			log.info("Action.PingFen failed. workerId:" + currentWorker.getWorkerId() + "; reason:lineNum is empty");
			return "分机号未设置";
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("service", "Action.PingFen");
		params.put("extnumber", currentWorker.getLineNum().toString());
		params.put("userid", currentWorker.getWorkerId().toString());
		params.put("memberid", CommonUtils.evalString(memberId));
		JSONObject result = sendSyncRequest(currentWorker, "Action.PingFen", params);
		if (result != null) {
			Integer status = result.getInteger("status");
			if (status == 1) {
				result = result.getJSONObject("errors");
				if (result != null) {
					return result.getString("codemsg") + "(" + result.getString("code") + ")";
				}
			}
			log.info("Action.PingFen Status:" + status + ". params:" + params.toString());
			return StringUtils.EMPTY;
		}
		return "请求异常";
	}

	/**
	 * 转接通话
	 *
	 * @param currentWorker
	 *            当前操作人
	 * @param target
	 *            转接目标(分机号或队列名称)
	 * @param transferType
	 *            转接类型 1分机 2队列
	 * @return
	 */
	public static String transferCall(Worker currentWorker, String target, int transferType) {
		if (currentWorker == null) {
			log.info("App.Sip_Call.TransferCall failed. logout");
			return "未登录或员工不存在";
		}
		if (currentWorker.getLineNum() == null) {
			log.info("App.Sip_Call.TransferCall failed. workerId:" + currentWorker.getWorkerId()
					+ "; reason:lineNum is empty");
			return "分机号未设置";
		}
		if (StringUtils.isBlank(target)) {
			log.info("App.Sip_Call.TransferCall failed. workerId:" + currentWorker.getWorkerId()
					+ "; reason:target is empty");
			return "目标" + (transferType == 1 ? "分机" : "队列") + "为空";
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("service", "App.Sip_Call.TransferCall");
		params.put("anumber", currentWorker.getLineNum().toString());
		params.put("destnumber", target);
		params.put("transfertype", String.valueOf(transferType));
		JSONObject result = sendSyncRequest(currentWorker, "App.Sip_Call.TransferCall", params);
		if (result != null) {
			Integer status = result.getInteger("status");
			if (status == 1) {
				result = result.getJSONObject("errors");
				if (result != null) {
					return result.getString("codemsg") + "(" + result.getString("code") + ")";
				}
			}
			log.info("App.Sip_Call.TransferCall Status:" + status + ". params:" + params.toString());
			return StringUtils.EMPTY;
		}
		return "请求异常";
	}

	/**
	 * 签入队列
	 *
	 * @param currentWorker
	 * @return
	 */
	public static Map<String, Object> signIn(Worker currentWorker) {
		Map<String, Object> returnresult = new HashMap<String, Object>();
		if (currentWorker == null) {
			log.info("App.Sip_Queue.SipSignIn failed. logout or not exists worker");
			returnresult.put("code", 0);
			returnresult.put("text", "签入失败，未登录或员工不存在");
			return returnresult;
		}
		if (currentWorker.getLineNum() == null) {
			log.info("App.Sip_Queue.SipSignIn failed. workerId:" + currentWorker.getWorkerId()
					+ "; reason:lineNum is empty");
			returnresult.put("code", 0);
			returnresult.put("text", "签入失败，分机号未设置");
			return returnresult;
		}
		// 拦截器中可能没有将队列参数读入,所以再查一次
		currentWorker = workerService.select(currentWorker.getWorkerId());
		if (StringUtils.isBlank(currentWorker.getWorkQueue())) {
			log.info("签入成功. workerId:" + currentWorker.getWorkerId() + "; reason:workqueue is empty.");
			returnresult.put("code", 0);
			returnresult.put("text", "签入失败，队列为空");
			return returnresult;
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("service", "App.Sip_Queue.SipSignIn");
		params.put("extnumber", currentWorker.getLineNum().toString());
		params.put("queuename", currentWorker.getWorkQueue());
		params.put("crmid", String.valueOf(currentWorker.getWorkerId()));
		JSONObject result = sendSyncRequest(currentWorker, "App.Sip_Queue.SipSignIn", params);
		if (result != null) {
			Integer status = result.getInteger("status");
			if (status == 1) {
				result = result.getJSONObject("errors");
				if (result != null) {
					returnresult.put("code", 0);
					returnresult.put("text", "签入失败，"+result.getString("codemsg") + "(" + result.getString("code") + ")");
					return returnresult;
				}
			}
			log.info("App.Sip_Queue.SipSignIn Status:" + status + ". params:" + params.toString());
			returnresult.put("code", 1);
			returnresult.put("text", "签入成功");
			return returnresult;
		}
		returnresult.put("code", 0);
		returnresult.put("text", "签入失败，API异常");
		return returnresult;
	}

	/**
	 * 签出队列
	 *
	 * @param currentWorker
	 * @return
	 */
	public static Map<String, Object> signOut(Worker currentWorker) {
		Map<String, Object> returnresult = new HashMap<>();
		if (currentWorker == null) {
			log.info("App.Sip_Queue.SipSignOut failed. logout");
			returnresult.put("code", 0);
			returnresult.put("text", "签出失败，未登录或员工不存在");
			return returnresult;
		}
		if (currentWorker.getLineNum() == null) {
			log.info("App.Sip_Queue.SipSignOut failed. workerId:" + currentWorker.getWorkerId()
					+ "; reason:lineNum is empty");
			returnresult.put("code", 0);
			returnresult.put("text", "签出失败，分机号未设置");
			return returnresult;
		}
		// 拦截器中可能没有将队列参数读入,所以再查一次
		currentWorker = workerService.select(currentWorker.getWorkerId());
		if (StringUtils.isBlank(currentWorker.getWorkQueue())) {
			log.info("App.Sip_Queue.SipSignOut failed. workerId:" + currentWorker.getWorkerId() + "; reason:workQueue is empty");
			returnresult.put("code", 0);
			returnresult.put("text", "签出失败，队列为空");
			return returnresult;
		}
		Map<String, String> params = new HashMap<>();
		params.put("service", "App.Sip_Queue.SipSignOut");
		params.put("extnumber", currentWorker.getLineNum().toString());
		params.put("queuename", currentWorker.getWorkQueue());
		params.put("crmid", String.valueOf(currentWorker.getWorkerId()));
		JSONObject result = sendSyncRequest(currentWorker, "App.Sip_Queue.SipSignOut", params);
		if (result != null) {
			Integer status = result.getInteger("status");
			if (status == 1) {
				result = result.getJSONObject("errors");
				if (result != null) {
					returnresult.put("code", 0);
					returnresult.put("text", "签出失败，"+result.getString("codemsg") + "(" + result.getString("code") + ")");
					return returnresult;
				}
			}
			log.info("App.Sip_Queue.SipSignOut Status:" + status + ". params:" + params.toString());
			returnresult.put("code", 1);
			returnresult.put("text", "签出成功");
			return returnresult;
		}
		returnresult.put("code", 0);
		returnresult.put("text", "签出失败，,API异常");
		return returnresult;

	}


	/**
	 * 获取当前队列中分机号的状态 ( 有数据表示签到成功/或签出成功
	 * 返回值：1表示已签入 0表示未签入
	 * @param currentWorker
	 * @return
	 */
	public static String queueSipStatus(Worker currentWorker) {
		String returnResult = "0";
		if (currentWorker == null) {
			log.info("App.Sip_Queue.GetQueueSipStatus failed. logout");
			return returnResult;
		}
		if (currentWorker.getLineNum() == null) {
			log.info("App.Sip_Queue.GetQueueSipStatus failed. workerId:" + currentWorker.getWorkerId()
					+ "; reason:lineNum is empty");
			return returnResult;
		}
		if (StringUtils.isBlank(currentWorker.getWorkQueue())) {
			log.info("App.Sip_Queue.GetQueueSipStatus failed. workerId:" + currentWorker.getWorkerId() + "; reason:workQueue is empty");
			return returnResult;
		}
		Map<String, String> params = new HashMap<>();
		params.put("service", "App.Sip_Queue.GetQueueSipStatus");
		params.put("queuename", currentWorker.getWorkQueue());
		params.put("extnumber", currentWorker.getLineNum().toString());
		JSONObject result = sendSyncRequest(currentWorker, "App.Sip_Queue.GetQueueSipStatus", params);
		if (result != null) {
			Integer status = result.getInteger("status");
			if (status == 1) {
				log.info("App.Sip_Queue.GetQueueSipStatus error. msg:" + result.getString("errors"));
				return returnResult;
			}
			String resStr = result.getString("result");
			JSONArray jsonArray = JSONArray.parseArray(resStr);
			// 队列数据无法获取 result
			if (jsonArray == null)
				return returnResult;

			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject job = jsonArray.getJSONObject(i); // 遍历 jsonarray 数组，把每一个对象转成 json 对象
				// 呼叫中心中返回的分机号与员工分机号相对应时,返回 "" 表示此数据正常
				if (currentWorker.getLineNum() != null && StringUtils.startsWith(job.getString("seatsname"), currentWorker.getLineNum().toString()) 
						/*&& StringUtils.equals(job.getString("extnumber"),currentWorker.getLineNum().toString())*/) {
					returnResult = "1";
					break;
				}
			}
			log.info("App.Sip_Queue.GetQueueSipStatus workerId:" + currentWorker.getWorkerId()+"; workQueue:"+ currentWorker.getWorkQueue() + ". result:" + jsonArray.toJSONString());
			return returnResult;
		}
		log.info("App.Sip_Queue.GetQueueSipStatus workerId:" + currentWorker.getWorkerId()+"; workQueue:"+ currentWorker.getWorkQueue() + ". params:" + params.toString());
		return returnResult;

	}

	/**
	 * 发起API请求
	 *
	 * @param currentWorker
	 *            当前操作人
	 * @param action
	 *            发起的操作
	 * @return
	 */
	private static JSONObject sendSyncRequest(Worker currentWorker, String action, Map<String, String> params) {
		// 获取当前操作人API配置信息
		CallAddress callAddress = callAddressService.select();
		if (callAddress == null) {
			log.info("sendSyncRequest[workerId=" + currentWorker.getWorkerId()
					+ ", companyId=" + currentWorker.getCompanyId()
					+ ", action+" + action + "] failed. not config CallAddress");
			return null;
		}
		// 如果token为空，先去获取token
		if (StringUtils.isBlank(token)) {
			token = acquireToken(currentWorker);
		}
		if (token == null) {
			log.info("sendSyncRequest[workerId=" + currentWorker.getWorkerId() + ", companyId="+ currentWorker.getCompanyId()+", action+" + action
					+ "] failed. token is empty");
			return null;
		}
		params.put("token", token);
		String result = HttpUtils.getInstance().doPost(callAddress.getAddress(), params);
		if (result == null){
			log.info("sendSyncRequest[workerId=" + currentWorker.getWorkerId() + ", companyId="+ currentWorker.getCompanyId()+", action+" + action
					+ "] result is empty");
			return null;
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = JSONObject.parseObject(result);
		} catch (Exception e) {
			log.info("sendSyncRequest[workerId=" + currentWorker.getWorkerId() + ", companyId="+ currentWorker.getCompanyId()+", action+" + action
					+ "] convert JSONObject Exception. jsonString=" + result);
			jsonObject = null;
		}
		// 如果返回600，表示token已失效，重新获取，并重新发起一次请求获取结果
		if (jsonObject != null && jsonObject.getInteger("ret") == 600) {
			log.info("sendSyncRequest[workerId=" + currentWorker.getWorkerId() + ", companyId="+ currentWorker.getCompanyId()+", action+" + action + "] token="
					+ token + " invalid. reacquire");
			token = acquireToken(currentWorker);
			params.put("token", token);
			result = HttpUtils.getInstance().doPost(callAddress.getAddress(), params);
			if (result == null)
				return null;
			try {
				jsonObject = JSONObject.parseObject(result);
			} catch (Exception e) {
				log.info("sendSyncRequest[workerId=" + currentWorker.getWorkerId() + ", companyId="+ currentWorker.getCompanyId()+", action+" + action
						+ "] convert JSONObject Exception2. jsonString=" + result);
				jsonObject = null;
			}
		}
		if (jsonObject != null && jsonObject.getInteger("ret") == 200) {
			return jsonObject.getJSONObject("data");
		} else {
			if (jsonObject != null) {
				log.info("sendSyncRequest failed. [" + action + "] ret code:" + jsonObject.getInteger("ret")
						+ ", result:" + jsonObject.toJSONString());
			}
		}
		return null;
	}

	/*外呼任务*/
	public static List<String> getPredictDisnumberList(Worker currentWorker){
		return getDisnumberList(currentWorker, "2");
	}
	/**
	 * 获取主叫号码列表
	 * @param currentWorker
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static List<String> getDisnumberList(Worker currentWorker, String type){
		List<String> result = new ArrayList<String>();
		if(currentWorker == null){
			log.info("App.Sip_Disnum.GetDisnumberInfo failed. logout");
			return result;
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("service", "App.Sip_Disnum.GetDisnumberInfo");
		params.put("autocall", type);//2表示是
		JSONObject jsonResult = sendSyncRequest(currentWorker, "App.Sip_Disnum.GetDisnumberInfo", params);
		if(jsonResult != null && jsonResult.getInteger("status") == 0){
			String jsonStr = jsonResult.getString("result");
			if(StringUtils.isBlank(jsonStr)) return result;
			List<Map> mapList = JSONArray.parseArray(jsonStr, Map.class);
			for (Map map : mapList) {
				String disnumber = CommonUtils.evalToNull(map.get("disnumber"));
				if(disnumber != null){
					result.add(disnumber);
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPredictTask(String taskId, Worker currentWorker){
		if(currentWorker == null){
			log.info("App.Sip_Yccall.TaskList failed. not login");
			return null;
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("taskid", taskId);
		params.put("service", "App.Sip_Yccall.TaskList");
		JSONObject jsonResult = sendSyncRequest(currentWorker, "App.Sip_Yccall.TaskList", params);
		//业务响应成功
		if(jsonResult != null && jsonResult.getInteger("status")==0){
			jsonResult = jsonResult.getJSONObject("result");
			JSONArray jsonArray = null;
			if(jsonResult != null && (jsonArray=jsonResult.getJSONArray("task")) != null && !jsonArray.isEmpty()){
				return JSON.parseObject(JSON.toJSONString(jsonArray.get(0)), Map.class);
			}
		}
		return null;
	}

	/**
	 * 预测式外呼任务创建
	 * @param taskName 任务名称
	 * @param phones 外呼号码 多个用英文逗号分隔
	 * @param agents 分机号或队列 多个用英文逗号分隔
	 * @param agentType 1分机 2队列
	 * @param radio 比例
	 * @param currentWorker
	 * @return 返回任务ID
	 */
	public static String predictCallTaskCreate(String taskName, String disnumbers, List<Map<String, Object>> phones,
			String agents, String agentType, String radio, Worker currentWorker) {
		if(currentWorker == null){
			log.info("App.Sip_Yccall.TaskCreat failed. not login");
			return null;
		}
		if(StringUtils.isBlank(radio)) radio = "1";
		Map<String, String> params = new HashMap<String, String>();
		params.put("assigntype", agentType);
		params.put("assignagent", agents);
		params.put("telphone", JSON.toJSONString(phones));
		params.put("taskname", taskName);
		params.put("disnumber", disnumbers);
		params.put("taskscale", "1:" + radio);
		params.put("service", "App.Sip_Yccall.TaskCreat");
		JSONObject jsonResult = sendSyncRequest(currentWorker, "App.Sip_Yccall.TaskCreat", params);
		//业务响应成功
		if(jsonResult != null && jsonResult.getInteger("status")==0){
			jsonResult = jsonResult.getJSONObject("result");
			if(jsonResult != null){
				return jsonResult.getString("taskid");
			}
		}
		return null;
	}

	/**
	 * 预测式外呼任务操作
	 * @param callTaskId 外呼任务ID
	 * @param taskStatus 任务状态 1开启 2暂停 3取消
	 * @param currentWorker
	 * @return
	 */
	public static String predictCallTaskOpt(String callTaskId, String taskStatus, Worker currentWorker){
		if(currentWorker == null){
			log.info("App.Sip_Yccall.TaskSet failed. logout or not exists worker");
			return "未登录或员工不存在";
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("service", "App.Sip_Yccall.TaskSet");
		params.put("taskid", callTaskId);
		params.put("taskstatus", taskStatus);
		JSONObject result = sendSyncRequest(currentWorker, "App.Sip_Yccall.TaskSet", params);
		if(result != null){
			Integer status = result.getInteger("status");
			if(status == 1){
				result = result.getJSONObject("errors");
				if(result != null){
					return result.getString("codemsg")+"("+result.getString("code")+")";
				}
			}
			log.info("App.Sip_Yccall.TaskSet Status:"+status+". params:"+params.toString());			
			return StringUtils.EMPTY;
		}
		return "请求异常";
	}
	
	/**
	 * token置空
	 */
	public static void cleanToken(){
		token = null;
	}
}
