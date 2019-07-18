package com.ctr.crm.moduls.approvalgroup.dao;

import com.ctr.crm.moduls.approvalgroup.models.ApprovalGroup;
import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-04-24
 */
public interface ApprovalGroupDao {

    @SelectProvider(type = ApprovalGroupSqlProvider.class, method = "get")
    ApprovalGroup get(String id);

    @SelectProvider(type = ApprovalGroupSqlProvider.class, method = "findList")
    List<ApprovalGroup> findList(ApprovalGroup approvalGroup);

    @SelectProvider(type = ApprovalGroupSqlProvider.class, method = "findAllList")
    List<ApprovalGroup> findAllList(ApprovalGroup approvalGroup);

    @InsertProvider(type = ApprovalGroupSqlProvider.class, method = "insert")
    void insert(ApprovalGroup approvalGroup);

    @InsertProvider(type = ApprovalGroupSqlProvider.class, method = "insertBatch")
    int insertBatch(List<ApprovalGroup> approvalGroups);

    @UpdateProvider(type = ApprovalGroupSqlProvider.class, method = "update")
    int update(ApprovalGroup approvalGroup);

    @UpdateProvider(type = ApprovalGroupSqlProvider.class, method = "delete")
    boolean delete(String id);

    @SelectProvider(type = ApprovalGroupSqlProvider.class, method = "getGroupIdByWorkerId")
    List<String> getGroupIdByWorkerId(Integer workerId);

    class ApprovalGroupSqlProvider {

        public String get(String id) {
            return "select * from ApprovalGroup where id ='" + id + "'";
        }

        public String findList(ApprovalGroup approvalGroup) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(approvalGroup, "serialVersionUID"));
                    FROM("ApprovalGroup");
                    WHERE("deleted=0");
                }
            }.toString();
        }

        public String findAllList() {
            return "select * from ApprovalGroup where deleted=0";
        }

        public String insert(ApprovalGroup approvalGroup) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, approvalGroup, "serialVersionUID");
            return PojoUtils.getInsertSQL(ApprovalGroup.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<ApprovalGroup> approvalGroups) {
            StringBuilder sql = new StringBuilder();
            sql.append("insert ignore into ApprovalGroup");
            StringBuffer column = new StringBuffer();
            for (String s :PojoUtils.getArrayFields(approvalGroups.get(0), "serialVersionUID", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            column.deleteCharAt(column.length()-1);
            sql.append("(").append(column).append(")").append(" values");
            for (ApprovalGroup bean :
                    approvalGroups) {
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

        public String update(ApprovalGroup approvalGroup) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, approvalGroup, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("ApprovalGroup", dataMap, "id", approvalGroup.getId());
        }

        public String delete(String id) {
            return "update ApprovalGroup set deleted=1 where id='" + id + "'";
        }

        public String getGroupIdByWorkerId(Integer workerId) {
            return "select id from ApprovalGroup where find_in_set('" + workerId + "',workerIds)";
        }
    }
}