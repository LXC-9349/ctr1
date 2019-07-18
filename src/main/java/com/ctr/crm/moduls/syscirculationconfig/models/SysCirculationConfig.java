package com.ctr.crm.moduls.syscirculationconfig.models;

import io.swagger.annotations.ApiParam;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户流转原因配置
 *
 * @author DoubleLi
 * @date 2019-04-23
 */
public class SysCirculationConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiParam(hidden = true)
    private String id;
    @ApiParam(required = true, value = "流转原因")
    private String reason;
    @ApiParam(value="黑名单")
    private Integer deleteMember;
    @ApiParam(value="转让客户")
    private Integer transferMember;
    @ApiParam(value="放弃客户")
    private Integer quitMember;
    @ApiParam(hidden = true)
    private Date createTime;
    @ApiParam(hidden = true)
    private Integer deleted;


    public SysCirculationConfig() {
    }

    public SysCirculationConfig(String reason) {
        this.reason = reason;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public Integer getDeleteMember() {
        return deleteMember;
    }

    public void setDeleteMember(Integer deleteMember) {
        this.deleteMember = deleteMember;
    }

    public Integer getTransferMember() {
        return transferMember;
    }

    public void setTransferMember(Integer transferMember) {
        this.transferMember = transferMember;
    }

    public Integer getQuitMember() {
        return quitMember;
    }

    public void setQuitMember(Integer quitMember) {
        this.quitMember = quitMember;
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