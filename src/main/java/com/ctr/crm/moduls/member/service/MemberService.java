package com.ctr.crm.moduls.member.service;

import java.util.List;
import java.util.Map;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.tag.models.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.ctr.crm.commons.es.MemberBean;
import com.ctr.crm.commons.es.MemberBeanSearch;
import com.ctr.crm.commons.result.ResponsePage;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;
import com.ctr.crm.moduls.member.models.MemberInfo;
import com.ctr.crm.moduls.member.models.MemberObjectInfo;

/**
 * 说明：
 *
 * @author eric
 * @date 2019年4月11日 下午4:29:55
 */
public interface MemberService {

    /**
     * 取数据库最大的客户ID
     *
     * @return
     */
    public Long getMaxMemberId();

    <T> T get(Class<T> clazz, Long memberId);

    /**
     * 新建客户
     *
     * @param baseInfo      客户信息
     * @param objectInfo    择偶信息
     * @param currentWorker 操作人
     * @return
     */
    String insert(MemberBaseInfo baseInfo, MemberObjectInfo objectInfo, Worker currentWorker);

    String update(MemberBaseInfo baseInfo, MemberObjectInfo objectInfo, Worker currentWorker);
    String update(MemberBaseInfo baseInfo);
    String update(MemberObjectInfo objectInfo);
    /**
     * 更新才俊佳丽标识
     * @param memberId
     * @param b true为是 false为否
     */
    void handsome(Long memberId, boolean b);
    boolean updateHeadurl(MultipartFile file, Long memberId, Worker currentWorker);

    Page<MemberBean> search(MemberBeanSearch memberBean, Worker currentWorker);
    ResponsePage<MemberBean> match(MemberBeanSearch search, MemberBaseInfo u, MemberObjectInfo o);
    
    Map<String, Object> importData(List<MemberInfo> memberList, Worker currentWorker);

    List<MemberBaseInfo> searchByMemberIds(String memberIds);

    /**
     * 功能描述:
     * 批量更新客户类型
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/10 14:47
     */
    Integer updateMemberType(List<MemberBaseInfo> updateMemberList);

    /**
     * 功能描述:
     * 批量更新es客户信息
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/10 14:48
     */
    void updateMemberBean(List<MemberBean> updateMemberBeanList);
    
    void insertMemberTag(Long memberId, String[] tagIds);
    void deleteMemberTag(Long memberId, String tagId);
    List<Tag> getMemberTag(Long memberId);
    
}

