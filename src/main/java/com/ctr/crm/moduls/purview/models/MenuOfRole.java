package com.ctr.crm.moduls.purview.models;

import java.io.Serializable;

public class MenuOfRole implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 163973887990701593L;
	private Integer autoId;
    private Integer roleId;
    private Integer menuId;

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
    public Integer getMenuId(){
        return this.menuId;
    }
    public void setMenuId(Integer menuId){
        this.menuId = menuId;
    }
}
