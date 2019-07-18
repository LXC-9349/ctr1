package com.ctr.crm.moduls.contract.models;

import io.swagger.annotations.ApiParam;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 合同订单
 *
 * @author DoubleLi
 * @date 2019-05-14
 */
public class ContractOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiParam(hidden = true)
    private Integer id;
    @ApiParam(hidden = true)
    private String contractNo;
    @ApiParam(value = "签订人名称", required = true)
    private String signerName;
    @ApiParam(value = "客户ID", required = true)
    private Long memberId;
    @ApiParam(value = "合同状态:0未完成1已完成", hidden = true)
    private Integer status;
    @ApiParam(value = "支付状态0待付款1未付清2付款完成", hidden = true)
    private Integer payStatus;
    @ApiParam(value = "合同名称", required = true)
    private String contractName;
    @ApiParam(value = "服务次数", required = true)
    private Integer serveCount;
    @ApiParam(value = "服务周期?月", required = true)
    private Integer serveCycle;
    @ApiParam(value = "contractAmount", required = true)
    private BigDecimal contractAmount;
    @ApiParam(hidden = true)
    private BigDecimal alreadyAmount;
    @ApiParam(hidden = true)
    private Date createTime;
    @ApiParam(hidden = true)
    private Date signTime;
    @ApiParam(hidden = true)
    private Integer workerId;
    @ApiParam(hidden = true)
    private Integer deleted;
    @ApiParam(hidden = true)
    private String annexFile;

    public ContractOrder() {
    }


    public String getAnnexFile() {
        return annexFile;
    }

    public void setAnnexFile(String annexFile) {
        this.annexFile = annexFile;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getSignerName() {
        return signerName;
    }

    public void setSignerName(String signerName) {
        this.signerName = signerName;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getContractName() {
        return contractName;
    }

    public void setServeCount(Integer serveCount) {
        this.serveCount = serveCount;
    }

    public Integer getServeCount() {
        return serveCount;
    }

    public Integer getServeCycle() {
        return serveCycle;
    }

    public void setServeCycle(Integer serveCycle) {
        this.serveCycle = serveCycle;
    }

    public BigDecimal getContractAmount() {
        return contractAmount;
    }

    public void setContractAmount(BigDecimal contractAmount) {
        this.contractAmount = contractAmount;
    }

    public BigDecimal getAlreadyAmount() {
        return alreadyAmount;
    }

    public void setAlreadyAmount(BigDecimal alreadyAmount) {
        this.alreadyAmount = alreadyAmount;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getSignTime() {
        return signTime;
    }

    public void setSignTime(Date signTime) {
        this.signTime = signTime;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Integer getDeleted() {
        return deleted;
    }

}