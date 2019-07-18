package com.ctr.crm.moduls.allocation.models;

import java.io.Serializable;
import java.util.Date;

/**
 * 分配规则条件设置
 *
 * @author DoubleLi
 * @date 2019-04-29
 */
public class AllocationCondition implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String ruleId;
    private String filed;
    private String connect;
    private Integer type;
    private String filedValue;
    private Integer row;
    private Integer position;
    private Date createTime;
    private Integer deleted;


    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public AllocationCondition() {
    }

    public AllocationCondition(String ruleId) {
        this.ruleId = ruleId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public void setFiled(String filed) {
        this.filed = filed;
    }

    public String getFiled() {
        return filed;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }

    public String getConnect() {
        return connect;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setFiledValue(String filedValue) {
        this.filedValue = filedValue;
    }

    public String getFiledValue() {
        return filedValue;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getRow() {
        return row;
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

    @Override
    public String toString() {
        return "AllocationCondition [id=" + id + ", allotruleIdId=" + ruleId + ", filed=" + filed + ", connect=" + connect
                + ", type=" + type + ", filedValue=" + filedValue + ", row=" + row + ", position=" + position
                + ", createTime=" + createTime + ", deleted=" + deleted + "]";
    }

}