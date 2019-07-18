package com.ctr.crm.commons.es;

import java.util.Arrays;
import java.util.Optional;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;
import com.ctr.crm.moduls.member.models.MemberObjectInfo;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Component;

import com.ctr.crm.commons.utils.BeanUtils;
import com.ctr.crm.commons.utils.CollectionUtils;

/**
 * 说明：
 *
 * @author eric
 * @date 2019年4月10日 下午1:58:14
 */
@Component
public class SearchClient {

    private final int default_page_size = 10;

    @Autowired
    private MemberRepository repository;

    private void insert(MemberBean bean) {
        repository.save(bean);
    }

    public void delete(Long memberId) {
        if (memberId == null) return;
        repository.deleteById(memberId);
    }
    
    public MemberBean select(Long memberId){
    	if(memberId == null) return null;
    	if(!repository.existsById(memberId))
    		return null;
    	Optional<MemberBean> optional = repository.findById(memberId);
        return optional.get();
    }

    public void update(MemberBean bean) {
        if (bean == null || bean.getMemberId() == null) return;
        boolean exists = repository.existsById(bean.getMemberId());
        if (!exists) {
            insert(bean);
            return;
        }
        Optional<MemberBean> optional = repository.findById(bean.getMemberId());
        MemberBean old = optional.get();
        BeanUtils.toUpate(old, bean);
        repository.save(old);
    }
    
    public void lost(Long memberId, Worker currentWorker, String reason){
    	if(memberId == null) return;
    	if(!repository.existsById(memberId)) return;
    	Optional<MemberBean> optional = repository.findById(memberId);
    	MemberBean bean = optional.get();
    	bean.setIsRecycling(true);
    	bean.setInSaleCase(false);
    	bean.setQuitWorkerId(currentWorker.getWorkerId());
    	bean.setQuitWorkerName(currentWorker.getWorkerName());
    	bean.setQuitReason(reason);
    	bean.setWorkerName(null);
    	bean.setWorkerId(null);
    	bean.setDeptId(null);
    	bean.setAllotTime(null);
    	bean.setCaseClass(null);
    	repository.save(bean);
    }
    
    public void blacklist(Long memberId, Worker currentWorker, String reason){
    	if(memberId == null) return;
    	if(!repository.existsById(memberId)) return;
    	Optional<MemberBean> optional = repository.findById(memberId);
    	MemberBean bean = optional.get();
    	bean.setIsBlacklist(true);
    	bean.setInSaleCase(false);
    	// 谁放弃的黑名单
    	bean.setQuitWorkerId(currentWorker.getWorkerId());
    	bean.setQuitWorkerName(currentWorker.getWorkerName());
    	bean.setQuitReason(reason);
    	bean.setWorkerName(null);
    	bean.setWorkerId(null);
    	bean.setDeptId(null);
    	bean.setAllotTime(null);
    	bean.setCaseClass(null);
    	repository.save(bean);
    }

    public void update(Long memberId, String phone) {
        if (memberId == null || phone == null) return;
        boolean exists = repository.existsById(memberId);
        if (!exists) {
            MemberBean bean = new MemberBean(memberId);
            bean.setMobiles(Arrays.asList(phone));
            insert(bean);
            return;
        }
        Optional<MemberBean> optional = repository.findById(memberId);
        MemberBean old = optional.get();
        if (old.getMobiles() == null) {
            old.setMobiles(Arrays.asList(phone));
        } else {
            old.setMobiles(CollectionUtils.addNotRepeat(old.getMobiles(), phone));
        }
        repository.save(old);
    }

    public void delete(Long memberId, String phone) {
        if (memberId == null || phone == null) return;
        boolean exists = repository.existsById(memberId);
        if (!exists) {
            return;
        }
        Optional<MemberBean> optional = repository.findById(memberId);
        MemberBean old = optional.get();
        if (old.getMobiles() == null) return;
        old.setMobiles(CollectionUtils.remove(old.getMobiles(), phone));
        repository.save(old);
    }

    /**
     * 客户搜索
     *
     * @param bean 搜索条件bean
     * @param page 第几页 ES从0开始
     * @param size 页大小
     * @return
     */
    public Page<MemberBean> search(MemberBeanSearch bean) {
    	Integer page = bean.getPage();
    	Integer size = bean.getPageSize();
        if (page == null) page = 0;
        if (page > 0) page -= 1;
        if (size == null) size = default_page_size;
        SearchQuery searchQuery = SearchQueryBuilder.build(bean, null, null, page, size);
        if(searchQuery == null) searchQuery = new NativeSearchQuery(new BoolQueryBuilder());
        return repository.search(searchQuery);
    }
    
    public Page<MemberBean> match(MemberBeanSearch search, MemberBaseInfo u, MemberObjectInfo o){
    	Integer page = search.getPage();
    	Integer size = search.getPageSize();
    	if (page == null) page = 0;
    	if (page > 0) page -= 1;
    	if (size == null) size = default_page_size;
    	SearchQuery searchQuery = SearchQueryBuilder.build(search, u, o, page, size);
    	if(searchQuery == null) searchQuery = new NativeSearchQuery(new BoolQueryBuilder());
    	return repository.search(searchQuery);
    }

}
