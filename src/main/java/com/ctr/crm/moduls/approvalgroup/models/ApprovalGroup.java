package com.ctr.crm.moduls.approvalgroup.models;

import io.swagger.annotations.ApiParam;

import java.io.Serializable;
import java.util.Date;

/**
 * 审批组设置
 *
 * @author DoubleLi
 * @date 2019-04-24
 */
public class ApprovalGroup implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiParam(hidden = true)
    private String id;
    @ApiParam(required = true, value = "组名")
    private String name;
    @ApiParam(value = "组员id以,隔开")
    private String workerIds;
    @ApiParam(hidden = true)
    private Date createTime;
    @ApiParam(hidden = true)
    private Integer deleted;


    public ApprovalGroup() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setWorkerIds(String workerIds) {
        this.workerIds = workerIds;
    }

    public String getWorkerIds() {
        return workerIds;
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