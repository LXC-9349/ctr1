package com.ctr.crm.moduls.tag.models;

import io.swagger.annotations.ApiParam;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 客户标签组
 * @author DoubleLi
 * @date 2019-04-19
 */
public class TagGroup implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiParam(hidden = true)
    private String id;
    @ApiParam(required = true, value = "组名")
    private String groupName;
    @ApiParam(value = "是否多选默认1多选2单选")
    private Integer isMs;
    @ApiParam(hidden = true)
    private Date createTime;
    @ApiParam(hidden = true)
    private Integer deleted;
    @ApiParam(hidden = true)
    private List<Tag> sonTagList;

    public TagGroup() {
    }

    public TagGroup(String groupName) {
        this.groupName = groupName;
    }

    public TagGroup(String id, String groupName) {
        this.id = id;
        this.groupName = groupName;
    }

    public List<Tag> getSonTagList() {
        return sonTagList;
    }

    public void setSonTagList(List<Tag> sonTagList) {
        this.sonTagList = sonTagList;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
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

    public Integer getIsMs() {
        return isMs;
    }

    public void setIsMs(Integer isMs) {
        this.isMs = isMs;
    }
}