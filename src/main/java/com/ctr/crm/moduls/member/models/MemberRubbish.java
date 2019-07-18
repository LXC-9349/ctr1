package com.ctr.crm.moduls.member.models;

import java.io.Serializable;
import java.util.Date;

/**
 * 黑名单
 *
 * @author DoubleLi
 * @date 2019-05-16
 */
public class MemberRubbish implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer autoId;
    private Long memberId;
    private Integer caseClass;
    private String reason;
    private Date createTime;
    private Integer workerId;
    private String companyId;

    public MemberRubbish() {
    }

    public MemberRubbish(Long memberId) {
        this.memberId = memberId;
    }

    public MemberRubbish(Long memberId, Integer workerId) {
        this.memberId = memberId;
        this.workerId = workerId;
    }

    public void setAutoId(Integer autoId) {
        this.autoId = autoId;
    }

    public Integer getAutoId() {
        return autoId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public void setCaseClass(Integer caseClass) {
        this.caseClass = caseClass;
    }

    public Integer getCaseClass() {
        return caseClass;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
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

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}