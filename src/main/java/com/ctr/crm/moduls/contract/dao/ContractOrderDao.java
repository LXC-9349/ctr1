package com.ctr.crm.moduls.contract.dao;

import com.ctr.crm.moduls.contract.models.ContractOrder;

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-05-14
 */
public interface ContractOrderDao {

    @SelectProvider(type = ContractOrderSqlProvider.class, method = "get")
    ContractOrder get(Integer id);

    @SelectProvider(type = ContractOrderSqlProvider.class, method = "findList")
    List<ContractOrder> findList(ContractOrder contractOrder);

    @SelectProvider(type = ContractOrderSqlProvider.class, method = "findAllList")
    List<ContractOrder> findAllList(ContractOrder contractOrder);

    @InsertProvider(type = ContractOrderSqlProvider.class, method = "insert")
    int insert(ContractOrder contractOrder);

    @InsertProvider(type = ContractOrderSqlProvider.class, method = "insertBatch")
    int insertBatch(@Param("contractOrders") List<ContractOrder> contractOrders);

    @UpdateProvider(type = ContractOrderSqlProvider.class, method = "update")
    int update(ContractOrder contractOrder);

    @UpdateProvider(type = ContractOrderSqlProvider.class, method = "delete")
    boolean delete(Integer id);

    @Select("select IFNULL((select max(id)+1 from ContractOrder),1) from dual")
    Integer getID();

    @Select("select * from ContractOrder where memberId=#{memberId} and status=1 and signTime is null order by createTime asc limit 1")
    ContractOrder getByMemberId(@Param("memberId") Long memberId);

    class ContractOrderSqlProvider {

        public String get(Integer id) {
            return "select * from ContractOrder where id ='" + id + "'";
        }

        public String findList(ContractOrder contractOrder) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(contractOrder, "serialVersionUID"));
                    FROM("ContractOrder");
                    WHERE("deleted=0");
                }
            }.toString();
        }

        public String findAllList(ContractOrder contractOrder) {
            return "select * from ContractOrder where deleted=0";
        }

        public String insert(ContractOrder contractOrder) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, contractOrder, "serialVersionUID");
            return PojoUtils.getInsertSQL(ContractOrder.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<ContractOrder> contractOrders) {
            StringBuffer sql = new StringBuffer();
            sql.append("insert ignore into ContractOrder");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(contractOrders.get(0), "serialVersionUID", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            sql.append("(").append(column.substring(0, column.lastIndexOf(","))).append(")").append(" values");
            for (ContractOrder bean :
                    contractOrders) {
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

        public String update(ContractOrder contractOrder) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, contractOrder, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("ContractOrder", dataMap, "id", contractOrder.getId());
        }

        public String delete(Integer id) {
            return "update ContractOrder set deleted=1 where id='" + id + "'";
        }
    }
}