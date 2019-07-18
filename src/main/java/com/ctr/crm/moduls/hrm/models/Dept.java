package com.ctr.crm.moduls.hrm.models;

import java.io.Serializable;
import java.util.List;

public class Dept implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6412186049583166896L;
	private Integer deptId;
    private Integer parentId;
    private String deptName;
    private String companyId;
    private Integer deleted;
    private String structure;
    private Integer custom;
    private List<Dept> children;

    public Integer getDeptId(){
        return this.deptId;
    }
    public void setDeptId(Integer deptId){
        this.deptId = deptId;
    }
    public Integer getParentId(){
        return this.parentId;
    }
    public void setParentId(Integer parentId){
        this.parentId = parentId;
    }
    public String getDeptName(){
        return this.deptName;
    }
    public void setDeptName(String deptName){
        this.deptName = deptName;
    }
    public String getCompanyId(){
        return this.companyId;
    }
    public void setCompanyId(String companyId){
        this.companyId = companyId;
    }
    public Integer getDeleted(){
        return this.deleted;
    }
    public void setDeleted(Integer deleted){
        this.deleted = deleted;
    }
    public String getStructure(){
        return this.structure;
    }
    public void setStructure(String structure){
        this.structure = structure;
    }
	public Integer getCustom() {
		return custom;
	}
	public void setCustom(Integer custom) {
		this.custom = custom;
	}
	public List<Dept> getChildren() {
		return children;
	}
	public void setChildren(List<Dept> children) {
		this.children = children;
	}
}
