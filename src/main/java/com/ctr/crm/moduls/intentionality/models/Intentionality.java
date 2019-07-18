package com.ctr.crm.moduls.intentionality.models;

import io.swagger.annotations.ApiParam;

import java.io.Serializable;
import java.util.Date;

/**
 * 意向度设置
 *
 * @author DoubleLi
 * @date 2019-04-19
 */
public class Intentionality implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiParam(hidden = true)
    private String id;
    @ApiParam(required = true, value = "1销售2红娘3才俊佳丽")
    private Integer type;
    @ApiParam(required = true, value = "意向度名称")
    private String name;
    @ApiParam(value = "0不计入库容1计入库容")
    private Integer isCapacity;
    @ApiParam(value = "意向度", hidden = true)
    private Integer caseClass;
    @ApiParam(hidden = true)
    private Date createTime;
    @ApiParam(hidden = true)
    private Integer deleted;


    public Intentionality() {
    }

    public Intentionality(Integer caseClass) {
        this.caseClass = caseClass;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setIsCapacity(Integer isCapacity) {
        this.isCapacity = isCapacity;
    }

    public Integer getIsCapacity() {
        return isCapacity;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public Integer getCaseClass() {
        return caseClass;
    }

    public void setCaseClass(Integer caseClass) {
        this.caseClass = caseClass;
    }
}