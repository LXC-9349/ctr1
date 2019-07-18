package com.ctr.crm.moduls.approval.util;

import com.ctr.crm.moduls.approval.result.ApprovalResult;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.interceptors.ApprovalInterceptor;

/**
 * 审批工具类
 *
 * @author DoubleLi
 * @date 2019年04月26日
 */
public class ApprovalUtils {

    /**
     * 流程拦截方法返回结果处理
     *
     * @param result
     * @param apiResult
     * @time 2018年10月10日
     * @author DoubleLi
     */
    public static void procResult(ApprovalResult result, ResponseData apiResult) {
        try {
            if (ApprovalInterceptor.PASS.equals(result.getType())) {
                apiResult.responseData(ResponseStatus.success, null, null);
            } else if (ApprovalInterceptor.APPROVAL.equals(result.getType())) {
                apiResult.responseData(ResponseStatus.approval, null, null);
            } else if (ApprovalInterceptor.FAIL.equals(result.getType())) {
                apiResult.responseData(ResponseStatus.failed, result.getErrMsg(), null);
            } else {
                apiResult.responseData(ResponseStatus.error, "系统异常", null);
            }
        } catch (Exception e) {
            System.err.println(e);
            apiResult.responseData(ResponseStatus.error, "系统异常", null);
        }
    }
}
