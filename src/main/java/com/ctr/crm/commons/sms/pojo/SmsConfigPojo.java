package com.ctr.crm.commons.sms.pojo;

/**
 * 功能描述: 短息发送配置
 *
 * @author: DoubleLi
 * @date: 2019/4/28 11:04
 */
public class SmsConfigPojo {

    private String sendurl;
    private String callbackUrl;
    private String name;
    private String pwd;

    public SmsConfigPojo(String sendurl, String callbackUrl, String name, String pwd) {
        this.sendurl = sendurl;
        this.callbackUrl = callbackUrl;
        this.name = name;
        this.pwd = pwd;
    }

    public SmsConfigPojo() {
    }

    public String getSendurl() {
        return sendurl;
    }

    public void setSendurl(String sendurl) {
        this.sendurl = sendurl;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
