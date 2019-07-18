package com.ctr.crm.moduls.sales.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;

import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.PojoUtils;
import com.ctr.crm.moduls.purview.models.DataRange;
import com.ctr.crm.moduls.sales.models.Handsome;
import com.ctr.crm.moduls.sales.models.MySearch;

/**
 * 说明：
 * @author eric
 * @date 2019年5月13日 下午5:41:47
 */
public interface HandsomeDao {

	@InsertProvider(type=HandsomeSqlBuilder.class,method="buildInsertSql")
	void insert(Handsome handsome);
	@SelectProvider(type=HandsomeSqlBuilder.class,method="buildSelectSql")
	Handsome select(Long memberId);
	
	@Delete("delete from Handsome where memberId=#{memberId}")
	void delete(Long memberId);
	
	@UpdateProvider(type=HandsomeSqlBuilder.class,method="buildUpdateSql")
	void update(Handsome handsome);
	
	@SelectProvider(type=HandsomeSqlBuilder.class, method="buildSearchSql")
	List<Map<String, Object>> search(MySearch search);
	
	public class HandsomeSqlBuilder{
		public String buildInsertSql(Handsome handsome){
			return new SQL(){
				{
					INSERT_INTO(Handsome.class.getSimpleName());
					INTO_COLUMNS(PojoUtils.getArrayFields(handsome, "id,serialVersionUID", false));
					INTO_VALUES(PojoUtils.getArrayFields(handsome, "id,serialVersionUID", true));
				}
			}.toString();
		}
		
		public String buildSelectSql(Long memberId){
			return new SQL(){
				{
					SELECT(PojoUtils.getArrayFields(Handsome.class, "serialVersionUID"));
					FROM(Handsome.class.getSimpleName());
					WHERE("memberId=#{memberId}");
				}
			}.toString();
		}
		
		public String buildUpdateSql(Handsome handsome){
			return new SQL(){
				{
					UPDATE("Handsome");
					SET(PojoUtils.getUpdateSets(handsome, false));
					WHERE("memberId=#{memberId}");
				}
			}.toString();
		}
		
		public String buildSearchSql(MySearch search){
			StringBuilder sqlBuilder = new StringBuilder(
					PojoUtils.getSelectSqlOfFields(Handsome.class, "hs", "serialVersionUID"));
			sqlBuilder.append(" from Handsome hs");
			sqlBuilder.append(" left join Worker w on hs.workerId=w.workerId");
			sqlBuilder.append(" left join Dept d on w.deptId=d.deptId");
			sqlBuilder.append(" where 1=1");
			if (search != null) {
				if(search.getMemberId() != null){
					sqlBuilder.append(" and hs.memberId=#{memberId}");
				}
				if(StringUtils.isNotBlank(search.getNickName())){
					sqlBuilder.append(" and hs.nickName like concat('%',#{nickName},'%')");
				}
				if(StringUtils.isNotBlank(search.getTrueName())){
					sqlBuilder.append(" and hs.trueName like concat('%',#{trueName},'%')");
				}
				if(StringUtils.isNotBlank(search.getMobile())){
					sqlBuilder.append(" and exists(select 1 from MemberPhone where memberId=hs.memberId and phone=#{mobile})");
				}
				if(StringUtils.isNotBlank(search.getWechatId())){
					sqlBuilder.append(" and hs.wechatId like concat('%',#{wechatId},'%')");
				}
				if(search.getWorkerId() != null && search.getWorkerId() > 0){
					sqlBuilder.append(" and hs.workerId=#{workerId}");
				}
				if(search.getDeptId() != null && search.getSelectDept() != null){
					sqlBuilder.append(" and d.structure like '"+search.getSelectDept().getStructure()+"%'");
				}
				if(search.getAgeB() != null){
					sqlBuilder.append(" and hs.birthday<'").append(CommonUtils.getBirthdayByAge(search.getAgeB())).append("' + interval 1 year");
				}
				if(search.getAgeE() != null){
					sqlBuilder.append(" and hs.birthday>='").append(CommonUtils.getBirthdayByAge(search.getAgeE())).append("'");
				}
				if(StringUtils.isNotBlank(search.getCreateTime())){
					String[] createTimeRange = StringUtils.split(search.getCreateTime(), "~");
					if(createTimeRange.length>0 && createTimeRange[0]!=null)
					sqlBuilder.append(" and hs.createTime>='").append(createTimeRange[0]).append("'");
					if(createTimeRange.length>1 && createTimeRange[1]!=null)
					sqlBuilder.append(" and hs.createTime<'").append(createTimeRange[1]).append("' + interval 1 day");
				}
				if(search.getSex() != null){
					sqlBuilder.append(" and hs.sex=#{sex}");
				}
				if(search.getHeightB() != null){
					sqlBuilder.append(" and hs.height>=#{heightB}");
				}
				if(search.getHeightE() != null){
					sqlBuilder.append(" and hs.height<=#{heightE}");
				}
				if(search.getSalary() != null){
					sqlBuilder.append(" and hs.salary in("+StringUtils.join(search.getSalary(), ",")+")");
				}
				if(search.getEducation() != null){
					sqlBuilder.append(" and hs.education in("+StringUtils.join(search.getEducation(), ",")+")");
				}
				if(search.getMarriage() != null){
					sqlBuilder.append(" and hs.marriage in("+StringUtils.join(search.getMarriage(), ",")+")");
				}
				if(StringUtils.isNotBlank(search.getWorkCity())){
					sqlBuilder.append(" and hs.workCity like concat('%',#{workCity},'%')");
				}
				if(StringUtils.isNotBlank(search.getHomeTown())){
					sqlBuilder.append(" and hs.homeTown like concat('%',#{homeTown},'%')");
				}
				if(search.getHouse() != null){
					sqlBuilder.append(" and hs.house=#{house}");
				}
				if(search.getCar() != null){
					sqlBuilder.append(" and hs.car=#{car}");
				}
				if(StringUtils.isNotBlank(search.getField1())){
					sqlBuilder.append(" and hs.field1 like concat('%',#{field1},'%')");
				}
				if(StringUtils.isNotBlank(search.getAllotTime())){
					String[] allotTimeRange = StringUtils.split(search.getAllotTime(), "~");
					if(allotTimeRange.length>0 && allotTimeRange[0]!=null)
					sqlBuilder.append(" and hs.allotTime>='").append(allotTimeRange[0]).append("'");
					if(allotTimeRange.length>1 && allotTimeRange[1]!=null)
					sqlBuilder.append(" and hs.allotTime<'").append(allotTimeRange[1]).append("' + interval 1 day");
				}
				if(StringUtils.isNotBlank(search.getLastContactTime())){
					String[] lastContactTimeRange = StringUtils.split(search.getLastContactTime(), "~");
					if(lastContactTimeRange.length>0 && lastContactTimeRange[0]!=null)
						sqlBuilder.append(" and hs.lastContactTime>='").append(lastContactTimeRange[0]).append("'");
					if(lastContactTimeRange.length>1 && lastContactTimeRange[1]!=null)
						sqlBuilder.append(" and hs.lastContactTime<'").append(lastContactTimeRange[1]).append("' + interval 1 day");
				}
				if(StringUtils.isNotBlank(search.getNextContactTime())){
					String[] nextContactTimeRange = StringUtils.split(search.getNextContactTime(), "~");
					if(nextContactTimeRange.length>0 && nextContactTimeRange[0]!=null)
						sqlBuilder.append(" and hs.nextContactTime>='").append(nextContactTimeRange[0]).append("'");
					if(nextContactTimeRange.length>1 && nextContactTimeRange[1]!=null)
						sqlBuilder.append(" and hs.nextContactTime<'").append(nextContactTimeRange[1]).append("' + interval 1 day");
				}
				if(search.getCaseClass() != null){
					sqlBuilder.append(" and hs.caseClass=#{caseClass}");
				}
				// 数据范围条件
				DataRange range = search.getRange();
				if (range != null) {
					if(range.getWorkerId() != null){
						sqlBuilder.append(" and hs.workerId").append(range.getSymbol()).append(range.getWorkerId());
					}else if(range.getStructure() != null){
						sqlBuilder.append(" and d.structure ").append(range.getSymbol()).append(" '").append(range.getStructure()).append("'");
					}
				}
				if(search.getScene() != null){
					if(search.getScene() == 1){
						// 今日入库
						sqlBuilder.append(" and hs.allotTime>='").append(CommonUtils.todayToStr()).append("'");
						sqlBuilder.append(" and hs.allotTime<'").append(CommonUtils.tomorrowToStr()).append("'");
					}else if(search.getScene() == 2){
						// 今日联系
						sqlBuilder.append(" and hs.nextContactTime>='").append(CommonUtils.todayToStr()).append("'");
						sqlBuilder.append(" and hs.nextContactTime<'").append(CommonUtils.tomorrowToStr()).append("'");
					}
				}
				if(search.getNotFollowedDays() != null){
					String date = CommonUtils.anyDayBeforeToStr(search.getNotFollowedDays());
					sqlBuilder.append(" and (hs.lastContactTime is null or hs.lastContactTime<'").append(date).append("')");
				}
			}
			return sqlBuilder.toString();
		}
	}
}
