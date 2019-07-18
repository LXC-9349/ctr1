package com.ctr.crm.moduls.sales.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.service.DeptService;
import com.ctr.crm.moduls.sales.dao.SaleCaseDao;
import com.ctr.crm.moduls.sales.models.MySearch;
import com.ctr.crm.moduls.sales.models.SaleCase;
import com.ctr.crm.moduls.sales.models.SaleCaseLost;
import com.ctr.crm.moduls.sales.service.SaleCaseService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ctr.crm.commons.es.MemberBean;
import com.ctr.crm.commons.es.SearchClient;
import com.ctr.crm.commons.jdbc.YunhuJdbcOperations;
import com.ctr.crm.commons.rbac.AccessAuth;
import com.ctr.crm.commons.result.ResponsePage;
import com.ctr.crm.commons.utils.MemberUtils;
import com.ctr.crm.commons.utils.PojoUtils;
import com.ctr.crm.moduls.intentionality.models.Intentionality;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;

/**
 * 说明：
 *
 * @author eric
 * @date 2019年4月17日 下午3:59:05
 */
@Service("saleCaseService")
public class SaleCaseServiceImpl implements SaleCaseService {
    private static final Log log = LogFactory.getLog("saleCaseInfo");
    @Autowired
    private SaleCaseDao saleCaseDao;
    @Resource
    private DeptService deptService;
    @Resource
    private YunhuJdbcOperations crmJdbc;
    @Autowired
    private SearchClient searchClient;

    @Override
    public boolean isInSaleCase(Long memberId) {
        if (memberId == null) return false;
        return saleCaseDao.inSaleCase(memberId);
    }

    @Override
    public Integer isInSaleCase(String memberIds) {
        if (StringUtils.isBlank(memberIds)) return 0;
        return saleCaseDao.inSaleCaseMembers(memberIds);
    }

    @Override
    public SaleCase getByMemberId(Long memberId) {
        return saleCaseDao.getByMemberId(memberId);
    }

    @Override
    public Integer insert(SaleCase sc) {
        try {
            if (sc.getClassChangeTime() == null) {
                sc.setClassChangeTime(new Date());
            }
            Map<String, String> dataMap = PojoUtils.comparePojo(null, sc, "serialVersionUID");
            final String insertSql = PojoUtils.getInsertSQL(SaleCase.class.getSimpleName(), dataMap, "caseId");
            KeyHolder keyHolder = new GeneratedKeyHolder();
            crmJdbc.getNamedParameterJdbcOperations().update(insertSql, null, keyHolder);
            return keyHolder.getKey().intValue();
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public Boolean update(SaleCase saleCase) {
        if (saleCase == null)
            return false;
        if (saleCase.getCaseId() == null || saleCase.getMemberId() == null) {
            log.info("更新SaleCase失败，原因主键ID或会员ID为空");
            return false;
        }
        try {
            boolean success = saleCaseDao.update(saleCase);
            log.info("更新SaleCase成功，会员ID：" + saleCase.getMemberId());
            return success;
        } catch (Exception e) {
            log.info("更新SaleCase失败，会员ID：" + saleCase.getMemberId(), e);
            return false;
        }
    }

    @Override
    public void updateSaleCase(MemberBaseInfo baseInfo) {
        if (baseInfo == null || baseInfo.getMemberId() == null)
            return;
        SaleCase sc = getByMemberId(baseInfo.getMemberId());
        if (sc == null) return;
        BeanUtils.copyProperties(baseInfo, sc);
        update(sc);
    }

    @Override
    public void updateLastPhoneTime(Long memberId, Date startTime) {
        SaleCase sc = getByMemberId(memberId);
        if (sc != null) {
            sc.setLastPhoneTime(startTime);
            update(sc);
            //更新es
            MemberBean mb = new MemberBean(memberId);
            mb.setLastContactTime(startTime);
            searchClient.update(mb);
        }
    }

    @Override
    public void updateLastSmsTime(Long memberId, Date sendTime) {
        SaleCase sc = getByMemberId(memberId);
        if (sc != null) {
            sc.setLastSmsTime(sendTime);
            update(sc);
            //更新es
            MemberBean mb = new MemberBean(memberId);
            mb.setLastContactTime(sendTime);
            searchClient.update(mb);
        }
    }

    @Override
    public void updatelastContactTime(Long memberId, Date contactTime) {
        SaleCase sc = getByMemberId(memberId);
        if (sc != null) {
            sc.setLastContactTime(contactTime);
            update(sc);
            //更新es
            MemberBean mb = new MemberBean(memberId);
            mb.setLastContactTime(contactTime);
            searchClient.update(mb);
        }
    }

    @Override
    public List<SaleCase> findListBySales(List<Intentionality> intentionalityList) {
        String sql = PojoUtils.getSelectSql(SaleCase.class, "serialVersionUID", true, null,
                null);
        if (intentionalityList != null) {
            String caseClass = "";
            for (Intentionality intentionality : intentionalityList) {
                caseClass += intentionality.getCaseClass() + ",";
            }
            sql += " and caseClass in(" + caseClass.substring(0, caseClass.length() - 1) + ")";
        }
        return crmJdbc.query(sql, BeanPropertyRowMapper.newInstance(SaleCase.class));
    }

    @Override
    public Integer deleteByMemberIds(List<Long> recylingMemberIds) {
        String memberIds = String.join(",", recylingMemberIds.toString());
        StringBuffer sql = new StringBuffer("delete from SaleCase where memberId in(");
        sql.append(memberIds.substring(1, memberIds.length() - 1).replaceAll(" ", ""));
        sql.append(")");
        return crmJdbc.update(sql.toString());
    }

    @Override
    public Integer insertSaleCaseLost(SaleCaseLost saleCaseLost) {
        try {
            if (saleCaseLost.getLostTime() == null) {
                saleCaseLost.setLostTime(new Date());
            }
            Map<String, String> dataMap = PojoUtils.comparePojo(null, saleCaseLost, "serialVersionUID");
            final String insertSql = PojoUtils.getInsertSQL(SaleCase.class.getSimpleName(), dataMap, "lostId");
            KeyHolder keyHolder = new GeneratedKeyHolder();
            crmJdbc.getNamedParameterJdbcOperations().update(insertSql, null, keyHolder);
            return keyHolder.getKey().intValue();
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public ResponsePage<Map<String, Object>> query(MySearch search,
                                                   Worker currentWorker) {
        // 设置数据范围
        if (search == null) search = new MySearch();
        if (search.getDeptId() != null) {
            search.setSelectDept(deptService.selectCache(search.getDeptId()));
        }
        search.setRange(AccessAuth.rangeAuth(search.getDeptId(), currentWorker));
        PageHelper.startPage(search.getPage(), search.getPageSize(), "sc.allotTime desc");
        List<Map<String, Object>> result = saleCaseDao.search(search);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(result);
        ResponsePage<Map<String, Object>> page = new ResponsePage<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
        MemberUtils.maskPhone(pageInfo.getList(), "mobile", currentWorker);
        page.setList(pageInfo.getList());
        return page;
    }

    @Override
    public SaleCase getByMobile(String mobile) {
        return saleCaseDao.getByMobile(mobile);
    }
}
