package com.ctr.crm.moduls.purview.models;

import java.io.Serializable;

public class Action implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2058642469204028784L;
	private Integer actionId;
    private String actionUri;
    private String actionName;
    private String note;
    private Integer isUse;
    private Integer foundational;
    private Integer appreciate;

    public Integer getActionId(){
        return this.actionId;
    }
    public void setActionId(Integer actionId){
        this.actionId = actionId;
    }
    public String getActionUri(){
        return this.actionUri;
    }
    public void setActionUri(String actionUri){
        this.actionUri = actionUri;
    }
    public String getActionName(){
        return this.actionName;
    }
    public void setActionName(String actionName){
        this.actionName = actionName;
    }
    public String getNote(){
        return this.note;
    }
    public void setNote(String note){
        this.note = note;
    }
    public Integer getIsUse(){
        return this.isUse;
    }
    public void setIsUse(Integer isUse){
        this.isUse = isUse;
    }
    public Integer getAppreciate(){
        return this.appreciate;
    }
    public void setAppreciate(Integer appreciate){
        this.appreciate = appreciate;
    }
	public Integer getFoundational() {
		return foundational;
	}
	public void setFoundational(Integer foundational) {
		this.foundational = foundational;
	}
}
