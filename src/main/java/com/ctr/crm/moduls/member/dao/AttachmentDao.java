package com.ctr.crm.moduls.member.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import com.ctr.crm.moduls.member.models.MemberAttachment;

/**
 * 说明：
 * @author eric
 * @date 2019年5月20日 上午11:29:52
 */
public interface AttachmentDao {

	@Insert("insert into MemberAttachment(memberId,attachmentUrl,fileName,operator) values(#{memberId},#{attachmentUrl},#{fileName},#{operator})")
	void insert(MemberAttachment attachment);
	@Delete("delete from MemberAttachment where id=#{attachmentId}")
	void delete(Integer attachmentId);
	@Select("select id,memberId,attachmentUrl,fileName,operator from MemberAttachment where id=#{id}")
	MemberAttachment selectById(Integer id);
	@Select("select id,memberId,attachmentUrl,fileName,operator from MemberAttachment where memberId=#{memberId}")
	List<MemberAttachment> select(Long memberId);
}
