package com.ctr.crm.api.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.moduls.allocation.models.AllocationRule;
import com.ctr.crm.moduls.allocation.service.AllocationRuleService;
import com.ctr.crm.moduls.allot.service.AllotService;
import com.ctr.crm.api.AllotJobService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 功能描述: 自动分配定时任务
 *
 * @author: DoubleLi
 * @date: 2019/5/5 14:44
 */
@Component
@Service
@EnableAsync
public class AllotJobServiceImpl implements AllotJobService {
    private static final Log log = LogFactory.getLog("allot");
    @Autowired
    private AllotService allotService;
    @Autowired
    private AllocationRuleService allocationRuleService;

    /**
     * 功能描述: 销售定时分配任务
     * 根据分配规则自动分配
     * 凌晨2:30执行
     * 采用异步调用防止执行方法超时
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/5 14:44
     */
    @Async
    @Override
    public void allot() {
        try {
            log.info("==========销售自动分配开始时间" + CommonUtils.serialObject(new Date()));
            /** 每日初始化员工分配数据记录 */
            allotService.initWorker();
            log.info("每日初始化员工分配数据记录");
            List<AllocationRule> rules = allocationRuleService.findList(new AllocationRule(1));
            if (rules.size() == 0) {
                log.warn("没有配置分配规则");
                return;
            }
            /** 初始化 待分配客户存入 */
            Long memberCount = allotService.initMember();
            log.info("初始化资源成功,可分配资源共:" + memberCount);
            if (memberCount == 0L) {
                /** 没有资源无须分配*/
                return;
            }
            /** 根据分配规则初始化员工游标表 */
            rules.forEach(r -> {
                allotService.initAllotCursor(r);
            });
            //休眠一秒防止分配更新时间与创建时间一样
            Thread.sleep(1000);
            /** 开始分配 */
            allotService.allot(rules);
        } catch (Exception e) {
            log.error("定时分配任务异常", e);
        } finally {
            log.info("********销售自动分配结束时间" + CommonUtils.serialObject(new Date()));
        }
    }

}