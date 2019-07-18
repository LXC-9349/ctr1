package com.ctr.crm.moduls.member.models;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.ctr.crm.commons.utils.CommonUtils;

public class MemberPhone implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1868812555133220512L;
	private Long id;
    private Long memberId;
    private String phone;
    private String linkman;
    private Date createTime;
    private Integer done;
    private Date doneTime;
    private String companyId;
    private Integer valid;

    public Long getId(){
        return this.id;
    }
    public void setId(Long id){
        this.id = id;
    }
    public Long getMemberId(){
        return this.memberId;
    }
    public void setMemberId(Long memberId){
        this.memberId = memberId;
    }
    public String getPhone(){
        return this.phone;
    }
    public void setPhone(String phone){
    	if(StringUtils.isNotBlank(phone)
    			&& CommonUtils.addZeroPhone(phone)){
    		phone = phone.substring(1, phone.length());
    	}
        this.phone = phone;
    }
    public String getLinkman(){
        return this.linkman;
    }
    public void setLinkman(String linkman){
        this.linkman = linkman;
    }
    public Date getCreateTime(){
        return this.createTime;
    }
    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }
    public Integer getDone(){
        return this.done;
    }
    public void setDone(Integer done){
        this.done = done;
    }
    public Date getDoneTime(){
        return this.doneTime;
    }
    public void setDoneTime(Date doneTime){
        this.doneTime = doneTime;
    }
    public String getCompanyId(){
        return this.companyId;
    }
    public void setCompanyId(String companyId){
        this.companyId = companyId;
    }
    public Integer getValid(){
        return this.valid;
    }
    public void setValid(Integer valid){
        this.valid = valid;
    }
}
