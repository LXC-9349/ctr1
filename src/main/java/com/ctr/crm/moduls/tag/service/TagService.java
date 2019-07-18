package com.ctr.crm.moduls.tag.service;

import com.ctr.crm.moduls.tag.models.Tag;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;

/**
 * @author DoubleLi
 * @date  2019-04-19
 */
public interface  TagService {

    void searchPage(ResponseData responseData, PageMode pageMode, Tag tag);

    Tag get(String id);

    List<Tag> findList(Tag tag);

    List<Tag> findAllList();

    String insert(Tag tag);

    int insertBatch(List<Tag> tags);

    int update(Tag tag);

    boolean delete(String id);

}
