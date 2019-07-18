package com.ctr.crm.commons.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * 说明：
 * @author eric
 * @date 2019年4月10日 下午2:00:49
 */
@Component
public interface MemberRepository extends ElasticsearchRepository<MemberBean, Long> {

}
