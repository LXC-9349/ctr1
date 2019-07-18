package com.ctr.crm.commons.es;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ctr.crm.commons.CommonSearch;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 功能描述: 筛选条件
 *
 * @param:
 * @return:
 * @author: DoubleLi
 * @date: 2019/5/15 11:48
 */
@ApiModel(value = "MemberBeanSearch", description = "客户筛选条件")
public class MemberBeanSearch extends CommonSearch {

    @ApiModelProperty(value = "客户ID")
    private Long memberId;
    @ApiModelProperty(value = "客户昵称")
    private String nickName;
    @ApiModelProperty(value = "真实姓名")
    private String trueName;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "微信号")
    private String wechatId;
    @ApiModelProperty(value = "创建日期 注：起始结束用~分隔")
    private String createTime;
    @ApiModelProperty(value = "年龄范围1")
    private Integer ageB;
    @ApiModelProperty(value = "年龄范围2")
    private Integer ageE;
    @ApiModelProperty(value = "身高范围1")
    private Integer heightB;
    @ApiModelProperty(value = "身高范围2")
    private Integer heightE;
    @ApiModelProperty(value = "体重范围1")
    private Integer weightB;
    @ApiModelProperty(value = "体重范围2")
    private Integer weightE;
    @ApiModelProperty(value = "月收入 多选")
    private Integer[] salary;
    @ApiModelProperty(value = "学历 多选")
    private Integer[] education;
    @ApiModelProperty(value = "婚姻状况  多选")
    private Integer[] marriage;
    @ApiModelProperty(value = "职业  多选")
    private Integer[] occupation;
    @ApiModelProperty(value = "生肖  多选")
    private Integer[] animals;
    @ApiModelProperty(value = "星座  多选")
    private Integer[] constellation;
    @ApiModelProperty(value = "星座  多选")
    private Integer[] body;
    @ApiModelProperty(value = "购房情况 多选")
    private Integer[] house;
    @ApiModelProperty(value = "性别")
    private Integer sex;
    @ApiModelProperty(value = "购车情况")
    private Integer car;
    @ApiModelProperty(value = "工作地")
    private String workCity;
    @ApiModelProperty(value = "籍贯")
    private String homeTown;
    @ApiModelProperty(value = "资源来源")
    private String field1;
    //======================业务条件==========================
    @ApiModelProperty(value = "是否VIP 1我在找谁 2天生一对 3谁在找我", allowableValues = "1,2,3")
    private Integer businessType;
    @ApiModelProperty(value = "是否VIP")
    private Boolean isVip;
    @ApiModelProperty(value = "是否在库")
    private Boolean inSaleCase;
    @ApiModelProperty(value = "是否才俊佳丽")
    private Boolean isHandsome;
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Boolean isRecycling;
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Boolean isBlacklist;
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String workerIds;
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String deptIds;
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String createTimeA;
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String createTimeB;
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String allotTimeA;
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String allotTimeB;
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String lastContactTimeA;
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String lastContactTimeB;
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String orderSignTimeA;
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String orderSignTimeB;
    @ApiModelProperty(value = "成熟度")
    private Integer caseClass;
    @ApiModelProperty(value = "分配人")
    private Integer workerId;
    @ApiModelProperty(value = "分配人名称")
    private String workerName;
    @ApiModelProperty(value = "放弃人")
    private Integer quitWorkerId;
    @ApiModelProperty(value = "放弃人名称")
    private String quitWorkerName;
    @ApiModelProperty(value = "放弃原因")
    private String quitReason;
    @ApiModelProperty(value = "签订人")
    private Integer orderSignWorkerId;
    @ApiModelProperty(value = "签订人名称")
    private String orderSignWorkerName;


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

    public Integer[] getOccupation() {
        return occupation;
    }

    public void setOccupation(Integer[] occupation) {
        this.occupation = occupation;
    }

    public Integer[] getAnimals() {
        return animals;
    }

    public void setAnimals(Integer[] animals) {
        this.animals = animals;
    }

    public Integer[] getConstellation() {
        return constellation;
    }

    public void setConstellation(Integer[] constellation) {
        this.constellation = constellation;
    }

    public Integer[] getBody() {
        return body;
    }

    public void setBody(Integer[] body) {
        this.body = body;
    }

    public Integer[] getHouse() {
        return house;
    }

    public void setHouse(Integer[] house) {
        this.house = house;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getCar() {
        return car;
    }

    public void setCar(Integer car) {
        this.car = car;
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

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Boolean getIsVip() {
        return isVip;
    }

    public void setIsVip(Boolean isVip) {
        this.isVip = isVip;
    }

    public Boolean getInSaleCase() {
        return inSaleCase;
    }

    public void setInSaleCase(Boolean inSaleCase) {
        this.inSaleCase = inSaleCase;
    }

    public Boolean getIsHandsome() {
        return isHandsome;
    }

    public void setIsHandsome(Boolean isHandsome) {
        this.isHandsome = isHandsome;
    }

    public Boolean getIsRecycling() {
        return isRecycling;
    }

    public void setIsRecycling(Boolean isRecycling) {
        this.isRecycling = isRecycling;
    }

    public Boolean getIsBlacklist() {
        return isBlacklist;
    }

    public void setIsBlacklist(Boolean isBlacklist) {
        this.isBlacklist = isBlacklist;
    }

    public String getWorkerIds() {
        return workerIds;
    }

    public void setWorkerIds(String workerIds) {
        this.workerIds = workerIds;
    }

    public String getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(String deptIds) {
        this.deptIds = deptIds;
    }

    public String getCreateTimeA() {
        return createTimeA;
    }

    public void setCreateTimeA(String createTimeA) {
        this.createTimeA = createTimeA;
    }

    public String getCreateTimeB() {
        return createTimeB;
    }

    public void setCreateTimeB(String createTimeB) {
        this.createTimeB = createTimeB;
    }

    public String getAllotTimeA() {
        return allotTimeA;
    }

    public void setAllotTimeA(String allotTimeA) {
        this.allotTimeA = allotTimeA;
    }

    public String getAllotTimeB() {
        return allotTimeB;
    }

    public void setAllotTimeB(String allotTimeB) {
        this.allotTimeB = allotTimeB;
    }

    public String getLastContactTimeA() {
        return lastContactTimeA;
    }

    public void setLastContactTimeA(String lastContactTimeA) {
        this.lastContactTimeA = lastContactTimeA;
    }

    public String getLastContactTimeB() {
        return lastContactTimeB;
    }

    public void setLastContactTimeB(String lastContactTimeB) {
        this.lastContactTimeB = lastContactTimeB;
    }

    public String getOrderSignTimeA() {
        return orderSignTimeA;
    }

    public void setOrderSignTimeA(String orderSignTimeA) {
        this.orderSignTimeA = orderSignTimeA;
    }

    public String getOrderSignTimeB() {
        return orderSignTimeB;
    }

    public void setOrderSignTimeB(String orderSignTimeB) {
        this.orderSignTimeB = orderSignTimeB;
    }

    public Integer getCaseClass() {
        return caseClass;
    }

    public void setCaseClass(Integer caseClass) {
        this.caseClass = caseClass;
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

    public Integer getQuitWorkerId() {
        return quitWorkerId;
    }

    public void setQuitWorkerId(Integer quitWorkerId) {
        this.quitWorkerId = quitWorkerId;
    }

    public String getQuitWorkerName() {
        return quitWorkerName;
    }

    public void setQuitWorkerName(String quitWorkerName) {
        this.quitWorkerName = quitWorkerName;
    }

    public String getQuitReason() {
        return quitReason;
    }

    public void setQuitReason(String quitReason) {
        this.quitReason = quitReason;
    }


    public Integer getOrderSignWorkerId() {
        return orderSignWorkerId;
    }

    public void setOrderSignWorkerId(Integer orderSignWorkerId) {
        this.orderSignWorkerId = orderSignWorkerId;
    }

    public String getOrderSignWorkerName() {
        return orderSignWorkerName;
    }

    public void setOrderSignWorkerName(String orderSignWorkerName) {
        this.orderSignWorkerName = orderSignWorkerName;
    }
}
