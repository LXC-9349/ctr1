package com.ctr.crm.moduls.smschannel.dao;

import com.ctr.crm.moduls.smschannel.models.SmsChannel;

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;
/**
 * @author DoubleLi
 * @date  2019-04-28
 */
public interface SmsChannelDao {

    @SelectProvider(type =SmsChannelSqlProvider.class, method = "get")
    SmsChannel get(String id);

    @SelectProvider(type =SmsChannelSqlProvider.class, method = "findList")
    List<SmsChannel> findList(SmsChannel smsChannel);

    @SelectProvider(type =SmsChannelSqlProvider.class, method = "findAllList")
    List<SmsChannel> findAllList(SmsChannel smsChannel);

    @InsertProvider(type =SmsChannelSqlProvider.class, method = "insert")
    void insert(SmsChannel smsChannel);

    @InsertProvider(type =SmsChannelSqlProvider.class, method = "insertBatch")
    int insertBatch(List<SmsChannel> smsChannels);

    @UpdateProvider(type =SmsChannelSqlProvider.class, method = "update")
    int update(SmsChannel smsChannel);

    @UpdateProvider(type =SmsChannelSqlProvider.class, method = "delete")
    boolean delete(String id);

    class SmsChannelSqlProvider {

        public String get(String id) {
            return "select * from SmsChannel where id ='" + id + "'";
        }

        public String findList(SmsChannel smsChannel) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(smsChannel, "serialVersionUID"));
                    FROM("SmsChannel");
                    WHERE("deleted=0");
                }
            }.toString();
        }

        public String findAllList(SmsChannel smsChannel) {
            return "select * from SmsChannel where deleted=0";
        }

        public String insert(SmsChannel smsChannel) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, smsChannel, "serialVersionUID");
            return PojoUtils.getInsertSQL(SmsChannel.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<SmsChannel> smsChannels) {
            StringBuilder sql = new StringBuilder();
            sql.append("insert ignore into SmsChannel");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(smsChannels.get(0), "serialVersionUID", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            column.deleteCharAt(column.length()-1);
            sql.append("(").append(column).append(")").append(" values");
            for (SmsChannel bean:
                              smsChannels) {
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

        public String update(SmsChannel smsChannel) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, smsChannel, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("SmsChannel", dataMap, "id",smsChannel.getId());
        }

        public String delete(String id) {
            return "update SmsChannel set deleted=1 where id='" + id + "'";
        }
    }
}