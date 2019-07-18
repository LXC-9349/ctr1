package com.ctr.crm.controlers.contract;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.RequestObjectUtil;
import com.ctr.crm.commons.utils.file.FileCommonUtils;
import com.ctr.crm.moduls.approval.result.ApprovalResult;
import com.ctr.crm.moduls.approval.util.ApprovalUtils;
import com.ctr.crm.moduls.contract.models.ContractOrderPayRecord;
import com.ctr.crm.moduls.contract.service.ContractOrderPayRecordService;
import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-05-14
 */
@Api(tags = "合同订单资金缴费记录")
@RestController
@RequestMapping(value = "/api/contract_pay_record")
@Secure(1)
@Menu(menuName = "合同缴费管理", menuUrl = "contract_pay_record", foundational = false, parent = @Parent(menuName = "合同管理", menuUrl = "contract", foundational = false))
public class ContractOrderPayRecordController implements CurrentWorkerAware {

    private static final Logger log = LoggerFactory.getLogger(ContractOrderPayRecordController.class);

    @Autowired
    private ContractOrderPayRecordService contractOrderPayRecordService;

    @ApiOperation(value = "查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "contractId", value = "合同ID", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "workerId", value = "操作人", dataType = "Integer", paramType = "query")
    })
    @GetMapping("search")
    @Menu(verify = false)
    @Secure(value = 1, actionName = "缴费记录查询", actionUri = "/api/contract_pay_record/search", actionNote = "合同缴费管理")
    public ResponseData search(HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        try {
            /** 获取条件页参数 */
            ContractOrderPayRecord contractOrderPayRecord = (ContractOrderPayRecord) RequestObjectUtil.mapToBean(request, ContractOrderPayRecord.class);
            /** 获取分页参数 */
            PageMode pageMode = (PageMode) RequestObjectUtil.mapToBean(request, PageMode.class);
            if (contractOrderPayRecord == null) {
                contractOrderPayRecord = new ContractOrderPayRecord();
            }
            if (pageMode == null) {
                pageMode = new PageMode();
            }
            contractOrderPayRecordService.searchPage(responseData, pageMode, contractOrderPayRecord);
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
    @Menu(verify = false)
    @Secure(value = 1, actionName = "缴费记录查询", actionUri = "/api/contract_pay_record/search", actionNote = "合同缴费管理")
    public ResponseData info(@RequestParam(required = false) Integer id) {
        ResponseData responseData = new ResponseData();
        try {
            if (id == null) {
                responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.null_param);
                return responseData;
            }
            ContractOrderPayRecord contractOrderPayRecord = contractOrderPayRecordService.get(id);
            Map<String, Object> resMap = new HashMap<>(1);
            resMap.put("contractOrderPayRecord", contractOrderPayRecord);
            responseData.responseData(com.ctr.crm.commons.result.ResponseStatus.success, null, resMap);
        } catch (Exception e) {
            log.error("详情", e);
            responseData.setStatus(com.ctr.crm.commons.result.ResponseStatus.error);
        }
        return responseData;
    }

    @ApiOperation(value = "添加")
    @PostMapping(value = "insert")
    @Menu(verify = false)
    @Secure(value = 1, actionName = "添加缴费记录", actionUri = "/api/contract_pay_record/insert", actionNote = "合同缴费管理")
    public ResponseData insert(@ModelAttribute(value = "ContractOrderPayRecord") @ApiParam(value = "Created ContractOrderPayRecord object", required = true) ContractOrderPayRecord contractOrderPayRecord,@RequestParam(value = "file", required = false) MultipartFile file) throws Exception{
        ResponseData responseData = new ResponseData();
        if (null == file) {
            return new ResponseData(com.ctr.crm.commons.result.ResponseStatus.failed, "缴费凭证不能为空");
        }
        if(!FileCommonUtils.checkImage(file.getInputStream())){
            return new ResponseData(com.ctr.crm.commons.result.ResponseStatus.failed, "非法图片格式");
        }
        if (contractOrderPayRecord.getContractId()==null||contractOrderPayRecord.getAmount()==null) {
            responseData.responseData(ResponseStatus.null_param, null, null);
        }else {
            ApprovalResult approvalResult=contractOrderPayRecordService.insert(contractOrderPayRecord,file, CurrentWorkerLocalCache.getCurrentWorker());
            ApprovalUtils.procResult(approvalResult, responseData);//流程结果处理
        }
        return responseData;
    }

    /*@ApiOperation("删除")
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
                responseData.responseData(ResponseStatus.null_param, null, null);
            } else {
                contractOrderPayRecordService.delete(id);
                responseData.responseData(ResponseStatus.success, null, null);
            }
        } catch (Exception e) {
            log.error("删除", e);
            responseData.setStatus(ResponseStatus.error);
        }
        return responseData;
    }*/

}
