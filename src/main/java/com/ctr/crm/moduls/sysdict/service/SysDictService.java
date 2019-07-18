package com.ctr.crm.moduls.sysdict.service;

import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.moduls.sysdict.models.SysDict;

import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-04-23
 */
public interface SysDictService {

    void searchPage(ResponseData responseData, PageMode pageMode, SysDict sysDict);

    SysDict get(String id);

    List<SysDict> findList(SysDict sysDict);
    List<SysDict> findList(String... types);
    List<Map<String, Object>> treeArea();

    List<SysDict> findAllList(SysDict sysDict);

    String insert(SysDict sysDict);

    int insertBatch(List<SysDict> sysDicts);

    int update(SysDict sysDict);
    /**
     * 更新指定type和value的label值
     * @param type
     * @param value
     * @param label
     * @return
     */
    int update(String type,String value, String label);

    boolean delete(String id);

    String findValue(String type,String label);

    /**
     * 功能描述:
     * 查找type 去掉父级
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/10 10:18
     */
    List<SysDict> findListByType(String type);

    String findLabel(String type, String value);

}
