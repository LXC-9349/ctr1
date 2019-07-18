package com.ctr.crm.moduls.allot.service;

import java.util.List;
import java.util.Map;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.allocation.models.AllocationRule;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;
import com.ctr.crm.moduls.sales.models.SaleCase;
import com.ctr.crm.moduls.syscirculationconfig.models.SysCirculationConfig;

/**
 * 分配服务
 *
 * @author DoubleLi
 * @date 2019-05-05
 */
public interface AllotService {

    Long initMember();

    void initWorker();

    void updateWorkerAllot(Integer workerId);

    void initAllotCursor(AllocationRule r);

    void allot(List<AllocationRule> rules);

    Map<String, Object> matchmakerAllot(List<MemberBaseInfo> memberList, List<Worker> workerList, Worker currWorker);

    Map<String, Object> matchmakerDeploy(List<MemberBaseInfo> memberList, List<Worker> workerList, Worker currWorker);

    Map<String, Object> salesAllot(List<MemberBaseInfo> memberList, List<Worker> workerList, Worker currentWorker, boolean isCapacity);

    Map<String, Object> salesDeploy(List<MemberBaseInfo> memberList, List<Worker> workerList, Worker currentWorker);

    /**
     * 功能描述: 销售捞取
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/10 17:48
     */
    String gain(Long memberId, Worker worker);

    /**
     * 功能描述: 销售放弃
     *
     * @param circulation 放弃原因
     * @param: quitReason 放弃原因
     * @param: worker 放弃人
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/10 17:49
     */
    String quit(SaleCase saleCase, Worker worker, SysCirculationConfig circulation);
}
