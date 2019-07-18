package com.ctr.crm.controlers.systemconfig;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.moduls.sysrecyclingmemberconfig.models.SysRecyclingMemberConfig;
import com.ctr.crm.moduls.sysrecyclingmemberconfig.service.SysRecyclingMemberConfigService;
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
@Api(tags = "客户回收策略设置")
@RestController
@RequestMapping(value = "/api/sys_recycling_member_config")
@Secure(1)
@Menu(menuName="客户回收策略设置", menuUrl="sys_recycling_member_config", foundational=false, parent=@Parent(menuName="系统设置", menuUrl="sys_setting", foundational=false))
public class SysRecyclingMemberConfigController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(SysRecyclingMemberConfigController.class);

    @Autowired
    private SysRecyclingMemberConfigService sysRecyclingMemberConfigService;

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query")
    })
    @GetMapping("search")
    @Menu(verify = false)
    public ResponseData search(HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            SysRecyclingMemberConfig sysRecyclingMemberConfig = (SysRecyclingMemberConfig) RequestObjectUtil.mapToBean(request, SysRecyclingMemberConfig.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (sysRecyclingMemberConfig == null) {
                sysRecyclingMemberConfig = new SysRecyclingMemberConfig();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            sysRecyclingMemberConfigService.searchPage(responseData, pageMode, sysRecyclingMemberConfig);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.success);
        } catch (Exception e) {
            log.error("查询", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation("详情")
    @ApiResponses({@ApiResponse(code = 100000, message = "请求成功"), @ApiResponse(code = 100001, message = "请求失败"),
            @ApiResponse(code = 100003, message = "参数为空"), @ApiResponse(code = 100004, message = "未登录"),
            @ApiResponse(code = 100005, message = "未设置可显字段"), @ApiResponse(code = 100008, message = "非法请求")})
    @GetMapping(value = "info")
    @Menu(verify = false)
    public ResponseData info() {
        ResponseData responseData = new ResponseData();
        try {
            SysRecyclingMemberConfig sysRecyclingMemberConfig = sysRecyclingMemberConfigService.get(null);
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("sysRecyclingMemberConfig", sysRecyclingMemberConfig);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加")
    @PostMapping(value = "insert")
    public ResponseData insert(@ModelAttribute(value = "SysRecyclingMemberConfig") @ApiParam(value = "Created SysRecyclingMemberConfig object", required = true) SysRecyclingMemberConfig sysRecyclingMemberConfig) {
        ResponseData responseData = new ResponseData();
        if(sysRecyclingMemberConfig.getTswitch()==null||sysRecyclingMemberConfig.getTswitch()==1){
            sysRecyclingMemberConfig.setCondition2(0);
            sysRecyclingMemberConfig.setCondition2(0);
            sysRecyclingMemberConfig.setCondition2(0);
        }
        if(sysRecyclingMemberConfig.getCondition2()!=null&&!sysRecyclingMemberConfig.getCondition2().equals(0)&&StringUtils.isBlank(sysRecyclingMemberConfig.getContactInformations())){
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.null_param, "必须选择有效联系方式", null);
        }else if (StringUtils.isNotBlank(sysRecyclingMemberConfigService.insert(sysRecyclingMemberConfig))) {
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, null);
        } else {
            responseData.responseData(ResponseStatus.failed, null, null);
        }
        return responseData;
    }


}
