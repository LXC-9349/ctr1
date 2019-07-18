package com.ctr.crm.moduls.sales.models;

import com.ctr.crm.moduls.hrm.models.Dept;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.ctr.crm.commons.CommonSearch;

@ApiModel(value="MySearch",description="我的客户搜索")
public class MySearch extends CommonSearch{
	@ApiModelProperty(value="客户ID")
	private Long memberId;
	@ApiModelProperty(value="昵称")
	private String nickName;
	@ApiModelProperty(value="姓名")
	private String trueName;
	@ApiModelProperty(value="姓名")
	private String mobile;
	@ApiModelProperty(value="微信号")
	private String wechatId;
	@ApiModelProperty(value="创建日期")
	private String createTime;
	@ApiModelProperty(value="年龄范围1")
	private Integer ageB;
	@ApiModelProperty(value="年龄范围2")
	private Integer ageE;
	@ApiModelProperty(value="性别 注：0男 1女")
	private Integer sex;
	@ApiModelProperty(value="月收入 多选  注：1:1000元以下 2:1001-2000元 3:2001-3000元 4:3001-5000元 5:5001-8000元 6:8001-10000元 7:10001-20000元 8:20001-50000元 9:50000元以上")
	private Integer[] salary;
	@ApiModelProperty(value="学历 多选  注：1高中及以下 2中专 3大专 4大学本科 5硕士 6博士")
	private Integer[] education;
	@ApiModelProperty(value="婚姻状况  多选  注：1未婚 2离异 3丧偶")
	private Integer[] marriage;
	@ApiModelProperty(value="身高范围1")
	private Integer heightB;
	@ApiModelProperty(value="身高范围2")
	private Integer heightE;
	@ApiModelProperty(value="体重范围1")
	private Integer weightB;
	@ApiModelProperty(value="体重范围2")
	private Integer weightE;
	private String workCity;
	@ApiModelProperty(value="籍贯")
	private String homeTown;
	@ApiModelProperty(value="购房情况 注：1租房 2全款购房 3按揭购房 4老家有住房")
	private Integer house;
	@ApiModelProperty(value="购车情况 注：1未购车 2已购车")
	private Integer car;
	@ApiModelProperty(value="资源来源")
	private String field1;
	@ApiModelProperty(value="入库日期")
	private String allotTime;
	@ApiModelProperty(value="员工ID")
	private Integer workerId;
	@ApiModelProperty(value="部门ID")
	private Integer deptId;
	@ApiModelProperty(hidden=true, required=false)
	private Dept selectDept;
	@ApiModelProperty(value="意向度")
	private Integer caseClass;
	@ApiModelProperty(value="最后联系时间")
	private String lastContactTime;
	@ApiModelProperty(value="下次联系时间")
	private String nextContactTime;
	@ApiModelProperty(value="重点客户 0否 1是 注：不用于才俊佳丽", allowableValues="0,1")
	private Integer intensive;
	@ApiModelProperty(value="库类型 1机会库 2VIP库", allowableValues="1,2", hidden=true)
	private Integer caseType;
	@ApiModelProperty(value="场景 1今日入库 2今日联系", allowableValues="1,2")
	private Integer scene;
	@ApiModelProperty(value="多少天未跟进")
	private Integer notFollowedDays;
	
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
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public Integer getAgeB() {
		return ageB;
	}
	public void setAgeB(Integer ageB) {
		this.ageB = ageB;
	}
	public Integer getAgeE() {
		return ageE;
	}
	public void setAgeE(Integer ageE) {
		this.ageE = ageE;
	}
	public Integer getSex() {
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
	}
	public Integer[] getSalary() {
		return salary;
	}
	public void setSalary(Integer[] salary) {
		this.salary = salary;
	}
	public Integer[] getEducation() {
		return education;
	}
	public void setEducation(Integer[] education) {
		this.education = education;
	}
	public Integer[] getMarriage() {
		return marriage;
	}
	public void setMarriage(Integer[] marriage) {
		this.marriage = marriage;
	}
	public Integer getHeightB() {
		return heightB;
	}
	public void setHeightB(Integer heightB) {
		this.heightB = heightB;
	}
	public Integer getHeightE() {
		return heightE;
	}
	public void setHeightE(Integer heightE) {
		this.heightE = heightE;
	}
	public Integer getWeightB() {
		return weightB;
	}
	public void setWeightB(Integer weightB) {
		this.weightB = weightB;
	}
	public Integer getWeightE() {
		return weightE;
	}
	public void setWeightE(Integer weightE) {
		this.weightE = weightE;
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
	public String getAllotTime() {
		return allotTime;
	}
	public void setAllotTime(String allotTime) {
		this.allotTime = allotTime;
	}
	public Integer getWorkerId() {
		return workerId;
	}
	public void setWorkerId(Integer workerId) {
		this.workerId = workerId;
	}
	public Integer getDeptId() {
		return deptId;
	}
	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}
	public Dept getSelectDept() {
		return selectDept;
	}
	public void setSelectDept(Dept selectDept) {
		this.selectDept = selectDept;
	}
	public Integer getCaseClass() {
		return caseClass;
	}
	public void setCaseClass(Integer caseClass) {
		this.caseClass = caseClass;
	}
	public String getLastContactTime() {
		return lastContactTime;
	}
	public void setLastContactTime(String lastContactTime) {
		this.lastContactTime = lastContactTime;
	}
	public String getNextContactTime() {
		return nextContactTime;
	}
	public void setNextContactTime(String nextContactTime) {
		this.nextContactTime = nextContactTime;
	}
	public Integer getIntensive() {
		return intensive;
	}
	public void setIntensive(Integer intensive) {
		this.intensive = intensive;
	}
	public Integer getCaseType() {
		return caseType;
	}
	/**
	 * 库类型 1机会库 2VIP库 通过意向度范围过滤
	 * @param caseType
	 */
	public void setCaseType(Integer caseType) {
		this.caseType = caseType;
	}
	public Integer getScene() {
		return scene;
	}
	public void setScene(Integer scene) {
		this.scene = scene;
	}
	public Integer getNotFollowedDays() {
		return notFollowedDays;
	}
	public void setNotFollowedDays(Integer notFollowedDays) {
		this.notFollowedDays = notFollowedDays;
	}
}
