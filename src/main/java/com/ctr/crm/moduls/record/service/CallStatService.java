package com.ctr.crm.moduls.record.service;

import java.util.List;
import java.util.Map;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.record.models.CallRecord;

/**
 * 说明：
 * @author eric
 * @date 2019年4月17日 下午3:32:49
 */
public interface CallStatService {

	void updateCallStat(CallRecord callRecord, boolean lostCall);

	List<Map<String, Object>> query(Map<String, String> condition,
			Worker currentWorker) throws Exception;

	/**
	 * 本月话务统计<br>
	 * 时间默认当月
	 * @param callType 录音类型(-1全部 0呼出 1呼入)
	 * @param currentWorker
	 * @return
	 */
	Map<String, Object> monthCallStat(Worker currentWorker, int callType);

	/**
	 * 今日话务统计
	 * @param condition 时间默认当天
	 * @param currentWorker
	 * @return
	 */
	Map<String, Object> todayCallStat(Worker currentWorker);

}
