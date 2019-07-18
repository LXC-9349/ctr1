package com.ctr.crm.controlers.systemconfig;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.moduls.sysdict.models.SysDict;
import com.ctr.crm.moduls.sysdict.service.SysDictService;
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
@Api(tags = "系统字典")
@RestController
@RequestMapping(value = "/api/sysdict")
@Secure(1)
public class SysDictController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(SysDictController.class);

    @Autowired
    private SysDictService sysDictService;

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "类型", dataType = "String", paramType = "query")
    })
    @GetMapping("search")
    @Menu(verify = false)
    public ResponseData search(HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            SysDict sysDict = (SysDict) RequestObjectUtil.mapToBean(request, SysDict.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (sysDict == null) {
                sysDict = new SysDict();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            sysDictService.searchPage(responseData, pageMode, sysDict);
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
            SysDict sysDict = sysDictService.get(id);
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("sysDict", sysDict);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加")
    @PostMapping(value = "insert")
    public ResponseData insert(@ModelAttribute(value = "SysDict") @ApiParam(value = "Created SysDict object", required = true) SysDict sysDict) {
        ResponseData responseData = new ResponseData();
        if (StringUtils.isNotBlank(sysDictService.insert(sysDict))) {
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
    public ResponseData update(@ModelAttribute(value = "SysDict") @ApiParam(value = "Created SysDict object", required = true) SysDict sysDict) {
        ResponseData responseData = new ResponseData();
        sysDict.setDeleted(null);
        if (StringUtils.isBlank(sysDict.getId())) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
        } else if (sysDictService.update(sysDict) > 0) {
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
    public ResponseData delete(@RequestParam(required = false) String id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
            } else {
                sysDictService.delete(id);
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
            }
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }
    
    @ApiOperation("地区")
    @RequestMapping(value="area", method={RequestMethod.GET})
    @Menu(verify = false)
    public ResponseData area() throws Exception{
    	return new ResponseData(ResponseStatus.success, sysDictService.treeArea());
    }

}
