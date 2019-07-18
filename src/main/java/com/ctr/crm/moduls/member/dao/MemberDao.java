package com.ctr.crm.moduls.member.dao;

import com.ctr.crm.moduls.tag.models.Tag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;

import com.ctr.crm.commons.utils.PojoUtils;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;

import java.util.List;

/**
 * 说明：
 * @author eric
 * @date 2019年4月11日 下午4:32:23
 */
public interface MemberDao {

	@Select("select max(memberId) from MemberBaseInfo")
	Long getMaxMemberId();

	@InsertProvider(type=MemberSqlBuilder.class, method="buildInsertSql")
	boolean insert(MemberBaseInfo baseInfo);
	@UpdateProvider(type=MemberSqlBuilder.class, method="buildUpdateSql")
	boolean update(MemberBaseInfo baseInfo);
	@SelectProvider(type=MemberSqlBuilder.class, method="buildSelectSql")
	MemberBaseInfo select(Long memberId);
	//void updateMemberType()

	@Select("select * from MemberBaseInfo where memberId in (${memberIds})")
	List<MemberBaseInfo> searchByMemberIds(@Param("memberIds") String memberIds);
	
	@Insert("insert ignore into MemberTag(memberId,tagId) values(#{memberId},#{tagId})")
	void insertMemberTag(@Param("memberId") Long memberId, @Param("tagId") String tagId);
	@Delete("delete from MemberTag where memberId=#{memberId} and tagId=#{tagId}")
	void deleteMemberTag(@Param("memberId") Long memberId, @Param("tagId") String tagId);
	@Select("select tag.tagName,tag.color,tag.groupId,tag.id from Tag tag join MemberTag mt on tag.id=mt.tagId where mt.memberId=#{memberId} and tag.deleted=0")
	List<Tag> getMemberTag(Long memberId);

	public class MemberSqlBuilder{

		public String buildInsertSql(MemberBaseInfo baseInfo){
			return new SQL(){
				{
					INSERT_INTO("MemberBaseInfo");
					INTO_COLUMNS(PojoUtils.getArrayFields(baseInfo, "serialVersionUID", false));
					INTO_VALUES(PojoUtils.getArrayFields(baseInfo, "serialVersionUID", true));
				}
			}.toString();
		}

		public String buildUpdateSql(MemberBaseInfo baseInfo){
			return new SQL(){
				{
					UPDATE("MemberBaseInfo");
					SET(PojoUtils.getUpdateSets(baseInfo, false));
					WHERE("memberId=#{memberId}");
				}
			}.toString();
		}

		public String buildSelectSql(Long memberId){
			return new SQL(){
				{
					SELECT(PojoUtils.getArrayFields(MemberBaseInfo.class, "serialVersionUID"));
					FROM(MemberBaseInfo.class.getSimpleName());
					WHERE("memberId=#{memberId}");
				}
			}.toString();
		}
	}
}
