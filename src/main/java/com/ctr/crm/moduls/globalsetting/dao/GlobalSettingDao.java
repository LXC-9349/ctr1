package com.ctr.crm.moduls.globalsetting.dao;

import com.ctr.crm.moduls.globalsetting.models.GlobalSetting;
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
public interface GlobalSettingDao {

    @SelectProvider(type =GlobalSettingSqlProvider.class, method = "get")
    GlobalSetting get(String id);

    @SelectProvider(type =GlobalSettingSqlProvider.class, method = "findList")
    List<GlobalSetting> findList(GlobalSetting globalSetting);

    @SelectProvider(type =GlobalSettingSqlProvider.class, method = "findAllList")
    List<GlobalSetting> findAllList(GlobalSetting globalSetting);

    @InsertProvider(type =GlobalSettingSqlProvider.class, method = "insert")
    void insert(GlobalSetting globalSetting);

    @InsertProvider(type =GlobalSettingSqlProvider.class, method = "insertBatch")
    int insertBatch(List<GlobalSetting> globalSettings);

    @UpdateProvider(type =GlobalSettingSqlProvider.class, method = "update")
    int update(GlobalSetting globalSetting);

    @UpdateProvider(type =GlobalSettingSqlProvider.class, method = "delete")
    boolean delete(String id);

    class GlobalSettingSqlProvider {

        public String get(String id) {
            return "select * from GlobalSetting where id ='"+id+"'";
        }

        public  String findList(GlobalSetting globalSetting) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(globalSetting, "serialVersionUID"));
                    FROM("GlobalSetting");
                    WHERE("deleted=0");
                    if(StringUtils.isNotBlank(globalSetting.getType())){
                        WHERE("type in ("+globalSetting.getType()+")");
                    }
                    ORDER_BY("createTime asc");
                }
            }.toString();
        }

        public  String findAllList(GlobalSetting globalSetting) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(globalSetting, "serialVersionUID"));
                    FROM("GlobalSetting");
                    WHERE("deleted=0");
                }
            }.toString();
        }

        public String insert(GlobalSetting globalSetting) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, globalSetting, "serialVersionUID");
            return PojoUtils.getInsertSQL(GlobalSetting.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<GlobalSetting> globalSettings) {
            StringBuilder sql = new StringBuilder();
            sql.append("insert ignore into GlobalSetting");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(globalSettings.get(0), "serialVersionUID", false)) {
                column.append("`").append(s).append("`,");
            }
            column.substring(0, column.length() - 1);
            sql.append("(").append(column).append(")").append(" values");
            for (GlobalSetting bean:
                              globalSettings) {
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

        public String update(GlobalSetting globalSetting) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, globalSetting, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("GlobalSetting", dataMap, "id",globalSetting.getId());
        }

        public String delete(String id) {
            return "update GlobalSetting set deleted=1 where id='"+id+"'";
        }
    }
}