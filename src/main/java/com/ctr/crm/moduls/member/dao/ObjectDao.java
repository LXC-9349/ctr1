package com.ctr.crm.moduls.member.dao;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;

import com.ctr.crm.commons.utils.PojoUtils;
import com.ctr.crm.moduls.member.models.MemberObjectInfo;

/**
 * 说明：
 * @author eric
 * @date 2019年5月6日 上午10:26:15
 */
public interface ObjectDao {

	@InsertProvider(type=ObjectSqlBuilder.class, method="buildInsertSql")
	boolean insert(MemberObjectInfo objectInfo);
	@UpdateProvider(type=ObjectSqlBuilder.class, method="buildUpdateSql")
	boolean update(MemberObjectInfo objectInfo);
	@SelectProvider(type=ObjectSqlBuilder.class, method="buildSelectSql")
	MemberObjectInfo select(Long memberId);
	
	public class ObjectSqlBuilder{
		
		public String buildInsertSql(MemberObjectInfo objectInfo){
			return new SQL(){
				{
					INSERT_INTO("MemberObjectInfo");
					INTO_COLUMNS(PojoUtils.getArrayFields(objectInfo, "serialVersionUID", false));
					INTO_VALUES(PojoUtils.getArrayFields(objectInfo, "serialVersionUID", true));
				}
			}.toString();
		}
		
		public String buildUpdateSql(MemberObjectInfo objectInfo){
			return new SQL(){
				{
					UPDATE("MemberObjectInfo");
					SET(PojoUtils.getUpdateSets(objectInfo, true));
					WHERE("memberId=#{memberId}");
				}
			}.toString();
		}
		
		public String buildSelectSql(Long memberId){
			return new SQL(){
				{
					SELECT(PojoUtils.getArrayFields(MemberObjectInfo.class, "serialVersionUID"));
					FROM(MemberObjectInfo.class.getSimpleName());
					WHERE("memberId=#{memberId}");
				}
			}.toString();
		}
	}
}
