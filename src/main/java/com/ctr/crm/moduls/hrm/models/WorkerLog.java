package com.ctr.crm.moduls.hrm.models;

import java.io.Serializable;
import java.util.Date;

/**
 * 说明：
 * @author eric
 * @date 2019年4月22日 下午3:45:37
 */
public class WorkerLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5681648771685600908L;

	private Integer workerId;
	private Integer logType;
	private Date logTime;
	private String token;
	public Integer getWorkerId() {
		return workerId;
	}
	public void setWorkerId(Integer workerId) {
		this.workerId = workerId;
	}
	public Integer getLogType() {
		return logType;
	}
	public void setLogType(Integer logType) {
		this.logType = logType;
	}
	public Date getLogTime() {
		return logTime;
	}
	public void setLogTime(Date logTime) {
		this.logTime = logTime;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
}
