package com.ctr.crm.moduls.smsrecord.dao;

import com.ctr.crm.moduls.smsrecord.models.SmsRecord;

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-04-28
 */
public interface SmsRecordDao {

    @SelectProvider(type = SmsRecordSqlProvider.class, method = "get")
    SmsRecord get(Integer id);

    @SelectProvider(type = SmsRecordSqlProvider.class, method = "findList")
    List<SmsRecord> findList(SmsRecord SmsRecord);

    @SelectProvider(type = SmsRecordSqlProvider.class, method = "findAllList")
    List<SmsRecord> findAllList(SmsRecord SmsRecord);

    @InsertProvider(type = SmsRecordSqlProvider.class, method = "insert")
    void insert(SmsRecord SmsRecord);

    @InsertProvider(type = SmsRecordSqlProvider.class, method = "insertBatch")
    int insertBatch(List<SmsRecord> SmsRecords);

    @UpdateProvider(type = SmsRecordSqlProvider.class, method = "update")
    int update(SmsRecord SmsRecord);

    @UpdateProvider(type = SmsRecordSqlProvider.class, method = "delete")
    boolean delete(Integer id);

    @Select("select * from SmsRecord where smsId=#{smsId}")
    SmsRecord findSmsRecordBySmsId(String smsId);

    @Select("select IFNULL((select max(id)+1 from SmsRecord),1) from dual")
    Integer getID();

    class SmsRecordSqlProvider {

        public String get(Integer id) {
            return "select * from SmsRecord where id ='" + id + "'";
        }

        public String findList(SmsRecord SmsRecord) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(SmsRecord, "serialVersionUID"));
                    FROM("SmsRecord");
                    WHERE("deleted=0");
                }
            }.toString();
        }

        public String findAllList(SmsRecord SmsRecord) {
            return "select * from SmsRecord where deleted=0";
        }

        public String insert(SmsRecord SmsRecord) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, SmsRecord, "serialVersionUID");
            return PojoUtils.getInsertSQL(SmsRecord.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<SmsRecord> SmsRecords) {
            StringBuilder sql = new StringBuilder();
            sql.append("insert ignore into SmsRecord");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(SmsRecords.get(0), "serialVersionUID", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            column.deleteCharAt(column.length()-1);
            sql.append("(").append(column).append(")").append(" values");
            for (SmsRecord bean :
                    SmsRecords) {
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

        public String update(SmsRecord SmsRecord) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, SmsRecord, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("SmsRecord", dataMap, "id", SmsRecord.getId());
        }

        public String delete(Integer id) {
            return "update SmsRecord set deleted=1 where id='" + id + "'";
        }
    }
}