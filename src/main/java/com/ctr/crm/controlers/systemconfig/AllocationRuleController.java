package com.ctr.crm.controlers.systemconfig;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.moduls.allocation.models.AllocationCondition;
import com.ctr.crm.moduls.allocation.models.AllocationRule;
import com.ctr.crm.moduls.allocation.service.AllocationConditionService;
import com.ctr.crm.moduls.allocation.service.AllocationRuleService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
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

import java.util.*;
import java.util.stream.Collectors;

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
@Api(tags = "自动分配规则配置")
@RestController
@RequestMapping(value = "/api/allocation_rule")
@Secure(1)
@Menu(menuName = "自动分配规则配置", menuUrl = "allocation_rule", foundational = false, parent = @Parent(menuName = "系统设置", menuUrl = "sys_setting", foundational = false))
public class AllocationRuleController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(AllocationRuleController.class);

    @Autowired
    private AllocationRuleService allocationRuleService;
    @Autowired
    private AllocationConditionService allocationConditionService;

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
            AllocationRule allocationRule = (AllocationRule) RequestObjectUtil.mapToBean(request, AllocationRule.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (allocationRule == null) {
                allocationRule = new AllocationRule();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            allocationRuleService.searchPage(responseData, pageMode, allocationRule);
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
            AllocationRule allocationRule = allocationRuleService.get(id);
            Map<String, Object> resMap = new HashMap<>(1);
            resMap.put("allocationRule", allocationRule);
            responseData.responseData(ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加/修改入口数据")
    @GetMapping("modify_main")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "修改必填规则id", dataType = "String")})
    public ResponseData modify_main(@RequestParam(required = false) String id) {
        ResponseData responseData = new ResponseData();
        try {
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("memberColumns", allocationRuleService.findMemberBaseInfoColumns());
            if (StringUtils.isNotBlank(id)) {
                AllocationRule ar = allocationRuleService.get(id);
                //详细列配置
                List<AllocationCondition> alcList = allocationConditionService.findList(new AllocationCondition(ar.getId()));
                //进行分排添加返回
                alcList.stream().sorted((a1, a2) -> a1.getPosition().compareTo(a2.getPosition()));
                Map<Integer, List<AllocationCondition>> alcMap = alcList.stream().collect(Collectors.groupingBy(AllocationCondition::getRow));
                resMap.put("allocationRule", ar);
                resMap.put("allocationConditions", alcMap);
            }
            responseData.responseData(ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("添加/修改入口数据", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加")
    @PostMapping(value = "insert")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jsonStr", value = "选择的条件json串(具体字段意义参照AllocationCondition表type:1区间值用\"-\"隔开；2包含值,隔开):[{'filed':'marriage','connect':'null','type':2,'filedValue':'1,2','row':1,'position':1},{'filed':'salary','connect':'and','type':1,'filedValue':'5000-10000','row':1,'position':2}]", dataType = "json")})
    @Secure(value = 1, actionName = "添加分配规则", actionUri = "/api/allocation_rule/insert", actionNote = "自动分配规则配置", foundational = false)
    public ResponseData insert(@ModelAttribute(value = "AllocationRule") @ApiParam(value = "Created AllocationRule object", required = true) AllocationRule allocationRule, @RequestParam(required = false) String jsonStr) {
        ResponseData responseData = new ResponseData();
        if (StringUtils.isAnyBlank(allocationRule.getName(),allocationRule.getWorkerIds(), jsonStr)) {
            responseData.responseData(ResponseStatus.null_param, null, null);
        }
        String id = allocationRuleService.insert(allocationRule);
        if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(jsonStr)) {
            List<AllocationCondition> list = null;
            try {
                list = JSONArray.parseArray(StringEscapeUtils.unescapeHtml4(jsonStr), AllocationCondition.class);
            } catch (Exception e) {
                responseData.responseData(ResponseStatus.failed, "json格式错误", null);
                return responseData;
            }
            list.forEach(alloca -> {
                alloca.setRuleId(id);
            });
            /** 批量添加条件*/
            allocationConditionService.insertBatch(list);
        }
        responseData.responseData(ResponseStatus.success, null, null);
        return responseData;
    }

    @ApiOperation(value = "修改")
    @PostMapping(value = "update")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "jsonStr", value = "选择的条件json串(具体字段意义参照AllocationCondition表):[{'filed':'marriage','connect':'','type':2,'filedValue':'1,2','row':1,'position':1},{'filed':'salary','connect':'and','type':1,'filedValue':'5000-10000','row':1,'position':2}]", required = true, dataType = "json")
    })
    @Secure(value = 1, actionName = "修改分配规则", actionUri = "/api/allocation_rule/update", actionNote = "自动分配规则配置", foundational = false)
    public ResponseData update(@ModelAttribute(value = "AllocationRule") @ApiParam(value = "Created AllocationRule object", required = true) AllocationRule allocationRule, @RequestParam(required = false) String jsonStr) {
        ResponseData responseData = new ResponseData();
        allocationRule.setDeleted(null);
        if (StringUtils.isAnyBlank(allocationRule.getId(),allocationRule.getName(),allocationRule.getWorkerIds())) {
            responseData.responseData(ResponseStatus.null_param, null, null);
        } else if (allocationRuleService.update(allocationRule) > 0 && StringUtils.isNotBlank(jsonStr)) {
            List<AllocationCondition> list = null;
            try {
                list = JSONArray.parseArray(StringEscapeUtils.unescapeHtml4(jsonStr), AllocationCondition.class);
            } catch (Exception e) {
                responseData.responseData(ResponseStatus.failed, "json格式错误", null);
                return responseData;
            }
            list.forEach(alloca -> {
                alloca.setRuleId(allocationRule.getId());
            });
            /** 批量添加条件*/
            allocationConditionService.deleteByAllotId(allocationRule.getId());
            allocationConditionService.insertBatch(list);
        }
        responseData.responseData(ResponseStatus.success, null, null);
        return responseData;
    }

    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "String")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段"), @ApiResponse(code = 100008, message = "非法请求")})
    @DeleteMapping(value = "delete")
    @Secure(value = 1, actionName = "删除分配规则", actionUri = "/api/allocation_rule/delete", actionNote = "自动分配规则配置", foundational = false)
    public ResponseData delete(@RequestParam(required = false) String id) {
        ResponseData responseData = new ResponseData();
        try {
            if (StringUtils.isBlank(id)) {
                responseData.responseData(ResponseStatus.null_param, null, null);
            } else {
                allocationRuleService.delete(id);
                responseData.responseData(ResponseStatus.success, null, null);
            }
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation("优先级顺序交换")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beforeId", value = "选择要交换的ID", required = true, dataType = "String"),
            @ApiImplicitParam(name = "afterId", value = "被交换的ID", required = true, dataType = "String")})
    @PostMapping(value = "exchange")
    @Secure(value = 1, actionName = "优先级顺序交换", actionUri = "/api/allocation_rule/exchange", actionNote = "自动分配规则配置", foundational = false)
    public ResponseData exchange(@RequestParam(required = false) String beforeId,@RequestParam(required = false) String afterId) {
        ResponseData responseData = new ResponseData();
        try {
            if (StringUtils.isAnyBlank(beforeId,beforeId)) {
                responseData.responseData(ResponseStatus.null_param, null, null);
            } else {
                String msg=allocationRuleService.exchange(beforeId,afterId);
                if (StringUtils.isBlank(msg)) {
                    responseData.responseData(ResponseStatus.success, "交换成功", null);
                } else {
                    responseData.responseData(ResponseStatus.failed, msg, null);
                }
            }
        } catch (Exception e) {
            log.error("优先级顺序交换", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation("启用/停用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "status", value = "1开启0关闭", required = true, dataType = "String")
    })
    @PostMapping(value = "status")
    @Secure(value = 1, actionName = "启用/停用", actionUri = "/api/allocation_rule/status", actionNote = "自动分配规则配置", foundational = false)
    public ResponseData status(@RequestParam(required = false) String id,Integer status) {
        ResponseData responseData = new ResponseData();
        try {
            if (StringUtils.isBlank(id)||status==null) {
                responseData.responseData(ResponseStatus.null_param, null, null);
            } else {
                String msg=allocationRuleService.status(id,status);
                if (StringUtils.isBlank(msg)) {
                    responseData.responseData(ResponseStatus.success, "修改成功", null);
                } else {
                    responseData.responseData(ResponseStatus.failed, msg, null);
                }
            }
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }
}
