package com.ctr.crm.moduls.allot.dao;

import com.ctr.crm.moduls.allot.models.AllotWorker;

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-05-05
 */
public interface AllotWorkerDao {

    @SelectProvider(type = AllotWorkerSqlProvider.class, method = "get")
    AllotWorker get(Integer id);

    @SelectProvider(type = AllotWorkerSqlProvider.class, method = "getByWorkerId")
    AllotWorker getByWorkerId(Integer workerId);

    @SelectProvider(type = AllotWorkerSqlProvider.class, method = "findList")
    List<AllotWorker> findList(AllotWorker allotWorker);

    @SelectProvider(type = AllotWorkerSqlProvider.class, method = "findAllList")
    List<AllotWorker> findAllList(AllotWorker allotWorker);

    @InsertProvider(type = AllotWorkerSqlProvider.class, method = "insert")
    void insert(AllotWorker allotWorker);

    @InsertProvider(type = AllotWorkerSqlProvider.class, method = "insertBatch")
    int insertBatch(@Param("allotWorkers") List<AllotWorker> allotWorkers);

    @UpdateProvider(type = AllotWorkerSqlProvider.class, method = "update")
    int update(AllotWorker allotWorker);

    @UpdateProvider(type = AllotWorkerSqlProvider.class, method = "delete")
    boolean delete(Integer id);

    class AllotWorkerSqlProvider {

        public String get(Integer id) {
            return "select * from AllotWorker where id ='" + id + "'";
        }

        public String getByWorkerId(Integer workerId) {
            return "select * from AllotWorker where workerId ='" + workerId + "'";
        }

        public String findList(AllotWorker allotWorker) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(allotWorker, "serialVersionUID"));
                    FROM("AllotWorker");
                    WHERE("deleted=0");
                }
            }.toString();
        }

        public String findAllList(AllotWorker allotWorker) {
            return "select * from AllotWorker where deleted=0";
        }

        public String insert(AllotWorker allotWorker) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, allotWorker, "serialVersionUID,id");
            return PojoUtils.getInsertSQL(AllotWorker.class.getSimpleName(), dataMap, "id");
        }

        public String insertBatch(List<AllotWorker> allotWorkers) {
            StringBuffer sql = new StringBuffer();
            sql.append("insert ignore into AllotWorker");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(allotWorkers.get(0), "serialVersionUID,id", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            sql.append("(").append(column.substring(0, column.lastIndexOf(","))).append(")").append(" values");
            for (AllotWorker bean :
                    allotWorkers) {
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

        public String update(AllotWorker allotWorker) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, allotWorker, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("AllotWorker", dataMap, "id", allotWorker.getId());
        }

        public String delete(Integer id) {
            return "update AllotWorker set deleted=1 where id='" + id + "'";
        }
    }
}