package com.ctr.crm.moduls.record.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;

import com.ctr.crm.commons.utils.PojoUtils;
import com.ctr.crm.moduls.purview.models.DataRange;
import com.ctr.crm.moduls.record.models.CallRecord;
import com.ctr.crm.moduls.record.models.RecordSearch;

/**
 * 说明：
 * @author eric
 * @date 2019年4月26日 上午10:07:19
 */
public interface RecordDao {

	@SelectProvider(type=RecordSqlBuilder.class, method="buildSearchSql")
	List<Map<String, Object>> search(RecordSearch condition);
	
	@SelectProvider(type=RecordSqlBuilder.class, method="buildSelectSql")
	CallRecord select(Integer callId);
	@SelectProvider(type=RecordSqlBuilder.class, method="buildSelectSqlByAnswerTime")
	CallRecord selectByAnswerTime(@Param("workerId")Integer workerId,@Param("answerTime")Date answerTime);
	@InsertProvider(type=RecordSqlBuilder.class, method="buildInsertSql")
	int insert(CallRecord record);
	
	public static class RecordSqlBuilder{
		public static String buildSearchSql(RecordSearch condition){
			StringBuilder sqlBuilder = new StringBuilder(
				PojoUtils.getSelectSqlOfFields(CallRecord.class, "C","serialVersionUID,recordFileName,downFileIp,callType,callTime,customuuid")
				+ ",sec_to_time(C.callTime) callTime,W.workerName,case when C.callType=0 then '呼出' else '呼入' end callType "
				+ "from CallRecord C left join Worker W on C.WorkerId=W.workerId left join Dept D on W.deptId=D.deptId");
			sqlBuilder.append(" where 1=1");
			if (condition != null) {
				if(StringUtils.isNotBlank(condition.getStartTime())){
					String[] startTimeRange = StringUtils.split(condition.getStartTime(), "~");
					if(startTimeRange.length>0 && startTimeRange[0]!=null)
						sqlBuilder.append(" and C.startTime>='").append(startTimeRange[0]).append("'");
					if(startTimeRange.length>1 && startTimeRange[1]!=null)
						sqlBuilder.append(" and C.startTime<'").append(startTimeRange[1]).append("' + interval 1 day");
				}
				// 客户ID
				if (condition.getMemberId() != null) {
					sqlBuilder.append(" and C.memberId=#{memberId}");
				}
				// 录音编号
				if (condition.getCallId() != null) {
					sqlBuilder.append(" and C.callId=#{callId}");
				}
				// 录音编号
				if (condition.getLineNum() != null) {
					sqlBuilder.append(" and C.lineNum=#{lineNum}");
				}
				// 客户号码
				if (StringUtils.isNotBlank(condition.getPhone())) {
					sqlBuilder.append(" and C.phone=#{phone}");
				}
				// 线路号码
				if (StringUtils.isNotBlank(condition.getTelephone())) {
					sqlBuilder.append(" and C.telephone=#{telephone}");
				}
				// 通话类型
				if (condition.getCallType() != null && condition.getCallType() >= 0) {
					sqlBuilder.append(" and C.callType=#{callType}");
				}
				// 员工工号
				if (condition.getWorkerId() != null && condition.getWorkerId() > 0) {
					sqlBuilder.append(" and C.workerId=#{workerId}");
				}
				// 数据范围条件
				DataRange range = condition.getRange();
				if (range != null) {
					if(range.getWorkerId() != null){
						sqlBuilder.append(" and C.workerId").append(range.getSymbol()).append(range.getWorkerId());
					}else if(range.getStructure() != null){
						sqlBuilder.append(" and D.structure ").append(range.getSymbol()).append(" '").append(range.getStructure()).append("'");
					}
				}
				if (condition.getThrough() != null && condition.getThrough() >= 0) {
					// 1接通 0未接通
					if (condition.getThrough() == 1) {
						sqlBuilder.append(" and C.callTime>0");
					} else if (condition.getThrough() == 0) {
						sqlBuilder.append(" and C.callTime=0");
					}
				}
			}
			return sqlBuilder.toString();
		}
		
		public static String buildSelectSql(Integer callId){
			return new SQL(){
				{
					SELECT(PojoUtils.getArrayFields(CallRecord.class, "serialVersionUID","customuuid"));
					FROM(CallRecord.class.getSimpleName());
					WHERE("callId=#{callId}");
				}
			}.toString();
		}
		
		public static String buildSelectSqlByAnswerTime(Integer workerId, Date answerTime){
			return new SQL(){
				{
					SELECT(PojoUtils.getArrayFields(CallRecord.class, "serialVersionUID","customuuid"));
					FROM(CallRecord.class.getSimpleName());
					WHERE("workerId=#{workerId}","answerTime=#{answerTime}");
				}
			}.toString();
		}
		
		public static String buildInsertSql(CallRecord record){
			return new SQL(){
				{
					INSERT_INTO(CallRecord.class.getSimpleName());
					INTO_COLUMNS(PojoUtils.getArrayFields(record, "serialVersionUID,customuuid", false));
					INTO_VALUES(PojoUtils.getArrayFields(record, "serialVersionUID,customuuid", true));
				}
			}.toString();
		}
	}
}
