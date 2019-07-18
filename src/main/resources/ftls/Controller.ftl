package ${BasePackageName}${ControllerPackageName};

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ${BasePackageName}${EntityPackageName}.${ClassName};
import ${BasePackageName}${ServicePackageName}.${ClassName}Service;

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

import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.interceptors.CurrentWorkerAware;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 *
 * @author ${Author}
 * @date  ${Date}
 */
@Api(tags = "")
@RestController
@RequestMapping(value = "/api/${LowerEntityName}")
public class ${ClassName}Controller implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(${ClassName}Controller.class);

    @Autowired
    private ${ClassName}Service ${EntityName}Service;

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query")
    })
    @GetMapping("search")
    public ResponseData search(HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
        ${ClassName} ${EntityName} =(${ClassName}) RequestObjectUtil.mapToBean(request, ${ClassName}. class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (${EntityName} ==null){
            ${EntityName} =new ${ClassName}();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            ${EntityName}Service.searchPage(responseData, pageMode,${EntityName});
            responseData.setStatus(ResponseStatus.success);
        } catch (Exception e) {
            log.error("查询", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation("详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "Integer", paramType = "query")})
    @GetMapping(value = "info")
    public ResponseData info(@RequestParam(required = false) Integer id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.setStatus(ResponseStatus.null_param);
                return responseData;
            }
        ${ClassName} ${EntityName} =${EntityName}Service.get(id);
            Map<String, Object> resMap = new HashMap<>(1);
            resMap.put("${EntityName}", ${EntityName});
            responseData.responseData(ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加")
    @PostMapping(value = "insert")
    public ResponseData insert(@ModelAttribute(value = "${ClassName}") @ApiParam(value = "Created ${ClassName} object", required = true) ${ClassName} ${EntityName}) {
        ResponseData responseData = new ResponseData();
        if (${EntityName}Service.insert(${EntityName})>0) {
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
    public ResponseData update(@ModelAttribute(value = "${ClassName}") @ApiParam(value = "Created ${ClassName} object", required = true) ${ClassName} ${EntityName}) {
        ResponseData responseData = new ResponseData();
        ${EntityName}.setDeleted(null);
        if (${EntityName}.getId()==null){
            responseData.responseData(ResponseStatus.null_param, null, null);
        }else if (${EntityName}Service.update(${EntityName}) > 0) {
            responseData.responseData(ResponseStatus.success, null, null);
        } else {
            responseData.responseData(ResponseStatus.failed, null, null);
        }
        return responseData;
    }

    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "Integer")})
    @DeleteMapping(value = "delete")
    public ResponseData delete(@RequestParam(required = false) Integer id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.responseData(ResponseStatus.null_param, null, null);
            } else {
                ${EntityName}Service.delete(id);
                responseData.responseData(ResponseStatus.success, null, null);
            }
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

}
