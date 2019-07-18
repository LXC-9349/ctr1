package com.ctr.crm.controlers.systemconfig;

import javax.servlet.http.HttpServletRequest;

import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.moduls.smschannel.models.SmsChannel;
import com.ctr.crm.moduls.smschannel.service.SmsChannelService;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
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
 * @date 2019-04-28
 */
@Api(tags = "短信渠道配置")
@RestController
@RequestMapping(value = "/api/sms_channel")
@Secure(1)
public class SmsChannelController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(SmsChannelController.class);

    @Autowired
    private SmsChannelService smsChannelService;

    @ApiOperation(value = "查询所有渠道")
    @GetMapping("search_all")
    @Menu(verify = false)
    public ResponseData search_all() {
        ResponseData responseData = new ResponseData();
        try {
            responseData.setStatus(ResponseStatus.success);
            responseData.setData(smsChannelService.findAllList(new SmsChannel()));
        } catch (Exception e) {
            log.error("查询", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

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
            SmsChannel smsChannel = (SmsChannel) RequestObjectUtil.mapToBean(request, SmsChannel.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (smsChannel == null) {
                smsChannel = new SmsChannel();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            smsChannelService.searchPage(responseData, pageMode, smsChannel);
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
            SmsChannel smsChannel = smsChannelService.get(id);
            Map<String, Object> resMap = new HashMap<>(1);
            resMap.put("smsChannel", smsChannel);
            responseData.responseData(ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加")
    @PostMapping(value = "insert")
    public ResponseData insert(@ModelAttribute(value = "SmsChannel") @ApiParam(value = "Created SmsChannel object", required = true) SmsChannel smsChannel) {
        ResponseData responseData = new ResponseData();
        if (!Constants.DEFAULT_WORKER_ID.equals(CurrentWorkerLocalCache.getCurrentWorker().getWorkerId())) {
            responseData.responseData(ResponseStatus.no_access, "非超级管理员无法修改", null);
        } else if (StringUtils.isNotBlank(smsChannelService.insert(smsChannel))) {
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
    public ResponseData update(@ModelAttribute(value = "SmsChannel") @ApiParam(value = "Created SmsChannel object", required = true) SmsChannel smsChannel) {
        ResponseData responseData = new ResponseData();
        smsChannel.setDeleted(null);
        if (!Constants.DEFAULT_WORKER_ID.equals(CurrentWorkerLocalCache.getCurrentWorker().getWorkerId())) {
            responseData.responseData(ResponseStatus.no_access, "非超级管理员无法修改", null);
        } else if (StringUtils.isBlank(smsChannel.getId())) {
            responseData.responseData(ResponseStatus.null_param, null, null);
        } else if (smsChannelService.update(smsChannel) > 0) {
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
            if (!Constants.DEFAULT_WORKER_ID.equals(CurrentWorkerLocalCache.getCurrentWorker().getWorkerId())) {
                responseData.responseData(ResponseStatus.no_access, "非超级管理员无法修改", null);
            } else if (id == null) {
                responseData.responseData(ResponseStatus.null_param, null, null);
            } else {
                smsChannelService.delete(id);
                responseData.responseData(ResponseStatus.success, null, null);
            }
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

}
