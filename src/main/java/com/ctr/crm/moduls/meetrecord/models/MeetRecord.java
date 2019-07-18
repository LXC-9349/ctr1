package com.ctr.crm.moduls.meetrecord.models;

import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 约见记录
 *
 * @author DoubleLi
 * @date 2019-05-23
 */
public class MeetRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiParam(hidden = true)
    private Integer id;
    @ApiParam(required = true, value = "客户Id")
    private Long memberId;
    @ApiParam(required = true, value = "约见对象Id")
    private Long meetId;
    @ApiParam(hidden = true)
    private Integer workerId;
    @ApiParam(hidden = true)
    private String companyId;
    @ApiParam(hidden = true)
    private Integer status;
    @ApiParam(required = true, value = "约见时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date meetTime;
    @ApiParam(hidden = true)
    private Date approvalTime;
    @ApiParam(hidden = true)
    private Date createTime;
    @ApiParam(hidden = true)
    private Integer deleted;
    @ApiParam(hidden = true)
    private String reason;
    @ApiParam(required = true, value = "录音编号")
    private Integer callId;
    @ApiParam(hidden = true)
    private String memberName;
    @ApiParam(hidden = true)
    private String meetName;

    public MeetRecord() {
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMeetName() {
        return meetName;
    }

    public void setMeetName(String meetName) {
        this.meetName = meetName;
    }

    public MeetRecord(Long memberId, Long meetId) {
        this.memberId = memberId;
        this.meetId = meetId;
    }

    public Integer getCallId() {
        return callId;
    }

    public void setCallId(Integer callId) {
        this.callId = callId;
    }

    public Date getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(Date approvalTime) {
        this.approvalTime = approvalTime;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    public void setMeetId(Long meetId) {
        this.meetId = meetId;
    }

    public Long getMeetId() {
        return meetId;
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

    public void setMeetTime(Date meetTime) {
        this.meetTime = meetTime;
    }

    public Date getMeetTime() {
        return meetTime;
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