package com.ctr.crm.moduls.smsconfig.dao;

import com.ctr.crm.moduls.smsconfig.models.SmsConfig;

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;
/**
 * @author DoubleLi
 * @date  2019-04-28
 */
public interface SmsConfigDao {

    @SelectProvider(type =SmsConfigSqlProvider.class, method = "get")
    SmsConfig get(String id);

    @SelectProvider(type =SmsConfigSqlProvider.class, method = "findList")
    List<SmsConfig> findList(SmsConfig smsConfig);

    @SelectProvider(type =SmsConfigSqlProvider.class, method = "findAllList")
    List<SmsConfig> findAllList(SmsConfig smsConfig);

    @InsertProvider(type =SmsConfigSqlProvider.class, method = "insert")
    void insert(SmsConfig smsConfig);

    @InsertProvider(type =SmsConfigSqlProvider.class, method = "insertBatch")
    int insertBatch(List<SmsConfig> smsConfigs);

    @UpdateProvider(type =SmsConfigSqlProvider.class, method = "update")
    int update(SmsConfig smsConfig);

    @UpdateProvider(type =SmsConfigSqlProvider.class, method = "delete")
    boolean delete(String id);

    @Select("select * from SmsConfig where status =1 limit 1")
    SmsConfig getEnable();

    class SmsConfigSqlProvider {

        public String get(String id) {
            return "select * from SmsConfig where id ='" + id + "'";
        }

        public String findList(SmsConfig smsConfig) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(smsConfig, "serialVersionUID"));
                    FROM("SmsConfig");
                    WHERE("deleted=0");
                    if(smsConfig.getStatus()!=null){
                        WHERE("status=#{status}");
                    }
                }
            }.toString();
        }

        public String findAllList(SmsConfig smsConfig) {
            return "select * from SmsConfig where deleted=0";
        }

        public String insert(SmsConfig smsConfig) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, smsConfig, "serialVersionUID");
            return PojoUtils.getInsertSQL(SmsConfig.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<SmsConfig> smsConfigs) {
            StringBuilder sql = new StringBuilder();
            sql.append("insert ignore into SmsConfig");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(smsConfigs.get(0), false)) {
                column.append("`").append(s).append("`").append(",");
            }
            column.deleteCharAt(column.length()-1);
            sql.append("(").append(column).append(")").append(" values");
            for (SmsConfig bean:
                              smsConfigs) {
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

        public String update(SmsConfig smsConfig) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, smsConfig, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("SmsConfig", dataMap, "id",smsConfig.getId());
        }

        public String delete(String id) {
            return "update SmsConfig set deleted=1 where id='" + id + "'";
        }
    }
}