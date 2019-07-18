package com.ctr.crm.moduls.approvalgroup.service;

import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.moduls.approvalgroup.models.ApprovalGroup;

import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-04-24
 */
public interface ApprovalGroupService {

    void searchPage(ResponseData responseData, PageMode pageMode, ApprovalGroup approvalGroup);

    ApprovalGroup get(String id);

    List<ApprovalGroup> findList(ApprovalGroup approvalGroup);

    List<ApprovalGroup> findAllList(ApprovalGroup approvalGroup);

    String insert(ApprovalGroup approvalGroup);

    int insertBatch(List<ApprovalGroup> approvalGroups);

    int update(ApprovalGroup approvalGroup);

    boolean delete(String id);

    List<Map<String, Object>> findWorker(String workerIds);

    List<String> getGroupIdByWorkerId(Integer workerId);

    List<ApprovalGroup> findListByIds(List<String> groupIds);
}
