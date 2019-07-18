package com.ctr.crm.api.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.ctr.crm.api.VipService;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.moduls.member.models.MemberVip;
import com.ctr.crm.moduls.member.service.MemberVipService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 功能描述: VIP定时任务
 *
 * @author: DoubleLi
 * @date: 2019/5/20 18:14
 */
@Component
@Service
@EnableAsync
public class VipServiceImpl implements VipService {
    private static final Log log = LogFactory.getLog("memberBaseInfo");

    @Autowired
    private MemberVipService memberVipService;

    /**
     * 功能描述: 合同服务过期处理
     * 凌晨0:10执行
     * 采用异步调用防止执行方法超时
     *
     * @param:
     * @return:
     * @author: DoubleLi
     */
    @Async
    @Override
    public void expired() {
        try {
            log.info("==========合同服务过期处理开始时间" + CommonUtils.serialObject(new Date()));
            MemberVip memberVip = new MemberVip();
            /** 服务中的合同*/
            memberVip.setStatus(0);
            List<MemberVip> list = memberVipService.findList(memberVip);
            if (list.isEmpty()) {
                return;
            }
            list.forEach(mv -> {
                /** 合同结束时间在当前时间之前*/
                if (mv.getEndTime().getTime() <= System.currentTimeMillis()) {
                    mv.setStatus(2);
                    mv.setRealEndTime(new Date());
                    memberVipService.update(mv);
                    log.info("服务合同结束memberId:"+mv.getMemberId()+" 结束时间:"+CommonUtils.serialObject(mv.getRealEndTime()));
                }
            });
        } catch (Exception e) {
            log.error("合同服务过期处理异常", e);
        } finally {
            log.info("********合同服务过期处理结束时间" + CommonUtils.serialObject(new Date()));
        }
    }

}