package com.ctr.crm.moduls.report.service;

import java.util.List;
import java.util.Map;

import com.ctr.crm.moduls.hrm.models.Worker;

/**
 * 说明：
 * @author eric
 * @date 2019年5月29日 上午10:22:06
 */
public interface HomepageService {

	/** 今日新分 */
	Map<String, Object> todayAllot(Worker currentWorker);
	/** 今日待沟通 */
	Map<String, Object> todayCommunicate(Worker currentWorker);
	/** 逾期未联系 */
	Map<String, Object> overdueEstranged(Worker currentWorker);
	
	/** 机会客户层级分布 */
	List<Map<String, Object>> chanceHierarchy(Worker currentWorker);
	
}
