package com.ctr.crm.moduls.allocation.models;

import io.swagger.annotations.ApiParam;

import java.io.Serializable;
import java.util.Date;

/**
 * 自动分配规则
 *
 * @author DoubleLi
 * @date 2019-04-29
 */
public class AllocationRule implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiParam(hidden = true)
    private String id;
    @ApiParam(value = "规则名称", required = true)
    private String name;
    @ApiParam(value = "选择销售Id ,隔开", required = true)
    private String workerIds;
    @ApiParam(value = "1平均分配2累计分配 默认1")
    private Integer way;
    @ApiParam(value = "1开启0关闭 默认0",hidden = true)
    private Integer status;
    @ApiParam(hidden = true)
    private Date createTime;
    @ApiParam(hidden = true)
    private Integer deleted;
    @ApiParam(value = "优先级",hidden = true)
    private Integer priority;

    public AllocationRule() {
    }

    public AllocationRule(Integer status) {
        this.status = status;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setWorkerIds(String workerIds) {
        this.workerIds = workerIds;
    }

    public String getWorkerIds() {
        return workerIds;
    }

    public void setWay(Integer way) {
        this.way = way;
    }

    public Integer getWay() {
        return way;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
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


    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}