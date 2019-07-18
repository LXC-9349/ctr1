package com.ctr.crm.moduls.allot.models;

import java.io.Serializable;
import java.util.Date;

/**
 * 员工分配情况表
 *
 * @author DoubleLi
 * @date 2019-05-05
 */
public class AllotWorker implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer workerId;
    private Integer maxAllot;
    private Integer maxAllotTheDay;
    private Integer hasAllot;
    private Integer alrAllotDay;
    private Integer gainNum;
    private Date createTime;
    private Integer deleted;


    public AllotWorker() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public void setMaxAllot(Integer maxAllot) {
        this.maxAllot = maxAllot;
    }

    public Integer getMaxAllot() {
        return maxAllot;
    }

    public void setMaxAllotTheDay(Integer maxAllotTheDay) {
        this.maxAllotTheDay = maxAllotTheDay;
    }

    public Integer getMaxAllotTheDay() {
        return maxAllotTheDay;
    }

    public void setHasAllot(Integer hasAllot) {
        this.hasAllot = hasAllot;
    }

    public Integer getHasAllot() {
        return hasAllot;
    }

    public void setAlrAllotDay(Integer alrAllotDay) {
        this.alrAllotDay = alrAllotDay;
    }

    public Integer getAlrAllotDay() {
        return alrAllotDay;
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

    public Integer getGainNum() {
        return gainNum;
    }

    public void setGainNum(Integer gainNum) {
        this.gainNum = gainNum;
    }
}