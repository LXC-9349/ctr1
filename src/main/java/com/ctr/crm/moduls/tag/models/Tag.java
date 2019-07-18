package com.ctr.crm.moduls.tag.models;

import io.swagger.annotations.ApiParam;

import java.io.Serializable;
import java.util.Date;
/**
 * 客户标签
* @author DoubleLi
* @date  2019-04-19
*/
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiParam(hidden = true)
    private String id;
    @ApiParam(required = true,value = "标签名")
    private String tagName;
    @ApiParam(value = "颜色")
    private String color;
    @ApiParam(hidden = true)
    private Date createTime;
    @ApiParam(hidden = true)
    private Integer creator;
    @ApiParam(required = true,value = "标签组id")
    private String groupId;
    @ApiParam(hidden = true)
    private Integer deleted;


    public Tag(){
    }

    public Tag(String groupId) {
        this.groupId = groupId;
    }

    public Tag(String tagName, String groupId) {
        this.tagName = tagName;
        this.groupId = groupId;
    }

    public Tag(String id, String tagName, String groupId) {
        this.id = id;
        this.tagName = tagName;
        this.groupId = groupId;
    }

    public void setId (String id) {this.id = id;}
    public String getId(){ return id;} 
    public void setTagName (String tagName) {this.tagName = tagName;} 
    public String getTagName(){ return tagName;} 
    public void setColor (String color) {this.color = color;} 
    public String getColor(){ return color;} 
    public void setCreateTime (Date createTime) {this.createTime = createTime;} 
    public Date getCreateTime(){ return createTime;} 
    public void setCreator (Integer creator) {this.creator = creator;} 
    public Integer getCreator(){ return creator;} 
    public void setGroupId (String groupId) {this.groupId = groupId;} 
    public String getGroupId(){ return groupId;} 
    public void setDeleted (Integer deleted) {this.deleted = deleted;} 
    public Integer getDeleted(){ return deleted;}

}