package com.ctr.crm.moduls.approval.modules;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程设置
 * 功能描述:
 *
 * @author: DoubleLi
 * @date: 2019/4/24 14:33
 */
public class ApprovalFlowSetting implements Serializable {

    private static final long serialVersionUID = -6407697004678869676L;
    private Integer id;
    private String approvalName;
    private String approvalType;
    private Integer factor;
    /**
     * 指定员工id
     */
    private Integer workerId;
    /**
     * 指定组id
     */
    private String groupId;
    /**
     * 审批类型1指定员工2审批组id
     */
    private String type;

    private int isUse;
    private Integer sort;
    private Date updateTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getApprovalName() {
        return approvalName;
    }

    public void setApprovalName(String approvalName) {
        this.approvalName = approvalName;
    }

    public String getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(String approvalType) {
        this.approvalType = approvalType;
    }


    public int getIsUse() {
        return isUse;
    }

    public void setIsUse(int isUse) {
        this.isUse = isUse;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getFactor() {
        return factor;
    }

    public void setFactor(Integer factor) {
        this.factor = factor;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ApprovalFlowSetting{" +
                "id=" + id +
                ", approvalName='" + approvalName + '\'' +
                ", approvalType='" + approvalType + '\'' +
                ", factor=" + factor +
                ", workerId=" + workerId +
                ", groupId='" + groupId + '\'' +
                ", type='" + type + '\'' +
                ", isUse=" + isUse +
                ", sort=" + sort +
                ", updateTime=" + updateTime +
                '}';
    }
}
