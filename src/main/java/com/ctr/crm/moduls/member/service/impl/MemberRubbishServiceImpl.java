package com.ctr.crm.moduls.member.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.member.dao.MemberRubbishDao;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;
import com.ctr.crm.moduls.member.models.MemberRubbish;
import com.ctr.crm.moduls.member.service.MemberRubbishService;
import com.ctr.crm.moduls.member.service.MemberService;
import com.ctr.crm.moduls.notice.service.NoticeService;
import com.ctr.crm.moduls.sales.models.SaleCase;
import com.ctr.crm.moduls.sales.models.SaleCaseLost;
import com.ctr.crm.moduls.sales.service.SaleCaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.es.MemberBean;
import com.ctr.crm.commons.es.SearchClient;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.MemberUtils;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.moduls.notice.models.Notice;
import com.ctr.crm.moduls.syscirculationconfig.models.SysCirculationConfig;

/**
 * @author DoubleLi
 * @date 2019-05-16
 */
@Service("memberRubbishService")
public class MemberRubbishServiceImpl implements MemberRubbishService {

    @Autowired
    private MemberRubbishDao memberRubbishDao;
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private MemberService memberService;
    @Autowired
    private SearchClient searchClient;
    @Autowired
    private SaleCaseService saleCaseService;
    @Autowired
    private NoticeService noticeService;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, MemberRubbish memberRubbish, String memberName, String createTimeA, String createTimeB) {
        pageMode.setSqlFrom("select mr.memberId,mr.reason,mr.workerId,w.workerName,mr.caseClass,mr.createTime,m.trueName,m.birthday,m.sex,m.salary,m.education,m.marriage,m.height,m.workCity,m.homeTown from MemberRubbish mr left join Worker w on mr.workerId=w.workerId inner join MemberBaseInfo m on mr.memberId=m.memberId");
        if (StringUtils.isNotBlank(memberName)) {
            pageMode.setSqlWhere("m.trueName like '%" + pageMode.noSqlInjection(memberName) + "%'");
        }
        if (StringUtils.isNotBlank(createTimeA)) {
            pageMode.setSqlWhere("mr.createTime >= '" + pageMode.noSqlInjection(createTimeA) + "'");
        }
        if (StringUtils.isNotBlank(createTimeB)) {
            pageMode.setSqlWhere("mr.createTime <= DATE_ADD('" + pageMode.noSqlInjection(createTimeB) + "',INTERVAL 1 DAY)");
        }
        if (memberRubbish.getMemberId() != null) {
            pageMode.setSqlWhere("mr.memberId = "+memberRubbish.getMemberId());
        }
        if(StringUtils.isNotBlank(memberRubbish.getReason())){
        	pageMode.setSqlWhere("mr.reason like '%"+memberRubbish.getReason()+"%'");
        }
        if(memberRubbish.getWorkerId() != null){
        	pageMode.setSqlWhere("mr.workerId="+memberRubbish.getWorkerId());
        }
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        pageMode.setOrderBy("order by mr.createTime desc");
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public MemberRubbish select(Long memberId) {
        if (memberId == null) {
            return null;
        }
        return memberRubbishDao.select(memberId);
    }

    @Override
    public List<MemberRubbish> findList(MemberRubbish memberRubbish) {
        return memberRubbishDao.findList(memberRubbish);
    }

    @Override
    public List<MemberRubbish> findAllList() {
        return memberRubbishDao.findAllList();
    }

    @Override
    @Transactional
    public String insert(Long memberId, SysCirculationConfig reason, Worker worker) {
        if (memberId==null) {
            return "客户ID不能为空";
        }
        MemberBaseInfo memberBaseInfo=memberService.get(MemberBaseInfo.class, memberId);
        if(memberBaseInfo==null){
            return "客户不存在";
        }
        MemberRubbish memberRubbish = select(memberId);
        if(memberRubbish != null){
            return "已存在黑名单";
        }
        memberRubbish = new MemberRubbish(memberId, worker.getWorkerId());
        //机会库处理
        SaleCase saleCase=saleCaseService.getByMemberId(memberId);
        if(saleCase!=null){
            memberRubbish.setCaseClass(saleCase.getCaseClass());
            saleCaseService.deleteByMemberIds(Arrays.asList(new Long[]{memberId}));
            /** 添加放弃表*/
            SaleCaseLost saleCaseLost = new SaleCaseLost();
            BeanUtils.copyProperties(saleCase, saleCaseLost);
            saleCaseLost.setLostTime(new Date());
            saleCaseLost.setLostReason(reason.getReason());
            saleCaseLost.setLostWorker(worker.getWorkerId());
            saleCaseService.insertSaleCaseLost(saleCaseLost);
        }
        memberRubbish.setReason(reason.getReason());
        memberRubbish.setCreateTime(new Date());
        memberRubbish.setCompanyId(worker.getCompanyId());
        memberRubbishDao.insert(memberRubbish);
        /**修改客户信息*/
        //不存在机会库
        MemberUtils.setMemberType(memberBaseInfo, Constants.member_type_insalecase, Constants.NO);
        //存在黑名单
        MemberUtils.setMemberType(memberBaseInfo, Constants.member_type_rubbish, Constants.YES);
        MemberBaseInfo mbi = new MemberBaseInfo(memberId, memberBaseInfo.getMemberType());
        memberService.update(mbi);
        searchClient.blacklist(memberId,worker, reason.getReason());
        if(saleCase != null){
	        Notice notice = new Notice();
	        notice.setWorkerId(saleCase.getWorkerId());
	        notice.setWorkerName(saleCase.getWorkerName());
	        notice.setCreateTime(new Date());
	        notice.setType(2);
	        notice.setContent("客户ID:" + memberBaseInfo.getMemberId() + " 客户名:" + memberBaseInfo.getTrueName() + ",已进入黑名单");
	        notice.setTitle("黑名单客户");
	        noticeService.insert(notice);
        }
        return null;
    }

    @Override
    public int insertBatch(List<MemberRubbish> memberRubbishs) {
        return memberRubbishDao.insertBatch(memberRubbishs);
    }

    @Override
    public int update(MemberRubbish memberRubbish) {
        return memberRubbishDao.update(memberRubbish);
    }

    @Override
    @Transactional
    public String relieve(Long memberId) {
        if (memberId == null) {
            return "客户ID为空";
        }
        MemberBaseInfo memberBaseInfo=memberService.get(MemberBaseInfo.class, memberId);
        if(memberBaseInfo==null){
            return "客户不存在";
        }
        memberRubbishDao.relieve(memberId);
        //修改memberBaseInfo标识
        MemberUtils.setMemberType(memberBaseInfo, Constants.member_type_rubbish, Constants.NO);
        MemberBaseInfo mbi = new MemberBaseInfo(memberId, memberBaseInfo.getMemberType());
        memberService.update(mbi);
        MemberBean mb = new MemberBean();
        mb.setMemberId(memberId);
        mb.setIsBlacklist(false);
        searchClient.update(mb);
        return null;
    }

}
