package com.ctr.crm.moduls.purview.models;

import java.io.Serializable;

public class Role implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3659940707012185609L;
	private Integer roleId;
    private String roleName;
    private Integer orderNo;
    private String remark;
    private String companyId;
    private Integer enabled;
    private Integer isDefault;

    public Integer getRoleId(){
        return this.roleId;
    }
    public void setRoleId(Integer roleId){
        this.roleId = roleId;
    }
    public String getRoleName(){
        return this.roleName;
    }
    public void setRoleName(String roleName){
        this.roleName = roleName;
    }
    public Integer getOrderNo(){
        return this.orderNo;
    }
    public void setOrderNo(Integer orderNo){
        this.orderNo = orderNo;
    }
    public String getRemark(){
        return this.remark;
    }
    public void setRemark(String remark){
        this.remark = remark;
    }
    public String getCompanyId(){
        return this.companyId;
    }
    public void setCompanyId(String companyId){
        this.companyId = companyId;
    }
    public Integer getEnabled(){
        return this.enabled;
    }
    public void setEnabled(Integer enabled){
        this.enabled = enabled;
    }
    public Integer getIsDefault(){
        return this.isDefault;
    }
    public void setIsDefault(Integer isDefault){
        this.isDefault = isDefault;
    }
}
