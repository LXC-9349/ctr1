package com.ctr.crm.controlers.allot;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.moduls.allot.models.AdjustHistory;
import com.ctr.crm.moduls.allot.service.AdjustHistoryService;
import com.ctr.crm.interceptors.CurrentWorkerAware;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-05-08
 */
@Api(tags = "调配历史")
@RestController
@RequestMapping(value = "/api/adjust_history")
@Menu(menuName = "调配历史", menuUrl = "adjust_history", foundational = false, parent = @Parent(menuName = "资源分配", menuUrl = "allot_resource", foundational = false))
public class AdjustHistoryController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(AdjustHistoryController.class);

    @Autowired
    private AdjustHistoryService adjustHistoryService;

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "memberId", value = "客户ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "adjustWorker", value = "调配人名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "fromWorkerId", value = "原员工ID", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "toWorkerId", value = "现员工ID", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "caseClass", value = "成熟度", dataType = "Integer", paramType = "query"),
    })
    @GetMapping("search")
    public ResponseData search(HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            AdjustHistory adjustHistory = (AdjustHistory) RequestObjectUtil.mapToBean(request, AdjustHistory.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (adjustHistory == null) {
                adjustHistory = new AdjustHistory();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            adjustHistoryService.searchPage(responseData, pageMode, adjustHistory);
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
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段"), @ApiResponse(code = 100008, message = "非法请求")})
    @GetMapping(value = "info")
    public ResponseData info(@RequestParam(required = false) Integer id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.null_param);
                return responseData;
            }
            AdjustHistory adjustHistory = adjustHistoryService.get(id);
            Map<String, Object> resMap = new HashMap<>(1);
            resMap.put("adjustHistory", adjustHistory);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加")
    @PostMapping(value = "insert")
    public ResponseData insert(@ModelAttribute(value = "AdjustHistory") @ApiParam(value = "Created AdjustHistory object", required = true) AdjustHistory adjustHistory) {
        ResponseData responseData = new ResponseData();
        if (adjustHistoryService.insert(adjustHistory)) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
        } else {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, null, null);
        }
        return responseData;
    }

    @ApiOperation(value = "修改")
    @PostMapping(value = "update")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "Integer", paramType = "query")})
    public ResponseData update(@ModelAttribute(value = "AdjustHistory") @ApiParam(value = "Created AdjustHistory object", required = true) AdjustHistory adjustHistory) {
        ResponseData responseData = new ResponseData();
        if (adjustHistory.getId() == null) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
        } else if (adjustHistoryService.update(adjustHistory) > 0) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
        } else {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, null, null);
        }
        return responseData;
    }

    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "Integer", paramType = "query")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段"), @ApiResponse(code = 100008, message = "非法请求")})
    @DeleteMapping(value = "delete")
    public ResponseData delete(@RequestParam(required = false) Integer id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
            } else {
                adjustHistoryService.delete(id);
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
            }
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

}
