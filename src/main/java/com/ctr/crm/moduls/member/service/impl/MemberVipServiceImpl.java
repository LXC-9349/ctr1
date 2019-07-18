package com.ctr.crm.moduls.member.service.impl;

import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.member.dao.MemberVipDao;
import com.ctr.crm.moduls.member.models.MemberVip;
import com.ctr.crm.moduls.member.service.MemberVipService;
import com.ctr.crm.moduls.contract.service.ContractOrderService;
import com.ctr.crm.commons.es.MemberBean;
import com.ctr.crm.commons.es.SearchClient;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-05-20
 */
@Service("memberVipService")
public class MemberVipServiceImpl implements MemberVipService {

    @Autowired
    private MemberVipDao memberVipDao;
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private SearchClient searchClient;
    @Autowired
    @Lazy
    private ContractOrderService contractOrderService;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, MemberVip memberVip) {
        pageMode.setSqlFrom("select * from MemberVip");
        pageMode.setSqlWhere("deleted=0");
        if (memberVip.getMemberId() != null) {
            pageMode.setSqlWhere("memberId = '" + memberVip.getMemberId() + "'");
        }
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        pageMode.setOrderBy("order by (case when status=0 then 0 else 1 end),createTime desc");
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public MemberVip get(Integer id) {
        if (id == null) {
            return null;
        }
        return memberVipDao.get(id);
    }

    @Override
    public List<MemberVip> findList(MemberVip memberVip) {
        return memberVipDao.findList(memberVip);
    }

    @Override
    public List<MemberVip> findAllList() {
        return memberVipDao.findAllList();
    }

    @Override
    public int insert(MemberVip memberVip) {
        memberVip.setId(memberVipDao.getID());
        if (memberVip.getCreateTime() == null) {
            memberVip.setCreateTime(new Date());
        }
        return memberVipDao.insert(memberVip);
    }

    @Override
    public int insertBatch(List<MemberVip> memberVips) {
        return memberVipDao.insertBatch(memberVips);
    }

    @Override
    public int update(MemberVip memberVip) {
        //合同过期或服务完处理VIP
        if (memberVip.getStatus() != 0) {
            MemberBean memberBean = new MemberBean(memberVip.getMemberId());
            memberBean.setIsVip(false);
            searchClient.update(memberBean);
            //查询续约处理
            contractOrderService.renewVip(memberVip.getMemberId());
        }
        return memberVipDao.update(memberVip);
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) {
            return false;
        }
        return memberVipDao.delete(id);
    }

    @Override
    public MemberVip getByMemberId(Long memberId) {
        if (memberId == null) {
            return null;
        }
        return memberVipDao.getByMemberId(memberId);
    }

    @Override
    public boolean isVip(Long memberId) {
        MemberBean bean = searchClient.select(memberId);
        if (bean != null) {
            return bean.getIsVip();
        }
        MemberVip vip = getByMemberId(memberId);
        if (vip == null) return false;
        return vip.getStatus() == null || vip.getStatus() == 0;
    }
}
