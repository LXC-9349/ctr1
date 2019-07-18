package com.ctr.crm.controlers.meetrecord;

import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.moduls.approval.result.ApprovalResult;
import com.ctr.crm.moduls.approval.service.ApprovalService;
import com.ctr.crm.moduls.approval.util.ApprovalUtils;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import com.ctr.crm.moduls.meetrecord.models.MeetRecord;
import com.ctr.crm.moduls.meetrecord.service.MeetRecordService;
import com.ctr.crm.moduls.sales.models.SaleCase;
import com.ctr.crm.moduls.sales.service.SaleCaseService;
import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author DoubleLi
 * @date 2019-05-23
 */
@Api(tags = "约见管理")
@RestController
@RequestMapping(value = "/api/meet_manage")
@Secure(1)
@Menu(menuName = "服务管理", menuUrl = "meet_manage")
public class MeetRecordController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(MeetRecordController.class);

    @Autowired
    private MeetRecordService meetRecordService;
    @Autowired
    private WorkerService workerService;
    @Autowired
    private ApprovalService approvalService;
    @Autowired
    private SaleCaseService saleCaseService;

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "memberId", value = "客户ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "meetId", value = "约见对象ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "workerId", value = "红娘ID", dataType = "Integer", paramType = "query"),
    })
    @GetMapping("search")
    @Menu(verify = false)
    public ResponseData search(HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            MeetRecord meetRecord = (MeetRecord) RequestObjectUtil.mapToBean(request, MeetRecord.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (meetRecord == null) {
                meetRecord = new MeetRecord();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            meetRecordService.searchPage(responseData, pageMode, meetRecord);
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
    @GetMapping(value = "info")
    @Menu(verify = false)
    public ResponseData info(@RequestParam(required = false) Integer id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.null_param);
                return responseData;
            }
            MeetRecord meetRecord = meetRecordService.get(id);
            Map<String, Object> resMap = new HashMap<>(1);
            resMap.put("meetRecord", meetRecord);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加")
    @PostMapping(value = "insert")
    @Secure(value = 1, actionName = "添加服务记录", actionUri = "/api/meet_manage/insert", actionNote = "服务管理")
    public ResponseData insert(@ModelAttribute(value = "MeetRecord") @ApiParam(value = "Created MeetRecord object", required = true) MeetRecord meetRecord) {
        ResponseData responseData = new ResponseData();
        if (meetRecord.getMemberId() == null || meetRecord.getMeetId() == null || meetRecord.getMeetTime() == null || meetRecord.getCallId() == null) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
        } else {
            Worker worker = CurrentWorkerLocalCache.getCurrentWorker();
            meetRecord.setWorkerId(worker.getWorkerId());
            SaleCase saleCase = saleCaseService.getByMemberId(meetRecord.getMemberId());
            if (!saleCase.getWorkerId().equals(meetRecord.getWorkerId()) && !meetRecord.getWorkerId().equals(Constants.DEFAULT_WORKER_ID)) {
                return new ResponseData(com.ctr.crm.commons.result.ResponseStatus.failed, "非跟进人无法添加约见服务", null);
            }
            ApprovalResult approvalResult = meetRecordService.approvalInsert(meetRecord);
            ApprovalUtils.procResult(approvalResult, responseData);//流程结果处理
        }
        return responseData;
    }

    @ApiOperation(value = "约见审批搜索（返回参数function为cancel拥有撤销权限approval拥有审批权限view查看）")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "groupId", value = "处理组Id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "applicant", value = "申请人员工ID或名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "meetTimeA", value = "约见开始时间 yyyy-MM-dd", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "meetTimeB", value = "约见结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "applicantTimeA", value = "申请开始时间 yyyy-MM-dd", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "applicantTimeB", value = "申请结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "meetStatus", value = "约见状态:0未完成1已完成2作废", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "0待审批,1审批中,2已通过,3已驳回,4已撤销", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "processId", value = "待处理人Id", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "processedId", value = "处理人Id", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "memberId", value = "客户Id", dataType = "String", paramType = "query")
    })
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段"), @ApiResponse(code = 100008, message = "非法请求")})
    @RequestMapping(value = "/meet_approval_search", method = {RequestMethod.GET})
    @Secure(value = 1, actionName = "服务审批", actionUri = "/api/meet_manage/meet_approval_search", actionNote = "服务管理")
    public ResponseData contract_approval_search(@RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "100") Integer pageSize,
                                                 @RequestParam(required = false) Map<String, String> params) {
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
            approvalService.searchMeet(responseData, pageMode, params, worker);// 审批结果
            responseData.setCode(com.ctr.crm.commons.result.ResponseStatus.success.getStatusCode());
        } catch (Exception e) {
            log.error("审批结果搜索", e);
            responseData.responseData(ResponseStatus.error, null, null);
        }
        return responseData;
    }


}
