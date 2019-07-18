package com.ctr.crm.moduls.base.dao;

import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Map;

/**
 * 功能描述:
 *
 * @author: DoubleLi
 * @date: 2019/4/18 15:33
 */
@Scope("prototype")
public interface BaseDao {

    /**
     * 总条数
     *
     * @param searchCountSql
     * @return
     * @time 2018年8月10日
     * @author DoubleLi
     */
    @SelectProvider(type = BaseDaoSqlProvider.class, method = "selectLong")
    Long selectLong(String searchCountSql);

    /**
     * sql数据
     *
     * @param sql
     * @return
     * @time 2018年8月10日
     * @author DoubleLi
     */
    @SelectProvider(type = BaseDaoSqlProvider.class, method = "select")
    List<Map<String, Object>> select(String sql);

    class BaseDaoSqlProvider {
        public String selectLong(String searchCountSql) {
            return searchCountSql;
        }

        public String select(String sql) {
            return sql;
        }
    }
}
