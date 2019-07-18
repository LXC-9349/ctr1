package com.ctr.crm.moduls.member.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.jdbc.SQL;

import com.ctr.crm.commons.utils.PojoUtils;
import com.ctr.crm.moduls.member.models.MemberPhone;

/**
 * 说明：
 * @author eric
 * @date 2019年5月6日 下午1:59:24
 */
public interface PhoneDao {

	@InsertProvider(type=PhoneSqlBuilder.class, method="buildInsertSql")
	boolean insert(MemberPhone memberPhone) throws Exception;
	@Delete("delete from MemberPhone where memberId=#{memberId} and phone=#{phone}")
	boolean delete(@Param("memberId")Long memberId, @Param("phone")String phone);
	@Select("select count(0) from MemberPhone where memberId=#{memberId} and phone=#{phone}")
	boolean exists(@Param("memberId")Long memberId, @Param("phone")String phone);
	@Select("select count(0) from MemberPhone where phone=#{phone}")
	int count(@Param("phone")String phone);
	@Select("select id,memberId,phone,linkman from MemberPhone where memberId=#{memberId}")
	public List<MemberPhone> getListByMemberId(Long memberId);
	@Select("select id,memberId,phone,linkman from MemberPhone where phone=#{phone}")
	public List<MemberPhone> getListByPhone(String phone);
	@Select("select id,memberId,phone,linkman from MemberPhone where phone=#{phone}")
	public MemberPhone getPhone(String phone);
	
	public class PhoneSqlBuilder{
		public String buildInsertSql(MemberPhone memberPhone){
			return new SQL(){
				{
					INSERT_INTO(MemberPhone.class.getSimpleName());
					INTO_COLUMNS(PojoUtils.getArrayFields(memberPhone, "serialVersionUID", false));
					INTO_VALUES(PojoUtils.getArrayFields(memberPhone, "serialVersionUID", true));
				}
			}.toString();
		}
	}
}
