package com.ctr.crm.moduls.allot.dao;

import com.ctr.crm.moduls.allot.models.AdjustHistory;

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;
/**
 * @author DoubleLi
 * @date  2019-05-08
 */
public interface AdjustHistoryDao {

    @SelectProvider(type =AdjustHistorySqlProvider.class, method = "get")
    AdjustHistory get(Integer id);

    @SelectProvider(type =AdjustHistorySqlProvider.class, method = "findList")
    List<AdjustHistory> findList(AdjustHistory adjustHistory);

    @SelectProvider(type =AdjustHistorySqlProvider.class, method = "findAllList")
    List<AdjustHistory> findAllList(AdjustHistory adjustHistory);

    @InsertProvider(type =AdjustHistorySqlProvider.class, method = "insert")
    boolean insert(AdjustHistory adjustHistory);

    @InsertProvider(type =AdjustHistorySqlProvider.class, method = "insertBatch")
    int insertBatch(@Param("adjustHistorys") List<AdjustHistory> adjustHistorys);

    @UpdateProvider(type =AdjustHistorySqlProvider.class, method = "update")
    int update(AdjustHistory adjustHistory);

    @UpdateProvider(type =AdjustHistorySqlProvider.class, method = "delete")
    boolean delete(Integer id);

    class AdjustHistorySqlProvider {

        public String get(Integer id) {
            return "select * from AdjustHistory where id ='" + id + "'";
        }

        public String findList(AdjustHistory adjustHistory) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(adjustHistory, "serialVersionUID"));
                    FROM("AdjustHistory");
                }
            }.toString();
        }

        public String findAllList(AdjustHistory adjustHistory) {
            return "select * from AdjustHistory ";
        }

        public String insert(AdjustHistory adjustHistory) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, adjustHistory, "serialVersionUID");
            return PojoUtils.getInsertSQL(AdjustHistory.class.getSimpleName(), dataMap, "id");
        }

        public String insertBatch(List<AdjustHistory> adjustHistorys) {
            StringBuffer sql = new StringBuffer();
            sql.append("insert ignore into AdjustHistory");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(adjustHistorys.get(0), "serialVersionUID,id", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            sql.append("(").append(column.substring(0, column.lastIndexOf(","))).append(")").append(" values");
            for (AdjustHistory bean :
                    adjustHistorys) {
                sql.append("(");
                for (String val : PojoUtils.getArrayFieldsValue(bean, "serialVersionUID,id")) {
                    sql.append("'").append(val).append("',");
                }
                sql.deleteCharAt(sql.length() - 1);
                sql.append("),");
            }
            sql.deleteCharAt(sql.length() - 1);
            return sql.toString();
        }

        public String update(AdjustHistory adjustHistory) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, adjustHistory, "serialVersionUID,id");
            return PojoUtils.getUpdateSQL("AdjustHistory", dataMap, "id",adjustHistory.getId());
        }

        public String delete(Integer id) {
            return "update AdjustHistory set deleted=1 where id='" + id + "'";
        }
    }
}