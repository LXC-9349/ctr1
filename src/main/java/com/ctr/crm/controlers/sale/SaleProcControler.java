package com.ctr.crm.controlers.sale;

import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.controlers.sale.pojo.SaleProc;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.sales.models.SaleCase;
import com.ctr.crm.moduls.sales.models.SaleCaseProc;
import com.ctr.crm.moduls.sales.service.SaleCaseProcService;
import com.ctr.crm.moduls.sales.service.SaleCaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;

/**
 * 说明：客户处理api
 * @author eric
 * @date 2019年5月17日 上午11:30:30
 */
@Api(tags = "我的客户")
@Secure(0)
@RestController
@RequestMapping("/api/sale")
public class SaleProcControler implements CurrentWorkerAware {
	
	@Resource
	private SaleCaseProcService saleCaseProcService;
	@Resource
	private SaleCaseService saleCaseService;

	@ApiOperation("历史跟进记录")
	@ApiImplicitParams({
		@ApiImplicitParam(name="memberId", value="客户ID", required=true, dataTypeClass=Long.class),
		@ApiImplicitParam(name="notesType", value="小记类型，不填为所有 0沟通小记 1共享小记", required=false, dataTypeClass=Integer.class)
	})
    @RequestMapping(value = "proc/list", method = {RequestMethod.GET})
    public ResponseData procList(Long memberId, Integer notesType) throws Exception {
		Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
        return new ResponseData(ResponseStatus.success, saleCaseProcService.selectByNotesType(memberId, notesType, currentWorker));
    }
	
	@ApiOperation("保存小记")
	@RequestMapping(value = "proc/save", method = {RequestMethod.POST})
	public ResponseData procSave(@ModelAttribute SaleProc saleProc, HttpServletRequest request) throws Exception {
		if(saleProc == null){
			return new ResponseData(ResponseStatus.failed, "参数为空");
		}
		if(saleProc.getMemberId() == null 
				|| saleProc.getCaseClass() == null 
				|| saleProc.getNextContactTime() == null
				|| StringUtils.isBlank(saleProc.getProcItem())){
			return new ResponseData(ResponseStatus.failed, "参数不完整");
		}
		SaleCase sc = saleCaseService.getByMemberId(saleProc.getMemberId());
		if(sc == null){
			return new ResponseData(ResponseStatus.failed, "客户已不在库中");
		}
		Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
		Date now = new Date();
		//意向度有变化
		if(sc.getCaseClass() != null && !sc.getCaseClass().equals(saleProc.getCaseClass())){
			sc.setClassChangeTime(now);
		}
		sc.setLastContactTime(now);
		sc.setNextContactTime(saleProc.getNextContactTime());
		sc.setCaseClass(saleProc.getCaseClass());
		boolean success = saleCaseService.update(sc);
		if(success){
			// 记小记
			SaleCaseProc caseProc = new SaleCaseProc();
			caseProc.setProcId(saleProc.getProcId());
			caseProc.setMemberId(saleProc.getMemberId());
			caseProc.setWorkerId(currentWorker.getWorkerId());
			caseProc.setWorkerName(currentWorker.getWorkerName());
			caseProc.setProcTime(now);
			caseProc.setCaseClass(saleProc.getCaseClass());
			caseProc.setProcIp(CommonUtils.getRealRemoteIp(request));
			caseProc.setProcItem(saleProc.getProcItem());
			caseProc.setNotesType(0);//沟通小记
			caseProc.setNextContactTime(saleProc.getNextContactTime());
			saleCaseProcService.save(caseProc, saleProc.getFiles());
		}
		return new ResponseData(ResponseStatus.success);
	}
	
	@ApiOperation("保存共享小记")
	@RequestMapping(value = "proc/share/save", method = {RequestMethod.POST})
	public ResponseData shareProcSave(@ModelAttribute SaleProc saleProc, HttpServletRequest request) throws Exception {
		if(saleProc == null){
			return new ResponseData(ResponseStatus.failed, "参数为空");
		}
		if(saleProc.getMemberId() == null){
			return new ResponseData(ResponseStatus.failed, "客户ID为空");
		}
		if(StringUtils.isBlank(saleProc.getProcItem())){
			return new ResponseData(ResponseStatus.failed, "共享小记内容为空");
		}
		Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
		Date now = new Date();
		// 记录共享小记
		SaleCaseProc caseProc = new SaleCaseProc();
		caseProc.setMemberId(saleProc.getMemberId());
		caseProc.setWorkerId(currentWorker.getWorkerId());
		caseProc.setWorkerName(currentWorker.getWorkerName());
		caseProc.setProcTime(now);
		caseProc.setProcIp(CommonUtils.getRealRemoteIp(request));
		caseProc.setProcItem(saleProc.getProcItem());
		caseProc.setNotesType(1);//共享小记
		saleCaseProcService.save(caseProc, saleProc.getFiles());
		return new ResponseData(ResponseStatus.success);
	}
	
}
