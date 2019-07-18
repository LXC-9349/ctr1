package com.ctr.crm.controlers.hrm;

import javax.annotation.Resource;

import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.moduls.hrm.service.DeptService;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;

/**
 * 说明：当前登录人数据范围
 * @author eric
 * @date 2019年5月14日 上午11:50:20
 */
@Secure(0)
@Api(tags="员工管理")
@RequestMapping("/api/hrm/range")
@RestController
public class RangeControler implements CurrentWorkerAware {

	@Resource
	private DeptService deptService;
	@Resource
	private WorkerService workerService;
	
	@ApiOperation(value="部门范围",notes="返回当前登录人最大数据范围的部门列表")
	@RequestMapping(value="dept", method={RequestMethod.GET})
	public ResponseData deptRange() throws Exception{
		return new ResponseData(ResponseStatus.success, deptService.treeDeptRange(CurrentWorkerLocalCache.getCurrentWorker()));
	}
	
	@ApiOperation(value="员工范围",notes="返回当前登录人最大数据范围的部门列表")
	@ApiImplicitParam(name="deptId", value="所选部门", required=false, dataTypeClass=Integer.class)
	@RequestMapping(value="worker", method={RequestMethod.GET})
	public ResponseData workerRange(Integer deptId) throws Exception{
		return new ResponseData(ResponseStatus.success, workerService.workerRange(deptId, CurrentWorkerLocalCache.getCurrentWorker()));
	}
}
