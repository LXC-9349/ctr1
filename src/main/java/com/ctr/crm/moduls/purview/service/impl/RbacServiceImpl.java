package com.ctr.crm.moduls.purview.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.ctr.crm.moduls.hrm.models.Dept;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.service.DeptService;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import com.ctr.crm.moduls.purview.dao.ActionDao;
import com.ctr.crm.moduls.purview.dao.MenuDao;
import com.ctr.crm.moduls.purview.dao.RangeDao;
import com.ctr.crm.moduls.purview.dao.RoleDao;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.rbac.RangeField;
import com.ctr.crm.commons.utils.BeanUtils;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.moduls.purview.models.Action;
import com.ctr.crm.moduls.purview.models.DataRange;
import com.ctr.crm.moduls.purview.models.Menu;
import com.ctr.crm.moduls.purview.models.Range;
import com.ctr.crm.moduls.purview.models.Role;
import com.ctr.crm.moduls.purview.service.RbacService;
import com.yunhus.redisclient.RedisProxy;

/**
 * 说明：
 * @author eric
 * @date 2019年4月15日 下午6:59:32
 */
@Service("rbacService")
public class RbacServiceImpl implements RbacService {

	@Autowired
	private ActionDao actionDao;
	@Autowired
	private MenuDao menuDao;
	@Autowired
	private RangeDao rangeDao;
	@Autowired
	private RoleDao roleDao;
	@Resource
	private WorkerService workerService;
	@Resource
	private DeptService deptService;
	private RedisProxy redis = RedisProxy.getInstance();
	
	@Override
	public void addRange(int rangeValue, String rangeName, int orderNo) {
		Range range = new Range();
		range.setRangeValue(rangeValue);
		range.setRangeName(rangeName);
		range.setOrderNo(orderNo);
		rangeDao.insertRange(range);
	}

	@Override
	public Integer addMenu(String menuName, String menuUrl, Integer orderNo,
			boolean foundational, Integer parentId) {
		if(menuName == null || menuUrl == null) return null;
		Menu menu = new Menu();
		menu.setMenuName(menuName);
		menu.setUrl(menuUrl);
		menu.setOrderNo(orderNo);
		menu.setFoundational(foundational?0:1);
		menu.setParentId(parentId);
		int row = menuDao.insertMenu(menu);
		if(row > 0) return menu.getMenuId();
		menu = menuDao.selectByUrl(menuUrl);
		return menu == null ? null : menu.getMenuId();
	}

	@Override
	public void addAction(String actionName, String actionUri, String actionNote,
			boolean foundational) {
		if(actionName == null || actionUri == null) return;
		Action action = new Action();
		action.setActionName(actionName);
		action.setActionUri(actionUri);
		action.setNote(actionNote);
		action.setFoundational(foundational?0:1);
		actionDao.insertAction(action);
	}
	
	@Override
	public boolean addRole(String roleName, String remark, int isDefault) {
		if(StringUtils.isBlank(roleName)) return false;
		Role role = new Role();
		role.setRoleName(roleName);
		role.setRemark(remark);
		role.setIsDefault(isDefault);
		int result = roleDao.insertRole(role);
		return result > 0;
	}

	@Override
	public boolean auth(Integer workerId, String uri, int uriType) {
		String key = "rbac_"+(uriType==1?"menu_":"action_")+workerId+"_"+uri;
		Object v = redis.get(key);
		if(v != null){
			return (boolean) v;
		}
		boolean auth = uriType == 1 ? menuDao.selectCount(workerId, uri)>0 : actionDao.selectCount(workerId, uri)>0;
		try {
			redis.set(key, auth, Constants.exp_12hours);
		} catch (Exception e) {
		}
		return auth;
	}

	@Override
	public List<Map<String, Object>> getAllActions() {
		List<Map<String, Object>> result = new ArrayList<>();
		List<Action> actionList = actionDao.selectAll();
		Set<String> actionGroup = new HashSet<String>();
		for (Action action : actionList) {
			if(action != null && StringUtils.isNotBlank(action.getNote())){
				actionGroup.add(action.getNote());
			}else{
				actionGroup.add("通用操作");
			}
		}
		for (String group : actionGroup) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("group", group);
			List<Action> childActions = new ArrayList<>();
			for (Action action : actionList) {
				String groupName = CommonUtils.evalString(action.getNote(), "通用操作");
				if(StringUtils.equals(groupName, group)){
					childActions.add(action);
				}
			}
			map.put("children", childActions);
			result.add(map);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getAllMenus() {
		List<Menu> menuList = menuDao.selectAll();
		return treeMenu(menuList);
	}
	
	private List<Map<String, Object>> treeMenu(List<Menu> menuList) {
		List<Map<String, Object>> parentMenus = new ArrayList<>();
		if(menuList == null || menuList.size() == 0) return parentMenus;
		List<Menu> childMenus = new ArrayList<>();
		// 找出所有的一级菜单和非一级菜单
		for (Menu menu : menuList) {
			if (menu.getParentId() == null) {
				parentMenus.add(BeanUtils.transBeanToMap(menu));
			} else {
				childMenus.add(menu);
			}
		}
		if(parentMenus.size() == 0) return parentMenus;
		for (Map<String, Object> parent : parentMenus) {
			List<Map<String, Object>> children = new ArrayList<>();
			Integer parentId = CommonUtils.evalInteger(parent.get("menuId"));
			for (Menu menu : childMenus) {
				if(menu.getParentId() != null && menu.getParentId().equals(parentId)){
					Map<String, Object> childMap = BeanUtils.transBeanToMap(menu);
					childMap.put("selected", false);
					children.add(childMap);
				}
			}
			parent.put("children", children);
			parent.put("isIndeterminate", false);
		}
		return parentMenus;
	}
	
	@Override
	public List<Range> getAllRange() {
		return rangeDao.selectAll();
	}

	@Override
	public List<Role> getAllRoles() {
		return roleDao.selectAll();
	}

	@Override
	public void initDefaultAuth() {
		deptService.defaultCompany();
		Integer deptId = deptService.defaultDept();
		workerService.defaultWorker(deptId);
		List<Role> roles = roleDao.selectAllDefault();
		List<Range> ranges = getAllRange();
		for (Role role : roles) {
			roleDao.insertWorkerOfRole(role.getRoleId(), Constants.DEFAULT_WORKER_ID);
			boolean adminRole = StringUtils.equals(role.getRoleName(), "管理员");
			menuDao.defaultMenuOfRole(role.getRoleId(), adminRole);
			actionDao.defaultActionOfRole(role.getRoleId(), adminRole);
			for (Range range : ranges) {
				if(range.getRangeValue() == RangeField.Company.getRangeValue()){
					rangeDao.insertRangeOfRole(role.getRoleId(), range.getRangeId());
				}
			}
		}
	}

	@Override
	public void editMenuOfRole(Integer roleId, Integer[] menuIds) {
		if(roleId == null) return;
		if(menuIds == null) menuIds = new Integer[]{};
		List<Integer> menuPermissions = menuDao.getMenuPermissionsOfRole(roleId);
		// 遍历已经有的菜单权限列表，如果不在menuIds数组，则删除
		for (Integer menuId : menuPermissions) {
			if(!ArrayUtils.contains(menuIds, menuId)){
				menuDao.deleteMenuOfRole(roleId, menuId);
			}
		}
		for (Integer menuId : menuIds) {
			menuDao.insertMenuOfRole(roleId, menuId);
		}
		// 清理缓存
		List<Menu> menus = menuDao.selectAll();
		if(menus!=null && !menus.isEmpty()){
			List<Integer> workerIds = roleDao.getWorkersOfRole(roleId);
			if(workerIds != null){
			for (Integer workerId : workerIds) {
				for (Menu menu : menus) {
					redis.delete("rbac_menu_"+workerId+"_"+menu.getUrl());
				}
			}
			}
		}
	}

	@Override
	public void editActionOfRole(Integer roleId, Integer[] actionIds) {
		if(roleId == null) return;
		if(actionIds == null) actionIds = new Integer[]{};
		List<Integer> actionPermissions = actionDao.getActionPermissionsOfRole(roleId);
		// 遍历已经有的操作权限列表，如果不在actionIds数组，说明被取消，则删除
		for (Integer actionId : actionPermissions) {
			if(!ArrayUtils.contains(actionIds, actionId)){
				actionDao.deleteActionOfRole(roleId, actionId);
			}
		}
		for (Integer actionId : actionIds) {
			actionDao.insertActionOfRole(roleId, actionId);
		}
		// 清理缓存
		List<Action> actions = actionDao.selectAll();
		if(actions!=null && !actions.isEmpty()){
			List<Integer> workerIds = roleDao.getWorkersOfRole(roleId);
			if(workerIds != null){
			for (Integer workerId : workerIds) {
				for (Action action : actions) {
					redis.delete("rbac_action_"+workerId+"_"+action.getActionUri());
				}
			}
			}
		}
	}

	@Override
	public void editRangeOfRole(Integer roleId, Integer rangeId) {
		if(roleId == null || rangeId == null) return;
		if(!rangeDao.updateRangeOfRole(roleId, rangeId)){
			rangeDao.insertRangeOfRole(roleId, rangeId);
		}
	}
	
	@Override
	public void editWorkerOfRole(Integer roleId, Integer[] workerIds) {
		if(roleId == null) return;
		if(workerIds == null) workerIds = new Integer[]{};
		List<Integer> workers = roleDao.getWorkersOfRole(roleId);
		// 遍历所有已经有该角色权限的人员列表，如果不在workerIds里面，说明被取消，则删除
		for (Integer workerId : workers) {
			if(!ArrayUtils.contains(workerIds, workerId)){
				roleDao.deleteWorkerOfRole(roleId, workerId);
			}
		}
		for (Integer workerId : workerIds) {
			roleDao.insertWorkerOfRole(roleId, workerId);
		}
	}
	
	@Override
	public List<String> getMenuPermissions(Integer workerId) {
		List<String> result = menuDao.getMenuPermissions(workerId);
		if(result == null) result = new ArrayList<String>();
		return result;
	}
	
	@Override
	public List<Map<String, Object>> getActionPermissions(Integer workerId) {
		List<Map<String, Object>> result = actionDao.getActionPermissions(workerId);
		if(result == null) result = new ArrayList<Map<String, Object>>();
		return result;
	}
	
	@Override
	public Integer getRangePermission(Integer workerId) {
		return rangeDao.getMaxRangeValue(workerId);
	}
	
	@Override
	public List<Integer> getMenuPermissionsOfRole(Integer roleId) {
		List<Integer> result = menuDao.getMenuPermissionsOfRole(roleId);
		if(result == null) result = new ArrayList<Integer>();
		return result;
	}
	
	@Override
	public List<Integer> getActionPermissionsOfRole(Integer roleId) {
		List<Integer> result = actionDao.getActionPermissionsOfRole(roleId);
		if(result == null) result = new ArrayList<Integer>();
		return result;
	}
	
	@Override
	public Integer getRangePermissionOfRole(Integer roleId) {
		return rangeDao.getRangeValueOfRole(roleId);
	}
	
	@Override
	public boolean forbidden(Integer roleId) {
		if(roleId == null) return false;
		// 默认角色不能删除
		if(isDefault(roleId)){
			return false;
		}
		if(roleDao.forbidden(roleId)){
			roleDao.deleteWorkerOfRoleByRoleId(roleId);
		}
		return true;
	}
	
	@Override
	public boolean isDefault(Integer roleId) {
		Role old = select(roleId);
		if(old != null && old.getIsDefault() == 1) {
			return true;
		}
		return false;
	}
	
	@Override
	public Role select(Integer roleId) {
		if(roleId == null) return null;
		return roleDao.select(roleId);
	}
	
	@Override
	public boolean update(Role role) {
		if(role == null || role.getRoleId() == null
				|| role.getRoleName() == null)
			return false;
		Role old = select(role.getRoleId());
		if(old == null || old.getIsDefault()==1) return false;
		int result = roleDao.updateRole(role.getRoleId(), role.getRoleName());
		return result > 0;
	}
	
	@Override
	public void updateWorkerOfRole(Integer workerId, Integer[] roleIds) {
		if(workerId == null) return;
		if(roleIds == null || roleIds.length == 0) return;
		roleDao.deleteWorkerOfRoleByWorkerId(workerId);
		for (int i = 0; i < roleIds.length; i++) {
			roleDao.insertWorkerOfRole(roleIds[i], workerId);
		}
	}
	
	@Override
	public List<Integer> getRolesOfWorker(Integer workerId) {
		if(workerId == null) return new ArrayList<Integer>();
		return roleDao.getRolesOfWorker(workerId);
	}
	
	@Override
	public List<Worker> getWorkersOfRole(Integer roleId) {
		if(roleId == null) return new ArrayList<>();
		List<Integer> workerIds = roleDao.getWorkersOfRole(roleId);
		List<Worker> result = new ArrayList<>(workerIds.size());
		for (Integer workerId : workerIds) {
			result.add(workerService.selectCache(workerId));
		}
		return result;
	}
	
	@Override
	public DataRange getDataRange(Integer deptId, Integer currentWorkerId) {
		Worker currentWorker = workerService.selectCache(currentWorkerId);
		return getDataRange(deptId, currentWorker);
	}
	
	@Override
	public DataRange getDataRange(Integer deptId, Worker currentWorker) {
		if(currentWorker == null) return null;
		Integer rangeValue = getRangePermission(currentWorker.getWorkerId());
		if(rangeValue == null) rangeValue = RangeField.Personal.getRangeValue();
		DataRange result = new DataRange();
		result.setRange(rangeValue);
		if(rangeValue == RangeField.Company.getRangeValue()){
			// 全公司
			Dept selectDept = deptService.selectCache(deptId);
			// 如果没有选择部门条件，默认匹配本部门k
			if(selectDept != null){
				result.setStructure(selectDept.getStructure()+"%");
				result.setSymbol("like");
			}
		}else if(rangeValue == RangeField.Departments.getRangeValue()
				|| rangeValue == RangeField.Department.getRangeValue()){
			boolean like = rangeValue == RangeField.Departments.getRangeValue() ? true : false;
			String suffix = like ? "%" : "";
			Dept currentDept = deptService.selectCache(currentWorker.getDeptId());
			Dept selectDept = deptService.selectCache(deptId);
			// 如果没有选择部门条件，默认匹配本部门
			if(selectDept == null){
				result.setStructure(currentDept.getStructure()+suffix);
			}else{
				// 如果所选的部门为本部门或下属部门,则匹配所选部门，否则匹配本部门
				if(StringUtils.contains(selectDept.getStructure(), currentDept.getStructure())){
					result.setStructure(selectDept.getStructure()+suffix);
				}else{
					result.setStructure(currentDept.getStructure()+suffix);
				}
			}
			result.setSymbol(like ? "like" : "=");
		}else if(rangeValue == RangeField.Personal.getRangeValue()){
			Dept currentDept = deptService.selectCache(currentWorker.getDeptId());
			result.setStructure(currentDept.getStructure());
			result.setWorkerId(currentWorker.getWorkerId());
			result.setSymbol("=");
		}
		return result;
	}
	
}
