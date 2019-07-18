package com.ctr.crm.moduls.purview.models;

import java.io.Serializable;

public class ActionOfRole implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 312026070414071239L;
	private Integer id;
    private Integer roleId;
    private Integer actionId;

    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
    }
    public Integer getRoleId(){
        return this.roleId;
    }
    public void setRoleId(Integer roleId){
        this.roleId = roleId;
    }
    public Integer getActionId(){
        return this.actionId;
    }
    public void setActionId(Integer actionId){
        this.actionId = actionId;
    }
}
