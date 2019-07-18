package com.ctr.crm.moduls.allocation.dao;

import com.ctr.crm.moduls.allocation.models.AllocationRule;

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-04-29
 */
public interface AllocationRuleDao {

    @SelectProvider(type = AllocationRuleSqlProvider.class, method = "get")
    AllocationRule get(String id);

    @SelectProvider(type = AllocationRuleSqlProvider.class, method = "findList")
    List<AllocationRule> findList(AllocationRule allocationRule);

    @SelectProvider(type = AllocationRuleSqlProvider.class, method = "findAllList")
    List<AllocationRule> findAllList(AllocationRule allocationRule);

    @InsertProvider(type = AllocationRuleSqlProvider.class, method = "insert")
    void insert(AllocationRule allocationRule);

    @InsertProvider(type = AllocationRuleSqlProvider.class, method = "insertBatch")
    int insertBatch(List<AllocationRule> allocationRules);

    @UpdateProvider(type = AllocationRuleSqlProvider.class, method = "update")
    int update(AllocationRule allocationRule);

    @UpdateProvider(type = AllocationRuleSqlProvider.class, method = "delete")
    boolean delete(String id);

    @Select("select IFNULL((select max(priority)+1 from AllocationRule),1) from dual")
    Integer getMaxPriority();

    class AllocationRuleSqlProvider {

        public String get(String id) {
            return "select * from AllocationRule where id ='" + id + "'";
        }

        public String findList(AllocationRule allocationRule) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(allocationRule, "serialVersionUID"));
                    FROM("AllocationRule");
                    WHERE("deleted=0");
                    if(allocationRule.getStatus()!=null){
                        WHERE("status=#{status}");
                    }
                }
            }.toString()+" order by priority asc";
        }

        public String findAllList(AllocationRule allocationRule) {
            return "select * from AllocationRule where deleted=0";
        }

        public String insert(AllocationRule allocationRule) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, allocationRule, "serialVersionUID");
            return PojoUtils.getInsertSQL(AllocationRule.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<AllocationRule> allocationRules) {
            StringBuilder sql = new StringBuilder();
            sql.append("insert ignore into AllocationRule");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(allocationRules.get(0), "serialVersionUID", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            column.deleteCharAt(column.length()-1);
            sql.append("(").append(column).append(")").append(" values");
            for (AllocationRule bean :
                    allocationRules) {
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

        public String update(AllocationRule allocationRule) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, allocationRule, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("AllocationRule", dataMap, "id", allocationRule.getId());
        }

        public String delete(String id) {
            return "update AllocationRule set deleted=1 where id='" + id + "'";
        }
    }
}