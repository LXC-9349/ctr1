package com.ctr.crm.moduls.sysrecyclingmemberconfig.dao;

import com.ctr.crm.moduls.sysrecyclingmemberconfig.models.SysRecyclingMemberConfig;

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;
/**
 * @author DoubleLi
 * @date  2019-04-23
 */
public interface SysRecyclingMemberConfigDao {

    @SelectProvider(type =SysRecyclingMemberConfigSqlProvider.class, method = "get")
    SysRecyclingMemberConfig get(String id);

    @SelectProvider(type =SysRecyclingMemberConfigSqlProvider.class, method = "findList")
    List<SysRecyclingMemberConfig> findList(SysRecyclingMemberConfig sysRecyclingMemberConfig);

    @SelectProvider(type =SysRecyclingMemberConfigSqlProvider.class, method = "findAllList")
    List<SysRecyclingMemberConfig> findAllList(SysRecyclingMemberConfig sysRecyclingMemberConfig);

    @InsertProvider(type =SysRecyclingMemberConfigSqlProvider.class, method = "insert")
    void insert(SysRecyclingMemberConfig sysRecyclingMemberConfig);

    @InsertProvider(type =SysRecyclingMemberConfigSqlProvider.class, method = "insertBatch")
    int insertBatch(List<SysRecyclingMemberConfig> sysRecyclingMemberConfigs);

    @UpdateProvider(type =SysRecyclingMemberConfigSqlProvider.class, method = "update")
    int update(SysRecyclingMemberConfig sysRecyclingMemberConfig);

    @UpdateProvider(type =SysRecyclingMemberConfigSqlProvider.class, method = "delete")
    boolean delete(String id);

    class SysRecyclingMemberConfigSqlProvider {

        public String get() {
            return "select * from SysRecyclingMemberConfig order by createTime desc limit 1";
        }

        public String findList(SysRecyclingMemberConfig sysRecyclingMemberConfig) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(sysRecyclingMemberConfig, "serialVersionUID"));
                    FROM("SysRecyclingMemberConfig");
                    WHERE("deleted=0");
                }
            }.toString();
        }

        public String findAllList(SysRecyclingMemberConfig sysRecyclingMemberConfig) {
            return "select * from SysRecyclingMemberConfig where deleted=0";
        }

        public String insert(SysRecyclingMemberConfig sysRecyclingMemberConfig) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, sysRecyclingMemberConfig, "serialVersionUID");
            return PojoUtils.getInsertSQL(SysRecyclingMemberConfig.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<SysRecyclingMemberConfig> sysRecyclingMemberConfigs) {
            StringBuilder sql = new StringBuilder();
            sql.append("insert ignore into SysRecyclingMemberConfig");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(sysRecyclingMemberConfigs.get(0), "serialVersionUID", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            column.deleteCharAt(column.length()-1);
            sql.append("(").append(column).append(")").append(" values");
            for (SysRecyclingMemberConfig bean:
                              sysRecyclingMemberConfigs) {
                sql.append("(");
                for (String val : PojoUtils.getArrayFieldsValue(bean, "serialVersionUID")) {
                    sql.append("'").append(val).append("',");
                }
                sql.deleteCharAt(sql.length()-1);
                sql.append("),");
            }
            sql.deleteCharAt(sql.length()-1);
            return sql.toString();
        }

        public String update(SysRecyclingMemberConfig sysRecyclingMemberConfig) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, sysRecyclingMemberConfig, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("SysRecyclingMemberConfig", dataMap, "id",sysRecyclingMemberConfig.getId());
        }

        public String delete(String id) {
            return "update SysRecyclingMemberConfig set deleted=1 where id='" + id + "'";
        }
    }
}