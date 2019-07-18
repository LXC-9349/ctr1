package com.ctr.crm.moduls.contract.models;

import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 合同资金流水
 *
 * @author DoubleLi
 * @date 2019-05-14
 */
public class ContractOrderPayRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiParam(hidden = true)
    private Integer id;
    @ApiParam(value = "合同ID",required = true)
    private Integer contractId;
    @ApiParam(value = "缴费金额",required = true)
    private BigDecimal amount;
    @ApiParam(hidden = true)
    private Integer workerId;
    @ApiParam(hidden = true)
    private BigDecimal alrAmount;
    @ApiParam(hidden = true)
    private Date createTime;
    @ApiParam(hidden = true)
    private Integer deleted;
    @ApiParam(hidden = true)
    private Integer status;
    @ApiParam(value = "备注",required = false)
    private String reason;
    @ApiParam(hidden = true)
    private String pic;
    @ApiParam(value = "支付时间",required = true)
    @DateTimeFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private Date paymentTime;
    @ApiParam(hidden = true)
    private Date approvalTime;
    public ContractOrderPayRecord() {
    }

    public String getPic() {
        return pic;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Date getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(Date approvalTime) {
        this.approvalTime = approvalTime;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getId() {
        return id;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public void setAlrAmount(BigDecimal alrAmount) {
        this.alrAmount = alrAmount;
    }

    public BigDecimal getAlrAmount() {
        return alrAmount;
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