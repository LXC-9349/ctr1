package com.ctr.crm.controlers.systemconfig;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.moduls.tag.models.Tag;
import com.ctr.crm.moduls.tag.service.TagGroupService;
import com.ctr.crm.moduls.tag.service.TagService;

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
 * @date 2019-04-19
 */
@Api(tags = "客户标签")
@RestController
@RequestMapping(value = "/api/member_tag")
@Secure(1)
@Menu(menuName="客户标签设置", menuUrl="member_tag", foundational=false, parent=@Parent(menuName="系统设置", menuUrl="sys_setting", foundational=false))
public class TagController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(TagController.class);

    @Autowired
    private TagService tagService;
    @Autowired
    private TagGroupService tagGroupService;


    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "tagName", value = "标签名", dataType = "String", paramType = "query")
            , @ApiImplicitParam(name = "groupId", value = "标签组ID", dataType = "String", paramType = "query")
    })
    @GetMapping("search")
    @Menu(verify = false)
    public ResponseData search(HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            com.ctr.crm.moduls.tag.models.Tag tag = (com.ctr.crm.moduls.tag.models.Tag) RequestObjectUtil.mapToBean(request, com.ctr.crm.moduls.tag.models.Tag.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (tag == null) {
                tag = new com.ctr.crm.moduls.tag.models.Tag();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            tagService.searchPage(responseData, pageMode, tag);
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
            com.ctr.crm.moduls.tag.models.Tag tag = tagService.get(id);
            Map<String, Object> resMap = new HashMap<>(1);
            resMap.put("memberTag", tag);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加", notes = "groupId,tagName必填")
    @PostMapping(value = "insert")
    @Secure(value=1, actionName="添加标签", actionUri="/api/member_tag/insert", actionNote="客户标签设置", foundational=false)
    public ResponseData insert(@ModelAttribute(value = "Tag") @ApiParam(value = "Created Tag object", required = true) com.ctr.crm.moduls.tag.models.Tag tag) {
        ResponseData responseData = new ResponseData();
        if (StringUtils.isAnyBlank(tag.getTagName(), tag.getGroupId())) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
        } else if (tagService.findList(new com.ctr.crm.moduls.tag.models.Tag(tag.getTagName(), tag.getGroupId())).size() > 0) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.duplicate, null, null);
        } else if (tagGroupService.get(tag.getGroupId()) == null) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, "标签组不存在", null);
        } else if (StringUtils.isNotBlank(tagService.insert(tag))) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
        } else {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, null, null);
        }
        return responseData;
    }

    @ApiOperation(value = "修改", notes = "groupId,tagName,id必填")
    @PostMapping(value = "update")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id", required = true, dataType = "String", paramType = "query")})
    @Secure(value=1, actionName="修改标签", actionUri="/api/member_tag/update", actionNote="客户标签设置", foundational=false)
    public ResponseData update(@ModelAttribute(value = "Tag") @ApiParam(value = "Created Tag object", required = true) com.ctr.crm.moduls.tag.models.Tag tag) {
        ResponseData responseData = new ResponseData();
        tag.setDeleted(null);
        if (StringUtils.isAllBlank(tag.getId(), tag.getGroupId(), tag.getTagName())) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
        } else if (tagService.findList(new Tag(tag.getId(), tag.getTagName(), tag.getGroupId())).size() > 0) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.duplicate, null, null);
        } else if (tagGroupService.get(tag.getGroupId()) == null) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, "标签组不存在", null);
        } else if (tagService.update(tag) > 0) {
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
    @Secure(value=1, actionName="删除标签", actionUri="/api/member_tag/delete", actionNote="客户标签设置", foundational=false)
    public ResponseData delete(@RequestParam(required = false) String id) {
        ResponseData responseData = new ResponseData();
        try {
            com.ctr.crm.commons.result.ResponseStatus rs = null;
            if (id == null) {
                rs = com.ctr.crm.commons.result.ResponseStatus.null_param;
            } else {
                tagService.delete(id);
                rs = com.ctr.crm.commons.result.ResponseStatus.success;
            }
            responseData.responseData(rs, null, null);
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

}
