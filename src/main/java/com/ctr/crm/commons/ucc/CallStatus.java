package com.ctr.crm.commons.ucc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 分机状态
 * @author Administrator
 *
 */
@ApiModel(value="分机状态")
public class CallStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8003722679628843896L;

	@ApiModelProperty(value="分机号")
	private String extnumber;
	@ApiModelProperty(value="分机状态")
	private Integer status;
	@ApiModelProperty(value="分机状态描述")
	private String statusDesc;
	@ApiModelProperty(value="客户号码")
	private String phone;
	@ApiModelProperty(value="通话时长(状态为通话中时)")
	private Integer callduration;
	@ApiModelProperty(value="空闲时长")
	private Integer idleduration;
	public String getExtnumber() {
		return extnumber;
	}
	public void setExtnumber(String extnumber) {
		this.extnumber = extnumber;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Integer getCallduration() {
		return callduration;
	}
	public void setCallduration(Integer callduration) {
		this.callduration = callduration;
	}
	public Integer getIdleduration() {
		return idleduration;
	}
	public void setIdleduration(Integer idleduration) {
		this.idleduration = idleduration;
	}
}
