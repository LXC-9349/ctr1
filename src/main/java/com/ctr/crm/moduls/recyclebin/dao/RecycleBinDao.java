package com.ctr.crm.moduls.recyclebin.dao;

import com.ctr.crm.moduls.recyclebin.models.RecycleBin;

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-05-10
 */
public interface RecycleBinDao {

    @SelectProvider(type = RecycleBinSqlProvider.class, method = "get")
    RecycleBin get(Integer id);

    @SelectProvider(type = RecycleBinSqlProvider.class, method = "findList")
    List<RecycleBin> findList(RecycleBin recycleBin);

    @SelectProvider(type = RecycleBinSqlProvider.class, method = "findAllList")
    List<RecycleBin> findAllList(RecycleBin recycleBin);

    @InsertProvider(type = RecycleBinSqlProvider.class, method = "insert")
    boolean insert(RecycleBin recycleBin);

    @InsertProvider(type = RecycleBinSqlProvider.class, method = "insertBatch")
    int insertBatch(@Param("recycleBins") List<RecycleBin> recycleBins);

    @UpdateProvider(type = RecycleBinSqlProvider.class, method = "update")
    int update(RecycleBin recycleBin);

    @UpdateProvider(type = RecycleBinSqlProvider.class, method = "delete")
    boolean delete(Integer id);

    class RecycleBinSqlProvider {

        public String get(Integer id) {
            return "select * from RecycleBin where id ='" + id + "'";
        }

        public String findList(RecycleBin recycleBin) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(recycleBin, "serialVersionUID"));
                    FROM("RecycleBin");
                }
            }.toString();
        }

        public String findAllList(RecycleBin recycleBin) {
            return "select * from RecycleBin ";
        }

        public String insert(RecycleBin recycleBin) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, recycleBin, "serialVersionUID");
            return PojoUtils.getInsertSQL(RecycleBin.class.getSimpleName(), dataMap, "id");
        }

        public String insertBatch(List<RecycleBin> recycleBins) {
            StringBuffer sql = new StringBuffer();
            sql.append("insert into RecycleBin");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(recycleBins.get(0), "serialVersionUID,id", false)) {
                column.append(s).append(",");
            }
            sql.append("(").append(column.substring(0, column.lastIndexOf(","))).append(")").append(" values");
            for (RecycleBin bean : recycleBins) {
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

        public String update(RecycleBin recycleBin) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, recycleBin, "serialVersionUID,id");
            return PojoUtils.getUpdateSQL("RecycleBin", dataMap, "id", recycleBin.getId());
        }

        public String delete(Integer id) {
            return "delete from RecycleBin where id='" + id + "'";
        }
    }
}