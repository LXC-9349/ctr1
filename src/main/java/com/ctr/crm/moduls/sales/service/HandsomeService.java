package com.ctr.crm.moduls.sales.service;

import java.util.Map;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.sales.models.Handsome;
import com.ctr.crm.moduls.sales.models.MySearch;
import com.ctr.crm.commons.result.ResponsePage;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;

/**
 * 说明：
 * @author eric
 * @date 2019年5月10日 下午4:56:53
 */
public interface HandsomeService {

	String insert(Long memberId, Worker currentWorker);
	String delete(Long memberId);
	Handsome select(Long memberId);
	
	void updateHandsome(MemberBaseInfo baseInfo);
	
	ResponsePage<Map<String, Object>> query(MySearch search, Worker currentWorker);
}
