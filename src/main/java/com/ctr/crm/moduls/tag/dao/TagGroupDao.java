package com.ctr.crm.moduls.tag.dao;

import com.ctr.crm.moduls.tag.models.TagGroup;
import com.ctr.crm.commons.utils.PojoUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-04-19
 */
public interface TagGroupDao {

    @SelectProvider(type = TagGroupSqlProvider.class, method = "get")
    TagGroup get(String id);

    @SelectProvider(type = TagGroupSqlProvider.class, method = "findList")
    List<TagGroup> findList(TagGroup tagGroup);

    @SelectProvider(type = TagGroupSqlProvider.class, method = "findAllList")
    List<TagGroup> findAllList(TagGroup tagGroup);

    @InsertProvider(type = TagGroupSqlProvider.class, method = "insert")
    void insert(TagGroup tagGroup);

    @InsertProvider(type = TagGroupSqlProvider.class, method = "insertBatch")
    int insertBatch(List<TagGroup> tagGroups);

    @UpdateProvider(type = TagGroupSqlProvider.class, method = "update")
    int update(TagGroup tagGroup);

    @UpdateProvider(type = TagGroupSqlProvider.class, method = "delete")
    boolean delete(String id);

    class TagGroupSqlProvider {

        public String get(String id) {
            return "select * from TagGroup where id ='" + id + "'";
        }

        public String findList(TagGroup tagGroup) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(tagGroup, "sonTagList,serialVersionUID"));
                    FROM("TagGroup");
                    WHERE("deleted=0");
                    if (StringUtils.isNotBlank(tagGroup.getGroupName())) {
                        WHERE("groupName=#{groupName}");
                    }
                    if (StringUtils.isNotBlank(tagGroup.getId())) {
                        WHERE("id<>#{id}");
                    }
                }
            }.toString();
        }

        public String findAllList(TagGroup tagGroup) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(tagGroup, "sonTagList,serialVersionUID"));
                    FROM("TagGroup");
                    WHERE("deleted=0");
                }
            }.toString();
        }

        public String insert(TagGroup tagGroup) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, tagGroup, "sonTagList,serialVersionUID");
            return PojoUtils.getInsertSQL(TagGroup.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<TagGroup> tagGroups) {
            StringBuilder sql = new StringBuilder();
            sql.append("insert ignore into TagGroup");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(tagGroups.get(0), "serialVersionUID,sonTagList", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            column.substring(0, column.length() - 1);
            sql.append("(").append(column).append(")").append(" values");
            for (TagGroup bean : tagGroups) {
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

        public String update(TagGroup tagGroup) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, tagGroup, "sonTagList,serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("TagGroup", dataMap, "id", tagGroup.getId());
        }

        public String delete(String id) {
            return "update TagGroup set deleted=1 where id='" + id + "'";
        }
    }
}