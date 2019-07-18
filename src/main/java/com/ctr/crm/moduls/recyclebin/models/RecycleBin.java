package com.ctr.crm.moduls.recyclebin.models;

import java.io.Serializable;
import java.util.Date;

/**
 * 回收站
 *
 * @author DoubleLi
 * @date 2019-05-10
 */
public class RecycleBin implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Date recycleTime;
    private Integer recycleWorker;
    private String recycleReason;
    private Integer caseId;
    private Long memberId;
    private Date allotTime;
    private Integer workerId;
    private String workerName;
    private Integer workerDept;
    private Integer caseClass;
    private Integer classSubItem;
    private Date lastSmsTime;
    private Date lastContactTime;
    private Date lastPhoneTime;
    private String lastPhoneNum;
    private Integer callNum;
    private String allotType;
    private String allotIP;
    private Integer pivot;
    private Integer adjustWorker;
    private Date adjustTime;
    private String lostIP;
    private String companyId;


    public RecycleBin() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRecycleTime(Date recycleTime) {
        this.recycleTime = recycleTime;
    }

    public Date getRecycleTime() {
        return recycleTime;
    }

    public void setRecycleWorker(Integer recycleWorker) {
        this.recycleWorker = recycleWorker;
    }

    public Integer getRecycleWorker() {
        return recycleWorker;
    }

    public void setRecycleReason(String recycleReason) {
        this.recycleReason = recycleReason;
    }

    public String getRecycleReason() {
        return recycleReason;
    }

    public void setCaseId(Integer caseId) {
        this.caseId = caseId;
    }

    public Integer getCaseId() {
        return caseId;
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

    public void setCaseClass(Integer caseClass) {
        this.caseClass = caseClass;
    }

    public Integer getCaseClass() {
        return caseClass;
    }

    public void setClassSubItem(Integer classSubItem) {
        this.classSubItem = classSubItem;
    }

    public Integer getClassSubItem() {
        return classSubItem;
    }

    public void setLastSmsTime(Date lastSmsTime) {
        this.lastSmsTime = lastSmsTime;
    }

    public Date getLastSmsTime() {
        return lastSmsTime;
    }

    public void setLastContactTime(Date lastContactTime) {
        this.lastContactTime = lastContactTime;
    }

    public Date getLastContactTime() {
        return lastContactTime;
    }

    public void setLastPhoneTime(Date lastPhoneTime) {
        this.lastPhoneTime = lastPhoneTime;
    }

    public Date getLastPhoneTime() {
        return lastPhoneTime;
    }

    public void setLastPhoneNum(String lastPhoneNum) {
        this.lastPhoneNum = lastPhoneNum;
    }

    public String getLastPhoneNum() {
        return lastPhoneNum;
    }

    public void setCallNum(Integer callNum) {
        this.callNum = callNum;
    }

    public Integer getCallNum() {
        return callNum;
    }

    public void setAllotType(String allotType) {
        this.allotType = allotType;
    }

    public String getAllotType() {
        return allotType;
    }

    public void setAllotIP(String allotIP) {
        this.allotIP = allotIP;
    }

    public String getAllotIP() {
        return allotIP;
    }

    public void setPivot(Integer pivot) {
        this.pivot = pivot;
    }

    public Integer getPivot() {
        return pivot;
    }

    public void setAdjustWorker(Integer adjustWorker) {
        this.adjustWorker = adjustWorker;
    }

    public Integer getAdjustWorker() {
        return adjustWorker;
    }

    public void setAdjustTime(Date adjustTime) {
        this.adjustTime = adjustTime;
    }

    public Date getAdjustTime() {
        return adjustTime;
    }

    public void setLostIP(String lostIP) {
        this.lostIP = lostIP;
    }

    public String getLostIP() {
        return lostIP;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyId() {
        return companyId;
    }

}