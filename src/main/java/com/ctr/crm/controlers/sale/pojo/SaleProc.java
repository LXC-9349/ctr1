package com.ctr.crm.controlers.sale.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

/**
 * 说明：
 * @author eric
 * @date 2019年5月20日 上午11:13:49
 */
@ApiModel(value="SaleProc")
public class SaleProc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6831344206403244317L;
	@ApiModelProperty(name="memberId", value="客户ID", required=true, dataType="Long")
	private Long memberId;
	@ApiModelProperty(name="procId", value="小记ID，点击拨打电话时返回", required=false, dataType="Long")
	private Long procId;
	@ApiModelProperty(name="caseClass", value="意向度", required=true, dataType="Integer")
	private Integer caseClass;
	@ApiModelProperty(name="nextContactTime", value="下次跟进时间", required=true, dataType="Date")
	private Date nextContactTime;
	@ApiModelProperty(name="procItem", value="跟进记录", required=true, dataType="String")
	private String procItem;
	@ApiModelProperty(name="files", value="图片", required=false)
	private MultipartFile[] files;
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public Long getProcId() {
		return procId;
	}
	public void setProcId(Long procId) {
		this.procId = procId;
	}
	public Integer getCaseClass() {
		return caseClass;
	}
	public void setCaseClass(Integer caseClass) {
		this.caseClass = caseClass;
	}
	public Date getNextContactTime() {
		return nextContactTime;
	}
	public void setNextContactTime(Date nextContactTime) {
		this.nextContactTime = nextContactTime;
	}
	public String getProcItem() {
		return procItem;
	}
	public void setProcItem(String procItem) {
		this.procItem = procItem;
	}
	public MultipartFile[] getFiles() {
		return files;
	}
	public void setFiles(MultipartFile[] files) {
		this.files = files;
	}
}
