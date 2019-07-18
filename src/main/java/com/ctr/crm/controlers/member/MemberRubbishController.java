package com.ctr.crm.controlers.member;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.member.models.MemberRubbish;
import com.ctr.crm.moduls.member.service.MemberRubbishService;
import com.ctr.crm.moduls.member.service.MemberVipService;
import com.ctr.crm.moduls.syscirculationconfig.models.SysCirculationConfig;
import com.ctr.crm.moduls.syscirculationconfig.service.SysCirculationConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;

/**
 * @author DoubleLi
 * @date 2019-05-16
 */
@Api(tags = "黑名单")
@RestController
@RequestMapping(value = "/api/member_rubbish")
@Secure(1)
@Menu(menuName = "黑名单", menuUrl = "member_rubbish", foundational = false, parent = @Parent(menuName = "资源管理", menuUrl = "member", foundational = false))
public class MemberRubbishController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(MemberRubbishController.class);

    @Autowired
    private MemberRubbishService memberRubbishService;
    @Resource
    private SysCirculationConfigService sysCirculationConfigService;
    @Resource
    private MemberVipService memberVipService;

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "memberId", value = "客户ID", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "reason", value = "黑名单原因", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "workerId", value = "操作人", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "客户名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "createTimeA", value = "添加开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "createTimeB", value = "添加结束时间", dataType = "String", paramType = "query"),
    })
    @GetMapping("search")
    public ResponseData search(HttpServletRequest request, String memberName, String createTimeA, String createTimeB) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            MemberRubbish memberRubbish = (MemberRubbish) RequestObjectUtil.mapToBean(request, MemberRubbish.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (memberRubbish == null) {
                memberRubbish = new MemberRubbish();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            memberRubbishService.searchPage(responseData, pageMode, memberRubbish, memberName, createTimeA, createTimeB);
            responseData.setStatus(ResponseStatus.success);
        } catch (Exception e) {
            log.error("查询", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }


    @ApiOperation(value = "添加黑名单")
	@ApiImplicitParams({ 
		@ApiImplicitParam(name = "memberId", value = "客户ID", required = true, dataType = "Long", paramType = "query"),
		@ApiImplicitParam(name = "reason", value = "黑名单原因。请到【客户流转原因设置-流转原因】中获取", required = true, dataType = "String", paramType = "query")
	})
    @PostMapping(value = "insert")
    @Secure(value = 1, actionName = "添加黑名单", actionUri = "/api/member_rubbish/insert", actionNote = "黑名单")
    public ResponseData insert(Long memberId, String reason) {
        ResponseData responseData = new ResponseData();
        Worker worker = CurrentWorkerLocalCache.getCurrentWorker();
        SysCirculationConfig quitReason = sysCirculationConfigService.get(reason);
        if(quitReason == null){
        	return new ResponseData(ResponseStatus.failed, "添加黑名单失败，选择原因不存在");
        }
        if(memberVipService.isVip(memberId)){
        	return new ResponseData(ResponseStatus.failed, "添加黑名单失败，客户为VIP");
        }
        String msg = memberRubbishService.insert(memberId, quitReason, worker);
        if (StringUtils.isBlank(msg)) {
            responseData.responseData(ResponseStatus.success, null, null);
        } else {
            responseData.responseData(ResponseStatus.failed, msg, null);
        }
        return responseData;
    }

    @ApiOperation("解除黑名单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberId", value = "memberId", required = true, dataType = "Long", paramType = "query")})
    @DeleteMapping(value = "relieve")
    @Secure(value = 1, actionName = "解除黑名单", actionUri = "/api/member_rubbish/relieve", actionNote = "黑名单")
    public ResponseData relieve(Long memberId) {
        ResponseData responseData = new ResponseData();
        String msg = memberRubbishService.relieve(memberId);
        if (StringUtils.isBlank(msg)) {
            responseData.responseData(ResponseStatus.success, null, null);
        } else {
            responseData.responseData(ResponseStatus.failed, msg, null);
        }
        return responseData;
    }

}
