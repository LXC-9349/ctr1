package com.ctr.crm.moduls.hrm.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "Worker", description = "员工表")
public class Worker implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -9197717055292478658L;
	@ApiModelProperty(value = "自增ID")
	private Integer id;
	@ApiModelProperty(value = "工号，系统自动生成")
    private Integer workerId;
	@ApiModelProperty(value = "员工账号，系统自动生成")
	private String workerAccount;
	@ApiModelProperty(value = "员工姓名")
    private String workerName;
	@ApiModelProperty(value = "员工级别(选项:15总监 10经理 5主管 0普通员工)")
    private Integer skill;
	@ApiModelProperty(value = "登录密码")
    private String psw;
    private String companyId;
    @ApiModelProperty(value = "所在部门")
    private Integer deptId;
    private Integer[] deptIds;
    private String deptName;
    @ApiModelProperty(value = "是否分配(选项:0否 1是")
    private Integer isAllot;
    @ApiModelProperty(value = "最大库容(填0表示不限)")
    private Integer allCount;
    @ApiModelProperty(value = "当天最大分配量(填0表示不限)")
    private Integer maxAllotTheDay;
    @ApiModelProperty(value = "邮箱地址")
    private String email;
    @ApiModelProperty(value = "分机号")
    private Integer lineNum;
    @ApiModelProperty(value = "手机号")
    private String phoneNumber;
    @ApiModelProperty(value = "入职日期")
    private Date joinDate;
    @ApiModelProperty(value = "上岗日期")
    private Date postDate;
    @ApiModelProperty(value = "转正日期")
    private Date regularDate;
    @ApiModelProperty(value = "离职日期")
    private Date dimissionDate;
    @ApiModelProperty(value = "员工状态(选项:0在职 1离职 2请假)")
    private Integer workerStatus;
    @ApiModelProperty(value = "电话队列")
    private String workQueue;
    @ApiModelProperty(value = "头像")
    private String headurl;
    private Integer online;
    private Integer serviceStatus;
    private Date createTime;
    private Date updateTime;

    public Worker() {
    }

    public Worker(Integer workerId, String companyId) {
        this.workerId = workerId;
        this.companyId = companyId;
    }

    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
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
    public Integer getSkill(){
        return this.skill;
    }
    public void setSkill(Integer skill){
        this.skill = skill;
    }
    public String getPsw(){
        return this.psw;
    }
    public void setPsw(String psw){
        this.psw = psw;
    }
    public String getCompanyId(){
        return this.companyId;
    }
    public void setCompanyId(String companyId){
        this.companyId = companyId;
    }
    public Integer getDeptId(){
        return this.deptId;
    }
    public void setDeptId(Integer deptId){
        this.deptId = deptId;
    }
    public Integer getIsAllot(){
        return this.isAllot;
    }
    public void setIsAllot(Integer isAllot){
        this.isAllot = isAllot;
    }
    public Integer getAllCount(){
        return this.allCount;
    }
    public void setAllCount(Integer allCount){
        this.allCount = allCount;
    }
    public Integer getMaxAllotTheDay(){
        return this.maxAllotTheDay;
    }
    public void setMaxAllotTheDay(Integer maxAllotTheDay){
        this.maxAllotTheDay = maxAllotTheDay;
    }
    public String getEmail(){
        return this.email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public Integer getLineNum(){
        return this.lineNum;
    }
    public void setLineNum(Integer lineNum){
        this.lineNum = lineNum;
    }
    public String getPhoneNumber(){
        return this.phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }
    public Date getJoinDate(){
        return this.joinDate;
    }
    public void setJoinDate(Date joinDate){
        this.joinDate = joinDate;
    }
    public Date getPostDate(){
        return this.postDate;
    }
    public void setPostDate(Date postDate){
        this.postDate = postDate;
    }
    public Date getRegularDate(){
        return this.regularDate;
    }
    public void setRegularDate(Date regularDate){
        this.regularDate = regularDate;
    }
    public Date getDimissionDate(){
        return this.dimissionDate;
    }
    public void setDimissionDate(Date dimissionDate){
        this.dimissionDate = dimissionDate;
    }
    public Integer getWorkerStatus(){
        return this.workerStatus;
    }
    public void setWorkerStatus(Integer workerStatus){
        this.workerStatus = workerStatus;
    }
    public String getWorkQueue(){
        return this.workQueue;
    }
    public void setWorkQueue(String workQueue){
        this.workQueue = workQueue;
    }
    public String getHeadurl(){
        return this.headurl;
    }
    public void setHeadurl(String headurl){
        this.headurl = headurl;
    }
    public Integer getOnline(){
        return this.online;
    }
    public void setOnline(Integer online){
        this.online = online;
    }
    public Integer getServiceStatus(){
        return this.serviceStatus;
    }
    public void setServiceStatus(Integer serviceStatus){
        this.serviceStatus = serviceStatus;
    }
    public Date getCreateTime(){
        return this.createTime;
    }
    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }
    public Date getUpdateTime(){
        return this.updateTime;
    }
    public void setUpdateTime(Date updateTime){
        this.updateTime = updateTime;
    }
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getWorkerAccount() {
		return workerAccount;
	}
	public void setWorkerAccount(String workerAccount) {
		this.workerAccount = workerAccount;
	}

	public Integer[] getDeptIds() {
		return deptIds;
	}

	public void setDeptIds(Integer[] deptIds) {
		this.deptIds = deptIds;
	}
}
