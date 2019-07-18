package com.ctr.crm.moduls.sales.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.sales.models.MySearch;
import com.ctr.crm.moduls.sales.models.SaleCase;
import com.ctr.crm.moduls.sales.models.SaleCaseLost;
import com.ctr.crm.commons.result.ResponsePage;
import com.ctr.crm.moduls.intentionality.models.Intentionality;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;

/**
 * 说明：
 *
 * @author eric
 * @date 2019年4月17日 下午3:10:22
 */
public interface SaleCaseService {

    boolean isInSaleCase(Long memberId);

    Integer insert(SaleCase sc);

    Integer isInSaleCase(String memberIds);

    SaleCase getByMemberId(Long memberId);

    Boolean update(SaleCase saleCase);

    void updateSaleCase(MemberBaseInfo baseInfo);

    List<SaleCase> findListBySales(List<Intentionality> intentionalityList);

    /**
     * 功能描述:
     * 更新最后通话时间
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/9 18:05
     */
    void updateLastPhoneTime(Long memberId, Date startTime);

    /**
     * 功能描述:
     * 更新最后短信时间
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/9 18:05
     */
    void updateLastSmsTime(Long memberId, Date sendTime);

    /**
     * 功能描述:
     * 更新最后小计联系时间
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/9 18:05
     */
    void updatelastContactTime(Long memberId, Date contactTime);

    /**
     * 功能描述:
     * 批量删除机会
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/10 14:54
     */
    Integer deleteByMemberIds(List<Long> recylingMemberIds);

    /**
     * 功能描述:
     * 机会放弃记录
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/10 18:04
     */
    Integer insertSaleCaseLost(SaleCaseLost saleCaseLost);

    ResponsePage<Map<String, Object>> query(MySearch search, Worker currentWorker);

    /**
     * 功能描述:
     * 根据手机号获取机会信息
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/28 17:27
     */
    SaleCase getByMobile(String mobile);
}
