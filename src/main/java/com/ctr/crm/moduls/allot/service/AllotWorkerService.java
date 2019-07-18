package com.ctr.crm.moduls.allot.service;

import com.ctr.crm.moduls.allot.models.AllotWorker;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;

/**
 * @author DoubleLi
 * @date  2019-05-05
 */
public interface  AllotWorkerService {

    void searchPage(ResponseData responseData, PageMode pageMode, AllotWorker allotWorker);

    AllotWorker get(Integer id);

    AllotWorker getByWorkerId(Integer workerId);

    List<AllotWorker> findList(AllotWorker allotWorker);

    List<AllotWorker> findAllList(AllotWorker allotWorker);

    Integer insert(AllotWorker allotWorker);

    int insertBatch(List<AllotWorker> allotWorkers);

    int update(AllotWorker allotWorker);

    boolean delete(Integer id);

}
