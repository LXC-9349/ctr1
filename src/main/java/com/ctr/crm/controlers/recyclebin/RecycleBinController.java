package com.ctr.crm.controlers.recyclebin;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.moduls.allot.service.AllotService;
import com.ctr.crm.moduls.recyclebin.models.RecycleBin;
import com.ctr.crm.moduls.recyclebin.service.RecycleBinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;

/**
 * @author DoubleLi
 * @date 2019-05-10
 */
@Api(tags = "资源回收站")
@RestController
@RequestMapping(value = "/api/recycle_bin")
@Secure(1)
@Menu(menuName = "资源回收站", menuUrl = "recycle_bin", foundational = false, parent = @Parent(menuName = "资源管理", menuUrl = "member", foundational = false))
public class RecycleBinController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(RecycleBinController.class);

    @Autowired
    private RecycleBinService recycleBinService;
    @Autowired
    private AllotService allotService;

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "recycleTimeA", value = "回收开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "recycleTimeB", value = "回收结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "memberId", value = "回收的客户ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "回收的客户名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "recycleReason", value = "回收原因", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "workerId", value = "执行回收的员Id", dataType = "Integer", paramType = "query")

    })
    @GetMapping("search")
    /*@Secure(value = 1, actionName = "查询", actionUri = "/api/recycle_bin/search", actionNote = "资源回收站", foundational = false)*/
    public ResponseData search(HttpServletRequest request, String recycleTimeA, String recycleTimeB, String memberName) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            RecycleBin recycleBin = (RecycleBin) RequestObjectUtil.mapToBean(request, RecycleBin.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (recycleBin == null) {
                recycleBin = new RecycleBin();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            recycleBinService.searchPage(responseData, pageMode, recycleBin, recycleTimeA, recycleTimeB, memberName);
            responseData.setStatus(ResponseStatus.success);
        } catch (Exception e) {
            log.error("查询", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation("详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", dataType = "Integer", paramType = "query")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段"), @ApiResponse(code = 100008, message = "非法请求")})
    @GetMapping(value = "info")
    public ResponseData info(Integer id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.setStatus(ResponseStatus.null_param);
                return responseData;
            }
            RecycleBin recycleBin = recycleBinService.get(id);
            Map<String, Object> resMap = new HashMap<>(1);
            resMap.put("recycleBin", recycleBin);
            responseData.responseData(ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation("回收站资源捞取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "回收Id", required = true, dataType = "Integer", paramType = "query")})
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段"), @ApiResponse(code = 100008, message = "非法请求")})
    @PostMapping(value = "gain")
    @Secure(value = 1, actionName = "回收站资源捞取", actionUri = "/api/recycle_bin/gain", actionNote = "资源回收站", foundational = false)
    public ResponseData gain(@RequestParam(required = false) Integer id) {
        ResponseData responseData = new ResponseData();
        try {
            RecycleBin recycleBin = recycleBinService.get(id);
            if (recycleBin == null) {
                responseData.responseData(ResponseStatus.null_param, null, null);
            } else {
                String res = allotService.gain(recycleBin.getMemberId(), CurrentWorkerLocalCache.getCurrentWorker());
                recycleBinService.delete(id);
                if (StringUtils.isBlank(res)) {
                    responseData.responseData(ResponseStatus.success, "捞取成功", null);
                } else {
                    responseData.responseData(ResponseStatus.failed, res, null);
                }
            }
        } catch (Exception e) {
            log.error("回收站资源捞取", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

}
