package com.ctr.crm.moduls.allot.service;

import com.ctr.crm.moduls.allot.models.AdjustHistory;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;

/**
 * @author DoubleLi
 * @date  2019-05-08
 */
public interface  AdjustHistoryService {

    void searchPage(ResponseData responseData, PageMode pageMode, AdjustHistory adjustHistory);

    AdjustHistory get(Integer id);

    List<AdjustHistory> findList(AdjustHistory adjustHistory);

    List<AdjustHistory> findAllList(AdjustHistory adjustHistory);

    Boolean insert(AdjustHistory adjustHistory);

    int insertBatch(List<AdjustHistory> adjustHistorys);

    int update(AdjustHistory adjustHistory);

    boolean delete(Integer id);

}
