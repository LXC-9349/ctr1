package com.ctr.crm.moduls.contract.service;

import com.ctr.crm.moduls.approval.modules.ApprovalData;
import com.ctr.crm.moduls.approval.result.ApprovalResult;
import com.ctr.crm.moduls.contract.models.ContractOrder;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.annontations.Approval;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author DoubleLi
 * @date 2019-05-14
 */
public interface ContractOrderService {

    void searchPage(ResponseData responseData, PageMode pageMode, ContractOrder contractOrder);

    ContractOrder get(Integer id);

    List<ContractOrder> findList(ContractOrder contractOrder);

    List<ContractOrder> findAllList(ContractOrder contractOrder);

    String insert(ContractOrder contractOrder, MultipartFile file);

    int insertBatch(List<ContractOrder> contractOrders);

    int update(ContractOrder contractOrder);

    Boolean delete(Integer id);

    //审批修改到账状态
    @Approval(action = "到账状态", beanName = "contractOrderService", method = "realApprovalUpdateStatus", delmethod = "delApprovalUpdateStatus", name = "合同审批", value = Constants.APPROVAL_CONTRACT, viewUrl = "/api/contract/approval_view", submit = true)
    ApprovalResult approvalUpdateStatus(Integer re_id);

    String realApprovalUpdateStatus(Integer re_id);

    String delApprovalUpdateStatus(Integer re_id);

    /**
     * 功能描述: 转换Vip
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/15 14:48
     */
    String isVip(Integer contractId, ApprovalData approvalData, Worker worker);

    /**
     * 功能描述:
     * 缴费之后升级vip
     * 用于审批通过后操作
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/23 17:06
     */
    void vipPayRecord(Integer recordId, ApprovalData approvalData, Worker currentWorker);

    /**
     * 功能描述:
     * 找到客户续约合同转换VIP
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/24 17:21
     */
    void renewVip(Long memberId);
}
