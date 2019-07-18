package com.ctr.crm.moduls.member.models;

import java.io.Serializable;
import java.util.Date;

/**
 * VIP信息表
 *
 * @author DoubleLi
 * @date 2019-05-20
 */
public class MemberVip implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Long memberId;
    private Integer contractId;
    private Integer workerId;
    private String companyId;
    private Integer status;
    private Date beginTime;
    private Date endTime;
    private Integer alrCount;
    private Date realEndTime;
    private Date createTime;
    private Integer deleted;


    public MemberVip() {
    }

    public MemberVip(Long memberId) {
        this.memberId = memberId;
    }

    public MemberVip(Long memberId, Integer status) {
        this.memberId = memberId;
        this.status = status;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setAlrCount(Integer alrCount) {
        this.alrCount = alrCount;
    }

    public Integer getAlrCount() {
        return alrCount;
    }

    public void setRealEndTime(Date realEndTime) {
        this.realEndTime = realEndTime;
    }

    public Date getRealEndTime() {
        return realEndTime;
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