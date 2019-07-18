package com.ctr.crm.commons.jdbc;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * 说明：扩展JdbcTemplate
 * @author eric
 * @date 2018年12月25日 下午5:19:04
 */
public class YunhuJdbcTemplate extends JdbcTemplate implements YunhuJdbcOperations{

	public YunhuJdbcTemplate(DataSource dataSource) {
		super(dataSource);
	}

	public NamedParameterJdbcOperations getNamedParameterJdbcOperations() {
		return new NamedParameterJdbcTemplate(this);
	}
}
