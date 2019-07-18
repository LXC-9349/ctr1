package com.ctr.crm.moduls.tag.service;

import com.ctr.crm.moduls.tag.models.TagGroup;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import java.util.List;

/**
 * @author DoubleLi
 * @date  2019-04-19
 */
public interface  TagGroupService {

    void searchPage(ResponseData responseData, PageMode pageMode, TagGroup tagGroup);

    TagGroup get(String id);

    List<TagGroup> findList(TagGroup tagGroup);

    List<TagGroup> findAllList(TagGroup tagGroup);

    String insert(TagGroup tagGroup);

    int insertBatch(List<TagGroup> tagGroups);

    int update(TagGroup tagGroup);

    boolean delete(String id);

}
