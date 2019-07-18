package com.ctr.crm.controlers.systemconfig;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.moduls.tag.models.Tag;
import com.ctr.crm.moduls.tag.models.TagGroup;
import com.ctr.crm.moduls.tag.service.TagGroupService;
import com.ctr.crm.moduls.tag.service.TagService;
import com.ctr.crm.interceptors.CurrentWorkerAware;

import io.swagger.annotations.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户标签组
 *
 * @author DoubleLi
 * @date 2019-04-19
 */
@Api(tags = "客户标签组")
@RestController
@RequestMapping(value = "/api/member_tag_group")
@Secure(1)
@Menu(menuName = "客户标签组", menuUrl = "member_tag_group", foundational = false, parent = @Parent(menuName = "系统设置", menuUrl = "sys_setting", foundational = false))
public class TagGroupController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(TagGroupController.class);

    @Autowired
    private TagGroupService tagGroupService;
    @Autowired
    private TagService tagService;

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "groupName", value = "组名", dataType = "String", paramType = "query")
    })
    @GetMapping("search")
    @Menu(verify = false)
    public ResponseData search(HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            TagGroup tagGroup = (TagGroup) RequestObjectUtil.mapToBean(request, TagGroup.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (tagGroup == null) {
                tagGroup = new TagGroup();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            tagGroupService.searchPage(responseData, pageMode, tagGroup);
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
            TagGroup tagGroup = tagGroupService.get(id);
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("memberTagGroup", tagGroup);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation("获取标签组及标签")
    @GetMapping(value = "group_tag")
    @Menu(verify = false)
    public ResponseData group_tag() {
        ResponseData responseData = new ResponseData();
        try {

            List<TagGroup> tagGroup = tagGroupService.findAllList(new TagGroup());
            if (tagGroup != null)
                tagGroup.forEach(g -> {
                    g.setSonTagList(tagService.findList(new com.ctr.crm.moduls.tag.models.Tag(g.getId())));
                });
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("tagGroup", tagGroup);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("获取标签组及标签", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加", notes = "groupName必填")
    @PostMapping(value = "insert")
    @Secure(value = 1, actionName = "添加客户标签组", actionUri = "/api/member_tag_group/insert", actionNote = "客户标签组", foundational = false)
    public ResponseData insert(@ModelAttribute(value = "TagGroup") @ApiParam(value = "Created TagGroup object", required = true) TagGroup tagGroup) {
        ResponseData responseData = new ResponseData();
        if (StringUtils.isNotBlank(tagGroup.getGroupName())) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
        }
        if (tagGroupService.findList(new TagGroup(tagGroup.getGroupName())).size() > 0) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.duplicate, null, null);
            return responseData;
        }
        if (StringUtils.isNotBlank(tagGroupService.insert(tagGroup))) {
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
    @Secure(value = 1, actionName = "修改客户标签组", actionUri = "/api/member_tag_group/update", actionNote = "客户标签组", foundational = false)
    public ResponseData update(@ModelAttribute(value = "TagGroup") @ApiParam(value = "Created TagGroup object", required = true) TagGroup tagGroup) {
        ResponseData responseData = new ResponseData();
        tagGroup.setDeleted(null);
        if (StringUtils.isAllBlank(tagGroup.getId(), tagGroup.getGroupName())) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
        } else if (tagGroupService.findList(new TagGroup(tagGroup.getId(), tagGroup.getGroupName())).size() > 0) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.duplicate, null, null);
        } else if (tagGroupService.update(tagGroup) > 0) {
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
    @Secure(value = 1, actionName = "删除客户标签组", actionUri = "/api/member_tag_group/delete", actionNote = "客户标签组", foundational = false)
    public ResponseData delete(@RequestParam(required = false) String id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, null, null);
            } else if (tagService.findList(new Tag(id)).size() > 0) {
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.failed, "组下面有标签无法删除", null);
            } else {
                tagGroupService.delete(id);
                responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
            }
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

}
