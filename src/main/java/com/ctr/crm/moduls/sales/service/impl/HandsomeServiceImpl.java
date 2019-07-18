package com.ctr.crm.moduls.sales.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.service.DeptService;
import com.ctr.crm.moduls.sales.dao.HandsomeDao;
import com.ctr.crm.moduls.sales.models.Handsome;
import com.ctr.crm.moduls.sales.models.MySearch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ctr.crm.commons.rbac.AccessAuth;
import com.ctr.crm.commons.result.ResponsePage;
import com.ctr.crm.commons.utils.MemberUtils;
import com.ctr.crm.moduls.intentionality.models.Intentionality;
import com.ctr.crm.moduls.intentionality.service.IntentionalityService;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;
import com.ctr.crm.moduls.member.service.MemberService;
import com.ctr.crm.moduls.sales.service.HandsomeService;

/**
 * 说明：
 * @author eric
 * @date 2019年5月13日 下午5:38:25
 */
@Service("handsomeService")
public class HandsomeServiceImpl implements HandsomeService {

	private static final Log log = LogFactory.getLog("exception");
	@Autowired
	private HandsomeDao handsomeDao;
	@Resource
	private DeptService deptService;
	@Resource
	private MemberService memberService;
	@Resource
	private IntentionalityService intentionalityService;
	
	@Override
	public Handsome select(Long memberId) {
		if(memberId == null) return null;
		return handsomeDao.select(memberId);
	}
	
	@Override
	public String insert(Long memberId, Worker currentWorker) {
		if(memberId == null) return "客户ID为空";
		if(currentWorker == null) return "未登录";
		Handsome handsome = select(memberId);
		if(handsome != null) return "客户已为才俊佳丽";
		handsome = new Handsome();
		handsome.setMemberId(memberId);
		MemberBaseInfo baseInfo = memberService.get(MemberBaseInfo.class, memberId);
		if(baseInfo == null) return "客户不存在";
		BeanUtils.copyProperties(baseInfo, handsome);
		Intentionality intentionality = intentionalityService.getByTypeFirst(3);
		if(intentionality == null) return "才俊佳丽意向度未设置";
		handsome.setCaseClass(intentionality.getCaseClass());
		handsome.setWorkerId(currentWorker.getWorkerId());
		handsome.setWorkerName(currentWorker.getWorkerName());
		handsome.setWorkerDept(currentWorker.getDeptId());
		handsome.setAllotTime(new Date());
		try {
			handsomeDao.insert(handsome);
			memberService.handsome(memberId, true);
			return null;
		} catch (Exception e) {
			log.error("新增才俊佳丽异常", e);
		}
		return "设置才俊佳丽失败";
	}
	
	@Override
	public String delete(Long memberId) {
		if(memberId == null) return "客户ID为空";
		handsomeDao.delete(memberId);
		memberService.handsome(memberId, false);
		return null;
	}

	@Override
	public ResponsePage<Map<String, Object>> query(MySearch search,
                                                   Worker currentWorker) {
		 // 设置数据范围
        if(search == null ) search = new MySearch();
        if(search.getDeptId() != null){
        	search.setSelectDept(deptService.selectCache(search.getDeptId()));
        }
        search.setRange(AccessAuth.rangeAuth(search.getDeptId(), currentWorker));
        PageHelper.startPage(search.getPage(), search.getPageSize(), "hs.allotTime desc");
        List<Map<String, Object>> result = handsomeDao.search(search);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(result);
        ResponsePage<Map<String, Object>> page = new ResponsePage<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
        MemberUtils.maskPhone(pageInfo.getList(), "mobile", currentWorker);
        page.setList(pageInfo.getList());
        return page;
	}
	
	@Override
	public void updateHandsome(MemberBaseInfo baseInfo) {
		if(baseInfo == null || baseInfo.getMemberId() == null)
    		return;
    	Handsome handsome = select(baseInfo.getMemberId());
    	if(handsome == null) return;
    	BeanUtils.copyProperties(baseInfo, handsome);
    	handsomeDao.update(handsome);
	}

}
