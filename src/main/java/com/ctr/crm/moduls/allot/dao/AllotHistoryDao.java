package com.ctr.crm.moduls.allot.dao;

import com.ctr.crm.moduls.allot.models.AllotHistory;

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-05-07
 */
public interface AllotHistoryDao {

    @SelectProvider(type = AllotHistorySqlProvider.class, method = "get")
    AllotHistory get(Integer autoId);

    @SelectProvider(type = AllotHistorySqlProvider.class, method = "findList")
    List<AllotHistory> findList(AllotHistory allotHistory);

    @SelectProvider(type = AllotHistorySqlProvider.class, method = "findAllList")
    List<AllotHistory> findAllList();

    @InsertProvider(type = AllotHistorySqlProvider.class, method = "insert")
    Integer insert(AllotHistory allotHistory);

    @InsertProvider(type = AllotHistorySqlProvider.class, method = "insertBatch")
    int insertBatch(@Param("allotHistorys") List<AllotHistory> allotHistorys);

    @UpdateProvider(type = AllotHistorySqlProvider.class, method = "update")
    int update(AllotHistory allotHistory);

    @UpdateProvider(type = AllotHistorySqlProvider.class, method = "delete")
    boolean delete(Integer autoId);

    class AllotHistorySqlProvider {

        public String get(Integer autoId) {
            return "select * from AllotHistory where autoId ='" + autoId + "'";
        }

        public String findList(AllotHistory allotHistory) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(allotHistory, "serialVersionUID"));
                    FROM("AllotHistory");
                }
            }.toString();
        }

        public String findAllList(AllotHistory allotHistory) {
            return "select * from AllotHistory  ";
        }

        public String insert(AllotHistory allotHistory) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, allotHistory, "serialVersionUID,autoId");
            return PojoUtils.getInsertSQL(AllotHistory.class.getSimpleName(), dataMap, "autoId");
        }

        public String insertBatch(List<AllotHistory> allotHistorys) {
            StringBuffer sql = new StringBuffer();
            sql.append("insert ignore into AllotHistory");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(allotHistorys.get(0), "serialVersionUID,autoId", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            sql.append("(").append(column.substring(0, column.lastIndexOf(","))).append(")").append(" values");
            for (AllotHistory bean :
                    allotHistorys) {
                sql.append("(");
                for (String val : PojoUtils.getArrayFieldsValue(bean, "serialVersionUID,autoId")) {
                    sql.append("'").append(val).append("',");
                }
                sql.deleteCharAt(sql.length() - 1);
                sql.append("),");
            }
            sql.deleteCharAt(sql.length() - 1);
            return sql.toString();
        }

        public String update(AllotHistory allotHistory) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, allotHistory, "serialVersionUID,autoId");
            return PojoUtils.getUpdateSQL("AllotHistory", dataMap, "id", allotHistory.getAutoId());
        }

        public String delete(Integer autoId) {
            return "delete from  AllotHistory  where autoId='" + autoId + "'";
        }
    }
}