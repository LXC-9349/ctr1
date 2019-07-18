package com.ctr.crm.moduls.purview.models;

import java.io.Serializable;

public class Range implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6682492206167981233L;
	private Integer rangeId;
    private String rangeName;
    private Integer rangeValue;
    private Integer orderNo;
    private String companyId;

    public Integer getRangeId(){
        return this.rangeId;
    }
    public void setRangeId(Integer rangeId){
        this.rangeId = rangeId;
    }
    public String getRangeName(){
        return this.rangeName;
    }
    public void setRangeName(String rangeName){
        this.rangeName = rangeName;
    }
    public Integer getRangeValue(){
        return this.rangeValue;
    }
    public void setRangeValue(Integer rangeValue){
        this.rangeValue = rangeValue;
    }
    public Integer getOrderNo(){
        return this.orderNo;
    }
    public void setOrderNo(Integer orderNo){
        this.orderNo = orderNo;
    }
    public String getCompanyId(){
        return this.companyId;
    }
    public void setCompanyId(String companyId){
        this.companyId = companyId;
    }
}
