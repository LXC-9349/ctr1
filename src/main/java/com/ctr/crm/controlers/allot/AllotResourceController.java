package com.ctr.crm.controlers.allot;

import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.es.MemberBean;
import com.ctr.crm.commons.es.MemberBeanSearch;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.moduls.allot.service.AllotService;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.service.DeptService;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;
import com.ctr.crm.moduls.member.service.MemberService;
import com.ctr.crm.moduls.sales.service.SaleCaseService;
import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author DoubleLi
 * @date 2019-05-08
 */
@Api(tags = "资源分配")
@RestController
@RequestMapping(value = "/api/allot_resource")
@Secure(1)
public class AllotResourceController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(AllotResourceController.class);
    private static final Log allotlog = LogFactory.getLog("allot");

    @Resource
    private MemberService memberService;
    @Resource
    private WorkerService workerService;
    @Autowired
    private AllotService allotService;
    @Resource
    private SaleCaseService saleCaseService;
    @Resource
    private DeptService deptService;

    @ApiOperation(value = "销售分配/调配查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "allotType", value = "1分配2调配", dataType = "Integer", paramType = "query"),
    })
    @GetMapping("sales_search")
    @Menu(menuName = "销售分配", menuUrl = "sales_allot", foundational = false, parent = @Parent(menuName = "资源分配", menuUrl = "allot_resource", foundational = false))
    public ResponseData sales_search(HttpServletRequest request, @ModelAttribute MemberBeanSearch memberBean, Integer allotType) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取分页参数 */
            responseData = allot_deploy_search(request, memberBean, allotType, responseData, false);
        } catch (Exception e) {
            log.error("查询", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "红娘分配/调配查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "allotType", value = "1分配2调配", dataType = "Integer", paramType = "query"),
    })
    @GetMapping("hn_search")
    @Menu(menuName = "红娘分配", menuUrl = "hn_allot", foundational = false, parent = @Parent(menuName = "资源分配", menuUrl = "allot_resource", foundational = false))
    public ResponseData hn_search(HttpServletRequest request, @ModelAttribute MemberBeanSearch memberBean, Integer allotType) {
        ResponseData responseData = new ResponseData();
        try {
            responseData = allot_deploy_search(request, memberBean, allotType, responseData, true);
        } catch (Exception e) {
            log.error("查询", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    private ResponseData allot_deploy_search(HttpServletRequest request, @ModelAttribute MemberBeanSearch memberBean, Integer allotType, ResponseData responseData, boolean isVip) throws Exception {
        /** 获取分页参数 */
        PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
        if (pageMode == null) {
            pageMode = new PageMode();
        }
        memberBean.setIsVip(isVip);
        if (allotType != null) {
            if (allotType.equals(1)) {
                //分配 取不在机会表
                memberBean.setInSaleCase(false);
            } else {
                memberBean.setInSaleCase(true);
            }
        }
        memberBean.setIsBlacklist(false);
        //部门添加
        Worker worker = CurrentWorkerLocalCache.getCurrentWorker();
        if (!worker.getWorkerId().equals(Constants.DEFAULT_WORKER_ID) && allotType != null && allotType.equals(2)) {
            List<Integer> workerIds = workerService.workerRange(null, worker).stream()
                    .map(Worker::getWorkerId).distinct()
                    .collect(Collectors.toList());
            String allWorkerIds = String.join(",", workerIds.toString());
            memberBean.setWorkerIds(allWorkerIds.substring(1, allWorkerIds.length() - 1));
        }
        memberBean.setPage(pageMode.getPage());
        memberBean.setPageSize(pageMode.getPageSize());
        Page<MemberBean> result = memberService.search(memberBean, worker);
        pageMode.setTotal(result.getTotalElements());
        responseData = new ResponseData(com.ctr.crm.commons.result.ResponseStatus.success);
        pageMode.setApiResult(responseData, result.getContent());
        return responseData;
    }

    @ApiOperation(value = "红娘分配")
    @PostMapping(value = "matchmaker_allot")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workerIds", value = "选择的红娘,隔开", required = true, dataType = "String"),
            @ApiImplicitParam(name = "memberIds", value = "选择的资源，隔开", required = true, dataType = "String")
    })
    @Secure(value = 1, actionName = "分配", actionUri = "/api/allot_resource/matchmaker_allot", actionNote = "红娘分配", foundational = false)
    public ResponseData matchmaker_allot(String workerIds, String memberIds) {
        ResponseData responseData = new ResponseData();
        if (StringUtils.isAnyBlank(workerIds, memberIds)) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
            return responseData;
        }
        List<Worker> workerList = workerService.selectByWorkerIds(workerIds);
        List<MemberBaseInfo> memberList = memberService.searchByMemberIds(memberIds);
        String msg = check(workerIds,workerList);
        if (StringUtils.isNotBlank(msg)) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, msg, null);
        } else {
            Map<String,Object> map = allotService.matchmakerAllot(memberList, workerList, CurrentWorkerLocalCache.getCurrentWorker());
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, map);
        }
        return responseData;
    }

    /**
     * 功能描述:
     * 分配/调配 数据校验
     *
     * @param: type 分配:Constants.NO 调配 Constants.YES
     * @return: true 校验成功 false 校验失败
     * @author: DoubleLi
     * @date: 2019/5/8 14:02
     */
    private String check(String workerIds, List<Worker> workerList) {
        if (workerIds.split(",").length != workerList.size()) {
            log.warn("选择的员工不存在" + workerIds);
            return "选择的员工不存在:" + workerList.removeAll(Arrays.asList(workerIds.split(",")));
        }
        return null;
    }

    @ApiOperation(value = "红娘调配")
    @PostMapping(value = "matchmaker_deploy")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workerIds", value = "选择的红娘,隔开", required = true, dataType = "String"),
            @ApiImplicitParam(name = "memberIds", value = "选择的资源，隔开", required = true, dataType = "String")
    })
    @Secure(value = 1, actionName = "调配", actionUri = "/api/allot_resource/matchmaker_deploy", actionNote = "红娘分配", foundational = false)
    public ResponseData matchmaker_deploy(String workerIds, String memberIds) {
        ResponseData responseData = new ResponseData();
        if (StringUtils.isAnyBlank(workerIds, memberIds)) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
            return null;
        }
        List<Worker> workerList = workerService.selectByWorkerIds(workerIds);
        List<MemberBaseInfo> memberList = memberService.searchByMemberIds(memberIds);
        String msg = check(workerIds,workerList);
        if (StringUtils.isNotBlank(msg)) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, msg, null);
        } else {
            Map<String,Object> map = allotService.matchmakerDeploy(memberList, workerList, CurrentWorkerLocalCache.getCurrentWorker());
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, map);
        }
        return responseData;
    }

    @ApiOperation(value = "销售分配")
    @PostMapping(value = "sales_allot")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workerIds", value = "选择的销售,隔开", required = true, dataType = "String"),
            @ApiImplicitParam(name = "memberIds", value = "选择的资源，隔开", required = true, dataType = "String")
    })
    @Secure(value = 1, actionName = "分配", actionUri = "/api/allot_resource/sales_allot", actionNote = "销售分配", foundational = false)
    public ResponseData sales_allot(String workerIds, String memberIds) {
        ResponseData responseData = new ResponseData();
        if (StringUtils.isAnyBlank(workerIds, memberIds)) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
            return responseData;
        }
        List<Worker> workerList = workerService.selectByWorkerIds(workerIds);
        List<MemberBaseInfo> memberList = memberService.searchByMemberIds(memberIds);
        String msg = check(workerIds,workerList);
        if (StringUtils.isNotBlank(msg)) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, msg, null);
        } else {
            Map<String,Object> map = allotService.salesAllot(memberList, workerList, CurrentWorkerLocalCache.getCurrentWorker(),true);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, map);
        }
        return responseData;
    }

    @ApiOperation(value = "销售调配")
    @PostMapping(value = "sales_deploy")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workerIds", value = "选择的红娘,隔开", required = true, dataType = "String"),
            @ApiImplicitParam(name = "memberIds", value = "选择的资源，隔开", required = true, dataType = "String")
    })
    @Secure(value = 1, actionName = "调配", actionUri = "/api/allot_resource/sales_deploy", actionNote = "销售分配", foundational = false)
    public ResponseData sales_deploy(String workerIds, String memberIds) {
        ResponseData responseData = new ResponseData();
        if (StringUtils.isAnyBlank(workerIds, memberIds)) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
            return null;
        }
        List<Worker> workerList = workerService.selectByWorkerIds(workerIds);
        List<MemberBaseInfo> memberList = memberService.searchByMemberIds(memberIds);
        String msg = check(workerIds, workerList);
        if (StringUtils.isNotBlank(msg)) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, msg, null);
        } else {
            Map<String,Object> map = allotService.salesDeploy(memberList, workerList, CurrentWorkerLocalCache.getCurrentWorker());
            responseData.responseData(ResponseStatus.success, null, map);
        }
        return responseData;
    }
}
