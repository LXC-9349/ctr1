package com.ctr.crm.moduls.allot.models;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 分配历史
 *
 * @author DoubleLi
 * @date 2019-05-07
 */
public class AllotHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "自增ID", hidden = true)
    private Integer autoId;
    @ApiModelProperty(value = "客户ID")
    private Long memberId;
    @ApiModelProperty(value = "分配时间")
    private Date allotTime;
    @ApiModelProperty(value = "分配类型")
    private String allotType;
    @ApiModelProperty(value = "分配员工")
    private Integer workerId;
    @ApiModelProperty(value = "员工姓名")
    private String workerName;
    @ApiModelProperty(value = "员工部门")
    private Integer workerDept;
    private Integer leadsOrder;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "所属客户")
    private String companyId;


    public AllotHistory() {
    }

    public AllotHistory(Long memberId, Date allotTime, String allotType, Integer workerId, String workerName, Integer workerDept, Integer leadsOrder, String mobile, String companyId) {
        this.memberId = memberId;
        this.allotTime = allotTime;
        this.allotType = allotType;
        this.workerId = workerId;
        this.workerName = workerName;
        this.workerDept = workerDept;
        this.leadsOrder = leadsOrder;
        this.mobile = mobile;
        this.companyId = companyId;
    }

    public void setAutoId(Integer autoId) {
        this.autoId = autoId;
    }

    public Integer getAutoId() {
        return autoId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setAllotTime(Date allotTime) {
        this.allotTime = allotTime;
    }

    public Date getAllotTime() {
        return allotTime;
    }

    public void setAllotType(String allotType) {
        this.allotType = allotType;
    }

    public String getAllotType() {
        return allotType;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerDept(Integer workerDept) {
        this.workerDept = workerDept;
    }

    public Integer getWorkerDept() {
        return workerDept;
    }

    public void setLeadsOrder(Integer leadsOrder) {
        this.leadsOrder = leadsOrder;
    }

    public Integer getLeadsOrder() {
        return leadsOrder;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyId() {
        return companyId;
    }

    @Override
    public String toString() {
        return "AllotHistory{" +
                "autoId=" + autoId +
                ", memberId=" + memberId +
                ", allotTime=" + allotTime +
                ", allotType='" + allotType + '\'' +
                ", workerId=" + workerId +
                ", workerName='" + workerName + '\'' +
                ", workerDept=" + workerDept +
                ", leadsOrder=" + leadsOrder +
                ", mobile='" + mobile + '\'' +
                ", companyId='" + companyId + '\'' +
                '}';
    }
}