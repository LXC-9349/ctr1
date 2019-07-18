package com.ctr.crm.moduls.purview.dao;

import java.util.List;
import java.util.Map;

import com.ctr.crm.moduls.purview.models.Menu;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

/**
 * 说明：
 * @author eric
 * @date 2019年4月16日 上午10:18:37
 */
public interface MenuDao {

	@Insert("insert ignore into Menu(menuName,url,parentId,orderNo,foundational) values(#{menuName},#{url},#{parentId},#{orderNo},#{foundational})")
	@Options(useGeneratedKeys=true, keyProperty="menuId", keyColumn="menuId")
	int insertMenu(Menu menu);
	
	@Insert("insert ignore into MenuOfRole(roleId,menuId) values(#{roleId},#{menuId})")
	void insertMenuOfRole(@Param("roleId") Integer roleId, @Param("menuId") Integer menuId);
	
	@Delete("delete from MenuOfRole where roleId=#{roleId} and menuId=#{menuId}")
	void deleteMenuOfRole(@Param("roleId") Integer roleId, @Param("menuId") Integer menuId);
	
	@InsertProvider(type=MenuSqlBuilder.class, method="buildDefaultMenuOfRoleSql")
	void defaultMenuOfRole(@Param("roleId") Integer roleId, @Param("adminRole") boolean adminRole);
	
	@Select("select menuId,menuName,parentId,url,isUse,orderNo,foundational,incremental from Menu where isUse=1")
	List<Menu> selectAll();
	
	@Select("select count(0) from MenuOfRole mor join WorkerOfRole wor on mor.roleId=wor.roleId join Menu m on mor.menuId=m.menuId where wor.workerId=#{workerId} and m.url=#{menuUrl}")
	@ResultType(value=Integer.class)
	int selectCount(@Param("workerId") Integer workerId, @Param("menuUrl") String menuUrl);
	
	@Select("select m.url from Menu m join MenuOfRole mor on m.menuId=mor.menuId join WorkerOfRole wor on mor.roleId=wor.roleId where wor.workerId=#{workerId}")
	List<String> getMenuPermissions(Integer workerId);
	
	@Select("select m.menuId from Menu m join MenuOfRole mor on m.menuId=mor.menuId where mor.roleId=#{roleId} and m.isUse=1")
	List<Integer> getMenuPermissionsOfRole(Integer roleId);
	
	@Select("select menuId,menuName,parentId,url,isUse,orderNo,foundational,incremental from Menu where url=#{url} limit 1")
	Menu selectByUrl(@Param("url") String menuUrl);
	
	public static class MenuSqlBuilder{
		public static String buildDefaultMenuOfRoleSql(Map<String, Object> map){
			StringBuilder sql = new StringBuilder();
			sql.append("insert ignore into MenuOfRole(roleId,menuId) select #{roleId},m.menuId from Menu m where m.isUse=1");
			boolean adminRole = (boolean)map.get("param2");
			if(!adminRole){
				sql.append(" and m.foundational=0");
			}
			return sql.toString();
		}
	}
}
