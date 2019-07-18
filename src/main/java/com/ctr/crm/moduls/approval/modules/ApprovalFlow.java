package com.ctr.crm.moduls.approval.modules;

import java.io.Serializable;
import java.util.Date;

/**
 * 功能描述: 处理记录
 *
 * @author: DoubleLi
 * @date: 2019/4/24 14:33
 */
public class ApprovalFlow implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1541997597677146326L;
    private Integer id;
    private Integer approvalFlowSettingid;
    private String approvalname;
    private Integer status;
    private Integer inspector;
    private Date examineTime;
    private String remake;
    private String approvalType;
    private Integer approvalDataId;
    private String proId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getApprovalFlowSettingid() {
        return approvalFlowSettingid;
    }

    public void setApprovalFlowSettingid(Integer approvalFlowSettingid) {
        this.approvalFlowSettingid = approvalFlowSettingid;
    }

    public String getApprovalname() {
        return approvalname;
    }

    public void setApprovalname(String approvalname) {
        this.approvalname = approvalname;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getInspector() {
        return inspector;
    }

    public void setInspector(Integer inspector) {
        this.inspector = inspector;
    }

    public Date getExamineTime() {
        return examineTime;
    }

    public void setExamineTime(Date examineTime) {
        this.examineTime = examineTime;
    }

    public String getRemake() {
        return remake;
    }

    public void setRemake(String remake) {
        this.remake = remake;
    }

    public String getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(String approvalType) {
        this.approvalType = approvalType;
    }

    public Integer getApprovalDataId() {
        return approvalDataId;
    }

    public void setApprovalDataId(Integer approvalDataId) {
        this.approvalDataId = approvalDataId;
    }

    public String getProId() {
        return proId;
    }

    public void setProId(String proId) {
        this.proId = proId;
    }

    @Override
    public String toString() {
        return "ApprovalFlow{" +
                "id=" + id +
                ", approvalFlowSettingid=" + approvalFlowSettingid +
                ", approvalname='" + approvalname + '\'' +
                ", status=" + status +
                ", inspector=" + inspector +
                ", examineTime=" + examineTime +
                ", remake='" + remake + '\'' +
                ", approvalType='" + approvalType + '\'' +
                ", approvalDataId=" + approvalDataId +
                ", proId='" + proId + '\'' +
                '}';
    }
}
