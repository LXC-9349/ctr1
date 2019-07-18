package com.ctr.crm.moduls.sales.models;

import java.io.Serializable;
import java.util.Date;

public class CaseClassChangeDetail implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4878803458838015807L;
	private Integer autoId;
    private Integer workerId;
    private Integer deptId;
    private Long memberId;
    private Integer oldClass;
    private Integer newClass;
    private Date writeTime;
    private Integer dataType;
    private String companyId;

    public Integer getAutoId(){
        return this.autoId;
    }
    public void setAutoId(Integer autoId){
        this.autoId = autoId;
    }
    public Integer getWorkerId(){
        return this.workerId;
    }
    public void setWorkerId(Integer workerId){
        this.workerId = workerId;
    }
    public Integer getDeptId(){
        return this.deptId;
    }
    public void setDeptId(Integer deptId){
        this.deptId = deptId;
    }
    public Long getMemberId(){
        return this.memberId;
    }
    public void setMemberId(Long memberId){
        this.memberId = memberId;
    }
    public Integer getOldClass(){
        return this.oldClass;
    }
    public void setOldClass(Integer oldClass){
        this.oldClass = oldClass;
    }
    public Integer getNewClass(){
        return this.newClass;
    }
    public void setNewClass(Integer newClass){
        this.newClass = newClass;
    }
    public Date getWriteTime(){
        return this.writeTime;
    }
    public void setWriteTime(Date writeTime){
        this.writeTime = writeTime;
    }
    public Integer getDataType(){
        return this.dataType;
    }
    public void setDataType(Integer dataType){
        this.dataType = dataType;
    }
    public String getCompanyId(){
        return this.companyId;
    }
    public void setCompanyId(String companyId){
        this.companyId = companyId;
    }
}
