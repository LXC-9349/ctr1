package com.ctr.crm.controlers.systemconfig;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.moduls.intentionality.models.Intentionality;
import com.ctr.crm.moduls.intentionality.service.IntentionalityService;
import com.ctr.crm.moduls.sales.service.SaleCaseService;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-04-19
 */
@Api(tags = "意向度设置")
@RestController
@RequestMapping(value = "/api/intentionality")
@Secure(1)
@Menu(menuName = "意向度设置", menuUrl = "intentionality", foundational = false, parent = @Parent(menuName = "系统设置", menuUrl = "sys_setting", foundational = false))
public class IntentionalityController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(IntentionalityController.class);

    @Autowired
    private IntentionalityService intentionalityService;
    @Resource
    private SaleCaseService saleCaseService;

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "1销售2红娘3才俊佳丽", dataType = "Integer", paramType = "query")
    })
    @GetMapping("search")
    @Menu(verify = false)
    public ResponseData search(HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            Intentionality intentionality = (Intentionality) RequestObjectUtil.mapToBean(request, Intentionality.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (intentionality == null) {
                intentionality = new Intentionality();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            intentionalityService.searchPage(responseData, pageMode, intentionality);
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
            Intentionality intentionality = intentionalityService.get(id);
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("intentionality", intentionality);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加")
    @PostMapping(value = "insert")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sort", value = "意向度", required = true, dataType = "Integer")})
    @Secure(value = 1, actionName = "添加意向度设置", actionUri = "/api/intentionality/insert", actionNote = "意向度设置", foundational = false)
    public ResponseData insert(@ModelAttribute(value = "Intentionality") @ApiParam(value = "Created Intentionality object", required = true) Intentionality intentionality, Integer sort) {
        ResponseData responseData = new ResponseData();
        if (StringUtils.isBlank(intentionality.getName()) || intentionality.getType() == null || sort == null) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
            return responseData;
        }
        if(sort<1){
            return new ResponseData(com.ctr.crm.commons.result.ResponseStatus.failed, "意向度必须大于0");
        }
        intentionality.setCaseClass(sort);
        String msg = intentionalityService.insert(intentionality);
        if (StringUtils.isNotBlank(msg)) {
            if ("dup".equals(msg)) {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, "意向度不能重复", null);
            } else {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
            }
        } else {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, null, null);
        }
        return responseData;
    }

    @ApiOperation(value = "修改")
    @PostMapping(value = "update")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "String")})
    @Secure(value = 1, actionName = "修改意向度设置", actionUri = "/api/intentionality/update", actionNote = "意向度设置", foundational = false)
    public ResponseData update(@ModelAttribute(value = "Intentionality") @ApiParam(value = "Created Intentionality object", required = true) Intentionality intentionality) {
        ResponseData responseData = new ResponseData();
        if (intentionality.getId() == null || StringUtils.isBlank(intentionality.getName())) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
            return responseData;
        }
        Intentionality intentionality1 = new Intentionality();
        intentionality1.setName(intentionality.getName());
        intentionality1.setId(intentionality.getId());
        if (intentionalityService.update(intentionality) > 0) {
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
    @Secure(value = 1, actionName = "删除意向度设置", actionUri = "/api/intentionality/delete", actionNote = "意向度设置", foundational = false)
    public ResponseData delete(@RequestParam(required = false) String id) {
        ResponseData responseData = new ResponseData();
        try {
            Intentionality intentionality = intentionalityService.get(id);
            if (intentionality == null) {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, "意向度不存在", null);
            } else {
                if (saleCaseService.findListBySales(Arrays.asList(intentionality)).size() > 0) {
                    responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, "销售机会数据存在该意向度无法删除", null);
                } else {
                    intentionalityService.delete(id);
                    responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
                }
            }
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

}
