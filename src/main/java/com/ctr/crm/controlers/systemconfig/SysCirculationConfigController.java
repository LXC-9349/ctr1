package com.ctr.crm.controlers.systemconfig;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.moduls.syscirculationconfig.models.SysCirculationConfig;
import com.ctr.crm.moduls.syscirculationconfig.service.SysCirculationConfigService;
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
 * @date 2019-04-23
 */
@Api(tags = "客户流转原因设置")
@RestController
@RequestMapping(value = "/api/sys_circulation_config")
@Secure(1)
@Menu(menuName="客户流转原因设置", menuUrl="sys_circulation_config", foundational=false, parent=@Parent(menuName="系统设置", menuUrl="sys_setting", foundational=false))
public class SysCirculationConfigController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(SysCirculationConfigController.class);

    @Autowired
    private SysCirculationConfigService sysCirculationConfigService;

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
            SysCirculationConfig sysCirculationConfig = sysCirculationConfigService.get(id);
            Map<String, Object> resMap = new HashMap<>(2);
            resMap.put("sysCirculationConfig", sysCirculationConfig);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "操作类型1删除2转让3放弃", dataType = "Integer", paramType = "query")
    })
    @GetMapping("search")
    @Menu(verify = false)
    public ResponseData search(HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            SysCirculationConfig sysCirculationConfig = (SysCirculationConfig) RequestObjectUtil.mapToBean(request, SysCirculationConfig.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            String type = request.getParameter("type");
            if (sysCirculationConfig == null) {
                sysCirculationConfig = new SysCirculationConfig();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            sysCirculationConfigService.searchPage(responseData, pageMode, sysCirculationConfig, type);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.success);
        } catch (Exception e) {
            log.error("查询", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加")
    @PostMapping(value = "insert")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "选择操作1删除2转让3放弃", dataType = "Integer", allowMultiple = true)})
    @Secure(value=1, actionName="添加客户流转原因", actionUri="/api/sys_circulation_config/insert", actionNote="客户流转原因设置", foundational=false)
    public ResponseData insert(@ModelAttribute(value = "SysCirculationConfig") @ApiParam(value = "Created SysCirculationConfig object", required = true) SysCirculationConfig sysCirculationConfig) {
        ResponseData responseData = new ResponseData();
        if (StringUtils.isBlank(sysCirculationConfig.getReason())) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
        } else if (sysCirculationConfigService.findList(new SysCirculationConfig(sysCirculationConfig.getReason())).size() > 0) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.duplicate, "原因不能重复", null);
        } else if (StringUtils.isNotBlank(sysCirculationConfigService.insert(sysCirculationConfig))) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
        } else {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, null, null);
        }
        return responseData;
    }

    @ApiOperation(value = "修改")
    @PostMapping(value = "update")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "String", paramType = "query")})
    @Secure(value=1, actionName="修改客户流转原因", actionUri="/api/sys_circulation_config/update", actionNote="客户流转原因设置", foundational=false)
    public ResponseData update(@ModelAttribute(value = "SysCirculationConfig") @ApiParam(value = "Created SysCirculationConfig object", required = true) SysCirculationConfig sysCirculationConfig) {
        ResponseData responseData = new ResponseData();
        if (StringUtils.isBlank(sysCirculationConfig.getId())) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
        } else if (sysCirculationConfigService.update(sysCirculationConfig) > 0) {
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
    @Secure(value=1, actionName="删除客户流转原因", actionUri="/api/sys_circulation_config/delete", actionNote="客户流转原因设置", foundational=false)
    public ResponseData delete(@RequestParam(required = false) String id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
            } else {
                SysCirculationConfig sysCirculationConfig=sysCirculationConfigService.get(id);
                String reason=sysCirculationConfig.getReason();
                if(reason.equals("无法联系")||reason.equals("无意向客户")||reason.equals("其它")){
                    responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.no_access, "系统默认无法删除", null);
                }else{
                    sysCirculationConfigService.delete(id);
                    responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
                }
            }
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }
    
    @ApiOperation("流转原因")
    @ApiImplicitParam(name="circulationType", value="不填取全部 1黑名单 2转让客户 3放弃客户", required=false, dataTypeClass=Integer.class)
    @RequestMapping(value="list", method={RequestMethod.GET})
    @Menu(verify=false)
    public ResponseData list(Integer circulationType) throws Exception{
    	return new ResponseData(ResponseStatus.success, sysCirculationConfigService.getCirculationList(circulationType));
    }

}
