package com.ctr.crm.moduls.sales.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.ctr.crm.commons.utils.file.FileCommonUtils.UploadInfo;

public class SaleCaseProc implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 897216864832116878L;
	private Long procId;
    private Long memberId;
    private Integer workerId;
    private String workerName;
    private Date procTime;
    private Date telStart;
    private Date telEnd;
    private Integer caseClass;
    private Date nextContactTime;
    private String procIp;
    private String interest;
    private String demurral;
    private String introduce;
    private String nextMain;
    private String procItem;
    private String attachments;
    private Integer notesType;
    private List<UploadInfo> attachmentList;
    private String record;

    public Long getProcId(){
        return this.procId;
    }
    public void setProcId(Long procId){
        this.procId = procId;
    }
    public Long getMemberId(){
        return this.memberId;
    }
    public void setMemberId(Long memberId){
        this.memberId = memberId;
    }
    public Integer getWorkerId(){
        return this.workerId;
    }
    public void setWorkerId(Integer workerId){
        this.workerId = workerId;
    }
    public String getWorkerName(){
        return this.workerName;
    }
    public void setWorkerName(String workerName){
        this.workerName = workerName;
    }
    public Date getProcTime(){
        return this.procTime;
    }
    public void setProcTime(Date procTime){
        this.procTime = procTime;
    }
    public Date getTelStart(){
        return this.telStart;
    }
    public void setTelStart(Date telStart){
        this.telStart = telStart;
    }
    public Date getTelEnd(){
        return this.telEnd;
    }
    public void setTelEnd(Date telEnd){
        this.telEnd = telEnd;
    }
    public Integer getCaseClass(){
        return this.caseClass;
    }
    public void setCaseClass(Integer caseClass){
        this.caseClass = caseClass;
    }
    public String getProcIp(){
        return this.procIp;
    }
    public void setProcIp(String procIp){
        this.procIp = procIp;
    }
    public String getInterest(){
        return this.interest;
    }
    public void setInterest(String interest){
        this.interest = interest;
    }
    public String getDemurral(){
        return this.demurral;
    }
    public void setDemurral(String demurral){
        this.demurral = demurral;
    }
    public String getIntroduce(){
        return this.introduce;
    }
    public void setIntroduce(String introduce){
        this.introduce = introduce;
    }
    public String getNextMain(){
        return this.nextMain;
    }
    public void setNextMain(String nextMain){
        this.nextMain = nextMain;
    }
    public String getProcItem(){
        return this.procItem;
    }
    public void setProcItem(String procItem){
        this.procItem = procItem;
    }
    public String getAttachments(){
        return this.attachments;
    }
    public void setAttachments(String attachments){
        this.attachments = attachments;
    }
    public Integer getNotesType(){
        return this.notesType;
    }
    public void setNotesType(Integer notesType){
        this.notesType = notesType;
    }
	public List<UploadInfo> getAttachmentList() {
		return attachmentList;
	}
	public void setAttachmentList(List<UploadInfo> attachmentList) {
		this.attachmentList = attachmentList;
	}
	public String getRecord() {
		return record;
	}
	public void setRecord(String record) {
		this.record = record;
	}
	public Date getNextContactTime() {
		return nextContactTime;
	}
	public void setNextContactTime(Date nextContactTime) {
		this.nextContactTime = nextContactTime;
	}
}
