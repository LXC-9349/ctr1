package com.ctr.crm.commons.result;

import java.io.Serializable;

public class ResponseData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4400810031527024007L;

	private int code;
	
	private String msg;
	
	private Object data;
	
	public ResponseData() {}
	
	public ResponseData(ResponseStatus status){
		this.code = status.getStatusCode();
		this.msg = status.getStatusDesc();
	}
	
	public ResponseData(ResponseStatus status, String msg){
		this.code = status.getStatusCode();
		this.msg = status.getStatusDesc();
		if(msg != null){
			this.msg = msg;
		}
	}
	
	public ResponseData(ResponseStatus status, Object data){
		this.code = status.getStatusCode();
		this.msg = status.getStatusDesc();
		this.data = data;
	}
	
	public ResponseData(ResponseStatus status, String msg, Object data){
		this.code = status.getStatusCode();
		this.msg = msg;
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setStatus(ResponseStatus status){
		this.code = status.getStatusCode();
		this.msg = status.getStatusDesc();
	}

	public void responseData(ResponseStatus status, String msg, Object data){
		this.code = status.getStatusCode();
		this.msg = status.getStatusDesc();
		if(msg != null) this.msg = msg;
		this.data = data;
	}
}
