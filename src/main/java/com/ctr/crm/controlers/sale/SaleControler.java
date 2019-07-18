package com.ctr.crm.controlers.sale;

import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponsePage;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.file.FileCommonUtils;
import com.ctr.crm.moduls.allot.service.AllotService;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.member.service.MemberAttachmentService;
import com.ctr.crm.moduls.member.service.MemberVipService;
import com.ctr.crm.moduls.sales.models.MySearch;
import com.ctr.crm.moduls.sales.models.SaleCase;
import com.ctr.crm.moduls.sales.service.SaleCaseService;
import com.ctr.crm.moduls.syscirculationconfig.models.SysCirculationConfig;
import com.ctr.crm.moduls.syscirculationconfig.service.SysCirculationConfigService;
import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import java.util.Map;

/**
 * 说明：
 *
 * @author eric
 * @date 2019年5月13日 下午4:33:58
 */
@Api(tags = "我的客户")
@Secure(0)
@RestController
@RequestMapping("/api/sale")
public class SaleControler implements CurrentWorkerAware {

    @Resource
    private SaleCaseService saleCaseService;
    @Autowired
    private AllotService allotService;
    @Resource
    private SysCirculationConfigService sysCirculationConfigService;
    @Resource
    private MemberAttachmentService memberAttachmentService;
    @Resource
	private MemberVipService memberVipService;

    @ApiOperation("我的机会")
    @RequestMapping(value = "mychance", method = {RequestMethod.GET})
    @Menu(menuName = "我的机会", menuUrl = "sale_mychance", parent = @Parent(menuName = "我的客户", menuUrl = "sale"))
    public ResponseData mychance(@ModelAttribute MySearch search) throws Exception {
        if (search == null) search = new MySearch();
        Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
        search.setCaseType(1);
        ResponsePage<Map<String, Object>> result = saleCaseService.query(search, currentWorker);
        return new ResponseData(ResponseStatus.success, result);
    }

    @ApiOperation("我的VIP")
    @RequestMapping(value = "myvip", method = {RequestMethod.GET})
    @Menu(menuName = "我的VIP", menuUrl = "sale_myvip", parent = @Parent(menuName = "我的客户", menuUrl = "sale"))
    public ResponseData myvip(@ModelAttribute MySearch search) throws Exception {
        if (search == null) search = new MySearch();
        Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
        search.setCaseType(2);
        ResponsePage<Map<String, Object>> result = saleCaseService.query(search, currentWorker);
        return new ResponseData(ResponseStatus.success, result);
    }

    @ApiOperation("资源放弃")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberId", value = "客户ID", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "circulationId", value = "放弃原因。请到【客户流转原因设置-流转原因】中获取", required = true, dataType = "String")})
    @RequestMapping(value = "quit", method = {RequestMethod.POST})
    @Secure(value = 1, actionName = "资源放弃", actionUri = "/api/sale/quit", actionNote = "我的客户")
    public ResponseData quit(Long memberId, String circulationId) throws Exception {
        ResponseData responseData = new ResponseData();
        Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
        SaleCase saleCase = saleCaseService.getByMemberId(memberId);
        if(saleCase == null){
        	return new ResponseData(ResponseStatus.failed, "放弃失败，资源未在库");
        }
        if (!saleCase.getWorkerId().equals(currentWorker.getWorkerId()) && currentWorker.getWorkerId().equals(Constants.DEFAULT_WORKER_ID))
            return new ResponseData(ResponseStatus.failed, "放弃失败，没有资源操作权限");
        if(memberVipService.isVip(memberId)){
        	return new ResponseData(ResponseStatus.failed, "放弃失败，客户为VIP");
        }
        SysCirculationConfig quitReason = sysCirculationConfigService.get(circulationId);
        if(quitReason == null){
        	return new ResponseData(ResponseStatus.failed, "放弃失败，选择放弃原因不存在");
        }
        String msg = allotService.quit(saleCase, currentWorker, quitReason);
        if (StringUtils.isNotBlank(msg)) {
            responseData.responseData(ResponseStatus.success, null, null);
        } else {
            responseData.responseData(ResponseStatus.failed, msg, null);
        }
        return responseData;
    }
    
    @ApiOperation("重点客户")
    @RequestMapping(value="intensive", method={RequestMethod.POST})
    public ResponseData intensive(Long memberId) throws Exception{
    	if(memberId == null){
    		return new ResponseData(ResponseStatus.failed, "客户ID为空");
    	}
    	SaleCase saleCase = saleCaseService.getByMemberId(memberId);
    	if(saleCase == null){
    		return new ResponseData(ResponseStatus.failed, "客户不在库");
    	}
    	saleCase.setIntensive(1);
    	saleCaseService.update(saleCase);
    	return new ResponseData(ResponseStatus.success);
    }
    
    @ApiOperation("取消重点客户")
    @RequestMapping(value="intensive/cancel", method={RequestMethod.POST})
    public ResponseData intensiveCancel(Long memberId) throws Exception{
    	if(memberId == null){
    		return new ResponseData(ResponseStatus.failed, "客户ID为空");
    	}
    	SaleCase saleCase = saleCaseService.getByMemberId(memberId);
    	if(saleCase == null){
    		return new ResponseData(ResponseStatus.failed, "客户不在库");
    	}
    	saleCase.setIntensive(0);
    	saleCaseService.update(saleCase);
    	return new ResponseData(ResponseStatus.success);
    }
    
    @ApiOperation("获取客户照片")
    @RequestMapping(value="photos", method={RequestMethod.GET})
    public ResponseData photos(Long memberId) throws Exception{
    	if(memberId == null){
    		return new ResponseData(ResponseStatus.failed, "客户ID为空");
    	}
    	return new ResponseData(ResponseStatus.success, memberAttachmentService.select(memberId));
    }

    @ApiOperation("上传客户照片")
    @RequestMapping(value="photos/upload", method={RequestMethod.POST})
    public ResponseData uploadPhotos(Long memberId, MultipartFile[] files) throws Exception{
    	if(memberId == null){
    		return new ResponseData(ResponseStatus.failed, "客户ID为空");
    	}
    	if(files == null || files.length == 0){
    		return new ResponseData(ResponseStatus.failed, "图片为空");
    	}
    	for (MultipartFile file : files) {
			if(!FileCommonUtils.checkImage(file.getInputStream())){
				return new ResponseData(ResponseStatus.failed, file.getName()+"为非法图片格式");
			}
		}
    	Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
    	for (MultipartFile file : files) {
			memberAttachmentService.insert(memberId, file, currentWorker);
		}
    	return new ResponseData(ResponseStatus.success);
    }

}
