package com.ctr.crm.moduls.record.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ctr.crm.moduls.hrm.models.Dept;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.service.DeptService;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import com.ctr.crm.moduls.record.models.CallRecord;
import com.ctr.crm.moduls.record.service.CallStatService;
import com.ctr.crm.moduls.sales.models.SaleCaseProc;
import com.ctr.crm.moduls.sales.service.SaleCaseProcService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.ctr.crm.commons.jdbc.YunhuJdbcOperations;
import com.ctr.crm.commons.rbac.AccessAuth;
import com.ctr.crm.commons.utils.Arith;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.PojoUtils;
import com.ctr.crm.moduls.purview.models.DataRange;
import com.ctr.crm.moduls.record.service.RecordService;

/**
 * 
 * 话务员工作量统计
 */
@Service("callStatService")
public class CallStatServiceImpl implements CallStatService {

	@Resource
	private YunhuJdbcOperations crmJdbc;
	@Resource
	private WorkerService workerService;
	@Resource
	private DeptService deptService;
	@Resource
	private RecordService recordService;
	@Resource
	private SaleCaseProcService saleCaseProcService;
	
	@Override
	public void updateCallStat(CallRecord callRecord, boolean lostCall) {
		// 更新小记
		handlerSaleCaseProc(callRecord);
		// 联系量统计
		//int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		//Worker worker = workerService.select(callRecord.getWorkerId(), callRecord.getCompanyId());
		//contactStatService.updateContactStat(hour, worker);
		// 更新话务统计数据
		if (callRecord.getCallType() == 0) {
			updateCallOutStat(callRecord.getWorkerId(), callRecord.getCompanyId(),
					callRecord.getCallTime(), callRecord.getStartTime(), lostCall);
		} else if (callRecord.getCallType() == 1) {
			updateCallInStat(callRecord.getWorkerId(), callRecord.getCompanyId(),
					callRecord.getCallTime(), callRecord.getStartTime(), lostCall);
		}
	}
	
	private void handlerSaleCaseProc(CallRecord callRecord) {
		// 如果客户ID、小记ID、应答时间有一个为空，则认为无效录音，不予记录录音小记
		if (null == callRecord || callRecord.getMemberId() == null
				|| StringUtils.isBlank(callRecord.getCustomuuid())
				|| callRecord.getAnswerTime() == null)
			return;
		Long procId = CommonUtils.evalLong(callRecord.getCustomuuid());
		if(procId == null || procId <= 0) return;
		SaleCaseProc saleCaseProc = saleCaseProcService.select(procId);
		if(saleCaseProc != null){
			SaleCaseProc caseProc = new SaleCaseProc();
			caseProc.setProcId(procId);
			caseProc.setTelStart(callRecord.getAnswerTime());
			caseProc.setTelEnd(callRecord.getHangupTime());
			saleCaseProcService.save(caseProc, null);
			return;
		}
		saleCaseProc = new SaleCaseProc();
		saleCaseProc.setProcId(procId);
		saleCaseProc.setMemberId(callRecord.getMemberId()); // 客户Id
		saleCaseProc.setWorkerId(callRecord.getWorkerId()); // 员工工号
		saleCaseProc.setProcTime(callRecord.getAnswerTime()); // 处理时间
		saleCaseProc.setTelStart(callRecord.getAnswerTime());
		saleCaseProc.setTelEnd(callRecord.getHangupTime());
		saleCaseProc.setCaseClass(callRecord.getCaseClass());
		saleCaseProcService.save(saleCaseProc, null);
	}
	
	private void updateCallOutStat(Integer workerId, String companyId,
			int linkTime, Date startTime, boolean lostCall) {
		if(workerId == null) return ;
		if(lostCall){
			//更新呼出未接通数据
			updateCallNum(workerId, companyId, true, startTime);
			return ;
		}
		String sdate = CommonUtils.formateDateToStr(startTime);
		int hour = CommonUtils.getHour(startTime);
		String sql = "insert into CallReport(writeDate,writeHour,workerId,companyId,callOutNum,callOutAllNum,callOutTimeCount,callNum,callAllNum,callTimeCount) " +
				"values(?,?,?,?,1,1,?,1,1,?) " +
				"on duplicate key update callOutNum=callOutNum+1,callOutAllNum=callOutAllNum+1,callOutTimeCount=callOutTimeCount+?,callNum=callNum+1,callAllNum=callAllNum+1,callTimeCount=callTimeCount+?";
		crmJdbc.update(sql, sdate, hour, workerId, companyId, linkTime,linkTime, linkTime, linkTime);
	}
	
	private void updateCallInStat(Integer workerId, String companyId,int linkTime,
			Date startTime, boolean lostCall) {
		if(workerId == null || startTime == null) return ;
		if(lostCall){
			//更新呼入未接通数据
			updateCallNum(workerId, companyId, false, startTime);
			return ;
		}
		String sdate = CommonUtils.formateDateToStr(startTime);
		int hour = CommonUtils.getHour(startTime);
		String sql = "insert into CallReport(writeDate,writeHour,workerId,companyId,callInNum,callInAllNum,callInTimeCount,callNum,callAllNum,callTimeCount) " +
				"values(?,?,?,?,1,1,?,1,1,?) " +
				"on duplicate key update callInNum=callInNum+1,callInAllNum=callInAllNum+1,callInTimeCount=callInTimeCount+?,callNum=callNum+1,callAllNum=callAllNum+1,callTimeCount=callTimeCount+?";
		crmJdbc.update(sql, sdate, hour, workerId, companyId, linkTime,linkTime, startTime, linkTime, linkTime);
	}
	
	private void updateCallNum(Integer workerId, String companyId, boolean callOut, Date startTime) {
		String sdate = CommonUtils.formateDateToStr(startTime);
		int hour = CommonUtils.getHour(startTime);
		if(workerId == null) return ;
		String sql = null;
		if(callOut){
			sql = "insert into CallReport(writeDate,writeHour,workerId,companyId,callOutAllNum,callAllNum) " +
					"values(?,?,?,?,1,1) " +
					"on duplicate key update callOutAllNum=callOutAllNum+1,callAllNum=callAllNum+1;";
		}else{
			sql = "insert into CallReport(writeDate,writeHour,workerId,companyId,callInAllNum,callAllNum) " +
					"values(?,?,?,?,1,1) " +
					"on duplicate key update callInAllNum=callInAllNum+1,callAllNum=callAllNum+1;";
		}
		crmJdbc.update(sql,sdate, hour, workerId, companyId);
	}
	
	/**
	 * 
	 * 方法说明：话务员工作量统计
	 * @param pageHolder
	 * @param param
	 * @param totalResult
	 * @throws Exception
	 */
	@Override
	public List<Map<String, Object>> query(Map<String, String> condition, Worker currentWorker) throws Exception{
		
		StringBuilder sql = new StringBuilder("select s.writeDate,s.workerId,w.workerName,d.deptId,"
				+ "sum(s.callOutNum) callOutNum,sum(s.callOutAllNum) callOutAllNum,sum(s.callOutNum)/sum(s.callOutAllNum) callOutRate,sum(s.callOutTimeCount) callOutTimeCount,"
				+ "SUM(s.callOutTimeCount)/SUM(s.callOutNum) callOutTimeCountRate,"
				+ "sum(s.callInNum) callInNum,sum(s.callInAllNum) callInAllNum,sum(s.callInNum)/sum(s.callInAllNum) callInRate,sum(s.callInTimeCount) callInTimeCount,"
				+ "sum(s.callNum) callNum,sum(s.callAllNum) callAllNum,sum(s.callNum)/sum(s.callAllNum) callRate,sum(s.callTimeCount) callTimeCount "
				+ "from CallReport s "
				+ "left join Worker w on s.workerId=w.workerId "
				+ "left join Dept d on w.deptId=d.deptId "
				+ "where 1=1"); 
		Map<String, Object> params = new HashMap<String, Object>();
		if(!condition.isEmpty()){
			if(StringUtils.isNotBlank(condition.get("writeDate"))){
				String[] writeDateRange = StringUtils.split(condition.get("writeDate"), "~");
				if(writeDateRange.length>0 && writeDateRange[0]!=null)
					sql.append(" and s.writeDate>='").append(writeDateRange[0].trim()).append("'");
				if(writeDateRange.length>1 && writeDateRange[1]!=null)
					sql.append(" and s.writeDate<'").append(writeDateRange[1].trim()).append("' + interval 1 day");
			}
			if(StringUtils.isNotBlank(condition.get("workerId"))) {
				sql.append(" and s.workerId=?");
				params.put("workerId", condition.get("workerId"));
			}
			int deptId = CommonUtils.evalInt(condition.get("deptId"), 0);
			if(deptId > 0) {
				Dept dept = deptService.select(deptId);
				sql.append(" and d.structure like '" + dept.getStructure()+"%'");
			}
		}
		DataRange range = AccessAuth.rangeAuth(null, currentWorker);
		if (range != null) {
			if(range.getWorkerId() != null){
				sql.append(" and w.workerId").append(range.getSymbol()).append(range.getWorkerId());
			}else if(range.getStructure() != null){
				sql.append(" and d.structure ").append(range.getSymbol()).append(" '").append(range.getStructure()).append("'");
			}
		}
		sql.append(" group by s.workerId");
		sql.append(" order by callOutAllNum desc");
		List<Map<String, Object>> result = crmJdbc.queryForList(sql.toString(), params.values().toArray());
		if (result != null && !result.isEmpty()) {
			for (Map<String, Object> o : result) {
				Integer deptId = null;
				deptId = CommonUtils.evalInteger(o.get("deptId"));
				if (null != deptId) {
					o.put("deptName", deptService.getDeptName(deptId));
				}
				Number callOutTimeCount = CommonUtils.evalNumber(o.get("callOutTimeCount"));
				o.put("callOutTimeCount", CommonUtils.secToTime(callOutTimeCount));
				Number callInTimeCount = CommonUtils.evalNumber(o.get("callInTimeCount"));
				o.put("callInTimeCount", CommonUtils.secToTime(callInTimeCount));
				Number callTimeCount = CommonUtils.evalNumber(o.get("callTimeCount"));
				o.put("callTimeCount", CommonUtils.secToTime(callTimeCount));
				Number callOutTimeCountRate = CommonUtils.evalNumber(o.get("callOutTimeCountRate"));
				if(callOutTimeCountRate==null) {
					callOutTimeCountRate=0.0;
				}
				o.put("callOutTimeCountRate", CommonUtils.secToTime(callOutTimeCountRate));
			}
		}
		return result;
	}
	
	@Override
	public Map<String, Object> monthCallStat(Worker currentWorker, int callType) {
		StringBuilder clause = new StringBuilder();
		DataRange range = AccessAuth.rangeAuth(null, currentWorker);
		if (range != null) {
			if(range.getWorkerId() != null){
				clause.append(" and w.workerId").append(range.getSymbol()).append(range.getWorkerId());
			}else if(range.getStructure() != null){
				clause.append(" and d.structure ").append(range.getSymbol()).append(" '").append(range.getStructure()).append("'");
			}
		}
		clause.append(" and ws.writeDate>='").append(CommonUtils.firstDayOfMonthToStr(new Date())).append("'");
		clause.append(" and ws.writeDate<'").append(CommonUtils.firstDayOfAnyMonth(1)).append("'");
		String sql = "select dayofmonth(ws.writeDate) day,"
				   + "sum(ws.callNum) callNum," //通话总次数
				   + "sum(ws.callTimeCount) callTimes,"
				   + "sum(ws.callOutNum) callOutNum," //通话呼出次数
				   + "sum(ws.callOutTimeCount) callOutTimes,"
				   + "sum(ws.callInNum) callInNum,"//通话呼入次数
				   + "sum(ws.callInTimeCount) callInTimes "
				   + "from Worker w left join CallReport ws on ws.workerId=w.workerId "
				   + "left join Dept d on w.deptId=d.deptId "
				   + "where 1=1 "+ clause.toString() 
				   + " group by day";
		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> data = crmJdbc.queryForList(sql);
		int monthDays = CommonUtils.getMonthDays(new Date());
		result.put("callNums", dealMonthCallNum(data, monthDays, callType));
		result.put("callTimes", dealMonthCallTimes(data, monthDays, callType));
		return result;
	}
	
	private List<Number> dealMonthCallNum(List<Map<String, Object>> data, int monthDays, int callType){
		String mapKey = callType == -1 ? "callNum" : (callType == 0 ? "callOutNum" : "callInNum");
		String[] days = PojoUtils.getArrayPropertis(data, "day", null, null);
		List<Number> result = new ArrayList<>();
		for (int i = 1; i <= monthDays; i++) {
			if(!ArrayUtils.contains(days, i+"")){
				result.add(0);
				continue;
			}
			for (Map<String, Object> map : data) {
				int day = CommonUtils.evalInt(map.get("day"));
				if(day == i){
					result.add(CommonUtils.evalInt(map.get(mapKey), 0));
					break;
				}
			}
		}
		return result;
	}
	
	private List<Double> dealMonthCallTimes(List<Map<String, Object>> data, int monthDays, int callType){
		String mapKey = callType == -1 ? "callTimes" : (callType == 0 ? "callOutTimes" : "callInTimes");
		String[] days = PojoUtils.getArrayPropertis(data, "day", null, null);
		List<Double> result = new ArrayList<>();
		for (int i = 1; i <= monthDays; i++) {
			if(!ArrayUtils.contains(days, i+"")){
				result.add(0.00);
				continue;
			}
			for (Map<String, Object> map : data) {
				int day = CommonUtils.evalInt(map.get("day"));
				if(day == i){
					result.add(Arith.div((Double)map.get(mapKey), 3600, 2));
					break;
				}
			}
		}
		return result;
	}
	
	@Override
	public Map<String, Object> todayCallStat(Worker currentWorker) {
		StringBuilder clause = new StringBuilder();
		DataRange range = AccessAuth.rangeAuth(null, currentWorker);
		if (range != null) {
			if(range.getWorkerId() != null){
				clause.append(" and w.workerId").append(range.getSymbol()).append(range.getWorkerId());
			}else if(range.getStructure() != null){
				clause.append(" and d.structure ").append(range.getSymbol()).append(" '").append(range.getStructure()).append("'");
			}
		}
		clause.append(" and ws.writeDate>='").append(CommonUtils.todayToStr()).append("'");
		clause.append(" and ws.writeDate<'").append(CommonUtils.tomorrowToStr()).append("'");
		String sql = "select sum(ws.callInAllNum) callInAllNum,"
				   + "sum(ws.callInNum) callInNum,"
				   + "sum(ws.callInTimeCount) callInTimes,"
				   + "sum(ws.callOutAllNum) callOutAllNum,"
				   + "sum(ws.callOutNum) callOutNum,"
				   + "sum(ws.callOutTimeCount) callOutTimes "
				   + "from Worker w left join CallReport ws on ws.workerId=w.workerId "
				   + "left join Dept d on w.deptId=d.deptId "
				   + "where 1=1 "+ clause.toString();
		Map<String, Object> result;
		try {
			result = crmJdbc.queryForMap(sql);
			DecimalFormat df = new DecimalFormat("0.00%");
			int callInAllNum = CommonUtils.evalInt(result.get("callInAllNum"), 0);
			int callInNum = CommonUtils.evalInt(result.get("callInNum"), 0);
			int callInTimes = CommonUtils.evalInt(result.get("callInTimes"), 0);
			int callOutAllNum = CommonUtils.evalInt(result.get("callOutAllNum"), 0);
			int callOutNum = CommonUtils.evalInt(result.get("callOutNum"), 0);
			int callOutTimes = CommonUtils.evalInt(result.get("callOutTimes"), 0);
			result.put("callInTimes", CommonUtils.secToTime(callInTimes));
			result.put("callInRate", df.format(Arith.div(callInNum, callInAllNum, 4)));
			result.put("callInAvgTimes", CommonUtils.secToTime(Arith.div(callInTimes, callInNum)));
			result.put("callOutTimes", CommonUtils.secToTime(callOutTimes));
			result.put("callOutRate", df.format(Arith.div(callOutNum, callOutAllNum, 4)));
			result.put("callOutAvgTimes", CommonUtils.secToTime(Arith.div(callOutTimes, callOutNum)));
			return result;
		} catch (EmptyResultDataAccessException e) {
		}
		return new HashMap<String, Object>();
	}

}
