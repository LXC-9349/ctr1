package com.ctr.crm.moduls.sysdict.dao;

import com.ctr.crm.moduls.sysdict.models.SysDict;
import com.ctr.crm.commons.utils.PojoUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;



import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-04-23
 */
public interface SysDictDao {

    @SelectProvider(type = SysDictSqlProvider.class, method = "get")
    SysDict get(String id);

    @SelectProvider(type = SysDictSqlProvider.class, method = "findList")
    List<SysDict> findList(SysDict sysDict);

    @SelectProvider(type = SysDictSqlProvider.class, method = "findAllList")
    List<SysDict> findAllList(SysDict sysDict);
    
    @SelectProvider(type = SysDictSqlProvider.class, method = "findListByTypes")
    List<SysDict> findListByTypes(List<String> types);

    @InsertProvider(type = SysDictSqlProvider.class, method = "insert")
    void insert(SysDict sysDict);

    @InsertProvider(type = SysDictSqlProvider.class, method = "insertBatch")
    int insertBatch(List<SysDict> sysDicts);

    @UpdateProvider(type = SysDictSqlProvider.class, method = "update")
    int update(SysDict sysDict);

    @UpdateProvider(type = SysDictSqlProvider.class, method = "delete")
    boolean delete(String id);

    @SelectProvider(type = SysDictSqlProvider.class, method = "findValue")
    String findValue(@Param("type")String type, @Param("label")String label);
    @Select("select label from SysDict where type=#{type} and value=#{value}")
    String findLabel(@Param("type")String type, @Param("value")String value);
    @Update("update SysDict set label=#{label} where type=#{type} and value=#{value}")
    int updateLabel(@Param("type")String type, @Param("value")String value, @Param("label")String label);

    @Select("select * from SysDict where type =#{type} and deleted=0 and parent<>'0'")
    List<SysDict> findListByType(String type);

    class SysDictSqlProvider {

        public String findValue(String type, String label) {
            String sql = "select value from SysDict where type =#{type}";
            if (StringUtils.isNotBlank(label)) {
                sql += " and label=#{label}";
            }
            sql+=" limit 1";
            return sql;
        }

        public String get(String id) {
            return "select * from SysDict where id ='" + id + "'";
        }

        public String findList(SysDict sysDict) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(sysDict, "serialVersionUID"));
                    FROM("SysDict");
                    WHERE("deleted=0");
                }
            }.toString();
        }
        
        @SuppressWarnings("unchecked")
		public String findListByTypes(Map<String,Object> map){
        	return new SQL(){
        		{
        			SELECT(PojoUtils.getArrayFields(SysDict.class, "serialVersionUID","id","sort","createTime","isedit","deleted"));
        			FROM(SysDict.class.getSimpleName());
        			WHERE("1=1");
        			List<String> types = (List<String>)map.get("list");
        			if(types != null && types.size() > 0){
        				boolean index = true;
        				for (String type : types) {
        					if(index) AND(); else OR();
							WHERE("type='"+type+"'");
							index =false;
						}
        			}
        			ORDER_BY("sort");
        		}
        	}.toString();
        }

        public String findAllList(SysDict sysDict) {
            return "select * from SysDict where deleted=0";
        }

        public String insert(SysDict sysDict) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, sysDict, "serialVersionUID");
            return PojoUtils.getInsertSQL(SysDict.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<SysDict> sysDicts) {
            StringBuilder sql = new StringBuilder();
            sql.append("insert ignore into SysDict");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(sysDicts.get(0), "serialVersionUID", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            column.deleteCharAt(column.length()-1);
            sql.append("(").append(column).append(")").append(" values");
            for (SysDict bean :
                    sysDicts) {
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

        public String update(SysDict sysDict) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, sysDict, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("SysDict", dataMap, "id", sysDict.getId());
        }

        public String delete(String id) {
            return "update SysDict set deleted=1 where id='" + id + "'";
        }
    }
}