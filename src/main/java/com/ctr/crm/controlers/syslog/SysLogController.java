package com.ctr.crm.controlers.syslog;

import javax.servlet.http.HttpServletRequest;

import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.moduls.syslog.models.SysLog;
import com.ctr.crm.moduls.syslog.service.SysLogService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import com.ctr.crm.interceptors.CurrentWorkerAware;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author DoubleLi
 * @date 2019-04-29
 */
@Api(tags = "系统日志")
@RestController
@RequestMapping(value = "/api/syslog")
@Secure(1)
public class SysLogController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(SysLogController.class);

    @Autowired
    private SysLogService sysLogService;

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "logType", value = "日志类型 字典SysDict log_type", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "logAction", value = "操作动作 字典SysDict log_action", dataType = "String", paramType = "query")
    })
    @GetMapping("search")
    public ResponseData search(HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            SysLog sysLog = (SysLog) RequestObjectUtil.mapToBean(request, SysLog.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (sysLog == null) {
                sysLog = new SysLog();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            sysLogService.searchPage(responseData, pageMode, sysLog);
            responseData.setStatus(ResponseStatus.success);
        } catch (Exception e) {
            log.error("查询", e);
            responseData.setStatus(ResponseStatus.error);
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
    public ResponseData info(@RequestParam(required = false) String id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.setStatus(ResponseStatus.null_param);
                return responseData;
            }
            SysLog sysLog = sysLogService.get(id);
            Map<String, Object> resMap = new HashMap<>(1);
            resMap.put("sysLog", sysLog);
            responseData.responseData(ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加")
    @PostMapping(value = "insert")
    public ResponseData insert(@ModelAttribute(value = "SysLog") @ApiParam(value = "Created SysLog object", required = true) SysLog sysLog) {
        ResponseData responseData = new ResponseData();
        if (StringUtils.isBlank(sysLogService.insert(sysLog))) {
            responseData.responseData(ResponseStatus.success, null, null);
        } else {
            responseData.responseData(ResponseStatus.failed, null, null);
        }
        return responseData;
    }

    @ApiOperation(value = "修改")
    @PostMapping(value = "update")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "String", paramType = "query")})
    public ResponseData update(@ModelAttribute(value = "SysLog") @ApiParam(value = "Created SysLog object", required = true) SysLog sysLog) {
        ResponseData responseData = new ResponseData();
        sysLog.setDeleted(null);
        if (StringUtils.isBlank(sysLog.getId())) {
            responseData.responseData(ResponseStatus.null_param, null, null);
        } else if (sysLogService.update(sysLog) > 0) {
            responseData.responseData(ResponseStatus.success, null, null);
        } else {
            responseData.responseData(ResponseStatus.failed, null, null);
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
    public ResponseData delete(@RequestParam(required = false) String id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.responseData(ResponseStatus.null_param, null, null);
            } else {
                sysLogService.delete(id);
                responseData.responseData(ResponseStatus.success, null, null);
            }
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

}
