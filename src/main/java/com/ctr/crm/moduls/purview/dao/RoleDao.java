package com.ctr.crm.moduls.purview.dao;

import java.util.List;

import com.ctr.crm.moduls.purview.models.Role;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 说明：
 * @author eric
 * @date 2019年4月15日 下午6:21:27
 */
public interface RoleDao {

	@Insert("insert ignore into Role(roleName,remark,isDefault) values(#{roleName},#{remark},#{isDefault})")
	int insertRole(Role Role);
	@Update("update Role set roleName=#{roleName} where roleId=#{roleId}")
	int updateRole(@Param("roleId")Integer roleId, @Param("roleName")String roleName);
	
	@Select("select roleId,roleName,remark,isDefault from Role where enabled=1 order by orderNo")
	List<Role> selectAll();
	@Select("select roleId,roleName,remark,isDefault from Role where enabled=1 and isDefault=1 order by orderNo")
	List<Role> selectAllDefault();
	@Select("select roleId,roleName,remark,isDefault from Role where roleId=#{roleId} and enabled=1")
	Role select(Integer roleId);
	
	@Delete("delete from Role where roleId=#{roleId}")
	boolean forbidden(Integer roleId);

	@Insert("insert ignore into WorkerOfRole(roleId,workerId) values(#{roleId},#{workerId})")
	void insertWorkerOfRole(@Param("roleId") Integer roleId, @Param("workerId") Integer workerId);
	
	@Delete("delete from WorkerOfRole where roleId=#{roleId}")
	void deleteWorkerOfRoleByRoleId(Integer roleId);
	@Delete("delete from WorkerOfRole where workerId=#{workerId}")
	void deleteWorkerOfRoleByWorkerId(Integer workerId);
	@Delete("delete from WorkerOfRole where roleId=#{roleId} and workerId=#{workerId}")
	void deleteWorkerOfRole(@Param("roleId") Integer roleId, @Param("workerId") Integer workerId);
	@Select("select roleId from WorkerOfRole where workerId=#{workerId}")
	List<Integer> getRolesOfWorker(Integer workerId);
	@Select("select workerId from WorkerOfRole where roleId=#{roleId}")
	List<Integer> getWorkersOfRole(Integer roleId);
}
