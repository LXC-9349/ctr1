package com.ctr.crm.moduls.meetrecord.service;

import com.ctr.crm.moduls.approval.result.ApprovalResult;
import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.annontations.Approval;
import com.ctr.crm.moduls.meetrecord.models.MeetRecord;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;

/**
 * @author DoubleLi
 * @date  2019-05-23
 */
public interface  MeetRecordService {

    void searchPage(ResponseData responseData, PageMode pageMode, MeetRecord meetRecord);

    MeetRecord get(Integer id);

    List<MeetRecord> findList(MeetRecord meetRecord);

    List<MeetRecord> findAllList(MeetRecord meetRecord);

    int insertBatch(List<MeetRecord> meetRecords);

    int update(MeetRecord meetRecord);

    boolean delete(Integer id);

    /**
     *
     * 功能描述:
     *  添加约见记录进入审批流程
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/29 10:25
     */
    @Approval(action = "约见记录", beanName = "meetRecordService", method = "realApprovalInsert", delmethod = "delApproval", name = "约见审批", value = Constants.APPROVAL_MEET, viewUrl = "/api/meet_manage/info", submit = true)
    ApprovalResult approvalInsert(MeetRecord meetRecord);

    String realApprovalInsert(Integer id);

    String  delApproval(Integer id);
}
