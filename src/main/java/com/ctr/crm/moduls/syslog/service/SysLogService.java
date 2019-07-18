package com.ctr.crm.moduls.syslog.service;

import com.ctr.crm.moduls.syslog.models.SysLog;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;

/**
 * @author DoubleLi
 * @date  2019-04-29
 */
public interface  SysLogService {

    void searchPage(ResponseData responseData, PageMode pageMode, SysLog sysLog);

    SysLog get(String id);

    List<SysLog> findList(SysLog sysLog);

    List<SysLog> findAllList(SysLog sysLog);

    String insert(SysLog sysLog);

    int insertBatch(List<SysLog> sysLogs);

    int update(SysLog sysLog);

    boolean delete(String id);

}
