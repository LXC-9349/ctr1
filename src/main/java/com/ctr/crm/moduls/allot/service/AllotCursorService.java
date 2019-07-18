package com.ctr.crm.moduls.allot.service;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.allot.models.AllotCursor;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;

/**
 * @author DoubleLi
 * @date  2019-05-05
 */
public interface  AllotCursorService {

    void searchPage(ResponseData responseData, PageMode pageMode, AllotCursor allotCursor);

    AllotCursor get(Integer id);

    List<AllotCursor> findList(AllotCursor allotCursor);

    List<AllotCursor> findAllList(AllotCursor allotCursor);

    Integer insert(AllotCursor allotCursor);

    int insertBatch(List<AllotCursor> allotCursors);

    int update(AllotCursor allotCursor);

    boolean delete(Integer id);

    List<AllotCursor> findByRuleId(String id);

    Worker findNext(String ruleId, int size, int i);
}
