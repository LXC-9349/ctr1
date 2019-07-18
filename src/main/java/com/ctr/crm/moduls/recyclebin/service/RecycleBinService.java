package com.ctr.crm.moduls.recyclebin.service;

import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.moduls.recyclebin.models.RecycleBin;

import java.util.List;

/**
 * 回收站
 * @author DoubleLi
 * @date  2019-05-10
 */
public interface  RecycleBinService {

    void searchPage(ResponseData responseData, PageMode pageMode, RecycleBin recycleBin, String recycleTimeA, String recycleTimeB, String memberName);

    RecycleBin get(Integer id);

    List<RecycleBin> findList(RecycleBin recycleBin);

    List<RecycleBin> findAllList(RecycleBin recycleBin);

    Boolean insert(RecycleBin recycleBin);

    int insertBatch(List<RecycleBin> recycleBins);

    int update(RecycleBin recycleBin);

    boolean delete(Integer id);

}
