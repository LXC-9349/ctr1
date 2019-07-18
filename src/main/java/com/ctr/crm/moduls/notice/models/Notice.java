package com.ctr.crm.moduls.notice.models;

import java.io.Serializable;
import java.util.Date;

/**
 * 通知
 *
 * @author DoubleLi
 * @date 2019-05-10
 */
public class Notice implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer workerId;
    private String workerName;
    private Integer type;//1系统通知 2员工通知
    private String title;
    private String content;
    private Date createTime;
    private Integer status;
    private Date readTime;
    private Integer deleted;


    public Notice() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Integer getDeleted() {
        return deleted;
    }

}