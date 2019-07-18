package com.ctr.crm.moduls.intentionality.dao;

import com.ctr.crm.moduls.intentionality.models.Intentionality;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.PojoUtils;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;



import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-04-19
 */
public interface IntentionalityDao {

    @SelectProvider(type = IntentionalitySqlProvider.class, method = "get")
    Intentionality get(String id);

    @SelectProvider(type = IntentionalitySqlProvider.class, method = "findList")
    List<Intentionality> findList(Intentionality intentionality);

    @SelectProvider(type = IntentionalitySqlProvider.class, method = "findAllList")
    List<Intentionality> findAllList(Intentionality intentionality);

    @InsertProvider(type = IntentionalitySqlProvider.class, method = "insert")
    void insert(Intentionality intentionality);

    @InsertProvider(type = IntentionalitySqlProvider.class, method = "insertBatch")
    int insertBatch(List<Intentionality> intentionalitys);

    @UpdateProvider(type = IntentionalitySqlProvider.class, method = "update")
    int update(Intentionality intentionality);

    @UpdateProvider(type = IntentionalitySqlProvider.class, method = "delete")
    boolean delete(String id);

    @Select("select * from Intentionality where type=#{type} and deleted=0 order by caseClass asc limit 1")
    Intentionality getByTypeFirst(Integer type);

    @Select("select ifnull((select count(1) from Intentionality where caseClass=#{caseClass} and deleted=0),0) from dual")
    Integer getCaseClass(Integer caseClass);
    
    @Select("select id,type,name,isCapacity,caseClass from Intentionality where caseClass=#{caseClass} and deleted=0")
    Intentionality select(Integer caseClass);
    
    @SelectProvider(type=IntentionalitySqlProvider.class,method="getStatisticsStr")
    String getStatisticsStr(Integer type, String targetAsAlias);

    class IntentionalitySqlProvider {

        public String get(String id) {
            return "select * from Intentionality where deleted=0 and id ='" + id + "'";
        }
        
        public String getStatisticsStr(Map<String, Object> params){
        	return new SQL(){
        		{
        			Integer type = CommonUtils.evalInteger(params.get("param1"));
        			String targetAsAlias = CommonUtils.evalString(params.get("param2"));
        			SELECT("group_concat(distinct concat('sum(if("+targetAsAlias+".caseClass=',caseClass,', 1, 0)) as \\'',caseClass,'\\'') order by caseClass)");
        			FROM("Intentionality");
        			WHERE("deleted=0","type="+type);
        		}
        	}.toString();
        }

        public String findList(Intentionality intentionality) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(intentionality, "serialVersionUID"));
                    FROM("Intentionality");
                    WHERE("deleted=0");
                    if (intentionality.getType() != null) {
                        WHERE("type =#{type}");
                    }
                }
            }.toString();
        }

        public String findAllList(Intentionality intentionality) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(intentionality, "serialVersionUID"));
                    FROM("Intentionality");
                    WHERE("deleted=0");
                }
            }.toString();
        }

        public String insert(Intentionality intentionality) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, intentionality, "serialVersionUID");
            return PojoUtils.getInsertSQL(Intentionality.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<Intentionality> intentionalitys) {
            StringBuilder sql = new StringBuilder();
            sql.append("insert ignore into Intentionality");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(intentionalitys.get(0), "serialVersionUID", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            column.substring(0, column.length() - 1);
            sql.append("(").append(column).append(")").append(" values");
            for (Intentionality bean :
                    intentionalitys) {
                sql.append("(");
                for (String val : PojoUtils.getArrayFieldsValue(bean, "serialVersionUID")) {
                    sql.append("'").append(val).append("',");
                }
                sql.substring(0, sql.length() - 1);
                sql.append("),");
            }
            sql.substring(0, sql.length() - 1);
            return sql.toString();
        }

        public String update(Intentionality intentionality) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, intentionality, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("Intentionality", dataMap, "id", intentionality.getId());
        }

        public String delete(String id) {
            return "update Intentionality set deleted=1 where id='" + id + "'";
        }
    }
}