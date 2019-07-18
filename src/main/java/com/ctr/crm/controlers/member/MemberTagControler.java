package com.ctr.crm.controlers.member;

import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.moduls.member.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ctr.crm.interceptors.CurrentWorkerAware;

/**
 * 说明：客户标签
 * @author eric
 * @date 2019年5月14日 下午4:25:50
 */
@Api(tags="资源管理")
@Secure(0)
@RestController
@RequestMapping("/api/member/tag")
public class MemberTagControler implements CurrentWorkerAware {

	@Resource
	private MemberService memberService;
	
	@ApiOperation("新增客户标签")
	@RequestMapping(value="insert", method={RequestMethod.POST})
	@Secure(value=1, actionName="新增客户标签", actionUri="/api/member/tag/insert", actionNote="资源管理")
	public ResponseData save(Long memberId, String tagIds){
		if(memberId == null || StringUtils.isBlank(tagIds)){
			return new ResponseData(ResponseStatus.failed, "参数为空");
		}
		String[] tags = StringUtils.split(tagIds, ",");
		memberService.insertMemberTag(memberId, tags);
		return new ResponseData(ResponseStatus.success);
	}
	
	@ApiOperation("删除客户标签")
	@RequestMapping(value="delete", method={RequestMethod.POST})
	@Secure(value=1, actionName="删除客户标签", actionUri="/api/member/tag/delete", actionNote="资源管理")
	public ResponseData delete(Long memberId, String tagId){
		if(memberId == null || StringUtils.isBlank(tagId)){
			return new ResponseData(ResponseStatus.failed, "参数为空");
		}
		memberService.deleteMemberTag(memberId, tagId);
		return new ResponseData(ResponseStatus.success);
	}
	
	@ApiOperation("客户标签列表")
	@RequestMapping(value="list", method={RequestMethod.GET})
	public ResponseData list(Long memberId){
		if(memberId == null){
			return new ResponseData(ResponseStatus.failed, "参数为空");
		}
		return new ResponseData(ResponseStatus.success, memberService.getMemberTag(memberId));
	}
	
}
