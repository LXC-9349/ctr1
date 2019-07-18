package com.ctr.crm.moduls.syslog.dao;

import com.ctr.crm.moduls.syslog.models.SysLog;

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-04-29
 */
public interface SysLogDao {

    @SelectProvider(type = SysLogSqlProvider.class, method = "get")
    SysLog get(String id);

    @SelectProvider(type = SysLogSqlProvider.class, method = "findList")
    List<SysLog> findList(SysLog sysLog);

    @SelectProvider(type = SysLogSqlProvider.class, method = "findAllList")
    List<SysLog> findAllList(SysLog sysLog);

    @InsertProvider(type = SysLogSqlProvider.class, method = "insert")
    void insert(SysLog sysLog);

    @InsertProvider(type = SysLogSqlProvider.class, method = "insertBatch")
    int insertBatch(List<SysLog> sysLogs);

    @UpdateProvider(type = SysLogSqlProvider.class, method = "update")
    int update(SysLog sysLog);

    @UpdateProvider(type = SysLogSqlProvider.class, method = "delete")
    boolean delete(String id);

    class SysLogSqlProvider {

        public String get(String id) {
            return "select * from SysLog where id ='" + id + "'";
        }

        public String findList(SysLog sysLog) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(sysLog, "serialVersionUID"));
                    FROM("SysLog");
                    WHERE("deleted=0");
                }
            }.toString();
        }

        public String findAllList(SysLog sysLog) {
            return "select * from SysLog where deleted=0";
        }

        public String insert(SysLog sysLog) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, sysLog, "serialVersionUID");
            return PojoUtils.getInsertSQL(SysLog.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<SysLog> sysLogs) {
            StringBuilder sql = new StringBuilder();
            sql.append("insert ignore into SysLog");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(sysLogs.get(0), "serialVersionUID", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            column.deleteCharAt(column.length() - 1);
            sql.append("(").append(column).append(")").append(" values");
            for (SysLog bean : sysLogs) {
                sql.append("(");
                for (String val : PojoUtils.getArrayFieldsValue(bean, "serialVersionUID")) {
                    sql.append("'").append(val).append("',");
                }
                sql.deleteCharAt(sql.length() - 1);
                sql.append("),");
            }
            sql.deleteCharAt(sql.length() - 1);
            return sql.toString();
        }

        public String update(SysLog sysLog) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, sysLog, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("SysLog", dataMap, "id", sysLog.getId());
        }

        public String delete(String id) {
            return "update SysLog set deleted=1 where id='" + id + "'";
        }
    }
}