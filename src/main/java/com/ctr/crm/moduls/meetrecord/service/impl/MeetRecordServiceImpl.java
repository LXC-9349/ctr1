package com.ctr.crm.moduls.meetrecord.service.impl;

import com.ctr.crm.moduls.approval.result.ApprovalResult;
import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.contract.models.ContractOrder;
import com.ctr.crm.moduls.contract.service.ContractOrderService;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;
import com.ctr.crm.moduls.member.models.MemberVip;
import com.ctr.crm.moduls.member.service.MemberService;
import com.ctr.crm.moduls.member.service.MemberVipService;
import com.ctr.crm.moduls.record.models.CallRecord;
import com.ctr.crm.moduls.record.service.RecordService;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.interceptors.ApprovalInterceptor;
import com.ctr.crm.moduls.meetrecord.dao.MeetRecordDao;
import com.ctr.crm.moduls.meetrecord.models.MeetRecord;
import com.ctr.crm.moduls.meetrecord.service.MeetRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-05-23
 */
@Service("meetRecordService")
public class MeetRecordServiceImpl implements MeetRecordService {

    @Autowired
    private MeetRecordDao meetRecordDao;
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private MemberVipService memberVipService;
    @Autowired
    private MemberService memberService;
    @Autowired
    @Lazy
    private ContractOrderService contractOrderService;
    @Autowired
    private RecordService recordService;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, MeetRecord meetRecord) {
        pageMode.setSqlFrom("select * from MeetRecord");
        pageMode.setSqlWhere("deleted=0");
        if (meetRecord.getMemberId() != null) {
            pageMode.setSqlWhere("memberId = '" + meetRecord.getMemberId() + "'");
        }
        if (meetRecord.getMeetId() != null) {
            pageMode.setSqlWhere("meetId = '" + meetRecord.getMeetId() + "'");
        }
        if (meetRecord.getWorkerId() != null) {
            pageMode.setSqlWhere("workerId = '" + meetRecord.getWorkerId() + "'");
        }
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        pageMode.setOrderBy("order by meetTime desc");
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public MeetRecord get(Integer id) {
        if (id == null) {
            return null;
        }
        return meetRecordDao.get(id);
    }

    @Override
    public List<MeetRecord> findList(MeetRecord meetRecord) {
        return meetRecordDao.findList(meetRecord);
    }

    @Override
    public List<MeetRecord> findAllList(MeetRecord meetRecord) {
        return meetRecordDao.findAllList(meetRecord);
    }

    @Override
    public ApprovalResult approvalInsert(MeetRecord meetRecord) {
        ApprovalResult approvalResult = new ApprovalResult();
        meetRecord.setId(meetRecordDao.getID());
        //校验
        Long memberId = meetRecord.getMemberId();
        MemberVip memberVip = memberVipService.getByMemberId(memberId);
        if (memberVip == null) {
            approvalResult.setErrMsg("客户不是VIP,无法操作");
            return approvalResult;
        }
        if (memberVip.getStatus() == 1 || memberVip.getStatus() == 2) {
            approvalResult.setErrMsg("会员合同已失效");
            return approvalResult;
        }
        approvalResult.setResultData(ApprovalInterceptor.FAIL);//先定为校验不通过
        CallRecord callRecord = recordService.selectByCallId(meetRecord.getCallId());
        if (callRecord == null) {
            approvalResult.setErrMsg("录音不存在");
            return approvalResult;
        }
        MemberBaseInfo memberBaseInfo = memberService.get(MemberBaseInfo.class, meetRecord.getMemberId());
        MemberBaseInfo memberBaseInfo1 = memberService.get(MemberBaseInfo.class, meetRecord.getMeetId());
        if (memberBaseInfo == null || memberBaseInfo1 == null) {
            approvalResult.setErrMsg("会员或约见人选择错误");
            return approvalResult;
        }
        if (memberBaseInfo.getMemberId().equals(memberBaseInfo1.getMemberId())) {
            approvalResult.setErrMsg("会员和约见人不能重复");
            return approvalResult;
        }
        if (memberBaseInfo.getSex().equals(memberBaseInfo1.getSex())) {
            approvalResult.setErrMsg("性别应选择异性");
            return approvalResult;
        }
        List<MeetRecord> meetRecords = meetRecordDao.findList(new MeetRecord(memberBaseInfo.getMemberId(), memberBaseInfo1.getMemberId()));
        if (meetRecords != null && meetRecords.size() > 0) {
            for (MeetRecord record : meetRecords) {
                if (record.getStatus() != 2) {
                    approvalResult.setErrMsg("会员和约见人有过约见记录无法重复添加");
                    return approvalResult;
                }
            }
        }
        meetRecord.setMemberName(memberBaseInfo.getTrueName());
        meetRecord.setMeetName(memberBaseInfo1.getTrueName());
        if (meetRecord.getCreateTime() == null) {
            meetRecord.setCreateTime(new Date());
        }
        meetRecord.setCreateTime(new Date());
        meetRecord.setStatus(0);
        meetRecordDao.insert(meetRecord);
        approvalResult.setBusinessId(meetRecord.getId());
        approvalResult.setType(ApprovalInterceptor.APPROVAL);
        return approvalResult;
    }

    @Override
    public String realApprovalInsert(Integer id) {
        MeetRecord meetRecord = get(id);
        MemberVip memberVip = memberVipService.getByMemberId(meetRecord.getMemberId());
        meetRecord.setStatus(1);
        meetRecord.setApprovalTime(new Date());
        memberVip.setAlrCount(memberVip.getAlrCount() + 1);
        ContractOrder contractOrder = contractOrderService.get(memberVip.getContractId());
        if (memberVip.getAlrCount() >= contractOrder.getServeCount()) {
            memberVip.setStatus(1);
            memberVip.setRealEndTime(new Date());
        }
        /** 服务完成处理vip*/
        memberVipService.update(memberVip);
        update(meetRecord);
        return "审批通过";
    }

    @Override
    public String delApproval(Integer id) {
        /** 作废处理*/
        MeetRecord meetRecord = get(id);
        meetRecord.setStatus(2);
        meetRecord.setApprovalTime(new Date());
        update(meetRecord);
        return "操作成功";
    }

    @Override
    public int insertBatch(List<MeetRecord> meetRecords) {
        return meetRecordDao.insertBatch(meetRecords);
    }

    @Override
    public int update(MeetRecord meetRecord) {
        return meetRecordDao.update(meetRecord);
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) {
            return false;
        }
        return meetRecordDao.delete(id);
    }

}
