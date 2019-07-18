package com.ctr.crm.moduls.system.service;

import com.ctr.crm.moduls.system.models.CallAddress;

/**
 * 说明：
 * @author eric
 * @date 2019年4月18日 上午11:47:46
 */
public interface CallAddressService {

	void insert(String companyCode, String appid, String accessKey, String addressIp);
	boolean update(CallAddress callAddress);
	CallAddress select();
}
