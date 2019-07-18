package com.ctr.crm.moduls.notice.dao;

import com.ctr.crm.moduls.notice.models.Notice;

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;
/**
 * @author DoubleLi
 * @date  2019-05-10
 */
public interface NoticeDao {

    @SelectProvider(type =NoticeSqlProvider.class, method = "get")
    Notice get(Integer id);

    @SelectProvider(type =NoticeSqlProvider.class, method = "findList")
    List<Notice> findList(Notice notice);

    @SelectProvider(type =NoticeSqlProvider.class, method = "findAllList")
    List<Notice> findAllList(Notice notice);

    @InsertProvider(type =NoticeSqlProvider.class, method = "insert")
    boolean insert(Notice notice);

    @InsertProvider(type =NoticeSqlProvider.class, method = "insertBatch")
    int insertBatch(@Param("notices") List<Notice> notices);

    @UpdateProvider(type =NoticeSqlProvider.class, method = "update")
    int update(Notice notice);

    @UpdateProvider(type =NoticeSqlProvider.class, method = "delete")
    boolean delete(Integer id);

    class NoticeSqlProvider {

        public String get(Integer id) {
            return "select * from Notice where id ='" + id + "'";
        }

        public String findList(Notice notice) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(notice, "serialVersionUID"));
                    FROM("Notice");
                    WHERE("deleted=0");
                }
            }.toString();
        }

        public String findAllList(Notice notice) {
            return "select * from Notice where deleted=0";
        }

        public String insert(Notice notice) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, notice, "serialVersionUID,id");
            return PojoUtils.getInsertSQL(Notice.class.getSimpleName(), dataMap, "id");
        }

        public String insertBatch(List<Notice> notices) {
            StringBuffer sql = new StringBuffer();
            sql.append("insert ignore into Notice");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(notices.get(0), "serialVersionUID,id", false)) {
                column.append(s).append(",");
            }
            sql.append("(").append(column.substring(0, column.lastIndexOf(","))).append(")").append(" values");
            for (Notice bean :
                    notices) {
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

        public String update(Notice notice) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, notice, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("Notice", dataMap, "id",notice.getId());
        }

        public String delete(Integer id) {
            return "update Notice set deleted=1 where id='" + id + "'";
        }
    }
}