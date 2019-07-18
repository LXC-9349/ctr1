package com.ctr.crm.moduls.sales.models;

import java.io.Serializable;
import java.util.Date;

public class SaleCaseLost implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -4457881215817370780L;
    private Integer lostId;
    private Date lostTime;
    private Integer lostWorker;
    private String lostReason;
    private Integer caseId;
    private Long memberId;
    private Date allotTime;
    private Integer workerId;
    private String workerName;
    private Integer workerGroup;
    private Integer workerDept;
    private Integer caseClass;
    private Integer classSubItem;
    private Date lastPhoneTime;
    private String lastPhoneNum;
    private Integer callNum;
    private String allotType;
    private String allotIP;
    private Integer intensive;
    private Integer adjustWorker;
    private Date adjustTime;
    private String lostIP;
    private String companyId;
    private Date lastSmsTime;
    private Date lastContactTime;


    public Integer getLostId() {
        return this.lostId;
    }

    public void setLostId(Integer lostId) {
        this.lostId = lostId;
    }

    public Date getLostTime() {
        return this.lostTime;
    }

    public void setLostTime(Date lostTime) {
        this.lostTime = lostTime;
    }

    public Integer getLostWorker() {
        return this.lostWorker;
    }

    public void setLostWorker(Integer lostWorker) {
        this.lostWorker = lostWorker;
    }

    public String getLostReason() {
        return this.lostReason;
    }

    public void setLostReason(String lostReason) {
        this.lostReason = lostReason;
    }

    public Integer getCaseId() {
        return this.caseId;
    }

    public void setCaseId(Integer caseId) {
        this.caseId = caseId;
    }

    public Long getMemberId() {
        return this.memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Date getAllotTime() {
        return this.allotTime;
    }

    public void setAllotTime(Date allotTime) {
        this.allotTime = allotTime;
    }

    public Integer getWorkerId() {
        return this.workerId;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public String getWorkerName() {
        return this.workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public Integer getWorkerGroup() {
        return this.workerGroup;
    }

    public void setWorkerGroup(Integer workerGroup) {
        this.workerGroup = workerGroup;
    }

    public Integer getWorkerDept() {
        return this.workerDept;
    }

    public void setWorkerDept(Integer workerDept) {
        this.workerDept = workerDept;
    }

    public Integer getCaseClass() {
        return this.caseClass;
    }

    public void setCaseClass(Integer caseClass) {
        this.caseClass = caseClass;
    }

    public Integer getClassSubItem() {
        return this.classSubItem;
    }

    public void setClassSubItem(Integer classSubItem) {
        this.classSubItem = classSubItem;
    }

    public Date getLastPhoneTime() {
        return this.lastPhoneTime;
    }

    public void setLastPhoneTime(Date lastPhoneTime) {
        this.lastPhoneTime = lastPhoneTime;
    }

    public String getLastPhoneNum() {
        return this.lastPhoneNum;
    }

    public void setLastPhoneNum(String lastPhoneNum) {
        this.lastPhoneNum = lastPhoneNum;
    }

    public Integer getCallNum() {
        return this.callNum;
    }

    public void setCallNum(Integer callNum) {
        this.callNum = callNum;
    }

    public String getAllotType() {
        return this.allotType;
    }

    public void setAllotType(String allotType) {
        this.allotType = allotType;
    }

    public String getAllotIP() {
        return this.allotIP;
    }

    public void setAllotIP(String allotIP) {
        this.allotIP = allotIP;
    }

    public Integer getIntensive() {
		return intensive;
	}
    
    public void setIntensive(Integer intensive) {
		this.intensive = intensive;
	}

    public Integer getAdjustWorker() {
        return this.adjustWorker;
    }

    public void setAdjustWorker(Integer adjustWorker) {
        this.adjustWorker = adjustWorker;
    }

    public Date getAdjustTime() {
        return this.adjustTime;
    }

    public void setAdjustTime(Date adjustTime) {
        this.adjustTime = adjustTime;
    }

    public String getLostIP() {
        return this.lostIP;
    }

    public void setLostIP(String lostIP) {
        this.lostIP = lostIP;
    }

    public String getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public Date getLastSmsTime() {
        return lastSmsTime;
    }

    public void setLastSmsTime(Date lastSmsTime) {
        this.lastSmsTime = lastSmsTime;
    }

    public Date getLastContactTime() {
        return lastContactTime;
    }

    public void setLastContactTime(Date lastContactTime) {
        this.lastContactTime = lastContactTime;
    }
}
