package com.ctr.crm.moduls.record.models;

import java.io.Serializable;
import java.util.Date;

public class CallRecord implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6819128910065750117L;
	private Integer callId;
    private Long memberId;
    private String phone;
    private String telephone;
    private Date startTime;
    private Date answerTime;
    private Date hangupTime;
    private Integer callTime;
    private Integer waitTime;
    private String recordFileName;
    private String downFileIp;
    private String remark;
    private Integer caseClass;
    private Integer workerId;
    private String companyId;
    private Integer lineNum;
    private Integer callType;
    private Integer callMode;
    private String customuuid;

    public Integer getCallId(){
        return this.callId;
    }
    public void setCallId(Integer callId){
        this.callId = callId;
    }
    public Long getMemberId(){
        return this.memberId;
    }
    public void setMemberId(Long memberId){
        this.memberId = memberId;
    }
    public String getPhone(){
        return this.phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public String getTelephone(){
        return this.telephone;
    }
    public void setTelephone(String telephone){
        this.telephone = telephone;
    }
    public Date getStartTime(){
        return this.startTime;
    }
    public void setStartTime(Date startTime){
        this.startTime = startTime;
    }
    public Date getAnswerTime(){
        return this.answerTime;
    }
    public void setAnswerTime(Date answerTime){
        this.answerTime = answerTime;
    }
    public Date getHangupTime(){
        return this.hangupTime;
    }
    public void setHangupTime(Date hangupTime){
        this.hangupTime = hangupTime;
    }
    public Integer getCallTime(){
        return this.callTime;
    }
    public void setCallTime(Integer callTime){
        this.callTime = callTime;
    }
    public Integer getWaitTime(){
        return this.waitTime;
    }
    public void setWaitTime(Integer waitTime){
        this.waitTime = waitTime;
    }
    public String getRecordFileName(){
        return this.recordFileName;
    }
    public void setRecordFileName(String recordFileName){
        this.recordFileName = recordFileName;
    }
    public String getDownFileIp(){
        return this.downFileIp;
    }
    public void setDownFileIp(String downFileIp){
        this.downFileIp = downFileIp;
    }
    public String getRemark(){
        return this.remark;
    }
    public void setRemark(String remark){
        this.remark = remark;
    }
    public Integer getCaseClass(){
        return this.caseClass;
    }
    public void setCaseClass(Integer caseClass){
        this.caseClass = caseClass;
    }
    public Integer getWorkerId(){
        return this.workerId;
    }
    public void setWorkerId(Integer workerId){
        this.workerId = workerId;
    }
    public String getCompanyId(){
        return this.companyId;
    }
    public void setCompanyId(String companyId){
        this.companyId = companyId;
    }
    public Integer getLineNum(){
        return this.lineNum;
    }
    public void setLineNum(Integer lineNum){
        this.lineNum = lineNum;
    }
    public Integer getCallType(){
        return this.callType;
    }
    public void setCallType(Integer callType){
        this.callType = callType;
    }
    public Integer getCallMode(){
        return this.callMode;
    }
    public void setCallMode(Integer callMode){
        this.callMode = callMode;
    }
	public String getCustomuuid() {
		return customuuid;
	}
	public void setCustomuuid(String customuuid) {
		this.customuuid = customuuid;
	}
}
