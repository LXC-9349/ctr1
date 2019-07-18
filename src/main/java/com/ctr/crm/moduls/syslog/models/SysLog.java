package com.ctr.crm.moduls.syslog.models;

import io.swagger.annotations.ApiParam;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统日志
 *
 * @author DoubleLi
 * @date 2019-04-29
 */
public class SysLog implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiParam(hidden = true)
    private String id;
    private String field1;
    private String field2;
    private String field3;
    private String field4;
    private String logText;
    @ApiParam(value = "日志类型 字典SysDict log_type")
    private String logType;
    @ApiParam(value = "操作人")
    private Integer workerId;
    @ApiParam(value = "操作动作 字典SysDict log_action")
    private String logAction;
    @ApiParam(hidden = true)
    private Date createTime;
    @ApiParam(hidden = true)
    private Integer deleted;


    public SysLog() {
    }

    public SysLog(String field1, String field2, String field3, String field4, String logText, String logType, Integer workerId, String logAction) {
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
        this.logText = logText;
        this.logType = logType;
        this.workerId = workerId;
        this.logAction = logAction;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField1() {
        return field1;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField2() {
        return field2;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public String getField3() {
        return field3;
    }

    public void setField4(String field4) {
        this.field4 = field4;
    }

    public String getField4() {
        return field4;
    }

    public void setLogText(String logText) {
        this.logText = logText;
    }

    public String getLogText() {
        return logText;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getLogType() {
        return logType;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public void setLogAction(String logAction) {
        this.logAction = logAction;
    }

    public String getLogAction() {
        return logAction;
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