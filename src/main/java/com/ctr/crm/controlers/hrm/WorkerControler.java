package com.ctr.crm.controlers.hrm;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.BeanUtils;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import com.ctr.crm.moduls.purview.service.RbacService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;

/**
 * 说明：
 * @author eric
 * @date 2019年4月9日 下午2:44:12
 */
@Secure(0)
@Api(tags="员工管理")
@RequestMapping("/api/hrm/worker")
@RestController
@Menu(menuName="组织架构", menuUrl="hrm_organ", foundational=false, parent=@Parent(menuName="员工管理", menuUrl="hrm", foundational=false))
public class WorkerControler implements CurrentWorkerAware{

	@Resource
	private WorkerService workerService;
	@Resource
	private RbacService rbacService;
	
	@ApiOperation(value="所有员工信息")
	@RequestMapping(value="all", method={RequestMethod.GET})
	public ResponseData getAllWorkers(){
		List<Worker> workers = workerService.selectAll();
		ResponseData result = new ResponseData();
		result.setCode(ResponseStatus.success.getStatusCode());
		result.setData(workers);
		return result;
	}
	
	@ApiOperation(value="账号回收站")
	@RequestMapping(value="recycle", method={RequestMethod.GET})
	@Menu(menuName="帐号回收站", menuUrl="hrm_recycle", foundational=false, parent=@Parent(menuName="员工管理", menuUrl="hrm", foundational=false))
	public ResponseData getAllLeaveWorkers(){
		List<Worker> workers = workerService.selectLeaveAll();
		ResponseData result = new ResponseData();
		result.setCode(ResponseStatus.success.getStatusCode());
		result.setData(workers);
		return result;
	}
	
	@ApiOperation("单个员工信息")
	@RequestMapping(value="single", method={RequestMethod.GET})
	@Menu(verify=false)
	public ResponseData getSingleWorker(Integer workerId){
		Worker worker = workerService.select(workerId);
		Map<String, Object> data = BeanUtils.transBeanToMap(worker);
		data.put("roleIds", rbacService.getRolesOfWorker(workerId));
		return new ResponseData(ResponseStatus.success, data);
	}
	
	@ApiOperation("部门员工信息")
	@RequestMapping(value="get", method={RequestMethod.GET})
	@Menu(verify=false)
	public ResponseData getWorkerByDeptId(Integer deptId){
		return new ResponseData(ResponseStatus.success, workerService.selectByDept(deptId));
	}
	
	@ApiOperation("更新员工信息")
	@ApiImplicitParam(name="roleIds", value="角色", dataType="Integer", allowMultiple=true)
	@Menu(verify=false)
	@Secure(value=1, actionName="修改员工", actionUri="/api/hrm/worker/update", actionNote="员工管理", foundational=false)
	@RequestMapping(value="update", method={RequestMethod.POST})
	public ResponseData updateWorker(@ModelAttribute("worker") Worker worker, Integer[] roleIds){
		if(workerService.isOccupy(worker.getLineNum(), worker.getWorkerId())){
			return new ResponseData(ResponseStatus.failed, "分机号已被占用");
		}
		if(!workerService.update(worker)){
			return new ResponseData(ResponseStatus.failed);
		}
		// 更新用户角色
		if(roleIds != null && roleIds.length > 0){
			rbacService.updateWorkerOfRole(worker.getWorkerId(), roleIds);
		}
		return new ResponseData(ResponseStatus.success);
	}
	
	@ApiOperation(value="修改个人信息", notes="只可修改分机号、邮箱、手机号。修改时其中分机号必须回传，如为空，则认为是解绑分机号")
	@Menu(verify=false)
	@RequestMapping(value="personal/update", method={RequestMethod.POST})
	public ResponseData updateWorker(@ModelAttribute("worker") Worker worker){
		Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
		if(workerService.isOccupy(worker.getLineNum(), currentWorker.getWorkerId())){
			return new ResponseData(ResponseStatus.failed, "分机号已被占用");
		}
		Worker w = new Worker(currentWorker.getWorkerId(), currentWorker.getCompanyId());
		w.setLineNum(worker.getLineNum());
		w.setEmail(worker.getEmail());
		w.setPhoneNumber(worker.getPhoneNumber());
		if(!workerService.update(w)){
			return new ResponseData(ResponseStatus.failed);
		}
		return new ResponseData(ResponseStatus.success);
	}
	
	@ApiOperation("修改密码")
	@Menu(verify=false)
	@RequestMapping(value="password/update", method={RequestMethod.POST})
	public ResponseData updatePassword(String password){
		Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
		if(StringUtils.isBlank(password)){
			return new ResponseData(ResponseStatus.failed, "密码不能为空");
		}
		Worker w = new Worker(currentWorker.getWorkerId(), currentWorker.getCompanyId());
		w.setPsw(password);
		if(!workerService.update(w)){
			return new ResponseData(ResponseStatus.failed);
		}
		return new ResponseData(ResponseStatus.success);
	}
	
	@ApiOperation("新增员工信息")
	@ApiImplicitParam(name="roleIds", value="角色", required=false, dataType="Integer", allowMultiple=true)
	@Secure(value=1, actionName="新增员工", actionUri="/api/hrm/worker/insert", actionNote="员工管理", foundational=false)
	@RequestMapping(value="insert", method={RequestMethod.POST})
	public ResponseData insertWorker(@ModelAttribute("worker") Worker worker, Integer[] roleIds){
		if(worker == null){
			return new ResponseData(ResponseStatus.failed);
		}
		if(StringUtils.isBlank(worker.getWorkerName())){
			return new ResponseData(ResponseStatus.failed, "员工姓名为空");
		}
		if(worker.getDeptId() == null){
			return new ResponseData(ResponseStatus.failed, "所属部门为空");
		}
		if(workerService.isOccupy(worker.getLineNum(), null)){
			return new ResponseData(ResponseStatus.failed, "分机号已被占用");
		}
		if(!workerService.insert(worker)){
			return new ResponseData(ResponseStatus.failed);
		}
		// 更新用户角色
		if(roleIds != null && roleIds.length > 0){
			rbacService.updateWorkerOfRole(worker.getWorkerId(), roleIds);
		}
		return new ResponseData(ResponseStatus.success);
	}
	
}
