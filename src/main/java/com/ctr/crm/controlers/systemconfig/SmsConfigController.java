package com.ctr.crm.controlers.systemconfig;

import javax.servlet.http.HttpServletRequest;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.moduls.smsconfig.models.SmsConfig;
import com.ctr.crm.moduls.smsconfig.service.SmsConfigService;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author DoubleLi
 * @date 2019-04-28
 */
@Api(tags = "短信用户配置")
@RestController
@RequestMapping(value = "/api/sms_config")
@Menu(menuName = "短信用户配置", menuUrl = "sms_config", foundational = false, parent = @Parent(menuName = "系统设置", menuUrl = "sys_setting", foundational = false))
public class SmsConfigController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(SmsConfigController.class);

    @Autowired
    private SmsConfigService smsConfigService;

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query")
    })
    @GetMapping("search")
    @Menu(verify = false)
    public ResponseData search(HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            SmsConfig smsConfig = (SmsConfig) RequestObjectUtil.mapToBean(request, SmsConfig.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (smsConfig == null) {
                smsConfig = new SmsConfig();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            smsConfigService.searchPage(responseData, pageMode, smsConfig);
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
    @Menu(verify = false)
    public ResponseData info(@RequestParam(required = false) String id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.setStatus(ResponseStatus.null_param);
                return responseData;
            }
            SmsConfig smsConfig = smsConfigService.get(id);
            Map<String, Object> resMap = new HashMap<>(1);
            resMap.put("smsConfig", smsConfig);
            responseData.responseData(ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加")
    @PostMapping(value = "insert")
    @Secure(value = 1, actionName = "添加短信用户配置", actionUri = "/api/sms_config/insert", actionNote = "短信用户配置", foundational = false)
    public ResponseData insert(@ModelAttribute(value = "SmsConfig") @ApiParam(value = "Created SmsConfig object", required = true) SmsConfig smsConfig) {
        ResponseData responseData = new ResponseData();
        if (StringUtils.isNotBlank(smsConfigService.insert(smsConfig))) {
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
    @Secure(value = 1, actionName = "修改短信用户配置", actionUri = "/api/sms_config/update", actionNote = "短信用户配置", foundational = false)
    public ResponseData update(@ModelAttribute(value = "SmsConfig") @ApiParam(value = "Created SmsConfig object", required = true) SmsConfig smsConfig) {
        ResponseData responseData = new ResponseData();
        smsConfig.setDeleted(null);
        if (StringUtils.isBlank(smsConfig.getId())) {
            responseData.responseData(ResponseStatus.null_param, null, null);
        } else if (smsConfigService.update(smsConfig) > 0) {
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
    @Secure(value = 1, actionName = "删除短信用户配置", actionUri = "/api/sms_config/delete", actionNote = "短信用户配置", foundational = false)
    public ResponseData delete(@RequestParam(required = false) String id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.responseData(ResponseStatus.null_param, null, null);
            } else {
                smsConfigService.delete(id);
                responseData.responseData(ResponseStatus.success, null, null);
            }
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

}
