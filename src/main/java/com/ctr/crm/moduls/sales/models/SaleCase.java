package com.ctr.crm.moduls.sales.models;

import java.io.Serializable;
import java.util.Date;

public class SaleCase implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -5091416159933610104L;
    private Integer caseId;
    private Long memberId;
    private String nickName;
    private String trueName;
    private String mobile;
    private String wechatId;
    private Date createTime;
    private Date birthday;
    private Integer sex;
    private Integer salary;
    private Integer education;
    private Integer marriage;
    private Integer height;
    private String workCity;
    private String homeTown;
    private Integer house;
    private Integer car;
    private String field1;
    private Date allotTime;
    private String allotType;
    private String allotIp;
    private Integer workerId;
    private String workerName;
    private Integer workerDept;
    private Integer caseClass;
    private Date classChangeTime;
    private Date lastContactTime;
    private Date nextContactTime;
    private Date lastPhoneTime;
    private Date lastSmsTime;
    private String lastPhoneNum;
    private Integer callNum;
    private Integer intensive;
    private String adjustWorker;
    private Date adjustTime;

    public Integer getCaseId() {
        return caseId;
    }

    public void setCaseId(Integer caseId) {
        this.caseId = caseId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public Integer getEducation() {
        return education;
    }

    public void setEducation(Integer education) {
        this.education = education;
    }

    public Integer getMarriage() {
        return marriage;
    }

    public void setMarriage(Integer marriage) {
        this.marriage = marriage;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getWorkCity() {
        return workCity;
    }

    public void setWorkCity(String workCity) {
        this.workCity = workCity;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public Integer getHouse() {
        return house;
    }

    public void setHouse(Integer house) {
        this.house = house;
    }

    public Integer getCar() {
        return car;
    }

    public void setCar(Integer car) {
        this.car = car;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public Date getAllotTime() {
        return allotTime;
    }

    public void setAllotTime(Date allotTime) {
        this.allotTime = allotTime;
    }

    public String getAllotType() {
        return allotType;
    }

    public void setAllotType(String allotType) {
        this.allotType = allotType;
    }

    public String getAllotIp() {
        return allotIp;
    }

    public void setAllotIp(String allotIp) {
        this.allotIp = allotIp;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public Integer getWorkerDept() {
        return workerDept;
    }

    public void setWorkerDept(Integer workerDept) {
        this.workerDept = workerDept;
    }

    public Integer getCaseClass() {
        return caseClass;
    }

    public void setCaseClass(Integer caseClass) {
        this.caseClass = caseClass;
    }

    public Date getClassChangeTime() {
        return classChangeTime;
    }

    public void setClassChangeTime(Date classChangeTime) {
        this.classChangeTime = classChangeTime;
    }

    public Date getLastContactTime() {
        return lastContactTime;
    }

    public void setLastContactTime(Date lastContactTime) {
        this.lastContactTime = lastContactTime;
    }

    public Date getNextContactTime() {
        return nextContactTime;
    }

    public void setNextContactTime(Date nextContactTime) {
        this.nextContactTime = nextContactTime;
    }

    public Date getLastPhoneTime() {
        return lastPhoneTime;
    }

    public void setLastPhoneTime(Date lastPhoneTime) {
        this.lastPhoneTime = lastPhoneTime;
    }

    public String getLastPhoneNum() {
        return lastPhoneNum;
    }

    public void setLastPhoneNum(String lastPhoneNum) {
        this.lastPhoneNum = lastPhoneNum;
    }

    public Integer getCallNum() {
        return callNum;
    }

    public void setCallNum(Integer callNum) {
        this.callNum = callNum;
    }

	public Integer getIntensive() {
		return intensive;
	}

	public void setIntensive(Integer intensive) {
		this.intensive = intensive;
	}
	
    public String getAdjustWorker() {
        return adjustWorker;
    }

    public void setAdjustWorker(String adjustWorker) {
        this.adjustWorker = adjustWorker;
    }

    public Date getAdjustTime() {
        return adjustTime;
    }

    public void setAdjustTime(Date adjustTime) {
        this.adjustTime = adjustTime;
    }

    @Override
    public String toString() {
        return "SaleCase{" +
                "caseId=" + caseId +
                ", memberId=" + memberId +
                ", nickName='" + nickName + '\'' +
                ", trueName='" + trueName + '\'' +
                ", wechatId='" + wechatId + '\'' +
                ", createTime=" + createTime +
                ", allotTime=" + allotTime +
                ", allotType='" + allotType + '\'' +
                ", allotIp='" + allotIp + '\'' +
                ", workerId=" + workerId +
                ", workerName='" + workerName + '\'' +
                ", workerDept=" + workerDept +
                ", caseClass=" + caseClass +
                ", classChangeTime=" + classChangeTime +
                ", lastContactTime=" + lastContactTime +
                ", nextContactTime=" + nextContactTime +
                ", lastPhoneTime=" + lastPhoneTime +
                ", lastPhoneNum='" + lastPhoneNum + '\'' +
                ", callNum=" + callNum +
                ", intensive=" + intensive +
                ", adjustWorker='" + adjustWorker + '\'' +
                ", adjustTime=" + adjustTime +
                '}';
    }

    public Date getLastSmsTime() {
        return lastSmsTime;
    }

    public void setLastSmsTime(Date lastSmsTime) {
        this.lastSmsTime = lastSmsTime;
    }
}
