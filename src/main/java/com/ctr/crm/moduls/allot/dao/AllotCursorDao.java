package com.ctr.crm.moduls.allot.dao;

import com.ctr.crm.moduls.allot.models.AllotCursor;

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-05-05
 */
public interface AllotCursorDao {

    @SelectProvider(type = AllotCursorSqlProvider.class, method = "get")
    AllotCursor get(Integer id);

    @SelectProvider(type = AllotCursorSqlProvider.class, method = "findList")
    List<AllotCursor> findList(AllotCursor allotCursor);

    @SelectProvider(type = AllotCursorSqlProvider.class, method = "findAllList")
    List<AllotCursor> findAllList(AllotCursor allotCursor);

    @InsertProvider(type = AllotCursorSqlProvider.class, method = "insert")
    void insert(AllotCursor allotCursor);

    @InsertProvider(type = AllotCursorSqlProvider.class, method = "insertBatch")
    int insertBatch(@Param("allotCursors") List<AllotCursor> allotCursors);

    @UpdateProvider(type = AllotCursorSqlProvider.class, method = "update")
    int update(AllotCursor allotCursor);

    @UpdateProvider(type = AllotCursorSqlProvider.class, method = "delete")
    boolean delete(Integer id);

    @Select("select * from AllotCursor where ruleId=#{id} order by updateTime asc")
    List<AllotCursor> findByRuleId(String id);

    @Select("select * from AllotCursor where ruleId=#{ruleId} order by updateTime asc limit 1")
    AllotCursor findNext(String ruleId);

    class AllotCursorSqlProvider {

        public String get(Integer id) {
            return "select * from AllotCursor where id ='" + id + "'";
        }

        public String findList(AllotCursor allotCursor) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(allotCursor, "serialVersionUID"));
                    FROM("AllotCursor");
                }
            }.toString();
        }

        public String findAllList(AllotCursor allotCursor) {
            return "select * from AllotCursor where deleted=0";
        }

        public String insert(AllotCursor allotCursor) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, allotCursor, "serialVersionUID,id");
            return PojoUtils.getInsertSQL(AllotCursor.class.getSimpleName(), dataMap, "id");
        }

        public String insertBatch(List<AllotCursor> allotCursors) {
            StringBuffer sql = new StringBuffer();
            sql.append("insert ignore into AllotCursor");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(allotCursors.get(0), "serialVersionUID,id", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            sql.append("(").append(column.substring(0, column.lastIndexOf(","))).append(")").append(" values");
            for (AllotCursor bean :
                    allotCursors) {
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

        public String update(AllotCursor allotCursor) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, allotCursor, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("AllotCursor", dataMap, "id", allotCursor.getId());
        }

        public String delete(Integer id) {
            return "delete from  AllotCursor where id='" + id + "'";
        }
    }
}