package com.ctr.crm.moduls.globalsetting.models;

import io.swagger.annotations.ApiParam;

import java.io.Serializable;
import java.util.Date;

/**
 * 全局变量设置
 *
 * @author DoubleLi
 * @date 2019-04-19
 */
public class GlobalSetting implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiParam(hidden = true)
    private String id;
    @ApiParam(required = true, value = "全局变量类型名称")
    private String name;
    @ApiParam(required = true, value = "en_name")
    private String type;
    @ApiParam(required = true, value = "实际值")
    private String value;
    @ApiParam(hidden = true)
    private String desc;
    @ApiParam(hidden = true)
    private Date createTime;
    @ApiParam(hidden = true)
    private Integer deleted;


    public GlobalSetting() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
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

}