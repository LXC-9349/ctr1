package com.ctr.crm.moduls.sales.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;

import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.PojoUtils;
import com.ctr.crm.moduls.purview.models.DataRange;
import com.ctr.crm.moduls.sales.models.SaleCase;
import com.ctr.crm.moduls.sales.models.MySearch;

/**
 * 说明：
 * @author eric
 * @date 2019年5月6日 下午1:54:49
 */
public interface SaleCaseDao {

	@Select("select count(0) from SaleCase where memberId=#{memberId}")
	boolean inSaleCase(Long memberId);
	
	@UpdateProvider(type=SaleCaseSqlBuilder.class, method="buildUpdateSql")
	boolean update(SaleCase saleCase);

	@Select("select count(0) from SaleCase where memberId in (${memberIds})")
    Integer inSaleCaseMembers(@Param("memberIds") String memberIds);

	@SelectProvider(type=SaleCaseSqlBuilder.class, method="buildSelectSql")
    SaleCase getByMemberId(Long memberId);
	
	@SelectProvider(type=SaleCaseSqlBuilder.class, method="buildSearchSql")
	List<Map<String, Object>> search(MySearch search);

	@Select("select * from SaleCase where mobile = #{mobile} limit 1")
    SaleCase getByMobile(@Param("mobile") String mobile);

    public class SaleCaseSqlBuilder {
		
		public String buildSelectSql(Long memberId){
			return new SQL(){
				{
					SELECT(PojoUtils.getArrayFields(SaleCase.class, "serialVersionUID"));
					FROM(SaleCase.class.getSimpleName());
					WHERE("memberId=#{memberId}");
				}
			}.toString();
		}
		
		public String buildUpdateSql(SaleCase saleCase){
			return new SQL(){
				{
					UPDATE(SaleCase.class.getSimpleName());
					SET(PojoUtils.getUpdateSets(saleCase, false));
					WHERE("memberId=#{memberId}");
				}
			}.toString();
		}
		
		public String buildSearchSql(MySearch search){
			StringBuilder sqlBuilder = new StringBuilder(
					PojoUtils.getSelectSqlOfFields(SaleCase.class, "sc", "serialVersionUID,workerName,workerDept"));
			sqlBuilder.append(",w.workerName");
			if(search.getCaseType() == 2){
				sqlBuilder.append(",vip.beginTime,vip.endTime,vip.alrCount,co.serveCount");
			}
			sqlBuilder.append(" from SaleCase sc");
			sqlBuilder.append(" left join Worker w on sc.workerId=w.workerId");
			sqlBuilder.append(" left join Dept d on w.deptId=d.deptId");
			if(search.getCaseType() == 2){
				sqlBuilder.append(" left join MemberVip vip on sc.memberId=vip.memberId and vip.id in (select substring_index(group_concat(id order by `status` asc,realEndTime desc),',',1) from `MemberVip` group by memberId )  ");
				sqlBuilder.append(" left join ContractOrder co on vip.contractId=co.id");
			}
			sqlBuilder.append(" where 1=1");
			if (search != null) {
				if(search.getMemberId() != null){
					sqlBuilder.append(" and sc.memberId=#{memberId}");
				}
				if(StringUtils.isNotBlank(search.getNickName())){
					sqlBuilder.append(" and sc.nickName like concat('%',#{nickName},'%')");
				}
				if(StringUtils.isNotBlank(search.getTrueName())){
					sqlBuilder.append(" and sc.trueName like concat('%',#{trueName},'%')");
				}
				if(StringUtils.isNotBlank(search.getMobile())){
					sqlBuilder.append(" and exists(select 1 from MemberPhone where memberId=sc.memberId and phone=#{mobile})");
				}
				if(StringUtils.isNotBlank(search.getWechatId())){
					sqlBuilder.append(" and sc.wechatId like concat('%',#{wechatId},'%')");
				}
				if(search.getCaseType() != null){
					sqlBuilder.append(" and sc.caseClass in(select caseClass from Intentionality where type="+search.getCaseType()+")");
				}
				if(search.getWorkerId() != null && search.getWorkerId() > 0){
					sqlBuilder.append(" and sc.workerId=#{workerId}");
				}
				if(search.getDeptId() != null && search.getSelectDept() != null){
					sqlBuilder.append(" and d.structure like '"+search.getSelectDept().getStructure()+"%'");
				}
				if(search.getAgeB() != null){
					sqlBuilder.append(" and sc.birthday<'").append(CommonUtils.getBirthdayByAge(search.getAgeB())).append("' + interval 1 year");
				}
				if(search.getAgeE() != null){
					sqlBuilder.append(" and sc.birthday>='").append(CommonUtils.getBirthdayByAge(search.getAgeE())).append("'");
				}
				if(StringUtils.isNotBlank(search.getCreateTime())){
					String[] createTimeRange = StringUtils.split(search.getCreateTime(), "~");
					if(createTimeRange.length>0 && createTimeRange[0]!=null)
					sqlBuilder.append(" and sc.createTime>='").append(createTimeRange[0]).append("'");
					if(createTimeRange.length>1 && createTimeRange[1]!=null)
					sqlBuilder.append(" and sc.createTime<'").append(createTimeRange[1]).append("' + interval 1 day");
				}
				if(search.getSex() != null){
					sqlBuilder.append(" and sc.sex=#{sex}");
				}
				if(search.getHeightB() != null){
					sqlBuilder.append(" and sc.height>=#{heightB}");
				}
				if(search.getHeightE() != null){
					sqlBuilder.append(" and sc.height<=#{heightE}");
				}
				/*if(search.getWeightB() != null){
					sqlBuilder.append(" and sc.weight>=#{weightB}");
				}
				if(search.getWeightE() != null){
					sqlBuilder.append(" and sc.weight<=#{weightE}");
				}*/
				if(search.getSalary() != null){
					sqlBuilder.append(" and sc.salary in("+StringUtils.join(search.getSalary(), ",")+")");
				}
				if(search.getEducation() != null){
					sqlBuilder.append(" and sc.education in("+StringUtils.join(search.getEducation(), ",")+")");
				}
				if(search.getMarriage() != null){
					sqlBuilder.append(" and sc.marriage in("+StringUtils.join(search.getMarriage(), ",")+")");
				}
				if(StringUtils.isNotBlank(search.getWorkCity())){
					sqlBuilder.append(" and sc.workCity like concat('%',#{workCity},'%')");
				}
				if(StringUtils.isNotBlank(search.getHomeTown())){
					sqlBuilder.append(" and sc.homeTown like concat('%',#{homeTown},'%')");
				}
				if(search.getHouse() != null){
					sqlBuilder.append(" and sc.house=#{house}");
				}
				if(search.getCar() != null){
					sqlBuilder.append(" and sc.car=#{car}");
				}
				if(StringUtils.isNotBlank(search.getField1())){
					sqlBuilder.append(" and sc.field1 like concat('%',#{field1},'%')");
				}
				if(StringUtils.isNotBlank(search.getAllotTime())){
					String[] allotTimeRange = StringUtils.split(search.getAllotTime(), "~");
					if(allotTimeRange.length>0 && allotTimeRange[0]!=null)
					sqlBuilder.append(" and sc.allotTime>='").append(allotTimeRange[0]).append("'");
					if(allotTimeRange.length>1 && allotTimeRange[1]!=null)
					sqlBuilder.append(" and sc.allotTime<'").append(allotTimeRange[1]).append("' + interval 1 day");
				}
				if(StringUtils.isNotBlank(search.getLastContactTime())){
					String[] lastContactTimeRange = StringUtils.split(search.getLastContactTime(), "~");
					if(lastContactTimeRange.length>0 && lastContactTimeRange[0]!=null)
						sqlBuilder.append(" and sc.lastContactTime>='").append(lastContactTimeRange[0]).append("'");
					if(lastContactTimeRange.length>1 && lastContactTimeRange[1]!=null)
						sqlBuilder.append(" and sc.lastContactTime<'").append(lastContactTimeRange[1]).append("' + interval 1 day");
				}
				if(StringUtils.isNotBlank(search.getNextContactTime())){
					String[] nextContactTimeRange = StringUtils.split(search.getNextContactTime(), "~");
					if(nextContactTimeRange.length>0 && nextContactTimeRange[0]!=null)
						sqlBuilder.append(" and sc.nextContactTime>='").append(nextContactTimeRange[0]).append("'");
					if(nextContactTimeRange.length>1 && nextContactTimeRange[1]!=null)
						sqlBuilder.append(" and sc.nextContactTime<'").append(nextContactTimeRange[1]).append("' + interval 1 day");
				}
				if(search.getCaseClass() != null){
					sqlBuilder.append(" and sc.caseClass=#{caseClass}");
				}
				if(search.getIntensive() != null){
					sqlBuilder.append(" and sc.intensive=#{intensive}");
				}
				// 数据范围条件
				DataRange range = search.getRange();
				if (range != null) {
					if(range.getWorkerId() != null){
						sqlBuilder.append(" and sc.workerId").append(range.getSymbol()).append(range.getWorkerId());
					}else if(range.getStructure() != null){
						sqlBuilder.append(" and d.structure ").append(range.getSymbol()).append(" '").append(range.getStructure()).append("'");
					}
				}
				if(search.getScene() != null){
					if(search.getScene() == 1){
						// 今日入库
						sqlBuilder.append(" and sc.allotTime>='").append(CommonUtils.todayToStr()).append("'");
						sqlBuilder.append(" and sc.allotTime<'").append(CommonUtils.tomorrowToStr()).append("'");
					}else if(search.getScene() == 2){
						// 今日联系
						sqlBuilder.append(" and sc.nextContactTime>='").append(CommonUtils.todayToStr()).append("'");
						sqlBuilder.append(" and sc.nextContactTime<'").append(CommonUtils.tomorrowToStr()).append("'");
					}
				}
				if(search.getNotFollowedDays() != null){
					String date = CommonUtils.anyDayBeforeToStr(search.getNotFollowedDays());
					sqlBuilder.append(" and (sc.lastContactTime is null or sc.lastContactTime<'").append(date).append("')");
				}
			}
			return sqlBuilder.toString();
		}
	}
}
