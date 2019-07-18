package com.ctr.crm.moduls.system.dao;

import com.ctr.crm.moduls.system.models.CallAddress;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;

import com.ctr.crm.commons.utils.PojoUtils;

/**
 * 说明：
 * @author eric
 * @date 2019年4月18日 上午11:39:15
 */
public interface CallAddressDao {

	@Insert("insert ignore into CallAddress(companyCode,address,appid,accessKey,proxyIp) values(#{companyCode},#{address},#{appid},#{accessKey},#{proxyIp})")
	void insert(CallAddress callAddress);
	
	@UpdateProvider(type=CallAddressSqlBuilder.class, method="buildUpdateSql")
	boolean update(CallAddress callAddress);
	
	@Select("select companyId,companyCode,address,appid,accessKey,proxyProtocol,proxyIp,proxyPort from CallAddress")
	CallAddress select();
	
	public static class CallAddressSqlBuilder{
		public static String buildUpdateSql(CallAddress callAddress){
			return new SQL(){
				{
					UPDATE("CallAddress");
					SET(PojoUtils.getUpdateSets(callAddress,"serialVersionUID,id,companyId", false));
					WHERE("companyId='0'");
				}
			}.toString();
		}
	}
}
