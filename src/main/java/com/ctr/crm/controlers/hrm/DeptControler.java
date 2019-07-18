package com.ctr.crm.controlers.hrm;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.moduls.hrm.models.Dept;
import com.ctr.crm.moduls.hrm.service.DeptService;
import io.swagger.annotations.Api;
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
 * @date 2019年4月9日 下午2:44:12
 */
@Secure(0)
@Api(tags="员工管理")
@RequestMapping("/api/hrm/dept")
@RestController
@Menu(menuName="组织架构", menuUrl="hrm_organ", foundational=false, parent=@Parent(menuName="员工管理", menuUrl="hrm", foundational=false))
public class DeptControler implements CurrentWorkerAware{

	@Resource
	private DeptService deptService;
	
	@ApiOperation(value="部门架构")
	@RequestMapping(value="all", method={RequestMethod.GET})
	@Menu(verify = false)
	public ResponseData getAllWorkers(){
		return new ResponseData(ResponseStatus.success, deptService.treeDept());
	}
	
	@ApiOperation("部门信息")
	@RequestMapping(value="single", method={RequestMethod.GET})
	@Menu(verify = false)
	public ResponseData getSingleDept(Integer deptId){
		return new ResponseData(ResponseStatus.success, deptService.select(deptId));
	}
	
	@ApiOperation("部门更新")
	@Secure(value=1, actionName="修改部门", actionUri="/api/hrm/dept/update", actionNote="员工管理", foundational=false)
	@RequestMapping(value="update", method={RequestMethod.POST})
	public ResponseData updateDept(@ModelAttribute("dept") Dept dept){
		if(!deptService.update(dept)){
			return new ResponseData(ResponseStatus.failed);
		}
		return new ResponseData(ResponseStatus.success);
	}
	
	@ApiOperation("部门新增")
	@Secure(value=1, actionName="新增部门", actionUri="/api/hrm/dept/insert", actionNote="员工管理", foundational=false)
	@RequestMapping(value="insert", method={RequestMethod.POST})
	public ResponseData insertDept(@ModelAttribute("dept") Dept dept){
		if(deptService.insert(dept.getDeptName(), dept.getParentId(), 1)==null){
			return new ResponseData(ResponseStatus.failed);
		}
		return new ResponseData(ResponseStatus.success);
	}
	
	@ApiOperation("部门删除")
	@Secure(value=1, actionName="删除部门", actionUri="/api/hrm/dept/delete", actionNote="员工管理", foundational=false)
	@RequestMapping(value="delete", method={RequestMethod.POST})
	public ResponseData deleteDept(Integer deptId){
		if(deptService.hasChildDept(deptId)){
			return new ResponseData(ResponseStatus.failed, "当前存在下属部门，不能直接删除");
		}
		if(!deptService.delete(deptId)){
			return new ResponseData(ResponseStatus.failed);
		}
		return new ResponseData(ResponseStatus.success);
	}
	
}
