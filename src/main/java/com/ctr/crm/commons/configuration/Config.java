package com.ctr.crm.commons.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 说明：
 * @author eric
 * @date 2019年4月18日 上午11:29:39
 */
@Configuration
public class Config {

	@Value("${superaicloud.ucc.companycode}")
	private String companyCode;
	@Value("${superaicloud.ucc.appid}")
	private String appid;
	@Value("${superaicloud.ucc.accesskey}")
	private String accessKey;
	@Value("${superaicloud.ucc.addressip}")
	private String addressIp;
	@Value("${superaicloud.ucc.download.port}")
	private Integer downloadPort;
	
	public String getCompanyCode() {
		return companyCode;
	}
	
	public String getAppid() {
		return appid;
	}
	
	public String getAccessKey() {
		return accessKey;
	}
	
	public String getAddressIp() {
		return addressIp;
	}
	
	public Integer getDownloadPort() {
		return downloadPort;
	}
	
}
