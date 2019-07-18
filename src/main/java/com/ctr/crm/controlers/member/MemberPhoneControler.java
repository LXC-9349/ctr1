package com.ctr.crm.controlers.member;

import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.BeanUtils;
import com.ctr.crm.commons.utils.Encrypt;
import com.ctr.crm.commons.utils.MemberUtils;
import com.ctr.crm.commons.utils.PhoneUtils;
import com.ctr.crm.moduls.member.models.MemberPhone;
import com.ctr.crm.moduls.member.service.MemberPhoneService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;

/**
 * 说明：客户号码管理
 * @author eric
 * @date 2019年5月14日 下午4:25:50
 */
@Api(tags="资源管理")
@Secure(0)
@RestController
@RequestMapping("/api/member/phone")
public class MemberPhoneControler implements CurrentWorkerAware {

	@Resource
	private MemberPhoneService memberPhoneService;
	
	@ApiOperation("新增客户号码")
	@RequestMapping(value="insert", method={RequestMethod.POST})
	@Secure(value=1,actionName="新增号码",actionNote="资源管理",actionUri="/api/member/phone/insert")
	public ResponseData save(Long memberId, String phone){
		if(memberId == null || StringUtils.isBlank(phone)){
			return new ResponseData(ResponseStatus.failed, "参数为空");
		}
		if(!PhoneUtils.isPhoneLegal(phone)){
			return new ResponseData(ResponseStatus.failed, "非法手机号");
		}
		memberPhoneService.insert(memberId, phone);
		return new ResponseData(ResponseStatus.success);
	}
	
	@ApiOperation("删除客户号码")
	@RequestMapping(value="delete", method={RequestMethod.POST})
	/*@Secure(value=1,actionName="删除号码",actionNote="资源管理",actionUri="/api/member/phone/delete")*/
	public ResponseData delete(Long memberId, String phone){
		if(memberId == null || StringUtils.isBlank(phone)){
			return new ResponseData(ResponseStatus.failed, "参数为空");
		}
		memberPhoneService.delete(memberId, phone);
		return new ResponseData(ResponseStatus.success);
	}
	
	@ApiOperation("客户号码列表")
	@RequestMapping(value="list", method={RequestMethod.GET})
	public ResponseData list(Long memberId){
		if(memberId == null){
			return new ResponseData(ResponseStatus.failed, "参数为空");
		}
		List<MemberPhone> phones = memberPhoneService.getMemberPhoneList(memberId);
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>(phones.size());
		for (MemberPhone phone : phones) {
			Map<String, Object> map = BeanUtils.transBeanToMap(phone);
			map.put("realPhone", Encrypt.encrypt(phone.getPhone()));
			map.put("phone", MemberUtils.maskPhone(phone.getPhone(), CurrentWorkerLocalCache.getCurrentWorker()));
			data.add(map);
		}
		return new ResponseData(ResponseStatus.success, data);
	}
	
}
