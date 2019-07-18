package com.ctr.crm.commons.jdbc;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

/**
 * 说明：
 * @author eric
 * @date 2018年12月25日 下午5:20:45
 */
public interface YunhuJdbcOperations extends JdbcOperations {

	NamedParameterJdbcOperations getNamedParameterJdbcOperations();
}
