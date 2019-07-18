package com.ctr.crm.moduls.invitetoshop.service.impl;

import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.invitetoshop.dao.InviteToShopDao;
import com.ctr.crm.moduls.invitetoshop.models.InviteToShop;
import com.ctr.crm.moduls.invitetoshop.service.InviteToShopService;
import com.ctr.crm.commons.utils.MemberUtils;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author DoubleLi
 * @date 2019-06-27
 */
@Service("inviteToShopService")
public class InviteToShopServiceImpl implements InviteToShopService {

    @Autowired
    private InviteToShopDao inviteToShopDao;
    @Autowired
    private BaseDao baseDao;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, Map<String, String> params) {
        pageMode.setSqlFrom("select its.*,mb.trueName,mb.mobile,mb.sex,mb.salary,w.workerName from InviteToShop its left join MemberBaseInfo mb on its.memberId = mb.memberId left join Worker w on its.inviteWorkerId=w.workerId");
        if (StringUtils.isNotBlank(params.get("memberId"))) {
            pageMode.setSqlWhere("its.memberId= '" + params.get("memberId") + "'");
        }
        if (StringUtils.isNotBlank(params.get("inviteDateA"))) {
            pageMode.setSqlWhere("its.inviteDate >='" + params.get("inviteDateA") + "'");
        }
        if (StringUtils.isNotBlank(params.get("inviteDateB"))) {
            pageMode.setSqlWhere("its.inviteDate < DATE_ADD('" + params.get("inviteDateB") + "',INTERVAL 1 DAY)");
        }
        if (StringUtils.isNotBlank(params.get("mobile"))) {
            pageMode.setSqlWhere("mb.mobile= '" + params.get("mobile") + "'");
        }
        if (StringUtils.isNotBlank(params.get("sex"))) {
            pageMode.setSqlWhere("mb.sex= '" + params.get("sex") + "'");
        }
        if (StringUtils.isNotBlank(params.get("shopTimeA"))) {
            pageMode.setSqlWhere("its.shopTime >='" + params.get("shopTimeA") + "'");
        }
        if (StringUtils.isNotBlank(params.get("shopTimeB"))) {
            pageMode.setSqlWhere("its.shopTime < DATE_ADD('" + params.get("shopTimeB") + "',INTERVAL 1 DAY)");
        }
        if (StringUtils.isNotBlank(params.get("workerId"))) {
            pageMode.setSqlWhere("its.inviteWorkerId ='" + params.get("workerId") + "'");
        } else if (StringUtils.isNotBlank(params.get("workerIds"))) {
            pageMode.setSqlWhere("w.workerId in(" + params.get("workerIds") + ")");
        }
        if (StringUtils.isNotBlank(params.get("shopStatus"))) {
            pageMode.setSqlWhere("its.shopStatus ='" + params.get("shopStatus") + "'");
        }
        if (StringUtils.isNotBlank(params.get("inviteType"))) {
            pageMode.setSqlWhere("its.inviteType ='" + params.get("inviteType") + "'");
        }
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        pageMode.setOrderBy("order by its.createTime desc");
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        boolean mask = MemberUtils.mask(CurrentWorkerLocalCache.getCurrentWorker());
        data.forEach(da -> {
            if (da.get("mobile") != null)
                da.put("mobile", MemberUtils.maskPhone(da.get("mobile").toString(), mask));
        });
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public InviteToShop get(Integer id) {
        if (id == null) {
            return null;
        }
        return inviteToShopDao.get(id);
    }

    @Override
    public List<InviteToShop> findList(InviteToShop inviteToShop) {
        return inviteToShopDao.findList(inviteToShop);
    }

    @Override
    public List<InviteToShop> findAllList(InviteToShop inviteToShop) {
        return inviteToShopDao.findAllList(inviteToShop);
    }

    @Override
    public int insert(InviteToShop inviteToShop) {
        inviteToShop.setId(inviteToShopDao.getID());
        if (inviteToShop.getCreateTime() == null) {
            inviteToShop.setCreateTime(new Date());
        }
        return inviteToShopDao.insert(inviteToShop);
    }

    @Override
    public int insertBatch(List<InviteToShop> inviteToShops) {
        return inviteToShopDao.insertBatch(inviteToShops);
    }

    @Override
    public int update(InviteToShop inviteToShop) {
        return inviteToShopDao.update(inviteToShop);
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) {
            return false;
        }
        return inviteToShopDao.delete(id);
    }

    @Override
    public List<InviteToShop> memberIdByRecord(Long memberId, Integer shopStatus) {
        if (memberId == null) {
            return null;
        }
        return inviteToShopDao.findList(new InviteToShop(memberId, shopStatus));
    }
}
