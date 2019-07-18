package com.ctr.crm.moduls.contract.dao;

import com.ctr.crm.moduls.contract.models.ContractOrderPayRecord;

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;
/**
 * @author DoubleLi
 * @date  2019-05-14
 */
public interface ContractOrderPayRecordDao {

    @SelectProvider(type =ContractOrderPayRecordSqlProvider.class, method = "get")
    ContractOrderPayRecord get(Integer id);

    @SelectProvider(type =ContractOrderPayRecordSqlProvider.class, method = "findList")
    List<ContractOrderPayRecord> findList(ContractOrderPayRecord contractOrderPayRecord);

    @SelectProvider(type =ContractOrderPayRecordSqlProvider.class, method = "findAllList")
    List<ContractOrderPayRecord> findAllList();

    @InsertProvider(type =ContractOrderPayRecordSqlProvider.class, method = "insert")
    int insert(ContractOrderPayRecord contractOrderPayRecord);

    @InsertProvider(type =ContractOrderPayRecordSqlProvider.class, method = "insertBatch")
    int insertBatch(@Param("contractOrderPayRecords") List<ContractOrderPayRecord> contractOrderPayRecords);

    @UpdateProvider(type =ContractOrderPayRecordSqlProvider.class, method = "update")
    int update(ContractOrderPayRecord contractOrderPayRecord);

    @UpdateProvider(type =ContractOrderPayRecordSqlProvider.class, method = "delete")
    boolean delete(Integer id);

    @Select("select IFNULL((select max(id)+1 from ContractOrderPayRecord),1) from dual")
    Integer getID();

    class ContractOrderPayRecordSqlProvider {

        public String get(Integer id) {
            return "select * from ContractOrderPayRecord where id ='" + id + "'";
        }

        public String findList(ContractOrderPayRecord contractOrderPayRecord) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(contractOrderPayRecord, "serialVersionUID"));
                    FROM("ContractOrderPayRecord");
                    WHERE("deleted=0");
                }
            }.toString();
        }

        public String findAllList(ContractOrderPayRecord contractOrderPayRecord) {
            return "select * from ContractOrderPayRecord where deleted=0";
        }

        public String insert(ContractOrderPayRecord contractOrderPayRecord) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, contractOrderPayRecord, "serialVersionUID");
            return PojoUtils.getInsertSQL(ContractOrderPayRecord.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<ContractOrderPayRecord> contractOrderPayRecords) {
            StringBuffer sql = new StringBuffer();
            sql.append("insert ignore into ContractOrderPayRecord");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(contractOrderPayRecords.get(0), "serialVersionUID,id", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            sql.append("(").append(column.substring(0, column.lastIndexOf(","))).append(")").append(" values");
            for (ContractOrderPayRecord bean :
                    contractOrderPayRecords) {
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

        public String update(ContractOrderPayRecord contractOrderPayRecord) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, contractOrderPayRecord, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("ContractOrderPayRecord", dataMap, "id",contractOrderPayRecord.getId());
        }

        public String delete(Integer id) {
            return "update ContractOrderPayRecord set deleted=1 where id='" + id + "'";
        }
    }
}