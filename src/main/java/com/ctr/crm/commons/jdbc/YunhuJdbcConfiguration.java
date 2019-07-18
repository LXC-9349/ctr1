package com.ctr.crm.commons.jdbc;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;

/**
 * 说明：springjdbc
 * @author eric
 * @date 2019年04月09日 上午16:41:55
 */
@Configuration
public class YunhuJdbcConfiguration {

	@Autowired
	private DataSource dataSource;
	
	@Bean(name="crmJdbc")
	public JdbcOperations crmJdbcOperations(){
		return new YunhuJdbcTemplate(dataSource);
	}
}
