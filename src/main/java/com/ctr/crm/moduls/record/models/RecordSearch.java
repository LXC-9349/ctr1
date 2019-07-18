package com.ctr.crm.moduls.record.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.ctr.crm.commons.CommonSearch;

/**
 * 说明：
 * @author eric
 * @date 2019年4月26日 上午10:44:47
 */
@ApiModel(value="RecordSearch", description="录音查询", parent=CommonSearch.class)
public class RecordSearch extends CommonSearch {

	@ApiModelProperty(value="录音编号", dataType="Integer")
	private Integer callId;
	@ApiModelProperty(value="客户ID", dataType="Long")
    private Long memberId;
    @ApiModelProperty(value="客户号码")
    private String phone;
    @ApiModelProperty(value="线路号码")
    private String telephone;
    @ApiModelProperty(value="发起时间")
    private String startTime;
    @ApiModelProperty(value="分机号")
    private Integer lineNum;
    @ApiModelProperty(value="呼叫类型 0呼出 1呼入", allowableValues="0,1")
    private Integer callType;
    @ApiModelProperty(value="工号")
    private Integer workerId;
    @ApiModelProperty(value="部门")
    private Integer deptId;
    @ApiModelProperty(value="是否接通 1接通 0未通", allowableValues="1,0")
    private Integer through;
	public Integer getCallId() {
		return callId;
	}
	public void setCallId(Integer callId) {
		this.callId = callId;
	}
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public Integer getLineNum() {
		return lineNum;
	}
	public void setLineNum(Integer lineNum) {
		this.lineNum = lineNum;
	}
	public Integer getCallType() {
		return callType;
	}
	public void setCallType(Integer callType) {
		this.callType = callType;
	}
	public Integer getWorkerId() {
		return workerId;
	}
	public void setWorkerId(Integer workerId) {
		this.workerId = workerId;
	}
	public Integer getDeptId() {
		return deptId;
	}
	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}
	public Integer getThrough() {
		return through;
	}
	public void setThrough(Integer through) {
		this.through = through;
	}
}
