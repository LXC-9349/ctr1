package com.ctr.crm.moduls.member.dao;

import com.ctr.crm.moduls.member.models.MemberVip;

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-05-20
 */
public interface MemberVipDao {

    @SelectProvider(type = MemberVipSqlProvider.class, method = "get")
    MemberVip get(Integer id);

    @SelectProvider(type = MemberVipSqlProvider.class, method = "findList")
    List<MemberVip> findList(MemberVip memberVip);

    @SelectProvider(type = MemberVipSqlProvider.class, method = "findAllList")
    List<MemberVip> findAllList();

    @InsertProvider(type = MemberVipSqlProvider.class, method = "insert")
    int insert(MemberVip memberVip);

    @InsertProvider(type = MemberVipSqlProvider.class, method = "insertBatch")
    int insertBatch(@Param("memberVips") List<MemberVip> memberVips);

    @UpdateProvider(type = MemberVipSqlProvider.class, method = "update")
    int update(MemberVip memberVip);

    @UpdateProvider(type = MemberVipSqlProvider.class, method = "delete")
    boolean delete(Integer id);

    @Select("select IFNULL((select max(id)+1 from MemberVip),1) from dual")
    Integer getID();

    @Select("select * from MemberVip where memberId=#{memberId} order by (case when status=0 then 0 else 1 end) limit 1")
    MemberVip getByMemberId(Long memberId);

    class MemberVipSqlProvider {

        public String get(Integer id) {
            return "select * from MemberVip where id ='" + id + "'";
        }

        public String findList(MemberVip memberVip) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(memberVip, "serialVersionUID"));
                    FROM("MemberVip");
                    WHERE("deleted=0");
                    if (memberVip.getStatus() != null) {
                        WHERE("status =#{status}");
                    }
                    if(memberVip.getContractId()!=null){
                        WHERE("contractId =#{contractId}");
                    }
                }
            }.toString();
        }

        public String findAllList(MemberVip memberVip) {
            return "select * from MemberVip where deleted=0";
        }

        public String insert(MemberVip memberVip) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, memberVip, "serialVersionUID");
            return PojoUtils.getInsertSQL(MemberVip.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<MemberVip> memberVips) {
            StringBuffer sql = new StringBuffer();
            sql.append("insert ignore into MemberVip");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(memberVips.get(0), "serialVersionUID", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            sql.append("(").append(column.substring(0, column.lastIndexOf(","))).append(")").append(" values");
            for (MemberVip bean :
                    memberVips) {
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

        public String update(MemberVip memberVip) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, memberVip, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("MemberVip", dataMap, "id", memberVip.getId());
        }

        public String delete(Integer id) {
            return "update MemberVip set deleted=1 where id='" + id + "'";
        }
    }
}