package com.ctr.crm.moduls.approval.modules;

import com.ctr.crm.moduls.approvalgroup.models.ApprovalGroup;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 流程设置包含子项组所有worker信息
 * 功能描述:
 *
 * @author: DoubleLi
 * @date: 2019/4/24 14:33
 */
public class ApprovalFlowSettingSon {

    ApprovalGroup approvalGroup;

    ApprovalFlowSetting approvalFlowSetting;

    public ApprovalGroup getApprovalGroup() {
        return approvalGroup;
    }

    public void setApprovalGroup(ApprovalGroup approvalGroup) {
        this.approvalGroup = approvalGroup;
    }

    public ApprovalFlowSetting getApprovalFlowSetting() {
        return approvalFlowSetting;
    }

    public void setApprovalFlowSetting(ApprovalFlowSetting approvalFlowSetting) {
        this.approvalFlowSetting = approvalFlowSetting;
    }
}
