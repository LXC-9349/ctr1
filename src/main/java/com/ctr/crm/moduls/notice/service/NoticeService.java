package com.ctr.crm.moduls.notice.service;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.notice.models.Notice;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-05-10
 */
public interface NoticeService {

    void searchPage(ResponseData responseData, PageMode pageMode, Notice notice, String createTimeA, String createTimeB);

    Notice get(Integer id);

    List<Notice> findList(Notice notice);

    List<Notice> findAllList(Notice notice);

    Boolean insert(Notice notice);

    int insertBatch(List<Notice> notices);

    int update(Notice notice);

    Boolean delete(Integer id);

    /**
     * 功能描述: 站内信统计
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/15 18:15
     */
    Map<String, Object> count(Worker currentWorker);

    /**
     * 功能描述:标记已读
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/17 16:37
     */
    String read(Integer type, Worker worker);
}
