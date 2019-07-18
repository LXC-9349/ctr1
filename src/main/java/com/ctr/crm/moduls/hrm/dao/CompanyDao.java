package com.ctr.crm.moduls.hrm.dao;

import org.apache.ibatis.annotations.Insert;

import com.ctr.crm.moduls.hrm.models.Company;

/**
 * 说明：
 * @author eric
 * @date 2019年4月16日 下午5:08:28
 */
public interface CompanyDao {

	@Insert("insert ignore into Company(companyId,companyName,accountId,accountSecret) values(#{companyId},#{companyName},#{accountId},#{accountSecret})")
	void insert(Company company);
}
