package com.ctr.crm.controlers.invitetoshop;

import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import com.ctr.crm.moduls.invitetoshop.models.InviteToShop;
import com.ctr.crm.moduls.invitetoshop.service.InviteToShopService;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;
import com.ctr.crm.moduls.member.service.MemberService;
import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author DoubleLi
 * @date 2019-06-27
 */
@Api(tags = "到店管理")
@RestController
@RequestMapping(value = "/api/invite_to_shop")
@Secure(1)
@Menu(menuName = "到店管理", menuUrl = "invite_to_shop")
public class InviteToShopController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(InviteToShopController.class);

    @Autowired
    private InviteToShopService inviteToShopService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private WorkerService workerService;

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "memberId", value = "客户ID", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "workerId", value = "预约人", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "deptId", value = "部门Id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "shopStatus", value = "到店情况 0未到店 1已到店", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "inviteType", value = "到店类型 1预约到店 2主动到店", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "inviteDate", value = "预约时间 yyyy-MM-dd~yyyy-MM-dd", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "mobile", value = "手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "sex", value = "性别", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "shopTime", value = "到店时间 yyyy-MM-dd~yyyy-MM-dd", dataType = "String", paramType = "query"),
    })
    @GetMapping("search")
    @Menu(verify = false)
    public ResponseData search(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "100") Integer pageSize, @RequestParam(required = false) Map<String, String> params) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取分页参数 */
            PageMode pageMode = new PageMode(page, pageSize);
            Worker worker = CurrentWorkerLocalCache.getCurrentWorker();
            if (!worker.getWorkerId().equals(Constants.DEFAULT_WORKER_ID) && StringUtils.isNotBlank(params.get("deptId"))) {
                List<Integer> workerIds = workerService.workerRange(Integer.valueOf(params.get("deptId")), worker).stream()
                        .map(Worker::getWorkerId).distinct()
                        .collect(Collectors.toList());
                String allWorkerIds = String.join(",", workerIds.toString());
                params.put("workerIds", allWorkerIds.substring(1, allWorkerIds.length() - 1));
            }
            if (StringUtils.isNotBlank(params.get("inviteDate"))) {
                String t[] = params.get("inviteDate").split("~");
                params.put("inviteDateA", t[0]);
                params.put("inviteDateB", t[1]);
            }
            if (StringUtils.isNotBlank(params.get("shopTime"))) {
                String t[] = params.get("shopTime").split("~");
                params.put("shopTimeA", t[0]);
                params.put("shopTimeB", t[1]);
            }
            inviteToShopService.searchPage(responseData, pageMode, params);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.success);
        } catch (Exception e) {
            log.error("查询", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation("详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "Integer", paramType = "query")})
    @GetMapping(value = "info")
    @Menu(verify = false)
    public ResponseData info(@RequestParam(required = false) Integer id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.null_param);
                return responseData;
            }
            InviteToShop inviteToShop = inviteToShopService.get(id);
            Map<String, Object> resMap = new HashMap<>(1);
            resMap.put("inviteToShop", inviteToShop);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation("客户到店记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberId", value = "memberId", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "shopStatus", value = "到店情况 0未到店 1已到店", dataType = "Integer", paramType = "query")
    })
    @GetMapping(value = "record")
    @Menu(verify = false)
    public ResponseData record(@RequestParam(required = false) Long memberId, Integer shopStatus) {
        ResponseData responseData = new ResponseData();
        try {
            if (memberId == null) {
                responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.null_param);
                return responseData;
            }
            List<InviteToShop> inviteToShopList = inviteToShopService.memberIdByRecord(memberId, shopStatus);
            Map<String, Object> resMap = new HashMap<>(1);
            resMap.put("list", inviteToShopList);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("客户到店记录", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加")
    @PostMapping(value = "insert")
    @Secure(value = 1, actionName = "添加", actionUri = "/api/invite_to_shop/insert", actionNote = "到店管理")
    public ResponseData insert(@ModelAttribute(value = "InviteToShop") @ApiParam(value = "Created InviteToShop object", required = true) InviteToShop inviteToShop) {
        ResponseData responseData = new ResponseData();
        if (inviteToShop.getMemberId() == null || inviteToShop.getInviteDate() == null || inviteToShop.getInviteTime() == null || inviteToShop.getInviteType() == null) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
        } else {
            if (memberService.get(MemberBaseInfo.class, inviteToShop.getMemberId()) == null) {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, "客户不存在", null);
                return responseData;
            }
            Worker worker = CurrentWorkerLocalCache.getCurrentWorker();
            inviteToShop.setInviteWorkerId(worker.getWorkerId());
            if (inviteToShopService.insert(inviteToShop) > 0) {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
            } else {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, null, null);
            }
        }
        return responseData;
    }

    @ApiOperation(value = "修改")
    @PostMapping(value = "update")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", dataType = "Integer")})
    @Secure(value = 1, actionName = "修改", actionUri = "/api/invite_to_shop/update", actionNote = "到店管理")
    public ResponseData update(@ModelAttribute(value = "InviteToShop") @ApiParam(value = "Created InviteToShop object", required = true) InviteToShop inviteToShop) {
        ResponseData responseData = new ResponseData();
        if (inviteToShop.getId() == null || inviteToShop.getMemberId() == null || inviteToShop.getInviteDate() == null || inviteToShop.getInviteTime() == null || inviteToShop.getInviteType() == null) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
        } else if (inviteToShopService.update(inviteToShop) > 0) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
        } else {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, null, null);
        }
        return responseData;
    }

    @ApiOperation(value = "确认到店")
    @PostMapping(value = "confirm")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", dataType = "Integer")})
    @Secure(value = 1, actionName = "确认到店", actionUri = "/api/invite_to_shop/confirm", actionNote = "到店管理")
    public ResponseData confirm(Integer id) {
        ResponseData responseData = new ResponseData();
        if (id == null) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
        } else {
            InviteToShop inviteToShop = inviteToShopService.get(id);
            inviteToShop.setShopTime(new Date());
            inviteToShop.setShopStatus(1);
            if (inviteToShop != null && inviteToShopService.update(inviteToShop) > 0) {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
            } else {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, null, null);
            }
        }
        return responseData;
    }

    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "Integer")})
    @DeleteMapping(value = "delete")
    @Secure(value = 1, actionName = "删除", actionUri = "/api/invite_to_shop/delete", actionNote = "到店管理")
    public ResponseData delete(@RequestParam(required = false) Integer id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
            } else {
                inviteToShopService.delete(id);
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
            }
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

}
