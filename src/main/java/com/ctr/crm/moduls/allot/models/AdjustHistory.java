package com.ctr.crm.moduls.allot.models;

import java.io.Serializable;
import java.util.Date;

/**
 * 调配历史
 *
 * @author DoubleLi
 * @date 2019-05-08
 */
public class AdjustHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer caseId;
    private Long memberId;
    private String adjustWorker;
    private Date adjustTime;
    private Integer fromWorkerId;
    private Integer toWorkerId;
    private Integer caseClass;
    private String companyId;


    public AdjustHistory() {
    }

    public AdjustHistory(Integer caseId, Long memberId, String adjustWorker, Date adjustTime, Integer fromWorkerId, Integer toWorkerId, Integer caseClass, String companyId) {
        this.caseId = caseId;
        this.memberId = memberId;
        this.adjustWorker = adjustWorker;
        this.adjustTime = adjustTime;
        this.fromWorkerId = fromWorkerId;
        this.toWorkerId = toWorkerId;
        this.caseClass = caseClass;
        this.companyId = companyId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setCaseId(Integer caseId) {
        this.caseId = caseId;
    }

    public Integer getCaseId() {
        return caseId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public void setAdjustWorker(String adjustWorker) {
        this.adjustWorker = adjustWorker;
    }

    public String getAdjustWorker() {
        return adjustWorker;
    }

    public void setAdjustTime(Date adjustTime) {
        this.adjustTime = adjustTime;
    }

    public Date getAdjustTime() {
        return adjustTime;
    }

    public void setFromWorkerId(Integer fromWorkerId) {
        this.fromWorkerId = fromWorkerId;
    }

    public Integer getFromWorkerId() {
        return fromWorkerId;
    }

    public void setToWorkerId(Integer toWorkerId) {
        this.toWorkerId = toWorkerId;
    }

    public Integer getToWorkerId() {
        return toWorkerId;
    }

    public void setCaseClass(Integer caseClass) {
        this.caseClass = caseClass;
    }

    public Integer getCaseClass() {
        return caseClass;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyId() {
        return companyId;
    }

    @Override
    public String toString() {
        return "AdjustHistory{" +
                "id=" + id +
                ", caseId=" + caseId +
                ", memberId=" + memberId +
                ", adjustWorker='" + adjustWorker + '\'' +
                ", adjustTime=" + adjustTime +
                ", fromWorkerId=" + fromWorkerId +
                ", toWorkerId=" + toWorkerId +
                ", caseClass=" + caseClass +
                ", companyId='" + companyId + '\'' +
                '}';
    }
}