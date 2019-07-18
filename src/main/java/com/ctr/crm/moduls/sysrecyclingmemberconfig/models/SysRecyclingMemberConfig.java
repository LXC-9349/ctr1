package com.ctr.crm.moduls.sysrecyclingmemberconfig.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统客户回收策略
 *
 * @author DoubleLi
 * @date 2019-04-23
 */
public class SysRecyclingMemberConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiParam(hidden = true)
    private String id;
    @ApiParam(value = "条件1")
    private Integer condition1;
    @ApiParam(value = "条件2")
    private Integer condition2;
    @ApiParam(value = "条件3")
    private Integer condition3;
    @ApiParam(value = "联系方式`,`隔开通过系统字典查询接口type=contact_information")
    private String contactInformations;
    @ApiParam(value = "开关1关闭2开启", required = true)
    private Integer tswitch;
    @ApiParam(value = "执行时间", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date runTime;
    @ApiParam(value = "在指定天数后从回收站删除", defaultValue = "15")
    private Integer recycleDay;
    @ApiParam(hidden = true)
    private Date createTime;
    @ApiParam(hidden = true)
    private Integer deleted;


    public Integer getRecycleDay() {
        return recycleDay;
    }

    public void setRecycleDay(Integer recycleDay) {
        this.recycleDay = recycleDay;
    }

    public SysRecyclingMemberConfig() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setCondition1(Integer condition1) {
        this.condition1 = condition1;
    }

    public Integer getCondition1() {
        return condition1;
    }

    public void setCondition2(Integer condition2) {
        this.condition2 = condition2;
    }

    public Integer getCondition2() {
        return condition2;
    }

    public void setCondition3(Integer condition3) {
        this.condition3 = condition3;
    }

    public Integer getCondition3() {
        return condition3;
    }

    public void setContactInformations(String contactInformations) {
        this.contactInformations = contactInformations;
    }

    public String getContactInformations() {
        return contactInformations;
    }

    public void setTswitch(Integer tswitch) {
        this.tswitch = tswitch;
    }

    public Integer getTswitch() {
        return tswitch;
    }

    public void setRunTime(Date runTime) {
        this.runTime = runTime;
    }

    public Date getRunTime() {
        return runTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Integer getDeleted() {
        return deleted;
    }

}