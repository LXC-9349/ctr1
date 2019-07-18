package com.ctr.crm.controlers.allot;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.DeptUtils;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.moduls.allot.models.AllotHistory;
import com.ctr.crm.moduls.allot.service.AllotHistoryService;
import com.ctr.crm.moduls.hrm.models.Dept;
import com.ctr.crm.moduls.hrm.service.DeptService;
import com.ctr.crm.interceptors.CurrentWorkerAware;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-05-07
 */
@Api(tags = "分配历史")
@RestController
@RequestMapping(value = "/api/allot_history")
@Secure(1)
@Menu(menuName = "分配历史", menuUrl = "allot_history", foundational = false, parent = @Parent(menuName = "资源分配", menuUrl = "allot_resource", foundational = false))
public class AllotHistoryController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(AllotHistoryController.class);

    @Autowired
    private AllotHistoryService allotHistoryService;
    @Autowired
    private DeptService deptService;

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "allotType", value = "分配类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "memberId", value = "客户ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "workerName", value = "销售姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "workerId", value = "销售ID", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "workerDept", value = "销售部门", dataType = "Integer", paramType = "query")
    })
    @GetMapping("search")
    public ResponseData search(HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            AllotHistory allotHistory = (AllotHistory) RequestObjectUtil.mapToBean(request, AllotHistory.class);
            if (allotHistory == null) {
            	allotHistory = new AllotHistory();
            }
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            String depts="";
            if(allotHistory.getWorkerDept()!=null){
                List<Dept> deList = deptService.selectAll();
                depts= DeptUtils.getDeptSonIds(allotHistory.getWorkerDept(),deList);
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            allotHistoryService.searchPage(responseData, pageMode, allotHistory,depts);
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
    public ResponseData info(@RequestParam(required = false) Integer id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.null_param);
                return responseData;
            }
            AllotHistory allotHistory = allotHistoryService.get(id);
            Map<String, Object> resMap = new HashMap<>(1);
            resMap.put("allotHistory", allotHistory);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加")
    @PostMapping(value = "insert")
    public ResponseData insert(@ModelAttribute(value = "AllotHistory") @ApiParam(value = "Created AllotHistory object", required = true) AllotHistory allotHistory) {
        ResponseData responseData = new ResponseData();
        if (allotHistoryService.insert(allotHistory)) {
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
    public ResponseData update(@ModelAttribute(value = "AllotHistory") @ApiParam(value = "Created AllotHistory object", required = true) AllotHistory allotHistory) {
        ResponseData responseData = new ResponseData();
        if (allotHistory.getAutoId() != null) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
        } else if (allotHistoryService.update(allotHistory) > 0) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
        } else {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, null, null);
        }
        return responseData;
    }

    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "autoId", value = "autoId", required = true, dataType = "Integer", paramType = "query")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段"), @ApiResponse(code = 100008, message = "非法请求")})
    @DeleteMapping(value = "delete")
    public ResponseData delete(@RequestParam(required = false) Integer autoId) {
        ResponseData responseData = new ResponseData();
        try {
            if (autoId == null) {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
            } else {
                allotHistoryService.delete(autoId);
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
            }
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

}
