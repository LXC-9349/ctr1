package com.ctr.crm.controlers.notice;

import javax.servlet.http.HttpServletRequest;

import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.notice.models.Notice;
import com.ctr.crm.moduls.notice.service.NoticeService;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
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

import java.util.HashMap;
import java.util.Map;

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
 * @date 2019-05-10
 */
@Api(tags = "通知(站内信)")
@RestController
@RequestMapping(value = "/api/notice")
@Secure(1)
public class NoticeController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(NoticeController.class);

    @Autowired
    private NoticeService noticeService;

    @ApiOperation("登陆用户站内信统计")
    @GetMapping(value = "notice_count")
    public ResponseData notice_count() {
        ResponseData responseData = new ResponseData();
        try {
            Map<String, Object> resMap = new HashMap<>(1);
            resMap.put("noticeCount", noticeService.count(CurrentWorkerLocalCache.getCurrentWorker()));
            responseData.responseData(ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "通知类型", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "workerId", value = "员工Id", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "workerName", value = "员工姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "title", value = "标题", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "createTimeA", value = "发送开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "createTimeB", value = "发送结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "1未读2已读", dataType = "Integer", paramType = "query")

    })
    @GetMapping("search")
    public ResponseData search(HttpServletRequest request, String createTimeA, String createTimeB) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            Notice notice = (Notice) RequestObjectUtil.mapToBean(request, Notice.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (notice == null) {
                notice = new Notice();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            noticeService.searchPage(responseData, pageMode, notice, createTimeA, createTimeB);
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
            Notice notice = noticeService.get(id);
            Map<String, Object> resMap = new HashMap<>(1);
            resMap.put("notice", notice);
            responseData.responseData(ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加")
    @PostMapping(value = "insert")
    public ResponseData insert(@ModelAttribute(value = "Notice") @ApiParam(value = "Created Notice object", required = true) Notice notice) {
        ResponseData responseData = new ResponseData();
        if (noticeService.insert(notice)) {
            responseData.responseData(ResponseStatus.success, null, null);
        } else {
            responseData.responseData(ResponseStatus.failed, null, null);
        }
        return responseData;
    }

    @ApiOperation(value = "修改")
    @PostMapping(value = "update")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", dataType = "Integer")})
    public ResponseData update(@ModelAttribute(value = "Notice") @ApiParam(value = "Created Notice object", required = true) Notice notice) {
        ResponseData responseData = new ResponseData();
        notice.setDeleted(null);
        if (notice.getId() == null) {
            responseData.responseData(ResponseStatus.null_param, null, null);
        } else if (noticeService.update(notice) > 0) {
            responseData.responseData(ResponseStatus.success, null, null);
        } else {
            responseData.responseData(ResponseStatus.failed, null, null);
        }
        return responseData;
    }

    @ApiOperation(value = "登陆用户通知标记为已读")
    @PostMapping(value = "read")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "通知类型 sysDict->notice_type", dataType = "Integer")})
    public ResponseData read(Integer type) {
        ResponseData responseData = new ResponseData();
        if (type == null) {
            return new ResponseData(ResponseStatus.null_param);
        }
        Worker worker = CurrentWorkerLocalCache.getCurrentWorker();
        String msg = noticeService.read(type,worker);
        if (StringUtils.isBlank(msg)) {
            responseData.responseData(ResponseStatus.success, null, null);
        } else {
            responseData.responseData(ResponseStatus.failed, msg, null);
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
    public ResponseData delete(@RequestParam(required = false) Integer id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.responseData(ResponseStatus.null_param, null, null);
            } else {
                noticeService.delete(id);
                responseData.responseData(ResponseStatus.success, null, null);
            }
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

}
