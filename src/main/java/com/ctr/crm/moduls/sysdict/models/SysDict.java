package com.ctr.crm.moduls.sysdict.models;

import io.swagger.annotations.ApiParam;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统字典
 * @author DoubleLi
 * @date 2019-04-23
 */
public class SysDict implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiParam(hidden = true)
    private String id;
    @ApiParam(required = true,value = "实际值")
    private String value;
    @ApiParam(required = true,value = "显示值")
    private String label;
    @ApiParam(required = true,value = "字典code")
    private String type;
    @ApiParam(required = true,value = "排序2")
    private Integer sort;
    private String remark;
    @ApiParam(value = "上级目录，不填默认顶级")
    private String parent;
    @ApiParam(hidden = true)
    private Date createTime;
    @ApiParam(value = "是否能编辑 1不能")
    private Integer isedit;
    @ApiParam(hidden = true)
    private Integer deleted;


    public SysDict() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getSort() {
        return sort;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getParent() {
        return parent;
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

    public Integer getIsedit() {
        return isedit;
    }

    public void setIsedit(Integer isedit) {
        this.isedit = isedit;
    }
}