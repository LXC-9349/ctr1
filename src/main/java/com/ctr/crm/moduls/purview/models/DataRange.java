package com.ctr.crm.moduls.purview.models;

import com.ctr.crm.commons.rbac.RangeField;

/**
 * 说明：数据范围信息
 * @author eric
 * @date 2019年4月26日 下午2:06:20
 * @since {@link RangeField}
 */
public class DataRange {

	/**
	 * 当范围值为个人时，此字段为当前登录人ID
	 * 其他范围值，此字段为空
	 */
	private Integer workerId;
	/**
	 * 部门结构值 如果符号为like，该值会拼接为 结构值+% <br>
	 * 当范围值为个人和全公司时，此字段为空
	 */
	private String structure;
	/**
	 * mysql符号 如：like、=
	 */
	private String symbol;
	/**
	 * 范围值
	 * @see RangeField
	 */
	private Integer range;
	public Integer getWorkerId() {
		return workerId;
	}
	public void setWorkerId(Integer workerId) {
		this.workerId = workerId;
	}
	public String getStructure() {
		return structure;
	}
	public void setStructure(String structure) {
		this.structure = structure;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Integer getRange() {
		return range;
	}
	public void setRange(Integer range) {
		this.range = range;
	}
}
