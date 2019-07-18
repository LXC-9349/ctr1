package com.ctr.crm.moduls.syscirculationconfig.dao;

import java.util.List;
import java.util.Map;

import com.ctr.crm.moduls.syscirculationconfig.models.SysCirculationConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;

import com.ctr.crm.commons.utils.PojoUtils;

/**
 * @author DoubleLi
 * @date  2019-04-23
 */
public interface SysCirculationConfigDao {

    @SelectProvider(type =SysCirculationConfigSqlProvider.class, method = "get")
    SysCirculationConfig get(String id);

    @SelectProvider(type =SysCirculationConfigSqlProvider.class, method = "findList")
    List<SysCirculationConfig> findList(SysCirculationConfig sysCirculationConfig);

    @SelectProvider(type =SysCirculationConfigSqlProvider.class, method = "findAllList")
    List<SysCirculationConfig> findAllList(SysCirculationConfig sysCirculationConfig);
    
    @SelectProvider(type =SysCirculationConfigSqlProvider.class, method = "getCirculationList")
    List<SysCirculationConfig> getCirculationList(Integer circulationType);

    @InsertProvider(type =SysCirculationConfigSqlProvider.class, method = "insert")
    void insert(SysCirculationConfig sysCirculationConfig);

    @InsertProvider(type =SysCirculationConfigSqlProvider.class, method = "insertBatch")
    int insertBatch(List<SysCirculationConfig> sysCirculationConfigs);

    @UpdateProvider(type =SysCirculationConfigSqlProvider.class, method = "update")
    int update(SysCirculationConfig sysCirculationConfig);

    @UpdateProvider(type =SysCirculationConfigSqlProvider.class, method = "delete")
    boolean delete(String id);

    class SysCirculationConfigSqlProvider {

        public String get(String id) {
            return PojoUtils.getSelectSql(SysCirculationConfig.class, "serialVersionUID", null, "id", id);
        }

        public String findList(SysCirculationConfig sysCirculationConfig) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(sysCirculationConfig, "serialVersionUID"));
                    FROM("SysCirculationConfig");
                    WHERE("deleted=0");
                    if(StringUtils.isNotBlank(sysCirculationConfig.getReason())){
                        WHERE("reason=#{reason}");
                    }
                }
            }.toString();
        }

        public String findAllList(SysCirculationConfig sysCirculationConfig) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(sysCirculationConfig, "serialVersionUID"));
                    FROM("SysCirculationConfig");
                    WHERE("deleted=0");
                }
            }.toString();
        }

        public String insert(SysCirculationConfig sysCirculationConfig) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, sysCirculationConfig, "serialVersionUID");
            return PojoUtils.getInsertSQL(SysCirculationConfig.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<SysCirculationConfig> sysCirculationConfigs) {
            StringBuilder sql = new StringBuilder();
            sql.append("insert ignore into SysCirculationConfig");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(sysCirculationConfigs.get(0), "serialVersionUID", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            column.deleteCharAt(column.length()-1);
            sql.append("(").append(column).append(")").append(" values");
            for (SysCirculationConfig bean:
                              sysCirculationConfigs) {
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

        public String update(SysCirculationConfig sysCirculationConfig) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, sysCirculationConfig, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("SysCirculationConfig", dataMap, "id",sysCirculationConfig.getId());
        }

        public String delete(String id) {
            return "update SysCirculationConfig set deleted=1 where id='" + id + "'";
        }
        
        public String getCirculationList(Integer circulationType){
        	return new SQL(){
        		{
        			SELECT(PojoUtils.getArrayFields(SysCirculationConfig.class, "serialVersionUID"));
        			FROM(SysCirculationConfig.class.getSimpleName());
        			WHERE("deleted=0");
        			if(circulationType != null){
	        			switch (circulationType) {
						case 1:
							AND().WHERE("deleteMember=1");
							break;
						case 2:
							AND().WHERE("transferMember=1");
							break;
						case 3:
							AND().WHERE("quitMember=1");
							break;
						default:
							break;
						}
        			}
        		}
        	}.toString();
        }
    }
}