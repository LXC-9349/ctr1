package ${BasePackageName}${DaoPackageName};

import ${BasePackageName}${EntityPackageName}.${ClassName};

import com.ctr.crm.commons.utils.PojoUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;


import java.util.List;
import java.util.Map;
/**
 * @author ${Author}
 * @date  ${Date}
 */
public interface ${ClassName}Dao {

    @SelectProvider(type =${ClassName}SqlProvider.class, method = "get")
    ${ClassName} get(Integer id);

    @SelectProvider(type =${ClassName}SqlProvider.class, method = "findList")
    List<${ClassName}> findList(${ClassName} ${EntityName});

    @SelectProvider(type =${ClassName}SqlProvider.class, method = "findAllList")
    List<${ClassName}> findAllList(${ClassName} ${EntityName});

    @InsertProvider(type =${ClassName}SqlProvider.class, method = "insert")
    int insert(${ClassName} ${EntityName});

    @InsertProvider(type =${ClassName}SqlProvider.class, method = "insertBatch")
    int insertBatch(@Param("${EntityName}s") List<${ClassName}> ${EntityName}s);

    @UpdateProvider(type =${ClassName}SqlProvider.class, method = "update")
    int update(${ClassName} ${EntityName});

    @UpdateProvider(type =${ClassName}SqlProvider.class, method = "delete")
    boolean delete(Integer id);

    @Select("select IFNULL((select max(id)+1 from ${ClassName}),1) from dual")
    Integer getID();

    class ${ClassName}SqlProvider {

        public String get(Integer id) {
            return "select * from ${ClassName} where id ='" + id + "'";
        }

        public String findList(${ClassName} ${EntityName}) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFieldsName(${EntityName}, "serialVersionUID"));
                    FROM("${ClassName}");
                    WHERE("deleted=0");
                }
            }.toString();
        }

        public String findAllList(${ClassName} ${EntityName}) {
            return "select * from ${ClassName} where deleted=0";
        }

        public String insert(${ClassName} ${EntityName}) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, ${EntityName}, "serialVersionUID");
            return PojoUtils.getInsertSQL(${ClassName}.class.getSimpleName(), dataMap, null);
        }

        public String insertBatch(List<${ClassName}> ${EntityName}s) {
            StringBuffer sql = new StringBuffer();
            sql.append("insert ignore into ${ClassName}");
            StringBuffer column = new StringBuffer();
            for (String s : PojoUtils.getArrayFields(${EntityName}s.get(0), "serialVersionUID", false)) {
                column.append("`").append(s).append("`").append(",");
            }
            sql.append("(").append(column.substring(0, column.lastIndexOf(","))).append(")").append(" values");
            for (${ClassName} bean: ${EntityName}s) {
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

        public String update(${ClassName} ${EntityName}) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, ${EntityName}, "serialVersionUID,id,deleted");
            return PojoUtils.getUpdateSQL("${ClassName}", dataMap, "id",${EntityName}.getId());
        }

        public String delete(Integer id) {
            return "update ${ClassName} set deleted=1 where id='" + id + "'";
        }
    }
}