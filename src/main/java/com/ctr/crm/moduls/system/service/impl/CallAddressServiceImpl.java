package com.ctr.crm.moduls.system.service.impl;

import com.ctr.crm.moduls.system.dao.CallAddressDao;
import com.ctr.crm.moduls.system.models.CallAddress;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.ucc.CallUtils;
import com.ctr.crm.commons.utils.LocalIpUtils;
import com.ctr.crm.moduls.system.service.CallAddressService;
import com.yunhus.redisclient.RedisProxy;

/**
 * 说明：
 * @author eric
 * @date 2019年4月18日 上午11:50:02
 */
@Service("callAddressService")
public class CallAddressServiceImpl implements CallAddressService {

	@Autowired
	private CallAddressDao callAddressDao;
	private RedisProxy redis = RedisProxy.getInstance();
	
	@Override
	public void insert(String companyCode, String appid, String accessKey, String addressIp) {
		CallAddress address = new CallAddress();
		address.setCompanyCode(companyCode);
		address.setAppid(appid);
		address.setAccessKey(accessKey);
		String localIp = addressIp;
		if(StringUtils.isBlank(localIp)){
			localIp = LocalIpUtils.INTRANET_IP;
		}
		address.setProxyIp(localIp);
		address.setAddress("http://" + localIp + ":4434/");
		callAddressDao.insert(address);
	}
	
	@Override
	public boolean update(CallAddress callAddress) {
		if(callAddress == null) return false;
		if(StringUtils.isAnyBlank(callAddress.getAccessKey(),
				callAddress.getAddress(),
				callAddress.getAppid(),
				callAddress.getCompanyCode()))
			return false;
		boolean result = callAddressDao.update(callAddress);
		if(result) {
			redis.delete("calladdress");
			CallUtils.cleanToken();
		}
		return result;
	}

	@Override
	public CallAddress select() {
		Object v = redis.get("calladdress");
		if(v != null){
			return (CallAddress) v;
		}
		CallAddress result = callAddressDao.select();
		if(result != null){
			redis.set("calladdress", result, Constants.exp_12hours);
		}
		return result;
	}

}
