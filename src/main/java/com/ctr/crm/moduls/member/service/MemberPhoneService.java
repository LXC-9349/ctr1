package com.ctr.crm.moduls.member.service;

import java.util.List;

import com.ctr.crm.moduls.member.models.MemberPhone;

/**
 * 说明：
 * @author eric
 * @date 2019年4月17日 下午2:50:24
 */
public interface MemberPhoneService {

	boolean insert(MemberPhone memberPhone);
	boolean insert(Long memberId, String phone);

	boolean delete(Long memberId, String phone);
	
	/***
	 * 号码是否已存在<br>
	 * 当memberId不为空时，如果被判断的号码存在指定客户的号码列表中，返回false<br>
	 * 一般判断号码是否存在不传memberId参数
	 * @param phone
	 * @param memberId
	 * @return
	 */
	boolean existsPhone(String phone, Long memberId);

	List<MemberPhone> getMemberPhoneList(Long memberId);

	List<MemberPhone> getMemberPhoneList(String phone);

	Long getMemberIdByPhone(String phone);

	/**
	 * 通过号码获取对应客户ID<br>
	 * 如果allotNotIn为true，表示优先获取在库的，不在库则取一个；如为false，只获取在库的，不在库返回空
	 * @param phone
	 * @param allowNotIn
	 * @return
	 */
	Long getMemberIdInSaleCaseByPhone(String phone, boolean allowNotIn);
	/**
	 * 通过号码获取在库的客户ID
	 * @param phone
	 * @return
	 */
	Long getMemberIdInSaleCaseByPhone(String phone);

	boolean hasSamePhoneMemberInSaleCase(Long memberId);

}
