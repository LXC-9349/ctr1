package com.ctr.crm.moduls.member.models;

import java.util.Date;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;

/**
 * 说明：
 * @author eric
 * @date 2019年5月9日 上午10:46:49
 */
@ExcelTarget("MemberInfo")
public class MemberInfo {

	@Excel(name="姓名", width=15)
	private String trueName;
	@Excel(name="手机号", width=15)
	private String mobile;
	@Excel(name="出生日期", format="yyyy-MM-dd", width=15)
	private Date birthday;
	@Excel(name="性别", replace={"男_0","女_1"})
	private Integer sex;
	@Excel(name="收入", width=15, replace={"1000元以下_1","1001-2000元_2","2001-3000元_3","3001-5000元_4","5001-8000元_5","8001-10000元_6","10001-20000元_7","20001-50000元_8","50000元以上_9"})
	private Integer salary;
	@Excel(name="学历", width=15, replace={"高中及以下_1","中专_2","大专_3","大学本科_4","硕士_5","博士_6"})
	private Integer education;
	@Excel(name="籍贯", width=20)
	private String homeTown;
	@Excel(name="婚姻状况", replace={"未婚_1","离异_2","丧偶_3"})
	private Integer marriage;
	@Excel(name="身高(cm)", type=10)
	private Integer height;
	@Excel(name="体重(kg)", type=10)
	private Integer weight;
	@Excel(name="工作地", width=15)
	private String workCity;
	@Excel(name="资源来源", width=15)
	private String field1;
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
	public String getHomeTown() {
		return homeTown;
	}
	public void setHomeTown(String homeTown) {
		this.homeTown = homeTown;
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
	public Integer getWeight() {
		return weight;
	}
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	public String getWorkCity() {
		return workCity;
	}
	public void setWorkCity(String workCity) {
		this.workCity = workCity;
	}
	public String getField1() {
		return field1;
	}
	public void setField1(String field1) {
		this.field1 = field1;
	}
	
}
