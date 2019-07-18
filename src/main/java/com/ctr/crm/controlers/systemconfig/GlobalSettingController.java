package com.ctr.crm.controlers.systemconfig;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.moduls.globalsetting.models.GlobalSetting;
import com.ctr.crm.moduls.globalsetting.service.GlobalSettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

/**
 * 系统全局配置
 *
 * @author DoubleLi
 * @date 2019-04-19
 */
@Api(tags = "系统全局配置")
@RestController
@RequestMapping(value = "/api/global_setting")
@Secure(1)
@Menu(menuName="系统全局配置", menuUrl="global_setting", foundational=false, parent=@Parent(menuName="系统设置", menuUrl="sys_setting", foundational=false))
public class GlobalSettingController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(GlobalSettingController.class);

    @Autowired
    private GlobalSettingService globalSettingService;

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "英文名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "名称", dataType = "String", paramType = "query")
    })
    @GetMapping("search")
    @Menu(verify = false)
    public ResponseData search(HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            GlobalSetting globalSetting = (GlobalSetting) RequestObjectUtil.mapToBean(request, GlobalSetting.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (globalSetting == null) {
                globalSetting = new GlobalSetting();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            globalSettingService.searchPage(responseData, pageMode, globalSetting);
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
            GlobalSetting globalSetting = globalSettingService.get(id);
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("globalSetting", globalSetting);
            responseData.responseData(ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加")
    @PostMapping(value = "insert")
    /*@Secure(value=1, actionName="添加全局配置", actionUri="/api/global_setting/insert", actionNote="系统全局配置", foundational=false)*/
    public ResponseData insert(@ModelAttribute(value = "GlobalSetting") @ApiParam(value = "Created GlobalSetting object", required = true) GlobalSetting globalSetting) {
        ResponseData responseData = new ResponseData();
        if (StringUtils.isNotBlank(globalSettingService.insert(globalSetting))) {
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
    @Secure(value=1, actionName="修改全局配置", actionUri="/api/global_setting/update", actionNote="系统全局配置", foundational=false)
    public ResponseData update(@ModelAttribute(value = "GlobalSetting") @ApiParam(value = "Created GlobalSetting object", required = true) GlobalSetting globalSetting) {
        globalSetting.setDeleted(null);
        ResponseData responseData = new ResponseData();
        ResponseStatus rs = null;
        if (StringUtils.isBlank(globalSetting.getId())) {
            rs = ResponseStatus.null_param;
        } else if (globalSettingService.update(globalSetting) > 0) {
            rs = ResponseStatus.success;
        } else {
            rs = ResponseStatus.failed;
        }
        responseData.responseData(rs, null, null);
        return responseData;
    }

    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "String", paramType = "query")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段"), @ApiResponse(code = 100008, message = "非法请求")})
    @DeleteMapping(value = "delete")
    /*@Secure(value=1, actionName="删除全局配置", actionUri="/api/global_setting/delete", actionNote="系统全局配置", foundational=false)*/
    public ResponseData delete(@RequestParam(required = false) String id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.responseData(ResponseStatus.null_param, null, null);
            } else {
                globalSettingService.delete(id);
                responseData.responseData(ResponseStatus.success, null, null);
            }
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

}
