package com.ctr.crm.moduls.allot.service;

import com.ctr.crm.moduls.allot.models.AllotHistory;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;

/**
 * @author DoubleLi
 * @date  2019-05-07
 */
public interface  AllotHistoryService {

    void searchPage(ResponseData responseData, PageMode pageMode, AllotHistory allotHistory, String depts);

    AllotHistory get(Integer id);

    List<AllotHistory> findList(AllotHistory allotHistory);

    List<AllotHistory> findAllList();

    Boolean insert(AllotHistory allotHistory);

    int insertBatch(List<AllotHistory> allotHistorys);

    int update(AllotHistory allotHistory);

    boolean delete(Integer autoId);

}
