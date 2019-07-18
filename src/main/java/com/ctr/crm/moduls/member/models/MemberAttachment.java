package com.ctr.crm.moduls.member.models;

import java.io.Serializable;

public class MemberAttachment implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 9204868212587712169L;
	private Integer id;
    private Long memberId;
    private String attachmentUrl;
    private String fileName;
    private Integer operator;

    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
    }
    public Long getMemberId(){
        return this.memberId;
    }
    public void setMemberId(Long memberId){
        this.memberId = memberId;
    }
    public String getAttachmentUrl(){
        return this.attachmentUrl;
    }
    public void setAttachmentUrl(String attachmentUrl){
        this.attachmentUrl = attachmentUrl;
    }
    public String getFileName(){
        return this.fileName;
    }
    public void setFileName(String fileName){
        this.fileName = fileName;
    }
    public Integer getOperator(){
        return this.operator;
    }
    public void setOperator(Integer operator){
        this.operator = operator;
    }
}
