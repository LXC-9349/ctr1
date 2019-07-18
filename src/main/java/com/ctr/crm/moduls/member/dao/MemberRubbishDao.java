package com.ctr.crm.moduls.member.dao;

import com.ctr.crm.moduls.member.models.MemberRubbish;

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-05-16
 */
public interface MemberRubbishDao {

    @SelectProvider(type = MemberRubbishSqlProvider.class, method = "get")
    MemberRubbish get(Integer autoId);
    @Select("select autoId,memberId,caseClass,reason,createTime,workerId from MemberRubbish where memberId=#{memberId}")
    MemberRubbish select(Long memberId);

    @SelectProvider(type = MemberRubbishSqlProvider.class, method = "findList")
    List<MemberRubbish> findList(MemberRubbish memberRubbish);

    @SelectProvider(type = MemberRubbishSqlProvider.class, method = "findAllList")
    List<MemberRubbish> findAllList();

    @InsertProvider(type = MemberRubbishSqlProvider.class, method = "insert")
    int insert(MemberRubbish memberRubbish);

    @InsertProvider(type = MemberRubbishSqlProvider.class, method = "insertBatch")
    int insertBatch(@Param("memberRubbishs") List<MemberRubbish> memberRubbishs);

    @UpdateProvider(type = MemberRubbishSqlProvider.class, method = "update")
    int update(MemberRubbish memberRubbish);

    @Delete("delete from MemberRubbish where memberId=#{memberId}")
    boolean relieve(Long memberId);

    class MemberRubbishSqlProvider {

        public String get(Integer autoId) {
            return "select * from MemberRubbish where id ='" + autoId + "'";
        }

        public String findList(MemberRubbish memberRubbish) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(memberRubbish, "serialVersionUID"));
                    FROM("MemberRubbish");
                    if (memberRubbish.getMemberId() != null) {
                        WHERE("memberId=#{memberId}");
                    }
                }
            }.toString();
        }

        public String findAllList(MemberRubbish memberRubbish) {
            return "select * from MemberRubbish ";
        }

        public String insert(MemberRubbish memberRubbish) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, memberRubbish, "serialVersionUID,autoId");
            return PojoUtils.getInsertSQL(MemberRubbish.class.getSimpleName(), dataMap, "autoId");
        }

        public String insertBatch(List<MemberRubbish> memberRubbishs) {
            StringBuffer sql = new StringBuffer();
            sql.append("insert ignore into MemberRubbish");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFieldsName(memberRubbishs.get(0), "serialVersionUID,autoId")) {
                column.append("`").append(s).append("`").append(",");
            }
            sql.append("(").append(column.substring(0, column.lastIndexOf(","))).append(")").append(" values");
            for (MemberRubbish bean :
                    memberRubbishs) {
                sql.append("(");
                for (String val : PojoUtils.getArrayFieldsValue(bean, "serialVersionUID,autoId")) {
                    sql.append("'").append(val).append("',");
                }
                sql.deleteCharAt(sql.length() - 1);
                sql.append("),");
            }
            sql.deleteCharAt(sql.length() - 1);
            return sql.toString();
        }

        public String update(MemberRubbish memberRubbish) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, memberRubbish, "serialVersionUID,autoId");
            return PojoUtils.getUpdateSQL("MemberRubbish", dataMap, "autoId", memberRubbish.getAutoId());
        }

        public String delete(Integer autoId) {
            return "delete from MemberRubbish   where autoId='" + autoId + "'";
        }
    }
}