package com.ctr.crm.controlers.member;

import javax.servlet.http.HttpServletRequest;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.moduls.contract.service.ContractOrderService;
import com.ctr.crm.moduls.member.models.MemberVip;
import com.ctr.crm.moduls.member.service.MemberVipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

/**
 * @author DoubleLi
 * @date 2019-05-20
 */
@Api(tags = "VIP客户信息")
@RestController
@RequestMapping(value = "/api/member_vip")
@Secure(1)
public class MemberVipController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(MemberVipController.class);

    @Autowired
    private MemberVipService memberVipService;
    @Autowired
    private ContractOrderService contractOrderService;

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "memberId", value = "客户Id", dataType = "Long", paramType = "query")
    })
    @GetMapping("search")
    @Menu(menuName = "vip客户", menuUrl = "member_vip", foundational = false, parent = @Parent(menuName = "资源管理", menuUrl = "member", foundational = false))
    public ResponseData search(HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            MemberVip memberVip = (MemberVip) RequestObjectUtil.mapToBean(request, MemberVip.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (memberVip == null) {
                memberVip = new MemberVip();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            memberVipService.searchPage(responseData, pageMode, memberVip);
            responseData.setStatus(ResponseStatus.success);
        } catch (Exception e) {
            log.error("查询", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation("vip升级信息详情 return:type 1正常 2没有升级信息 3合同已过期 4合已服务完")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberId", value = "memberId", required = true, dataType = "Long", paramType = "query")})
    @GetMapping(value = "info")
    public ResponseData info(@RequestParam(required = false) Long memberId) {
        ResponseData responseData = new ResponseData();
        Map<String, Object> resMap = new HashMap<>(3);
        try {
            if (memberId == null) {
                responseData.setStatus(ResponseStatus.null_param);
                return responseData;
            }
            MemberVip memberVip = memberVipService.getByMemberId(memberId);
            if (memberVip == null) {
                resMap.put("type", 2);
            } else{
                if (memberVip.getStatus() == 2) {
                    resMap.put("type", 3);
                } else if (memberVip.getStatus() == 1) {
                    resMap.put("type", 4);
                } else {
                    resMap.put("type", 1);
                }
                resMap.put("memberVip", memberVip);
                resMap.put("contract", contractOrderService.get(memberVip.getContractId()));
            }

            responseData.responseData(ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }


}
