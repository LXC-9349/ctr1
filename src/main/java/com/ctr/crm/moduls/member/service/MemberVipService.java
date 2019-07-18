package com.ctr.crm.moduls.member.service;

import com.ctr.crm.moduls.member.models.MemberVip;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;

/**
 * @author DoubleLi
 * @date  2019-05-20
 */
public interface  MemberVipService {

    void searchPage(ResponseData responseData, PageMode pageMode, MemberVip memberVip);

    MemberVip get(Integer id);

    List<MemberVip> findList(MemberVip memberVip);

    List<MemberVip> findAllList();

    int insert(MemberVip memberVip);

    int insertBatch(List<MemberVip> memberVips);

    int update(MemberVip memberVip);

    boolean delete(Integer id);

    MemberVip getByMemberId(Long memberId);
    
    boolean isVip(Long memberId);
}
