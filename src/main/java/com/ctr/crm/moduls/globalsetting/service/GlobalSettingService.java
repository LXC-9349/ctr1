package com.ctr.crm.moduls.globalsetting.service;

import com.ctr.crm.moduls.globalsetting.models.GlobalSetting;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;

/**
 * @author DoubleLi
 * @date  2019-04-19
 */
public interface  GlobalSettingService {

    void searchPage(ResponseData responseData, PageMode pageMode, GlobalSetting globalSetting);

    GlobalSetting get(String id);

    List<GlobalSetting> findList(GlobalSetting globalSetting);

    List<GlobalSetting> findAllList(GlobalSetting globalSetting);

    String insert(GlobalSetting globalSetting);

    int insertBatch(List<GlobalSetting> globalSettings);

    int update(GlobalSetting globalSetting);

    boolean delete(String id);

    boolean isDuplicate();

    boolean isPhoneEncrypt();

    Integer gainNum();
}
