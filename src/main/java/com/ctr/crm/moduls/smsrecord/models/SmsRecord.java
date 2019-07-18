package com.ctr.crm.moduls.smsrecord.models;

import java.io.Serializable;
import java.util.Date;

/**
 * @author DoubleLi
 * @date 2019-04-28
 */
public class SmsRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    /**
     * 短信平台id
     */
    private String smsId;
    private String channelId;
    private String smsConfigId;
    private Integer type;
    private String content;
    private String mobile;
    private Integer status;
    private String failReason;
    private Long memberId;
    private Integer workerId;
    private Integer sendWorkerId;
    private Integer readStatus;
    private Date readTime;
    private Date sendTime;
    private Date submitTime;
    private Date createTime;
    private Integer deleted;


    public SmsRecord() {
    }

    public SmsRecord(String channelId, String smsConfigId, Integer type, String content, String mobile, Long memberId, Integer workerId, Integer sendWorkerId) {
        this.channelId = channelId;
        this.smsConfigId = smsConfigId;
        this.type = type;
        this.content = content;
        this.mobile = mobile;
        this.memberId = memberId;
        this.workerId = workerId;
        this.sendWorkerId = sendWorkerId;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSendWorkerId() {
        return sendWorkerId;
    }

    public void setSendWorkerId(Integer sendWorkerId) {
        this.sendWorkerId = sendWorkerId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setSmsConfigId(String smsConfigId) {
        this.smsConfigId = smsConfigId;
    }

    public String getSmsConfigId() {
        return smsConfigId;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public String getFailReason() {
        return failReason;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public void setReadStatus(Integer readStatus) {
        this.readStatus = readStatus;
    }

    public Integer getReadStatus() {
        return readStatus;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public Date getReadTime() {
        return readTime;
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

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }
}