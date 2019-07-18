package com.ctr.crm.moduls.invitetoshop.dao;

import com.ctr.crm.moduls.invitetoshop.models.InviteToShop;

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-06-27
 */
public interface InviteToShopDao {

    @SelectProvider(type = InviteToShopSqlProvider.class, method = "get")
    InviteToShop get(Integer id);

    @SelectProvider(type = InviteToShopSqlProvider.class, method = "findList")
    List<InviteToShop> findList(InviteToShop inviteToShop);

    @SelectProvider(type = InviteToShopSqlProvider.class, method = "findAllList")
    List<InviteToShop> findAllList(InviteToShop inviteToShop);

    @InsertProvider(type = InviteToShopSqlProvider.class, method = "insert")
    int insert(InviteToShop inviteToShop);

    @InsertProvider(type = InviteToShopSqlProvider.class, method = "insertBatch")
    int insertBatch(@Param("inviteToShops") List<InviteToShop> inviteToShops);

    @UpdateProvider(type = InviteToShopSqlProvider.class, method = "update")
    int update(InviteToShop inviteToShop);

    @UpdateProvider(type = InviteToShopSqlProvider.class, method = "delete")
    boolean delete(Integer id);

    @Select("select IFNULL((select max(id)+1 from InviteToShop),1) from dual")
    Integer getID();

    class InviteToShopSqlProvider {

        public String get(Integer id) {
            return "select * from InviteToShop where id ='" + id + "'";
        }

        public String findList(InviteToShop inviteToShop) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(inviteToShop, "serialVersionUID"));
                    FROM("InviteToShop");
                    if (inviteToShop.getMemberId() != null) {
                        WHERE("memberId = #{memberId}");
                    }
                    if (inviteToShop.getShopStatus() != null) {
                        WHERE("shopStatus = #{shopStatus}");
                    }
                }
            }.toString();
        }

        public String findAllList(InviteToShop inviteToShop) {
            return "select * from InviteToShop";
        }

        public String insert(InviteToShop inviteToShop) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, inviteToShop, "serialVersionUID");
            return PojoUtils.getInsertSQL(InviteToShop.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<InviteToShop> inviteToShops) {
            StringBuffer sql = new StringBuffer();
            sql.append("insert ignore into InviteToShop");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(inviteToShops.get(0), "serialVersionUID", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            sql.append("(").append(column.substring(0, column.lastIndexOf(","))).append(")").append(" values");
            for (InviteToShop bean : inviteToShops) {
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

        public String update(InviteToShop inviteToShop) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, inviteToShop, "serialVersionUID,id");
            return PojoUtils.getUpdateSQL("InviteToShop", dataMap, "id", inviteToShop.getId());
        }

        public String delete(Integer id) {
            return "delete from InviteToShop where id='" + id + "'";
        }
    }
}