package com.ctr.crm.moduls.invitetoshop.service;

import com.ctr.crm.moduls.invitetoshop.models.InviteToShop;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date  2019-06-27
 */
public interface  InviteToShopService {

    void searchPage(ResponseData responseData, PageMode pageMode, Map<String, String> params);

    InviteToShop get(Integer id);

    List<InviteToShop> findList(InviteToShop inviteToShop);

    List<InviteToShop> findAllList(InviteToShop inviteToShop);

    int insert(InviteToShop inviteToShop);

    int insertBatch(List<InviteToShop> inviteToShops);

    int update(InviteToShop inviteToShop);

    boolean delete(Integer id);

    List<InviteToShop> memberIdByRecord(Long memberId, Integer shopStatus);
}
