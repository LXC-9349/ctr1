package com.ctr.crm.controlers.contract;

import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.moduls.approval.modules.ApprovalData;
import com.ctr.crm.moduls.approval.modules.ApprovalFlowSetting;
import com.ctr.crm.moduls.approval.pojo.ApprovalSource;
import com.ctr.crm.moduls.approval.service.ApprovalService;
import com.ctr.crm.moduls.approvalgroup.service.ApprovalGroupService;
import com.ctr.crm.moduls.contract.models.ContractOrder;
import com.ctr.crm.moduls.contract.service.ContractOrderService;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author DoubleLi
 * @date 2019-05-14
 */
@Api(tags = "合同管理")
@RestController
@RequestMapping(value = "/api/contract")
@Secure(1)
@Menu(menuName="合同管理", menuUrl="contract")
public class ContractOrderController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(ContractOrderController.class);

    @Autowired
    private ContractOrderService contractOrderService;
    @Autowired
    private ApprovalService approvalService;
    @Autowired
    private WorkerService workerService;
    @Autowired
    private ApprovalGroupService approvalGroupService;

    @ApiOperation(value = "合同查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "memberId", value = "客户ID", dataType = "Long", paramType = "query")
    })
    @GetMapping("search")
    @Menu(verify = false)
    public ResponseData search(HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            ContractOrder contractOrder = (ContractOrder) RequestObjectUtil.mapToBean(request, ContractOrder.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (contractOrder == null) {
                contractOrder = new ContractOrder();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            contractOrderService.searchPage(responseData, pageMode, contractOrder);
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
    @Menu(verify = false)
    public ResponseData info(@RequestParam(required = false) Integer id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.null_param);
                return responseData;
            }
            ContractOrder contractOrder = contractOrderService.get(id);
            Map<String, Object> resMap = new HashMap<>(1);
            resMap.put("contractOrder", contractOrder);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加")
    @PostMapping(value = "insert")
    @Secure(value = 1, actionName = "添加合同订单", actionUri = "/api/contract/insert", actionNote = "合同管理")
    public ResponseData insert(@ModelAttribute(value = "ContractOrder") @ApiParam(value = "Created ContractOrder object", required = true) ContractOrder contractOrder,@RequestParam(value = "file", required = false) MultipartFile file) {
        ResponseData responseData = new ResponseData();
        contractOrder.setWorkerId(CurrentWorkerLocalCache.getCurrentWorker().getWorkerId());
        if (contractOrder.getContractAmount() == null || contractOrder.getServeCount() == null || contractOrder.getServeCycle() == null || StringUtils.isBlank(contractOrder.getContractName())) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
        } else {
            String msg=contractOrderService.insert(contractOrder,file);
            if(StringUtils.isBlank(msg)){
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
            }else{
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, msg, null);
            }
        }
        return responseData;
    }

    @ApiOperation(value = "修改")
    @PostMapping(value = "update")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "Integer", paramType = "query")})
    @Secure(value = 1, actionName = "修改合同订单", actionUri = "/api/contract/update", actionNote = "合同管理")
    public ResponseData update(@ModelAttribute(value = "ContractOrder") @ApiParam(value = "Created ContractOrder object", required = true) ContractOrder contractOrder) {
        ResponseData responseData = new ResponseData();
        contractOrder.setDeleted(null);
        if (contractOrder.getId() == null) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
        } else if (contractOrderService.update(contractOrder) > 0) {
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
    @Secure(value = 1, actionName = "删除合同订单", actionUri = "/api/contract/delete", actionNote = "合同管理")
    public ResponseData delete(@RequestParam(required = false) Integer id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
            } else {
                contractOrderService.delete(id);
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
            }
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "合同审批搜索（返回参数function为cancel拥有撤销权限approval拥有审批权限view查看vip升级vip）")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "groupId", value = "处理组Id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "applicant", value = "申请人员工ID或名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "signTimeA", value = "签订开始时间 yyyy-MM-dd", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "signTimeB", value = "签订结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "applicantTimeA", value = "申请开始时间 yyyy-MM-dd", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "applicantTimeB", value = "申请结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "contractStatus", value = "合同状态:0未完成1已完成", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "payStatus", value = "缴费状态0待付款1未付清2付款完成", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "0待审批,1审批中,2已通过,3已驳回,4已撤销", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "processId", value = "待处理人Id", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "processedId", value = "处理人Id", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "memberId", value = "客户Id", dataType = "String", paramType = "query")
    })
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段"), @ApiResponse(code = 100008, message = "非法请求")})
    @RequestMapping(value = "/contract_approval_search", method = {RequestMethod.GET})
    @Secure(value = 1, actionName = "合同审批", actionUri = "/api/contract/contract_approval_search", actionNote = "合同管理")
    public ResponseData contract_approval_search(@RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "100") Integer pageSize,
                                                 @RequestParam(required = false) Map<String, String> params, HttpServletRequest request) throws Exception {
        ResponseData responseData = new ResponseData();
        try {
            PageMode pageMode = new PageMode(page, pageSize);
            Worker worker = CurrentWorkerLocalCache.getCurrentWorker();
            if (!worker.getWorkerId().equals(Constants.DEFAULT_WORKER_ID)) {
                List<Integer> workerIds = workerService.workerRange(null, worker).stream()
                        .map(Worker::getWorkerId).distinct()
                        .collect(Collectors.toList());
                String allWorkerIds = String.join(",", workerIds.toString());
                params.put("workerIds", allWorkerIds.substring(1, allWorkerIds.length() - 1));
            }
            approvalService.searchContract(responseData, pageMode, params, worker);// 审批结果
            responseData.setCode(com.ctr.crm.commons.result.ResponseStatus.success.getStatusCode());
        } catch (Exception e) {
            log.error("合同审批搜索", e);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.error, null, null);
        }
        return responseData;
    }

    @ApiOperation("返回合同审批视图详情数据function:vip有转换vip权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "approvalDataId", value = "Id", required = true, dataType = "Integer", paramType = "query")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段"), @ApiResponse(code = 100008, message = "非法请求")})
    @GetMapping(value = "approval_view")
    @Menu(verify = false)
    public ResponseData approval_view(@RequestParam(required = false) Integer approvalDataId) {
        ResponseData responseData = new ResponseData();
        ApprovalData approvalData = approvalService.searchApprovalData(approvalDataId);
        //解析source
        ApprovalSource as = new ApprovalSource().analyze(approvalData.getSource());//解析参数
        Integer contractId = (Integer) as.getParmValue()[0];
        List<ApprovalFlowSetting> thisafsList = approvalService.searchApprovalFlowSettingByType("contract");
        Worker worker = CurrentWorkerLocalCache.getCurrentWorker();
        List<String> groupIds = approvalGroupService.getGroupIdByWorkerId(worker.getWorkerId());
        //最后处理人
        Integer workerId = thisafsList.get(thisafsList.size() - 1).getWorkerId();
        String groupId = thisafsList.get(thisafsList.size() - 1).getGroupId();
        Map<String, Object> resmap = new HashMap<>();
        /*approvalData.getStatus()==2||*/
        ContractOrder contractOrder=contractOrderService.get(contractId);
        if (contractOrder.getSignTime()==null&&((StringUtils.isNotBlank(groupId) && groupIds.contains(groupId)) || worker.getWorkerId().equals(workerId) || worker.getWorkerId().equals(Constants.DEFAULT_WORKER_ID))) {//当前操作用户为最后一级处理人
            resmap.put("function", "vip");
        } else {
            resmap.put("function", "");
        }
        resmap.put("contract",contractOrder );
        responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, resmap);
        return responseData;
    }

    @ApiOperation("客户升级vip")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contractId", value = "合同ID",dataType = "Integer"),
            @ApiImplicitParam(name = "approvalDataId", value = "流程ID",  dataType = "Integer")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段"), @ApiResponse(code = 100008, message = "非法请求")})
    @PostMapping(value = "is_vip")
    @Menu(verify = false)
    public ResponseData is_vip(@RequestParam(required = false) Integer contractId,@RequestParam(required = false) Integer approvalDataId) {
        ResponseData responseData = new ResponseData();
        try {
            if (contractId == null) {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
            } else {
                Worker worker=CurrentWorkerLocalCache.getCurrentWorker();
                String msg = contractOrderService.isVip(contractId,approvalService.searchApprovalData(approvalDataId),worker);
                if (StringUtils.isBlank(msg)) {
                    responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
                } else {
                    responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, msg, null);
                }
            }
        } catch (Exception e) {
            log.error("客户转换vip", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

}
