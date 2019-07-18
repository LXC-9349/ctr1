package com.ctr.crm.moduls.approval.service;

import com.ctr.crm.moduls.approval.modules.ApprovalFlow;
import com.ctr.crm.moduls.approval.modules.ApprovalFlowSetting;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.moduls.approval.modules.ApprovalData;

import java.util.List;
import java.util.Map;

public interface ApprovalService {

    /**
     * 流程设置根据审批类型分组
     *
     * @return
     * @time 2018年9月29日
     * @author DoubleLi
     */
    List<ApprovalFlowSetting> searchApprovalFlowSettingSing(String approvalType);

    /**
     * 新增或更新流程数据
     *
     * @param ad
     * @return
     * @time 2018年9月29日
     * @author DoubleLi
     */
    Integer insertOrUpdateApprovalData(ApprovalData ad) throws Exception;

    /**
     * 查询审批类型对应的流程设置
     *
     * @param value
     * @return
     * @time 2018年9月29日
     * @author DoubleLi
     */
    List<ApprovalFlowSetting> searchApprovalFlowSettingByType(String value);

    /**
     * 新增或修改审批流表
     *
     * @param af
     * @return
     * @throws Exception
     * @time 2018年9月29日
     * @author DoubleLi
     */
    Integer insertOrUpdateApprovalFlow(ApprovalFlow af) throws Exception;

    /**
     * 查询流程数据
     *
     * @param id
     * @return
     * @time 2018年9月29日
     * @author DoubleLi
     */
    ApprovalData searchApprovalData(Integer id);

    /**
     * 查询审批人数据
     *
     * @param id
     * @return
     * @time 2018年9月30日
     * @author DoubleLi
     */
    ApprovalFlow searchApprovalFlow(Integer id);


    /**
     * 查询审批流
     *
     * @param dateId
     * @return
     * @time 2018年9月30日
     * @author DoubleLi
     */
    List<ApprovalFlow> searchApprovalFlowListByDateId(Integer dateId);

    /**
     * 流程处理
     *
     * @param approvalFlow
     * @return
     * @time 2018年9月29日
     * @author DoubleLi
     * （调用前先填充所有属性值，[approvalData.version为表单提交(必填),approvalFlow里面所有参数为必填,currentWorker登陆用户]）
     */
    String processApproval(ApprovalData approvalData, ApprovalFlow approvalFlow, Worker currentWorker) throws Exception;

    /**
     * 删除审批数据
     *
     * @param approvalDataId
     * @return
     * @time 2018年9月30日
     * @author DoubleLi
     */
    boolean deleteApprovalData(Integer approvalDataId);

    /**
     * 新增/修改审批设置
     *
     * @param typeMap
     * @param approvalNumber
     * @param roleList
     * @time 2018年10月8日
     * @author DoubleLi
     */
    boolean insertOrUpdateTypeSetting(Map<String, Object> typeMap, Integer approvalNumber, List<Map> roleList, Worker currentWorker) throws Exception;


    /**
     * 查询审批流数据
     *
     * @param approvalType
     * @param status
     * @return
     * @time 2018年10月8日
     * @author DoubleLi
     */
    List<ApprovalData> searchApprovalData(String approvalType, String status);

    /**
     * 根据数据id和状态查询审批流数据
     *
     * @param approvalDataIds
     * @param status
     * @return
     * @time 2018年10月8日
     * @author DoubleLi
     */
    List<ApprovalFlow> searchApprovalFlowList(String approvalDataIds, String status);

    /**
     * 待审批搜索
     *
     * @param params
     * @param currentWorker
     * @return
     * @time 2018年10月8日
     * @author DoubleLi
     */
    List<Map<String, Object>> searchPending(Map<String, String> params, Worker currentWorker) throws Exception;

    /**
     * 审批历史查询
     *
     * @param approvalData
     * @return
     * @time 2018年10月8日
     * @author DoubleLi
     */
    List<Map<String, Object>> searchApprovalHistory(ApprovalData approvalData);

    /**
     * 审批结果
     *
     * @param pageMode
     * @param params
     * @param currentWorker2
     * @time 2018年10月8日
     * @author DoubleLi
     */
    void searchApprovalResult(ResponseData responseData, PageMode pageMode, Map<String, String> params,
                              Worker currentWorker2);

    /**
     * 更新审批数据处理流
     *
     * @param approvalDataId
     * @param approvalFlowId
     * @time 2018年10月8日
     * @author DoubleLi
     */
    void updateApprovalDataApprovalFlowId(Integer approvalDataId, Integer approvalFlowId);

    /**
     * 审批详情
     *
     * @param approvalDataId
     * @return
     * @time 2018年10月12日
     * @author DoubleLi
     */
    Map<String, Object> searchApprovalDetail(Integer approvalDataId);

    /**
     * 功能描述:
     * 合同审批搜索
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/14 15:11
     */
    void searchContract(ResponseData responseData, PageMode pageMode, Map<String, String> params,
                        Worker currentWorker2) throws Exception;

    /**
     * 流程进度
     *
     * @param approvalDataId
     * @return
     */
    Integer schedule(Integer approvalDataId);

    List<Map<String, Object>> scheduleDetail(ApprovalData approvalData);

    /**
     * 功能描述:
     * 约见审批搜索
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/23 16:11
     */
    void searchMeet(ResponseData responseData, PageMode pageMode, Map<String, String> params, Worker worker);

    /**
     * 功能描述:
     * 发送通知
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/29 11:38
     */
    void sendMessage(ApprovalData approvalData, Worker currentWorker, String... msg);
}
