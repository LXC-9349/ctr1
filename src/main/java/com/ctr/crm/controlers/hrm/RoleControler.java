package com.ctr.crm.controlers.hrm;

import java.util.HashMap;
import java.util.Map;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.BeanUtils;
import com.ctr.crm.moduls.purview.models.Role;
import com.ctr.crm.moduls.purview.service.RbacService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ctr.crm.interceptors.CurrentWorkerAware;

/**
 * 说明：
 * @author eric
 * @date 2019年4月23日 下午6:32:37
 */
@Secure(0)
@Api(tags="员工管理")
@RequestMapping("/api/hrm/role")
@RestController
@Menu(menuName="角色权限", menuUrl="hrm_role", foundational=false, parent=@Parent(menuName="员工管理", menuUrl="hrm", foundational=false))
public class RoleControler implements CurrentWorkerAware {

	@Resource
	private RbacService rbacService;
	
	@ApiOperation(value="所有角色")
	@RequestMapping(value="all", method={RequestMethod.GET})
	@Menu(verify = false)
	public ResponseData getAllRoles(){
		return new ResponseData(ResponseStatus.success, rbacService.getAllRoles());
	}
	
	@ApiOperation("角色信息")
	@RequestMapping(value="single", method={RequestMethod.GET})
	@Menu(verify = false)
	public ResponseData getSingleRole(Integer roleId){
		Role role = rbacService.select(roleId);
		Map<String, Object> data = BeanUtils.transBeanToMap(role);
		data.put("workerList", rbacService.getWorkersOfRole(roleId));
		return new ResponseData(ResponseStatus.success, data);
	}
	
	@ApiOperation("修改角色")
	@Secure(value=1, actionName="修改角色", actionUri="/api/hrm/role/update", actionNote="员工管理", foundational=false)
	@RequestMapping(value="update", method={RequestMethod.POST})
	public ResponseData updateRole(@ModelAttribute("Role") Role role){
		if(!rbacService.update(role)){
			return new ResponseData(ResponseStatus.failed);
		}
		return new ResponseData(ResponseStatus.success);
	}
	
	@ApiOperation("新增角色")
	@Secure(value=1, actionName="新增角色", actionUri="/api/hrm/role/insert", actionNote="员工管理", foundational=false)
	@RequestMapping(value="insert", method={RequestMethod.POST})
	public ResponseData insertRole(@ModelAttribute("Role") Role role){
		boolean success = rbacService.addRole(role.getRoleName(), role.getRemark(), 0);
		if(!success){
			return new ResponseData(ResponseStatus.failed);
		}
		return new ResponseData(ResponseStatus.success);
	}
	
	@ApiOperation("删除角色")
	@Secure(value=1, actionName="删除角色", actionUri="/api/hrm/role/delete", actionNote="员工管理", foundational=false)
	@RequestMapping(value="delete", method={RequestMethod.POST})
	public ResponseData deleteRole(Integer roleId){
		if(!rbacService.forbidden(roleId)){
			return new ResponseData(ResponseStatus.failed);
		}
		return new ResponseData(ResponseStatus.success);
	}
	
	@ApiOperation("角色权限信息")
	@RequestMapping(value="permission/info", method={RequestMethod.GET})
	public ResponseData permissionsInfo(Integer roleId){
		Role role = rbacService.select(roleId);
		if(role == null){
			return new ResponseData(ResponseStatus.failed, "角色不存在");
		}
		Map<String, Object> data = new HashMap<String, Object>();
		// 角色信息
		data.put("role", role);
		// 所有菜单,操作,范围数据
		data.put("menus", rbacService.getAllMenus());
		data.put("actions", rbacService.getAllActions());
		data.put("ranges", rbacService.getAllRange());
		// 当前角色拥有的菜单,操作,范围项
		data.put("menuPermissions", rbacService.getMenuPermissionsOfRole(roleId));
		data.put("actionPermissions", rbacService.getActionPermissionsOfRole(roleId));
		data.put("rangePermission", rbacService.getRangePermissionOfRole(roleId));
		return new ResponseData(ResponseStatus.success, data);
	}
	
	@ApiOperation("编辑角色权限")
	@ApiImplicitParams({
		@ApiImplicitParam(name="roleId", value="角色", required=true, dataType="Integer"),
		@ApiImplicitParam(name="menuIds", value="菜单", required=false, dataType="Integer", allowMultiple=true),
		@ApiImplicitParam(name="actionIds", value="操作", required=false, dataType="Integer", allowMultiple=true),
		@ApiImplicitParam(name="rangeId", value="数据范围", required=false, dataType="Integer")
	})
	@RequestMapping(value="permission/edit", method={RequestMethod.POST})
	public ResponseData permissionsEdit(Integer roleId, Integer[] menuIds, Integer[] actionIds, Integer rangeId){
		if(rbacService.isDefault(roleId)){
			return new ResponseData(ResponseStatus.failed, "默认角色不能操作");
		}
		rbacService.editMenuOfRole(roleId, menuIds);
		rbacService.editActionOfRole(roleId, actionIds);
		rbacService.editRangeOfRole(roleId, rangeId);
		return new ResponseData(ResponseStatus.success);
	}
	
	@ApiOperation("编辑角色人员")
	@ApiImplicitParams({
		@ApiImplicitParam(name="roleId", value="角色", required=true, dataType="Integer"),
		@ApiImplicitParam(name="workerIds", value="工号", required=false, dataType="Integer", allowMultiple=true)
	})
	@RequestMapping(value="worker/edit", method={RequestMethod.POST})
	public ResponseData permissionsEdit(Integer roleId, Integer[] workerIds){
		if(rbacService.isDefault(roleId)){
			return new ResponseData(ResponseStatus.failed, "默认角色不能操作");
		}
		rbacService.editWorkerOfRole(roleId, workerIds);
		return new ResponseData(ResponseStatus.success);
	}
	
}
