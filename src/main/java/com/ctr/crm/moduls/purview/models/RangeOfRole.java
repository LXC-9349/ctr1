package com.ctr.crm.moduls.purview.models;

import java.io.Serializable;

public class RangeOfRole implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5236326136698291128L;
	private Integer id;
    private Integer roleId;
    private Integer rangeId;

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
    public Integer getRangeId(){
        return this.rangeId;
    }
    public void setRangeId(Integer rangeId){
        this.rangeId = rangeId;
    }
}
