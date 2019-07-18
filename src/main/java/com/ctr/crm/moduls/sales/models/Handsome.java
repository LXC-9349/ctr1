package com.ctr.crm.moduls.sales.models;

import java.io.Serializable;
import java.util.Date;

/**
 * 才俊佳丽
 * @author eric
 *
 */
public class Handsome implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5091416159933610104L;
	private Integer id;
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
	private Integer workerId;
	private String workerName;
	private Integer workerDept;
	private Integer caseClass;
	private Date classChangeTime;
	private Date lastContactTime;
	private Date nextContactTime;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Date getAllotTime() {
		return allotTime;
	}
	public void setAllotTime(Date allotTime) {
		this.allotTime = allotTime;
	}
}
