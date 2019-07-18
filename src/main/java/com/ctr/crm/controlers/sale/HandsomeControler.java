package com.ctr.crm.controlers.sale;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponsePage;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.sales.models.MySearch;
import com.ctr.crm.moduls.sales.service.HandsomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;

/**
 * 说明：
 * @author eric
 * @date 2019年5月13日 下午4:33:58
 */
@Api(tags="我的客户")
@Secure(0)
@RestController
@RequestMapping("/api/sale")
public class HandsomeControler implements CurrentWorkerAware {
	
	@Resource
	private HandsomeService handsomeService;

	@ApiOperation("才俊佳丽")
	@RequestMapping(value="handsome", method={RequestMethod.GET})
	@Menu(menuName="才俊佳丽", menuUrl="sale_handsome", parent=@Parent(menuName="我的客户", menuUrl="sale"))
	public ResponseData mychance(@ModelAttribute MySearch search) throws Exception{
		if(search == null) search = new MySearch();
		Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
		ResponsePage<Map<String, Object>> result = handsomeService.query(search, currentWorker);
		return new ResponseData(ResponseStatus.success, result);
	}
	
	@ApiOperation("新增才俊佳丽")
	@RequestMapping(value="handsome/insert", method={RequestMethod.POST})
	@Secure(actionName="")
	public ResponseData insert(Long memberId) throws Exception{
		Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
		if(memberId == null){
			return new ResponseData(ResponseStatus.failed, "客户ID为空");
		}
		String result = handsomeService.insert(memberId, currentWorker);
		if(result != null){
			return new ResponseData(ResponseStatus.failed, result);
		}
		return new ResponseData(ResponseStatus.success);
	}
	
	@ApiOperation("取消才俊佳丽")
	@RequestMapping(value="handsome/delete", method={RequestMethod.POST})
	@Secure(actionName="")
	public ResponseData delete(Long memberId) throws Exception{
		if(memberId == null){
			return new ResponseData(ResponseStatus.failed, "客户ID为空");
		}
		String result = handsomeService.delete(memberId);
		if(result != null){
			return new ResponseData(ResponseStatus.failed, result);
		}
		return new ResponseData(ResponseStatus.success);
	}
	
}