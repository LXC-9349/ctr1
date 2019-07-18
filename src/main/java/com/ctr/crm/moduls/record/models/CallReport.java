package com.ctr.crm.moduls.record.models;

import java.io.Serializable;
import java.util.Date;

public class CallReport implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8999397067422123153L;
	private Date writeDate;
    private Integer writeHour;
    private Integer workerId;
    private String companyId;
    private Date firstLoginTime;
    private Date firstCallTime;
    private Integer callInNum;
    private Integer callInAllNum;
    private Float callInTimeCount;
    private Integer callOutNum;
    private Integer callOutAllNum;
    private Float callOutTimeCount;
    private Integer callNum;
    private Integer callAllNum;
    private Float callTimeCount;

    public Date getWriteDate(){
        return this.writeDate;
    }
    public void setWriteDate(Date writeDate){
        this.writeDate = writeDate;
    }
    public Integer getWriteHour(){
        return this.writeHour;
    }
    public void setWriteHour(Integer writeHour){
        this.writeHour = writeHour;
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
    public Date getFirstLoginTime(){
        return this.firstLoginTime;
    }
    public void setFirstLoginTime(Date firstLoginTime){
        this.firstLoginTime = firstLoginTime;
    }
    public Date getFirstCallTime(){
        return this.firstCallTime;
    }
    public void setFirstCallTime(Date firstCallTime){
        this.firstCallTime = firstCallTime;
    }
    public Integer getCallInNum(){
        return this.callInNum;
    }
    public void setCallInNum(Integer callInNum){
        this.callInNum = callInNum;
    }
    public Integer getCallInAllNum(){
        return this.callInAllNum;
    }
    public void setCallInAllNum(Integer callInAllNum){
        this.callInAllNum = callInAllNum;
    }
    public Float getCallInTimeCount(){
        return this.callInTimeCount;
    }
    public void setCallInTimeCount(Float callInTimeCount){
        this.callInTimeCount = callInTimeCount;
    }
    public Integer getCallOutNum(){
        return this.callOutNum;
    }
    public void setCallOutNum(Integer callOutNum){
        this.callOutNum = callOutNum;
    }
    public Integer getCallOutAllNum(){
        return this.callOutAllNum;
    }
    public void setCallOutAllNum(Integer callOutAllNum){
        this.callOutAllNum = callOutAllNum;
    }
    public Float getCallOutTimeCount(){
        return this.callOutTimeCount;
    }
    public void setCallOutTimeCount(Float callOutTimeCount){
        this.callOutTimeCount = callOutTimeCount;
    }
    public Integer getCallNum(){
        return this.callNum;
    }
    public void setCallNum(Integer callNum){
        this.callNum = callNum;
    }
    public Integer getCallAllNum(){
        return this.callAllNum;
    }
    public void setCallAllNum(Integer callAllNum){
        this.callAllNum = callAllNum;
    }
    public Float getCallTimeCount(){
        return this.callTimeCount;
    }
    public void setCallTimeCount(Float callTimeCount){
        this.callTimeCount = callTimeCount;
    }
}
