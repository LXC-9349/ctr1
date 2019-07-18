package com.ctr.crm.controlers.systemconfig;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.moduls.approvalgroup.models.ApprovalGroup;
import com.ctr.crm.moduls.approvalgroup.service.ApprovalGroupService;
import com.ctr.crm.interceptors.CurrentWorkerAware;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-04-24
 */
@Api(tags = "审批组设置")
@RestController
@RequestMapping(value = "/api/approval_group")
@Secure(1)
@Menu(menuName = "审批组设置", menuUrl = "approval_group", foundational = false, parent = @Parent(menuName = "审批管理", menuUrl = "approval", foundational = false))
public class ApprovalGroupController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(ApprovalGroupController.class);

    @Autowired
    private ApprovalGroupService approvalGroupService;

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "组名", dataType = "String", paramType = "query")
    })
    @GetMapping("search")
    @Menu(verify = false)
    public ResponseData search(HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            ApprovalGroup approvalGroup = (ApprovalGroup) RequestObjectUtil.mapToBean(request, ApprovalGroup.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (approvalGroup == null) {
                approvalGroup = new ApprovalGroup();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            approvalGroupService.searchPage(responseData, pageMode, approvalGroup);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.success);
        } catch (Exception e) {
            log.error("查询", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation("详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "String", paramType = "query")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段"), @ApiResponse(code = 100008, message = "非法请求")})
    @GetMapping(value = "info")
    @Menu(verify = false)
    public ResponseData info(@RequestParam(required = false) String id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.null_param);
                return responseData;
            }
            ApprovalGroup approvalGroup = approvalGroupService.get(id);
            Map<String, Object> resMap = new HashMap<>(2);
            resMap.put("approvalGroup", approvalGroup);
            if (StringUtils.isNotBlank(approvalGroup.getWorkerIds())) {
                resMap.put("workerList", approvalGroupService.findWorker(approvalGroup.getWorkerIds()));
            }
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation("组员查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "组Id", required = true, dataType = "String", paramType = "query")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段"), @ApiResponse(code = 100008, message = "非法请求")})
    @GetMapping(value = "find_workers")
    @Menu(verify = false)
    public ResponseData find_workers(@RequestParam(required = false) String id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.null_param);
                return responseData;
            }
            ApprovalGroup approvalGroup = approvalGroupService.get(id);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, approvalGroupService.findWorker(approvalGroup.getWorkerIds()));
        } catch (Exception e) {
            log.error("组员查询", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加")
    @PostMapping(value = "insert")
    @Secure(value = 1, actionName = "添加审批组", actionUri = "/api/approval_group/insert", actionNote = "审批组设置", foundational = false)
    public ResponseData insert(@ModelAttribute(value = "ApprovalGroup") @ApiParam(value = "Created ApprovalGroup object", required = true) ApprovalGroup approvalGroup) {
        ResponseData responseData = new ResponseData();
        if (StringUtils.isNotBlank(approvalGroupService.insert(approvalGroup))) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
        } else {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, null, null);
        }
        return responseData;
    }

    @ApiOperation(value = "修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "String", paramType = "query")})
    @PostMapping(value = "update")
    @Secure(value = 1, actionName = "修改审批组", actionUri = "/api/approval_group/update", actionNote = "审批组设置", foundational = false)
    public ResponseData update(@ModelAttribute(value = "ApprovalGroup") @ApiParam(value = "Created ApprovalGroup object", required = true) ApprovalGroup approvalGroup) {
        ResponseData responseData = new ResponseData();
        approvalGroup.setDeleted(null);
        if (StringUtils.isBlank(approvalGroup.getId())) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
        } else if (approvalGroupService.update(approvalGroup) > 0) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
        } else {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, null, null);
        }
        return responseData;
    }

    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "String", paramType = "query")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段"), @ApiResponse(code = 100008, message = "非法请求")})
    @DeleteMapping(value = "delete")
    @Secure(value = 1, actionName = "删除审批组", actionUri = "/api/approval_group/delete", actionNote = "审批组设置", foundational = false)
    public ResponseData delete(@RequestParam(required = false) String id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
            } else {
                boolean flag = approvalGroupService.delete(id);
                if (flag) {
                    responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
                } else {
                    responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, "无法删除，当前组已被流程引用。", null);
                }
            }
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

}
