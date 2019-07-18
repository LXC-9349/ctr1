package com.ctr.crm.moduls.meetrecord.dao;

import com.ctr.crm.moduls.meetrecord.models.MeetRecord;

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-05-23
 */
public interface MeetRecordDao {

    @SelectProvider(type = MeetRecordSqlProvider.class, method = "get")
    MeetRecord get(Integer id);

    @SelectProvider(type = MeetRecordSqlProvider.class, method = "findList")
    List<MeetRecord> findList(MeetRecord meetRecord);

    @SelectProvider(type = MeetRecordSqlProvider.class, method = "findAllList")
    List<MeetRecord> findAllList(MeetRecord meetRecord);

    @InsertProvider(type = MeetRecordSqlProvider.class, method = "insert")
    int insert(MeetRecord meetRecord);

    @InsertProvider(type = MeetRecordSqlProvider.class, method = "insertBatch")
    int insertBatch(@Param("meetRecords") List<MeetRecord> meetRecords);

    @UpdateProvider(type = MeetRecordSqlProvider.class, method = "update")
    int update(MeetRecord meetRecord);

    @UpdateProvider(type = MeetRecordSqlProvider.class, method = "delete")
    boolean delete(Integer id);

    @Select("select IFNULL((select max(id)+1 from MeetRecord),1) from dual")
    Integer getID();

    class MeetRecordSqlProvider {

        public String get(Integer id) {
            return "select * from MeetRecord where id ='" + id + "'";
        }

        public String findList(MeetRecord meetRecord) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(meetRecord, "serialVersionUID"));
                    FROM("MeetRecord");
                    WHERE("deleted=0");
                    if (meetRecord.getMemberId() != null) {
                        WHERE("memberId=#{memberId}");
                    }
                    if (meetRecord.getMeetId() != null) {
                        WHERE("meetId=#{meetId}");
                    }
                }
            }.toString();
        }

        public String findAllList(MeetRecord meetRecord) {
            return "select * from MeetRecord where deleted=0";
        }

        public String insert(MeetRecord meetRecord) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, meetRecord, "serialVersionUID");
            return PojoUtils.getInsertSQL(MeetRecord.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<MeetRecord> meetRecords) {
            StringBuffer sql = new StringBuffer();
            sql.append("insert ignore into MeetRecord");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(meetRecords.get(0), "serialVersionUID", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            sql.append("(").append(column.substring(0, column.lastIndexOf(","))).append(")").append(" values");
            for (MeetRecord bean : meetRecords) {
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

        public String update(MeetRecord meetRecord) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, meetRecord, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("MeetRecord", dataMap, "id", meetRecord.getId());
        }

        public String delete(Integer id) {
            return "update MeetRecord set deleted=1 where id='" + id + "'";
        }
    }
}