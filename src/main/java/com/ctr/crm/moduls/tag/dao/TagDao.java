package com.ctr.crm.moduls.tag.dao;

import com.ctr.crm.moduls.tag.models.Tag;
import com.ctr.crm.commons.utils.PojoUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;
/**
 * @author DoubleLi
 * @date  2019-04-19
 */
public interface TagDao {

    @SelectProvider(type =TagSqlProvider.class, method = "get")
    Tag get(String id);

    @SelectProvider(type =TagSqlProvider.class, method = "findList")
    List<Tag> findList(Tag tag);

    @SelectProvider(type =TagSqlProvider.class, method = "findAllList")
    List<Tag> findAllList();

    @InsertProvider(type =TagSqlProvider.class, method = "insert")
    void insert(Tag tag);

    @InsertProvider(type =TagSqlProvider.class, method = "insertBatch")
    int insertBatch(List<Tag> tags);

    @UpdateProvider(type =TagSqlProvider.class, method = "update")
    int update(Tag tag);

    @UpdateProvider(type =TagSqlProvider.class, method = "delete")
    boolean delete(String id);

    class TagSqlProvider {

        public String get(String id) {
            return "select * from Tag where id ='"+id+"'";
        }

        public  String findList(Tag tag) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(tag, "serialVersionUID"));
                    FROM("Tag");
                    WHERE("deleted=0");
                    if(StringUtils.isNotBlank(tag.getGroupId())){
                        WHERE("groupId=#{groupId}");
                    }
                    if(StringUtils.isNotBlank(tag.getTagName())){
                        WHERE("tagName=#{tagName}");
                    }
                    /* 用于去重*/
                    if(StringUtils.isNotBlank(tag.getId())){
                        WHERE("id <> #{id}");
                    }
                }
            }.toString();
        }

        public  String findAllList(Tag tag) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(tag, "serialVersionUID"));
                    FROM("Tag");
                    WHERE("deleted=0");
                }
            }.toString();
        }

        public String insert(Tag tag) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, tag, "serialVersionUID");
            return PojoUtils.getInsertSQL(Tag.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<Tag> tags) {
            StringBuilder sql = new StringBuilder();
            sql.append("insert ignore into Tag");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(tags.get(0), "serialVersionUID", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            column.substring(0, column.length() - 1);
            sql.append("(").append(column).append(")").append(" values");
            for (Tag bean: tags) {
                sql.append("(");
                for (String val : PojoUtils.getArrayFields(bean, true)) {
                    sql.append("'").append(val).append("',");
                }
                sql.substring(0, sql.length() - 1);
                sql.append("),");
            }
            sql.substring(0, sql.length() - 1);
            return sql.toString();
        }

        public String update(Tag tag) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, tag, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("Tag", dataMap, "id",tag.getId());
        }

        public String delete(String id) {
            return "update Tag set deleted=1 where id='"+id+"'";
        }
    }
}