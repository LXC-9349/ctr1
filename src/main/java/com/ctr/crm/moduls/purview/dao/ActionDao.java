package com.ctr.crm.moduls.purview.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.ctr.crm.moduls.purview.models.Action;

/**
 * 说明：
 * @author eric
 * @date 2019年4月15日 下午5:31:17
 */
public interface ActionDao {

	@Insert("insert ignore into Action(actionUri,actionName,note,foundational) values(#{actionUri},#{actionName},#{note},#{foundational})")
	void insertAction(Action action);
	
	@Insert("insert ignore into ActionOfRole(roleId,actionId) values(#{roleId},#{actionId})")
	void insertActionOfRole(@Param("roleId") Integer roleId, @Param("actionId") Integer actionId);
	
	@Delete("delete from ActionOfRole where roleId=#{roleId} and actionId=#{actionId}")
	void deleteActionOfRole(@Param("roleId") Integer roleId, @Param("actionId") Integer actionId);
	
	@InsertProvider(type=ActionSqlBuilder.class, method="buildDefaultActionOfRoleSql")
	void defaultActionOfRole(@Param("roleId") Integer roleId, @Param("adminRole") boolean adminRole);
	
	@Select("select actionId,actionUri,actionName,note,isUse,appreciate,foundational from Action where isUse=1")
	List<Action> selectAll();
	
	@Select("select a.actionId,a.actionUri from Action a join ActionOfRole aor on a.actionId=aor.actionId join WorkerOfRole wor on aor.roleId=wor.roleId where wor.workerId=#{workerId} and a.isUse=1")
	List<Map<String, Object>> getActionPermissions(Integer workerId);
	
	@Select("select a.actionId from Action a join ActionOfRole aor on a.actionId=aor.actionId where aor.roleId=#{roleId} and a.isUse=1")
	List<Integer> getActionPermissionsOfRole(Integer roleId);
	
	@Select("select count(0) from ActionOfRole aor join WorkerOfRole wor on aor.roleId=wor.roleId join Action a on aor.actionId=a.actionId where wor.workerId=#{workerId} and a.actionUri=#{actionUri}")
	@ResultType(value=Integer.class)
	int selectCount(@Param("workerId") Integer workerId, @Param("actionUri") String actionUri);
	
	public static class ActionSqlBuilder{
		public static String buildDefaultActionOfRoleSql(Map<String, Object> map){
			StringBuilder sql = new StringBuilder();
			sql.append("insert ignore into ActionOfRole(roleId,actionId) select #{roleId},a.actionId from Action a where a.isUse=1");
			boolean adminRole = (boolean)map.get("param2");
			if(!adminRole){
				sql.append(" and a.foundational=0");
			}
			return sql.toString();
		}
	}
}
