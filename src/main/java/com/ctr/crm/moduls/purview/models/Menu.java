package com.ctr.crm.moduls.purview.models;

import java.io.Serializable;

public class Menu implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7817840206687614773L;
	private Integer menuId;
    private String menuName;
    private Integer parentId;
    private String url;
    private Integer isUse;
    private Integer orderNo;
    private Integer foundational;
    private Integer incremental;

    public Integer getMenuId(){
        return this.menuId;
    }
    public void setMenuId(Integer menuId){
        this.menuId = menuId;
    }
    public String getMenuName(){
        return this.menuName;
    }
    public void setMenuName(String menuName){
        this.menuName = menuName;
    }
    public Integer getParentId(){
        return this.parentId;
    }
    public void setParentId(Integer parentId){
        this.parentId = parentId;
    }
    public String getUrl(){
        return this.url;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public Integer getIsUse(){
        return this.isUse;
    }
    public void setIsUse(Integer isUse){
        this.isUse = isUse;
    }
    public Integer getOrderNo(){
        return this.orderNo;
    }
    public void setOrderNo(Integer orderNo){
        this.orderNo = orderNo;
    }
    public Integer getFoundational(){
        return this.foundational;
    }
    public void setFoundational(Integer foundational){
        this.foundational = foundational;
    }
    public Integer getIncremental(){
        return this.incremental;
    }
    public void setIncremental(Integer incremental){
        this.incremental = incremental;
    }
}
