package com.ctr.crm.moduls.system.models;

import java.io.Serializable;

public class CrmSmsConfigure implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7142936514081729187L;
	private Integer id;
    private String accoundSid;
    private String token;
    private String customUrl;
    private String templateUrl;
    private String smsTemplateUrl;
    private String upSmsUrl;
    private String smsStatusUrl;
    private String companyId;
    private String autograph;
    private String smsId;

    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
    }
    public String getAccoundSid(){
        return this.accoundSid;
    }
    public void setAccoundSid(String accoundSid){
        this.accoundSid = accoundSid;
    }
    public String getToken(){
        return this.token;
    }
    public void setToken(String token){
        this.token = token;
    }
    public String getCustomUrl(){
        return this.customUrl;
    }
    public void setCustomUrl(String customUrl){
        this.customUrl = customUrl;
    }
    public String getTemplateUrl(){
        return this.templateUrl;
    }
    public void setTemplateUrl(String templateUrl){
        this.templateUrl = templateUrl;
    }
    public String getSmsTemplateUrl(){
        return this.smsTemplateUrl;
    }
    public void setSmsTemplateUrl(String smsTemplateUrl){
        this.smsTemplateUrl = smsTemplateUrl;
    }
    public String getUpSmsUrl(){
        return this.upSmsUrl;
    }
    public void setUpSmsUrl(String upSmsUrl){
        this.upSmsUrl = upSmsUrl;
    }
    public String getSmsStatusUrl(){
        return this.smsStatusUrl;
    }
    public void setSmsStatusUrl(String smsStatusUrl){
        this.smsStatusUrl = smsStatusUrl;
    }
    public String getCompanyId(){
        return this.companyId;
    }
    public void setCompanyId(String companyId){
        this.companyId = companyId;
    }
    public String getAutograph(){
        return this.autograph;
    }
    public void setAutograph(String autograph){
        this.autograph = autograph;
    }
    public String getSmsId(){
        return this.smsId;
    }
    public void setSmsId(String smsId){
        this.smsId = smsId;
    }
}
