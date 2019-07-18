package com.ctr.crm.moduls.syscirculationconfig.service;

import com.ctr.crm.moduls.syscirculationconfig.models.SysCirculationConfig;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;

/**
 * @author DoubleLi
 * @date  2019-04-23
 */
public interface  SysCirculationConfigService {

    void searchPage(ResponseData responseData, PageMode pageMode, SysCirculationConfig sysCirculationConfig,String type);

    SysCirculationConfig get(String id);

    List<SysCirculationConfig> findList(SysCirculationConfig sysCirculationConfig);

    List<SysCirculationConfig> findAllList(SysCirculationConfig sysCirculationConfig);
    /**
     * 获取流转原因列表
     * @param circulationType 1黑名单 2转让客户 3放弃客户
     * @return
     */
    List<SysCirculationConfig> getCirculationList(Integer circulationType);

    String insert(SysCirculationConfig sysCirculationConfig);

    int insertBatch(List<SysCirculationConfig> sysCirculationConfigs);

    int update(SysCirculationConfig sysCirculationConfig);

    boolean delete(String id);

}
