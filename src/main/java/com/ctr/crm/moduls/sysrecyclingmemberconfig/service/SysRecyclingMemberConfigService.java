package com.ctr.crm.moduls.sysrecyclingmemberconfig.service;

import com.ctr.crm.moduls.sysrecyclingmemberconfig.models.SysRecyclingMemberConfig;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;

/**
 * @author DoubleLi
 * @date  2019-04-23
 */
public interface  SysRecyclingMemberConfigService {

    void searchPage(ResponseData responseData, PageMode pageMode, SysRecyclingMemberConfig sysRecyclingMemberConfig);

    SysRecyclingMemberConfig get(String id);

    List<SysRecyclingMemberConfig> findList(SysRecyclingMemberConfig sysRecyclingMemberConfig);

    List<SysRecyclingMemberConfig> findAllList(SysRecyclingMemberConfig sysRecyclingMemberConfig);

    String insert(SysRecyclingMemberConfig sysRecyclingMemberConfig);

    int insertBatch(List<SysRecyclingMemberConfig> sysRecyclingMemberConfigs);

    int update(SysRecyclingMemberConfig sysRecyclingMemberConfig);

    boolean delete(String id);

}
