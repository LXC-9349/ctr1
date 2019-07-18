package com.ctr.crm.moduls.intentionality.service;

import com.ctr.crm.moduls.intentionality.models.Intentionality;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;

/**
 * @author DoubleLi
 * @date  2019-04-19
 */
public interface  IntentionalityService {

    void searchPage(ResponseData responseData, PageMode pageMode, Intentionality intentionality);

    Intentionality get(String id);

    List<Intentionality> findList(Intentionality intentionality);

    List<Intentionality> findAllList(Intentionality intentionality);

    String insert(Intentionality intentionality);

    int insertBatch(List<Intentionality> intentionalitys);

    int update(Intentionality intentionality);

    boolean delete(String id);

    /**
     * 获取指定类型的起始意向度
     * @param type 1销售2红娘3才俊佳丽
     * @return
     */
    Intentionality getByTypeFirst(Integer type);
    Intentionality select(Integer caseClass);
    /**
     * 生成意向度统计串 如：sum(if(sc.caseClass = '1', 1, 0)) as '1
     * @param type 意向度类型 1销售2红娘3才俊佳丽
     * @param targetAsAlias 统计源表别名
     * @return
     */
    String getStatisticsStr(Integer type, String targetAsAlias);

}
