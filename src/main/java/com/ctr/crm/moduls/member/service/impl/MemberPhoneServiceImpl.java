package com.ctr.crm.moduls.member.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.ctr.crm.moduls.member.dao.PhoneDao;
import com.ctr.crm.moduls.member.models.MemberPhone;
import com.ctr.crm.moduls.member.service.MemberPhoneService;
import com.ctr.crm.moduls.sales.service.SaleCaseService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ctr.crm.commons.es.SearchClient;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.PojoUtils;

@Service("memberPhoneService")
public class MemberPhoneServiceImpl implements MemberPhoneService {

	private static final Log logger = LogFactory.getLog("memberBaseInfo");

	@Autowired
	private PhoneDao phoneDao;
	@Autowired
	private SearchClient searchClient;
	@Resource
	private SaleCaseService saleCaseService;

	@Override
	public boolean insert(MemberPhone memberPhone) {
		if (memberPhone == null || memberPhone.getMemberId() == null 
				|| StringUtils.isBlank(memberPhone.getPhone()))
			return false;
		if(phoneDao.exists(memberPhone.getMemberId(), memberPhone.getPhone())){
			logger.info("member's phone is exists. memberid:"+memberPhone.getMemberId()+",phone:"+memberPhone.getPhone());
			return false;
		}
		try {
			boolean success = phoneDao.insert(memberPhone);
			if(success){
				// 更新es手机号码
				searchClient.update(memberPhone.getMemberId(), memberPhone.getPhone());
			}
			return success;
		} catch (Exception e) {
			logger.error("Insert MemberPhone failed.", e);
		}
		return false;
	}
	
	@Override
	public boolean insert(Long memberId, String phone) {
		if (memberId == null || StringUtils.isBlank(phone))
			return false;
		MemberPhone memberPhone = new MemberPhone();
		memberPhone.setMemberId(memberId);
		memberPhone.setPhone(phone);
		return insert(memberPhone);
	}

	@Override
	public boolean delete(Long memberId, String phone) {
		if (null == memberId || StringUtils.isBlank(phone))
			return false;
		boolean success = phoneDao.delete(memberId, phone);
		if(success){
			searchClient.delete(memberId, phone);
		}
		return success;
	}
	
	@Override
	public boolean existsPhone(String phone, Long memberId) {
		if(StringUtils.isBlank(phone)) return false;
		if(memberId == null)
		return phoneDao.count(phone)>0;
		List<MemberPhone> phoneList = getMemberPhoneList(memberId);
		String[] phones = PojoUtils.getArrayPropertis(phoneList, "phone", null, null);
		//如果被判断的号码存在指定客户的号码列表中，返回false
		if(ArrayUtils.contains(phones, phone)){
			return false;
		}
		return phoneDao.count(phone)>0;
	}

	@Override
	public List<MemberPhone> getMemberPhoneList(Long memberId) {
		if (memberId == null)
			return null;
		return phoneDao.getListByMemberId(memberId);
	}

	@Override
	public List<MemberPhone> getMemberPhoneList(String phone) {
		if (null == phone)
			return null;
		return phoneDao.getListByPhone(phone);
	}

	@Override
	public Long getMemberIdByPhone(String phone) {
		if (StringUtils.isBlank(phone))
			return null;
		if(CommonUtils.addZeroPhone(phone)){
			phone = phone.substring(1, phone.length());
		}
		MemberPhone memberPhone = phoneDao.getPhone(phone);
		if(memberPhone == null) return null;
		return memberPhone.getMemberId();
	}

	@Override
	public Long getMemberIdInSaleCaseByPhone(String phone, boolean allowNotIn) {
		List<MemberPhone> memberPhoneList = getMemberPhoneList(phone);
		if (null == memberPhoneList || memberPhoneList.isEmpty())
			return null;
		for (MemberPhone memberPhone : memberPhoneList) {
			if (null == memberPhone)
				continue;
			if (saleCaseService.isInSaleCase(memberPhone.getMemberId())) {
				return memberPhone.getMemberId();
			}
		}
		if(!allowNotIn) return null;
		return memberPhoneList.get(0).getMemberId();
	}
	
	@Override
	public Long getMemberIdInSaleCaseByPhone(String phone) {
		return getMemberIdInSaleCaseByPhone(phone, false);
	}

	@Override
	public boolean hasSamePhoneMemberInSaleCase(Long memberId) {
		List<MemberPhone> memberPhones1 = getMemberPhoneList(memberId);
		if (null == memberPhones1 || memberPhones1.isEmpty())
			return false;
		List<MemberPhone> memberPhones2 = null;
		for (MemberPhone mp1 : memberPhones1) {
			if (null == mp1)
				continue;
			memberPhones2 = getMemberPhoneList(mp1.getPhone());
			if (null == memberPhones2 || memberPhones2.isEmpty())
				return false;
			for (MemberPhone mp2 : memberPhones2) {
				if (null == mp2)
					continue;
				if (mp2.getMemberId() != memberId && saleCaseService.isInSaleCase(mp2.getMemberId())) {
					return true;
				}
			}
		}
		return false;
	}
	
}
