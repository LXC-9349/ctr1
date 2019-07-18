package com.ctr.crm.controlers.ucc;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.models.WorkerSearch;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;

@Api(tags="呼叫中心")
@RequestMapping("/api/ucc/")
@RestController
@Secure(0)
@Menu(menuName="坐席监控", menuUrl="ucc_monitor")
public class ListenControler implements CurrentWorkerAware {

	@Resource
	private WorkerService workerService;
	
	@ApiOperation(value = "坐席监控", notes = "坐席监控")
	@RequestMapping(value = "monitor", method = { RequestMethod.GET })
	public ResponseData monitor(WorkerSearch search) throws Exception {
		Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
		return new ResponseData(ResponseStatus.success, workerService.monitor(search, currentWorker));
	}
}
