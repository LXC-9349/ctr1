package com.ctr.crm.moduls.hrm.models;

import java.io.Serializable;
import java.util.Date;

public class Company implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -9014254726795927657L;
	private Integer autoId;
    private String companyId;
    private String companyName;
    private String contactName;
    private String phone;
    private Integer companyType;
    private Date createTime;
    private Integer valid;
    private String accountId;
    private String accountSecret;
    private String count;

    public Integer getAutoId(){
        return this.autoId;
    }
    public void setAutoId(Integer autoId){
        this.autoId = autoId;
    }
    public String getCompanyId(){
        return this.companyId;
    }
    public void setCompanyId(String companyId){
        this.companyId = companyId;
    }
    public String getCompanyName(){
        return this.companyName;
    }
    public void setCompanyName(String companyName){
        this.companyName = companyName;
    }
    public String getContactName(){
        return this.contactName;
    }
    public void setContactName(String contactName){
        this.contactName = contactName;
    }
    public String getPhone(){
        return this.phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public Integer getCompanyType(){
        return this.companyType;
    }
    public void setCompanyType(Integer companyType){
        this.companyType = companyType;
    }
    public Date getCreateTime(){
        return this.createTime;
    }
    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }
    public Integer getValid(){
        return this.valid;
    }
    public void setValid(Integer valid){
        this.valid = valid;
    }
    public String getAccountId(){
        return this.accountId;
    }
    public void setAccountId(String accountId){
        this.accountId = accountId;
    }
    public String getAccountSecret(){
        return this.accountSecret;
    }
    public void setAccountSecret(String accountSecret){
        this.accountSecret = accountSecret;
    }
    public String getCount(){
        return this.count;
    }
    public void setCount(String count){
        this.count = count;
    }
}
