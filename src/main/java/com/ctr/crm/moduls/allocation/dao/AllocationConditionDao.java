package com.ctr.crm.moduls.allocation.dao;

import com.ctr.crm.moduls.allocation.models.AllocationCondition;

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-04-29
 */
public interface AllocationConditionDao {

    @SelectProvider(type = AllocationConditionSqlProvider.class, method = "get")
    AllocationCondition get(String id);

    @SelectProvider(type = AllocationConditionSqlProvider.class, method = "findList")
    List<AllocationCondition> findList(AllocationCondition allocationCondition);

    @SelectProvider(type = AllocationConditionSqlProvider.class, method = "findAllList")
    List<AllocationCondition> findAllList(AllocationCondition allocationCondition);

    @InsertProvider(type = AllocationConditionSqlProvider.class, method = "insert")
    void insert(AllocationCondition allocationCondition);

    @InsertProvider(type = AllocationConditionSqlProvider.class, method = "insertBatch")
    int insertBatch(@Param("allocationConditions") List<AllocationCondition> allocationConditions);

    @UpdateProvider(type = AllocationConditionSqlProvider.class, method = "update")
    int update(AllocationCondition allocationCondition);

    @UpdateProvider(type = AllocationConditionSqlProvider.class, method = "delete")
    boolean delete(String id);

    @Update("update AllocationCondition set deleted=1 where allotId=#{id}")
    void deleteByAllotId(String id);

    class AllocationConditionSqlProvider {

        public String get(String id) {
            return "select * from AllocationCondition where id ='" + id + "'";
        }

        public String findList(AllocationCondition allocationCondition) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(allocationCondition, "serialVersionUID"));
                    FROM("AllocationCondition");
                    WHERE("deleted=0");
                    if (StringUtils.isNotBlank(allocationCondition.getRuleId())) {
                        WHERE("ruleId=#{ruleId}");
                    }
                    ORDER_BY("row,position");
                }
            }.toString();
        }

        public String findAllList(AllocationCondition allocationCondition) {
            return "select * from AllocationCondition where deleted=0";
        }

        public String insert(AllocationCondition allocationCondition) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, allocationCondition, "serialVersionUID");
            return PojoUtils.getInsertSQL(AllocationCondition.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<AllocationCondition> allocationConditions) {
            StringBuffer sql = new StringBuffer();
            sql.append("insert ignore into AllocationCondition");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(allocationConditions.get(0), "serialVersionUID", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            sql.append("(").append(column.substring(0, column.lastIndexOf(","))).append(")").append(" values");
            for (AllocationCondition bean :
                    allocationConditions) {
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

        public String update(AllocationCondition allocationCondition) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, allocationCondition, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("AllocationCondition", dataMap, "id", allocationCondition.getId());
        }

        public String delete(String id) {
            return "update AllocationCondition set deleted=1 where id='" + id + "'";
        }
    }
}