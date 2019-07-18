package com.ctr.crm.moduls.allot.models;

import java.io.Serializable;
import java.util.Date;

/**
 * 分配游标表
 *
 * @author DoubleLi
 * @date 2019-05-05
 */
public class AllotCursor implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String ruleId;
    private Long updateTime;
    private Integer workerId;
    private Date createtime;

    public AllotCursor() {
    }

    public AllotCursor(String ruleId, Integer workerId) {
        this.ruleId = ruleId;
        this.workerId = workerId;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public Integer getWorkerId() {
        return workerId;
    }

}