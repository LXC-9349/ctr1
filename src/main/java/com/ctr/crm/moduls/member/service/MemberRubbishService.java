package com.ctr.crm.moduls.member.service;

import java.util.List;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.moduls.member.models.MemberRubbish;
import com.ctr.crm.moduls.syscirculationconfig.models.SysCirculationConfig;

/**
 * @author DoubleLi
 * @date  2019-05-16
 */
public interface  MemberRubbishService {

    void searchPage(ResponseData responseData, PageMode pageMode, MemberRubbish memberRubbish, String memberName, String createTimeA, String createTimeB);

    MemberRubbish select(Long memberId);

    List<MemberRubbish> findList(MemberRubbish memberRubbish);

    List<MemberRubbish> findAllList();

    String insert(Long memberId, SysCirculationConfig reason, Worker worker);

    int insertBatch(List<MemberRubbish> memberRubbishs);

    int update(MemberRubbish memberRubbish);

    /**
     * 从黑名单解除
     * @param memberId
     * @return
     */
    String relieve(Long memberId);

}
