package com.ctr.crm.commons.utils;

import com.ctr.crm.moduls.sysdict.service.SysDictService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yunhus.redisclient.RedisProxy;

/**
 * 说明：ID生成器
 * @author eric
 * @date 2019年4月11日 下午4:28:50
 */
public class Id {

	private final static Log logger = LogFactory.getLog(Id.class);
	private final static SysDictService sysDictService = SpringContextUtils.getBean("sysDictService", SysDictService.class);
	
	private final static String biz_member = "memberbaseinfo";
	private final static String biz_worker = "worker";
	private final static String biz_salecaseproc = "salecaseproc";
	// 初始化客户和坐席ID
	private static Long initialMemberId = 146903538L;
	private static Integer initialWorkerId = 8001;
	private static Long initialProcId = 1L;
	
	private static RedisProxy redis = RedisProxy.getInstance();
	
	static{
		String maxMemberId = sysDictService.findLabel("generator", biz_member);
		if(maxMemberId != null){
			initialMemberId = CommonUtils.evalLong(maxMemberId, 146903538L); 
		}
		String maxWorkerId = sysDictService.findLabel("generator", biz_worker);
		if(maxWorkerId != null){
			initialWorkerId = CommonUtils.evalInt(maxWorkerId, 8001); 
		}
		logger.info(String.format("initial memberId: %s, workerId: %s", initialMemberId, initialWorkerId));
	}
	
	public static Long generateMemberID(){
		Long memberId = redis.generateId(biz_member, initialMemberId);
		sysDictService.update("generator", biz_member, memberId.toString());
		return memberId;
	}
	
	public static Integer generateWorkerID(){
		Number result = redis.generateId(biz_worker, initialWorkerId);
		sysDictService.update("generator", biz_worker, result.toString());
		return result.intValue();
	}
	
	public static Long generateSaleProcID(){
		Long procId = redis.generateId(biz_salecaseproc, initialProcId);
		sysDictService.update("generator", biz_salecaseproc, procId.toString());
		return procId;
	}
	
}
