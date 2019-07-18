package com.ctr.crm.moduls.smschannel.models;

import io.swagger.annotations.ApiParam;

import java.io.Serializable;
import java.util.Date;

/**
 * @author DoubleLi
 * @date 2019-04-28
 */
public class SmsChannel implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiParam(hidden = true)
    private String id;
    @ApiParam(required = true, value = "渠道名称")
    private String name;
    @ApiParam(required = true, value = "发送地址")
    private String sendUrl;
    @ApiParam(value = "状态回调地址", defaultValue = "http://{ip}:{port}/api/sms_back/status/#{serviceName}")
    private String callbackUrl;
    @ApiParam(value = "上行短信地址", defaultValue = "http://{ip}:{port}/api/sms_back/up/#{serviceName}")
    private String callbackUpUrl;
    @ApiParam(required = true, value = "服务名称")
    private String serviceName;
    @ApiParam(required = true, value = "签名")
    private String sign;
    @ApiParam(hidden = true)
    private Date createTime;
    @ApiParam(hidden = true)
    private Integer deleted;
    @ApiParam(required = true, value = "回调方式 1回推 2主动拉取")
    private Integer backType;

    public SmsChannel() {
    }

    public Integer getBackType() {
        return backType;
    }

    public void setBackType(Integer backType) {
        this.backType = backType;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getCallbackUpUrl() {
        return callbackUpUrl;
    }

    public void setCallbackUpUrl(String callbackUpUrl) {
        this.callbackUpUrl = callbackUpUrl;
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

    public void setSendUrl(String sendUrl) {
        this.sendUrl = sendUrl;
    }

    public String getSendUrl() {
        return sendUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
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