package com.ctr.crm.moduls.system.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value="CallAddress", description="呼叫配置信息")
public class CallAddress implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1539497846338510322L;
	@ApiModelProperty(hidden=true)
	private Integer id;
	@ApiModelProperty(hidden=true)
    private String companyId;
	@ApiModelProperty(value = "呼叫帐号")
    private String companyCode;
	@ApiModelProperty(value = "呼叫地址")
    private String address;
	@ApiModelProperty(value = "安全ID")
    private String appid;
	@ApiModelProperty(value = "密钥")
    private String accessKey;
	@ApiModelProperty(value = "代理协议 ws wss")
    private String proxyProtocol;
	@ApiModelProperty(value = "代理地址")
    private String proxyIp;
	@ApiModelProperty(value = "代理端口")
    private String proxyPort;
	@ApiModelProperty(hidden=true)
    private Integer display;
	@ApiModelProperty(hidden=true)
    private Integer orderNo;
    @ApiModelProperty(value = "呼叫模式 1普通呼叫,2策略呼叫",hidden=true)
    private Integer callMode;

    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
    }
    public String getCompanyId(){
        return this.companyId;
    }
    public void setCompanyId(String companyId){
        this.companyId = companyId;
    }
    public String getCompanyCode(){
        return this.companyCode;
    }
    public void setCompanyCode(String companyCode){
        this.companyCode = companyCode;
    }
    public String getAddress(){
        return this.address;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public String getAppid(){
        return this.appid;
    }
    public void setAppid(String appid){
        this.appid = appid;
    }
    public String getAccessKey(){
        return this.accessKey;
    }
    public void setAccessKey(String accessKey){
        this.accessKey = accessKey;
    }
    public String getProxyProtocol(){
        return this.proxyProtocol;
    }
    public void setProxyProtocol(String proxyProtocol){
        this.proxyProtocol = proxyProtocol;
    }
    public String getProxyIp(){
        return this.proxyIp;
    }
    public void setProxyIp(String proxyIp){
        this.proxyIp = proxyIp;
    }
    public String getProxyPort(){
        return this.proxyPort;
    }
    public void setProxyPort(String proxyPort){
        this.proxyPort = proxyPort;
    }
    public Integer getDisplay(){
        return this.display;
    }
    public void setDisplay(Integer display){
        this.display = display;
    }
    public Integer getOrderNo(){
        return this.orderNo;
    }
    public void setOrderNo(Integer orderNo){
        this.orderNo = orderNo;
    }
    public Integer getCallMode(){
        return this.callMode;
    }
    public void setCallMode(Integer callMode){
        this.callMode = callMode;
    }
}
