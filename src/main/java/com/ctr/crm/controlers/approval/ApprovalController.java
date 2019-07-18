package com.ctr.crm.controlers.approval;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.listeners.ApplicationInitialEvent;
import com.ctr.crm.moduls.approval.modules.ApprovalData;
import com.ctr.crm.moduls.approval.modules.ApprovalFlow;
import com.ctr.crm.moduls.approval.modules.ApprovalFlowSetting;
import com.ctr.crm.moduls.approval.pojo.ApprovalSource;
import com.ctr.crm.moduls.approval.service.ApprovalService;
import com.ctr.crm.moduls.approvalgroup.models.ApprovalGroup;
import com.ctr.crm.moduls.approvalgroup.service.ApprovalGroupService;
import com.ctr.crm.moduls.hrm.models.Dept;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.service.DeptService;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
import io.swagger.annotations.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author: DoubleLi
 * @date: 2019/4/24 15:03
 */
@Api(tags = "审批管理")
@RequestMapping("/api/approval")
@RestController
@Secure(1)
@Menu(menuName = "审批管理", menuUrl = "approval")
public class ApprovalController implements CurrentWorkerAware {

    private static final Log log = LogFactory.getLog("approval");
    @Autowired
    private ApprovalService approvalService;
    @Autowired
    private ApprovalGroupService approvalGroupService;
    @Autowired
    private WorkerService workerService;
    @Autowired
    private DeptService deptService;

    @ApiOperation(value = "审批设置入口")
    @RequestMapping(value = "/setting_main", method = RequestMethod.GET)
    @Menu(verify = false, menuUrl = "approval_setting", foundational = false, menuName = "审批设置", parent = @Parent(menuName = "审批管理", menuUrl = "approval", foundational = false))
    @Secure(value = 1, actionName = "审批设置", actionUri = "/api/contract/setting_main", actionNote = "审批设置")
    public ResponseData setting_main() {
        ResponseData responseData = new ResponseData();
        try {
            Map<String, Object> model = new HashMap<>(3);
            Set<Map<String, Object>> approvalType = ApplicationInitialEvent.approval_list;
            if (approvalType.size() > 0) {
                model.put("approvalType", JSONArray.toJSON(approvalType));
                List<ApprovalGroup> ag_list = approvalGroupService.findAllList(new ApprovalGroup());
                model.put("ag_list", ag_list);
            } else {
                model.put("approvalType", null);
            }
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, model);
        } catch (Exception e) {
            log.error("审批设置入口", e);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.error, null, null);
        }
        return responseData;
    }

    @ApiOperation(value = "获得审批设置类型的角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "approvalType", value = "考核标准的类型", dataType = "String", paramType = "query")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段")})
    @RequestMapping(value = "/search_approvalrole", method = {RequestMethod.GET})
    @Menu(verify = false, menuName = "审批设置")
    @Secure(value = 1, actionName = "审批设置", actionUri = "/api/contract/setting_main", actionNote = "审批设置")
    public ResponseData search_approvalrole(@RequestParam(required = false) String approvalType) {
        ResponseData responseData = new ResponseData();
        if (StringUtils.isBlank(approvalType)) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
            return responseData;
        }
        try {
            List<ApprovalFlowSetting> afsList = approvalService.searchApprovalFlowSettingByType(approvalType);
            List<Map<String, Object>> returnList = new ArrayList<>();
            for (ApprovalFlowSetting approvalFlowSetting : afsList) {
                Map<String, Object> map = new HashMap<>();
                map.put("worker", workerService.select(approvalFlowSetting.getWorkerId()));
                map.put("group", approvalGroupService.get(approvalFlowSetting.getGroupId()));
                map.put("approvalFlowSetting", approvalFlowSetting);
                returnList.add(map);
            }
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, returnList);
        } catch (Exception e) {
            log.error("获得审批设置类型的角色", e);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.error, null, null);
        }
        return responseData;
    }

    @ApiOperation(value = "新增/修改审批设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "approvalType", value = "考核标准的类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "approvalNumber", value = "需要审批数量", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "approvalRole", value = "json串type1指定员工id2审批组id:[{'type':2,'id':'sddasd54564'},{'type':1,'id':'8016'}]", dataType = "json", paramType = "query")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段")})
    @RequestMapping(value = "/modify_approval_setting", method = {RequestMethod.POST})
    @Menu(verify = false, menuName = "审批设置")
    @Secure(value = 1, actionName = "审批设置", actionUri = "/api/contract/setting_main", actionNote = "审批设置")
    public ResponseData modify_approval_setting(@RequestParam(required = false) String approvalType,
                                                @RequestParam(required = false) Integer approvalNumber,
                                                @RequestParam(required = false) String approvalRole) {
        Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
        ResponseData responseData = new ResponseData();
        if (approvalType == null) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
            return responseData;
        }
        try {
            Set<Map<String, Object>> alist = ApplicationInitialEvent.approval_list;
            if (alist.size() <= 0) {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, "当前系统没有审批类型设置", null);
                return responseData;
            }
            Map<String, Object> typeMap = new HashMap<>();
            //找到配置信息
            for (Map<String, Object> map : alist) {
                String value = (String) map.get("value");
                if (approvalType.equals(value)) {
                    typeMap = map;
                    break;
                }
            }
            if (typeMap.isEmpty()) {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, "当前系统没有选择的审批类型设置", null);
                return responseData;
            }
            String name = (String) typeMap.get("name");// 类型名称
            List<Map> roleMap = null;
            if (StringUtils.isNotBlank(approvalRole)) {
                try {
                    roleMap = JSONObject.parseArray(StringEscapeUtils.unescapeHtml4(approvalRole), Map.class);
                    for (Map rmap : roleMap) {
                        Integer type = (Integer) rmap.get("type");
                        String id = (String) rmap.get("id");
                        if (type == null && StringUtils.isBlank(id)) {
                            throw new Exception("流程审批组json错误");
                        }
                    }
                } catch (Exception e) {
                    log.error("新增/修改审批设置", e);
                    responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, "流程审批组json错误", null);
                    return responseData;
                }

                if (roleMap.size() > 10) {
                    responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, "审批级别默认为最多10级", null);
                    return responseData;
                }
            }
            approvalService.insertOrUpdateTypeSetting(typeMap, approvalNumber, roleMap, currentWorker);
        } catch (Exception e) {
            log.error("新增/修改审批设置", e);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.error, null, null);
            return responseData;
        }
        responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
        return responseData;
    }

    @ApiOperation(value = "待审批入口")
    @RequestMapping(value = "/pending_main", method = RequestMethod.GET)
    public ResponseData pending_main() {
        ResponseData responseData = new ResponseData();
        // 查询考核标准List
        try {
            Map<String, Object> map = new HashMap<>();
            Set<Map<String, Object>> alist = ApplicationInitialEvent.approval_list;// approvalType
            if (alist.size() > 0) {
                map.put("approvalType", JSONArray.toJSON(alist));
            } else {
                map.put("approvalType", null);
            }
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, map);
        } catch (Exception e) {
            log.error("待审批入口", e);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.error, null, null);
        }
        return responseData;
    }

    @ApiOperation(value = "待审批搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "approvalType", value = "考核标准的类型（多个用','隔开）", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "applicationTimeA", value = "申请开始时间 yyyy-MM-dd", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "applicationTimeB", value = "申请结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "approvalDataId", value = "审批编号", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "workerId", value = "申请人", dataType = "String", paramType = "query")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段")})
    @RequestMapping(value = "/pending_search", method = {RequestMethod.GET})
    public ResponseData pending_search(@RequestParam(required = false) Map<String, String> params) {
        ResponseData responseData = new ResponseData();
        Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
        try {
            List<Map<String, Object>> pendingList = approvalService.searchPending(params, currentWorker);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, pendingList);
        } catch (Exception e) {
            log.error("待审批搜索", e);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.error, null, null);
        }
        return responseData;
    }

    @ApiOperation(value = "审批详情（内容视图需要传输审批编号）")
    @RequestMapping(value = "/approval_main", method = RequestMethod.GET)
    @Menu(verify = false, menuName = "审批管理")
    public ResponseData approval_main(@RequestParam(required = false) Integer approvalDataId) {
        ResponseData responseData = new ResponseData();
        try {
            Map<String, Object> map = new HashMap<>();
            if (approvalDataId != null) {
                Map<String, Object> approval = approvalService.searchApprovalDetail(approvalDataId);
                map.put("approval", approval);// 基本信息
                ApprovalData approvalData = approvalService.searchApprovalData(approvalDataId);
                // 解析source
                ApprovalSource as = new ApprovalSource().analyze(approvalData.getSource());// 解析参数
                if (approvalData.getApprovalType().equals(Constants.APPROVAL_MEET)) {
                    map.put("viewUrl", as.getViewUrl() + "?id=" + approvalData.getBuisId());// 审批内容视图
                } else {
                    map.put("viewUrl", as.getViewUrl() + "?approvalDataId=" + approvalDataId);// 审批内容视图
                }

                // 审批历史查询
                List<Map<String, Object>> afHistory = approvalService.searchApprovalHistory(approvalData);
                map.put("afHistory", afHistory);// 处理历史记录
                Integer schedule = 0;
                switch (approvalData.getStatus()) {
                    case 0:
                    case 1:
                        schedule = approvalService.schedule(approvalDataId);
                        break;
                    case 2:
                        schedule = 100;
                        break;
                    case 3:
                    case 4:
                        schedule = 0;
                        break;

                    default:
                        schedule = 0;
                }
                map.put("schedule", schedule);// 进度
                List<Map<String, Object>> scheduleDetail = approvalService.scheduleDetail(approvalData);
                map.put("scheduleDetail", scheduleDetail);
            }
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, map);
        } catch (Exception e) {
            log.error("审批详情入口", e);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.error, null, null);
        }
        return responseData;
    }

    @ApiOperation(value = "提交审批")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "approvalDataId", value = "基本信息里面的approvalDataId", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "version", value = "基本信息里面的version", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "id", value = "基本信息里面的Id", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "审批结果（1通过0驳回）", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "驳回原因", dataType = "String", paramType = "query")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段")})
    @RequestMapping(value = "/process", method = {RequestMethod.POST})
    @Menu(verify = false, menuName = "审批管理")
    public ResponseData process(@RequestParam(required = false) Integer approvalDataId,
                                @RequestParam(required = false) Integer id, @RequestParam(required = false) Integer status,
                                @RequestParam(required = false) String remark, @RequestParam(required = false) Integer version) {
        ResponseData responseData = new ResponseData();
        Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
        if (approvalDataId == null || id == null || status == null || version == null) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
            return responseData;
        }
        try {
            ApprovalData approvalData = approvalService.searchApprovalData(approvalDataId);
            approvalData.setVersion(version);
            ApprovalFlow approvalFlow = approvalService.searchApprovalFlow(id);
            approvalFlow.setStatus(status);
            approvalFlow.setRemake(remark);
            approvalFlow.setInspector(currentWorker.getWorkerId());
            approvalFlow.setExamineTime(new Date());
            String msg = approvalService.processApproval(approvalData, approvalFlow, currentWorker);// 流程处理
            if (StringUtils.isNotBlank(msg)) {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, msg, null);
            } else {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
            }
        } catch (Exception e) {
            log.error("提交审批", e);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.error, null, null);
        }
        return responseData;
    }

    @ApiOperation(value = "审批结果入口")
    @RequestMapping(value = "/approval_result_main", method = RequestMethod.GET)
    public ResponseData approval_result_main() {
        ResponseData responseData = new ResponseData();
        // 查询考核标准List
        try {
            Map<String, Object> map = new HashMap<>();
            Set<Map<String, Object>> alist = ApplicationInitialEvent.approval_list;// approvalType
            if (alist.size() > 0) {
                map.put("approvalType", JSONArray.toJSON(alist));
            } else {
                map.put("approvalType", null);
            }
            List<Map<String, Object>> status = new ArrayList<>();
            Map<String, Object> map0 = new HashMap<>(2);
            map0.put("type", 0);
            map0.put("name", "待审批");
            Map<String, Object> map1 = new HashMap<>(2);
            map1.put("type", 1);
            map1.put("name", "审批中");
            Map<String, Object> map2 = new HashMap<>(2);
            map2.put("type", 2);
            map2.put("name", "已通过");
            Map<String, Object> map3 = new HashMap<>(2);
            map3.put("type", 3);
            map3.put("name", "已驳回");
            Map<String, Object> map4 = new HashMap<>(2);
            map4.put("type", 4);
            map4.put("name", "已撤销");
            status.add(map0);
            status.add(map1);
            status.add(map2);
            status.add(map3);
            status.add(map4);
            map.put("status", JSONArray.toJSON(status));
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
        } catch (Exception e) {
            log.error("审批结果入口", e);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.error, null, null);
        }
        return responseData;
    }


    @ApiOperation(value = "审批结果搜索（返回参数function为cancel拥有撤销权限）")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "deptId", value = "部门", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "workerId", value = "员工ID", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "approvalType", value = "审批类型（'aaa','bbb'隔开）", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "action", value = "审批动作", dataType = "String（'aa','bb'隔开）", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "审批状态1,2", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "approvalDataId", value = "审批编号", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "applicant", value = "申请人", dataType = "String", paramType = "query")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段"), @ApiResponse(code = 100008, message = "非法请求")})
    @RequestMapping(value = "/result_search", method = {RequestMethod.GET})
    public ResponseData result_search(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "100") Integer limit,
                                      @RequestParam(required = false) Map<String, String> params, HttpServletRequest request) throws Exception {
        Worker currentWorker2 = CurrentWorkerLocalCache.getCurrentWorker();
        ResponseData responseData = new ResponseData();
        try {
            PageMode pageMode = new PageMode(page, limit);
            List<Dept> deList = deptService.selectAll();
            getDeptSonIds(params, deList);// 部门子集参数填充
            approvalService.searchApprovalResult(responseData, pageMode, params, currentWorker2);// 审批结果
        } catch (Exception e) {
            log.error("审批结果搜索", e);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.error, null, null);
        }
        return responseData;
    }

    /**
     * 功能描述:部门子集参数填充
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/4/25 15:56
     */
    public static void getDeptSonIds(Map<String, String> params, List<Dept> deList) {
        if (StringUtils.isNotBlank(params.get("deptId"))) {
            Set<Integer> deptIds = new HashSet<Integer>();
            convetDeptId(Integer.valueOf(params.get("deptId")), deList, deptIds);
            String deptId = ArrayUtils.toString(deptIds);
            params.put("deptId", deptId.substring(1, deptId.length() - 1));
        }
    }

    private static void convetDeptId(Integer deptId, List<Dept> deList, Set<Integer> deptIds) {
        if (null == deptId)
            return;
        deptIds.add(deptId);
        for (Dept dept : deList) {
            if (deptId.intValue() == dept.getParentId().intValue()) {
                deptIds.add(deptId);
                convetDeptId(dept.getDeptId(), deList, deptIds);
            }
        }
    }

    @ApiOperation(value = "撤销")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "数据id", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "version", value = "数据version", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "afId", value = "流程处理Id", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "驳回原因", dataType = "String", paramType = "query")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段")})
    @RequestMapping(value = "/cancel", method = {RequestMethod.POST})
    @ResponseBody
    @Menu(verify = false, menuName = "审批管理")
    public ResponseData cancel(@RequestParam(required = false) Integer id, @RequestParam(required = false) Integer afId,
                               @RequestParam(required = false) Integer version, @RequestParam(required = false) String remark) {
        Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
        ResponseData responseData = new ResponseData();
        if (id == null || id == null || afId == null || version == null) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
            return responseData;
        }
        try {
            ApprovalData approvalData = approvalService.searchApprovalData(id);
            approvalData.setVersion(version);
            ApprovalFlow approvalFlow = approvalService.searchApprovalFlow(afId);
            approvalFlow.setStatus(2);// 撤销
            approvalFlow.setRemake(remark);
            approvalFlow.setInspector(currentWorker.getWorkerId());
            approvalFlow.setExamineTime(new Date());
            String msg = approvalService.processApproval(approvalData, approvalFlow, currentWorker);// 流程处理
            if (StringUtils.isNotBlank(msg)) {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, msg, null);
            } else {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
            }
        } catch (Exception e) {
            log.error("撤销审批", e);
            responseData.responseData(ResponseStatus.error, null, null);
        }
        return responseData;
    }

}
