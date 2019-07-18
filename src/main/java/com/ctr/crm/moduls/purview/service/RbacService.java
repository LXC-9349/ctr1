package com.ctr.crm.moduls.purview.service;

import java.util.List;
import java.util.Map;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.purview.models.DataRange;
import com.ctr.crm.moduls.purview.models.Range;
import com.ctr.crm.moduls.purview.models.Role;
import com.ctr.crm.commons.rbac.RangeField;

/**
 * 说明：权限管理
 * @author eric
 * @date 2019年4月15日 下午6:59:01
 */
public interface RbacService {

	/**
	 * 添加数据范围
	 * @param rangeValue
	 * @param rangeName
	 * @see RangeField
	 */
	void addRange(int rangeValue, String rangeName, int orderNo);
	/**
	 * 新增菜单
	 * @param menuName 菜单名称
	 * @param menuUrl 菜单地址
	 * @param orderNo 序号
	 * @param foundational 是否基础功能 0基础功能 1高级功能 一般地，高级功能赋予管理员,所以系统初始化管理员角色时才有高级功能权限
	 * @param parentId 父菜单ID
	 * @return 返回菜单ID
	 */
	Integer addMenu(String menuName, String menuUrl,Integer orderNo, boolean foundational, Integer parentId);
	/**
	 * 新增操作权限项
	 * @param actionName
	 * @param actionUri
	 * @param actionNote
	 */
	void addAction(String actionName, String actionUri, String actionNote, boolean foundational);
	
	/**
	 * 新增角色
	 * @param roleName 角色名称
	 * @param remark 备注
	 * @param isDefault 是否系统默认 1是 0否，如果是默认，则不可做任何编辑
	 */
	boolean addRole(String roleName, String remark, int isDefault);
	
	/**
	 * 判断坐席是否有该URI的访问权限
	 * @param workerId
	 * @param uri 
	 * @param uriType 1表示菜单 2表示操作
	 * @return
	 */
	boolean auth(Integer workerId, String uri, int uriType);
	
	// 获取所有的操作、菜单和范围
	List<Map<String, Object>> getAllActions();
	List<Map<String, Object>> getAllMenus();
	List<Range> getAllRange();
	List<Role> getAllRoles();
	
	/**
	 * 初始化默认角色权限和系统管理员权限
	 */
	void initDefaultAuth();
	
	/**
	 * 赋予指定角色菜单权限
	 * @param roleId
	 * @param menuIds
	 */
	void editMenuOfRole(Integer roleId, Integer[] menuIds);
	/**
	 * 赋予指定角色操作权限
	 * @param roleId
	 * @param menuIds
	 */
	void editActionOfRole(Integer roleId, Integer[] actionIds);
	/**
	 * 赋予指定角色范围权限
	 * @param roleId
	 * @param menuIds
	 */
	void editRangeOfRole(Integer roleId, Integer rangeId);
	
	/**
	 * 编辑角色人员
	 * @param roleId
	 * @param workerIds
	 */
	void editWorkerOfRole(Integer roleId, Integer[] workerIds);
	
	//------员工所有的菜单、操作、范围权限-------//
	List<String> getMenuPermissions(Integer workerId);
	List<Map<String, Object>> getActionPermissions(Integer workerId);
	Integer getRangePermission(Integer workerId);
	//--------------end----------------//
	//------员工所有的菜单、操作、范围权限-------//
	List<Integer> getMenuPermissionsOfRole(Integer roleId);
	List<Integer> getActionPermissionsOfRole(Integer roleId);
	Integer getRangePermissionOfRole(Integer roleId);
	//--------------end----------------//
	
	List<Integer> getRolesOfWorker(Integer workerId);
	List<Worker> getWorkersOfRole(Integer roleId);
	
	/**
	 * 删除角色
	 * @param roleId
	 * @return
	 */
	boolean forbidden(Integer roleId);
	
	boolean isDefault(Integer roleId);
	
	Role select(Integer roleId);
	boolean update(Role role);
	
	void updateWorkerOfRole(Integer workerId, Integer[] roleIds);
	/**
	 * 根据当前登录人的数据范围生成DeptRange
	 * @param deptId
	 * @param currentWorkerId
	 * @return
	 */
	DataRange getDataRange(Integer deptId, Integer currentWorkerId);
	/**
	 * 根据当前登录人的数据范围生成DeptRange
	 * 
	 * {@link DataRange}
	 * 
	 * @param deptId
	 * @param currentWorker
	 * @return
	 */
	DataRange getDataRange(Integer deptId, Worker currentWorker);
}
