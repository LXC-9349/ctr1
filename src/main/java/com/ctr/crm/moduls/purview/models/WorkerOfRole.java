package com.ctr.crm.moduls.purview.models;

import java.io.Serializable;

public class WorkerOfRole implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3241309303852293255L;
	private Integer autoId;
    private Integer roleId;
    private Integer workerId;
    private String companyId;

    public Integer getAutoId(){
        return this.autoId;
    }
    public void setAutoId(Integer autoId){
        this.autoId = autoId;
    }
    public Integer getRoleId(){
        return this.roleId;
    }
    public void setRoleId(Integer roleId){
        this.roleId = roleId;
    }
    public Integer getWorkerId(){
        return this.workerId;
    }
    public void setWorkerId(Integer workerId){
        this.workerId = workerId;
    }
    public String getCompanyId(){
        return this.companyId;
    }
    public void setCompanyId(String companyId){
        this.companyId = companyId;
    }
}
