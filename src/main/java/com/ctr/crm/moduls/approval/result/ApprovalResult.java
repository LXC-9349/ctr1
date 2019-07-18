package com.ctr.crm.moduls.approval.result;

public class ApprovalResult {
	
	/**
	 * 审批方法返回的状态
	 */
	private String type;
	
	/**
	 * 实际方法返回结果
	 */
	private Object resultData;
	
	/**
	 * 错误原因
	 */
	private String errMsg;
	
	/**
	 * 业务方法返回的ID
	 */
	private Object businessId;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getResultData() {
		return resultData;
	}
	public void setResultData(Object resultData) {
		this.resultData = resultData;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	public Object getBusinessId() {
		return businessId;
	}
	public void setBusinessId(Object businessId) {
		this.businessId = businessId;
	}
	
	
}
