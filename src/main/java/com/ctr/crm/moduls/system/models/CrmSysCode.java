package com.ctr.crm.moduls.system.models;

import java.io.Serializable;

public class CrmSysCode implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4856169088929009446L;
	private Integer id;
    private Integer codeId;
    private Integer parentId;
    private String codeName;
    private Integer vType;
    private Integer codeType;
    private String remark;

    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
    }
    public Integer getCodeId(){
        return this.codeId;
    }
    public void setCodeId(Integer codeId){
        this.codeId = codeId;
    }
    public Integer getParentId(){
        return this.parentId;
    }
    public void setParentId(Integer parentId){
        this.parentId = parentId;
    }
    public String getCodeName(){
        return this.codeName;
    }
    public void setCodeName(String codeName){
        this.codeName = codeName;
    }
    public Integer getVType(){
        return this.vType;
    }
    public void setVType(Integer vType){
        this.vType = vType;
    }
    public Integer getCodeType(){
        return this.codeType;
    }
    public void setCodeType(Integer codeType){
        this.codeType = codeType;
    }
    public String getRemark(){
        return this.remark;
    }
    public void setRemark(String remark){
        this.remark = remark;
    }
}
