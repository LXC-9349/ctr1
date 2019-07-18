package com.ctr.crm.api.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.ctr.crm.api.RecyclingMemberJobService;
import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.es.MemberBean;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.MemberUtils;
import com.ctr.crm.moduls.intentionality.models.Intentionality;
import com.ctr.crm.moduls.intentionality.service.IntentionalityService;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;
import com.ctr.crm.moduls.member.service.MemberService;
import com.ctr.crm.moduls.notice.models.Notice;
import com.ctr.crm.moduls.notice.service.NoticeService;
import com.ctr.crm.moduls.recyclebin.models.RecycleBin;
import com.ctr.crm.moduls.recyclebin.service.RecycleBinService;
import com.ctr.crm.moduls.sales.models.SaleCase;
import com.ctr.crm.moduls.sales.service.SaleCaseService;
import com.ctr.crm.moduls.sysrecyclingmemberconfig.models.SysRecyclingMemberConfig;
import com.ctr.crm.moduls.sysrecyclingmemberconfig.service.SysRecyclingMemberConfigService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.beans.Transient;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 功能描述: 客户收回
 *
 * @author: DoubleLi
 * @date: 2019/5/9 15:43
 */
@Component
@Service
@EnableAsync
public class RecyclingMemberJobServiceImpl implements RecyclingMemberJobService {
    private static final Log log = LogFactory.getLog("recycling");
    @Autowired
    private SysRecyclingMemberConfigService sysRecyclingMemberConfigService;
    @Autowired
    private IntentionalityService intentionalityService;
    @Autowired
    private SaleCaseService saleCaseService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private RecycleBinService recycleBinService;
    @Autowired
    private NoticeService noticeService;

    @Async
    @Override
    public void recyclingMember() {
        log.info("==========客户收回开始时间" + CommonUtils.serialObject(new Date()));
        try {
            SysRecyclingMemberConfig sysRecyclingMemberConfig = sysRecyclingMemberConfigService.get(null);
            /**回收站的资源达到指定天数后放回公海*/
            recyclingDelete(sysRecyclingMemberConfig);
            if (sysRecyclingMemberConfig == null || (sysRecyclingMemberConfig != null && sysRecyclingMemberConfig.getTswitch().equals(1))) {
                log.info("客户收回没有开启");
                return;
            }
            Intentionality intentionality = new Intentionality();
            /** 销售意向度*/
            intentionality.setType(1);
            List<Intentionality> intentionalityList = intentionalityService.findList(intentionality);
            if (intentionalityList == null || intentionalityList.size() == 0) {
                log.info("没有设置销售意向度");
                return;
            }
            /** 查找机会表*/
            List<SaleCase> saleCasesList = saleCaseService.findListBySales(intentionalityList);
            if (saleCasesList == null || saleCasesList.size() == 0) {
                log.info("没有可回收机会");
                return;
            }
            List<Long> memberIds = saleCasesList.stream()
                    .map(SaleCase::getMemberId).distinct()
                    .collect(Collectors.toList());
            String allMember = String.join(",", memberIds.toString());
            List<MemberBaseInfo> memberBaseInfos = memberService.searchByMemberIds(allMember.substring(1, allMember.length() - 1).replaceAll(" ", ""));
            List<Long> recyclingMemberIds = new ArrayList<>();
            Map<Long, String> reasonMap = new HashMap<>();
            /** 取出需要执行收回的资源*/
            ExecutorService pool = Executors.newCachedThreadPool();
            for (SaleCase saleCase : saleCasesList) {
                pool.submit(() -> getRecyclingMember(sysRecyclingMemberConfig, recyclingMemberIds, saleCase, reasonMap));
            }
            pool.shutdown();
            pool.awaitTermination(1, TimeUnit.HOURS);
            /** 执行完毕添加回收站*/
            //拼装数据
            List<RecycleBin> recycleBins = new ArrayList<>();
            List<MemberBaseInfo> updateMemberList = new ArrayList<>();//database
            List<MemberBean> updateMemberBeanList = new ArrayList<>();//es
            List<Notice> notices = new ArrayList<>();
            if (recyclingMemberIds.size() > 0) {
                recyclingMemberIds.forEach(memberId -> {
                    Optional<MemberBaseInfo> memberIdObject = memberBaseInfos.stream().filter(p -> p.getMemberId().equals(memberId)).
                            findFirst();
                    MemberBaseInfo memberBaseInfo = memberIdObject.get();
                    Optional<SaleCase> saleCaseIdObject = saleCasesList.stream().filter(p -> p.getMemberId().equals(memberId)).
                            findFirst();
                    SaleCase saleCase = saleCaseIdObject.get();
                    //存在回收站
                    MemberUtils.setMemberType(memberBaseInfo, Constants.member_type_isrecycling, Constants.YES);
                    //不存在机会库
                    MemberUtils.setMemberType(memberBaseInfo, Constants.member_type_insalecase, Constants.NO);
                    MemberBaseInfo mbi = new MemberBaseInfo(memberId, memberBaseInfo.getMemberType());
                    updateMemberList.add(mbi);
                    MemberBean mb = new MemberBean();
                    mb.setIsRecycling(true);
                    mb.setInSaleCase(false);
                    mb.setMemberId(memberId);
                    mb.setWorkerName("");
                    mb.setWorkerId(0);
                    mb.setQuitReason("系统自动回收");
                    mb.setQuitWorkerId(saleCase.getWorkerId());
                    mb.setQuitWorkerName(saleCase.getWorkerName());
                    updateMemberBeanList.add(mb);
                    RecycleBin recycleBin = new RecycleBin();
                    recycleBin.setRecycleWorker(Constants.DEFAULT_WORKER_ID);
                    recycleBin.setRecycleReason("系统自动收回," + reasonMap.get(saleCase.getMemberId()));
                    recycleBin.setRecycleTime(new Date());
                    BeanUtils.copyProperties(saleCase, recycleBin, "id");
                    recycleBins.add(recycleBin);
                    Notice notice = new Notice();
                    notice.setWorkerId(saleCase.getWorkerId());
                    notice.setWorkerName(saleCase.getWorkerName());
                    notice.setCreateTime(new Date());
                    notice.setType(1);
                    notice.setContent("客户ID:" + saleCase.getMemberId() + "客户名:" + memberBaseInfo.getTrueName() + ",已被系统自动回收," + reasonMap.get(saleCase.getMemberId()));
                    notice.setTitle("系统自动回收客户");
                    notices.add(notice);
                });
                log.info("系统添加至回收站" + recyclingMemberIds.toString());
                recycleBinService.insertBatch(recycleBins);//添加回收站
                memberService.updateMemberType(updateMemberList);//更新客户
                memberService.updateMemberBean(updateMemberBeanList);//更新es
                saleCaseService.deleteByMemberIds(recyclingMemberIds);//批量删除机会
                noticeService.insertBatch(notices);//添加通知
            }
        } catch (Exception e) {
            log.error("客户回收", e);
        } finally {
            log.info("**********客户收回结束时间" + CommonUtils.serialObject(new Date()));
        }
    }

    /**
     * 功能描述:
     * 回收站处理
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/10 16:41
     */
    private void recyclingDelete(SysRecyclingMemberConfig sysRecyclingMemberConfig) {
        if (sysRecyclingMemberConfig == null)
            return;
        Integer day = sysRecyclingMemberConfig.getRecycleDay();
        if (day == null)
            return;
        List<RecycleBin> recycleBins = recycleBinService.findAllList(new RecycleBin());
        if (recycleBins.size() == 0) return;
        List<MemberBaseInfo> updateMemberList = new ArrayList<>();//database
        List<MemberBean> updateMemberBeanList = new ArrayList<>();//es
        for (RecycleBin recycleBin : recycleBins) {
            //符合条件回收
            if (MemberUtils.diffDayNow(recycleBin.getRecycleTime()) > day) {
                //状态--更新客户信息
                MemberBaseInfo memberBaseInfo = memberService.get(MemberBaseInfo.class, recycleBin.getMemberId());
                //状态--回收站删除
                MemberUtils.setMemberType(memberBaseInfo, Constants.member_type_isrecycling, Constants.NO);
                //状态--未分配
                MemberUtils.setMemberType(memberBaseInfo, Constants.member_type_insalecase, Constants.NO);
                MemberBaseInfo mbi = new MemberBaseInfo(memberBaseInfo.getMemberId(), memberBaseInfo.getMemberType());
                updateMemberList.add(mbi);
                MemberBean mb = new MemberBean();
                //机会库
                mb.setIsRecycling(false);
                mb.setInSaleCase(false);
                mb.setMemberId(memberBaseInfo.getMemberId());
                updateMemberBeanList.add(mb);
                //删除回收站数据
                recycleBinService.delete(recycleBin.getId());
            }
        }
        memberService.updateMemberType(updateMemberList);//更新客户
        memberService.updateMemberBean(updateMemberBeanList);//更新es
    }

    private void getRecyclingMember(SysRecyclingMemberConfig sysRecyclingMemberConfig, List<Long> recylingMemberIds, SaleCase saleCase, Map<Long, String> resonMap) {
        /** 执行是否符合收回条件*/
        //当客户入库后，达到\r\n1~365以内整数\r\n天，从未发生有效联系时
        c1:
        if (sysRecyclingMemberConfig.getCondition1() != null && sysRecyclingMemberConfig.getCondition1() != 0) {
            Integer condition1 = sysRecyclingMemberConfig.getCondition1();
            //发生过联系
            if (saleCase.getLastContactTime() != null || saleCase.getLastPhoneTime() != null || saleCase.getLastSmsTime() != null) {
                break c1;
            }
            //超过天数 添加至回收站
            int day = MemberUtils.diffDayNow(saleCase.getAllotTime());
            if (day >= condition1.intValue()) {
                resonMap.put(saleCase.getMemberId(), "当客户入库后，达到" + day + "天,从未发生有效联系");
                recylingMemberIds.add(saleCase.getMemberId());
                return;
            }
        }
        //当客户入库后，距上次有效联系客户后，达到\r\n1~365以内整数\r\n天，从未发生有效联系时
        c2:
        if (sysRecyclingMemberConfig.getCondition2() != null && sysRecyclingMemberConfig.getCondition2() != 0) {
            if (StringUtils.isBlank(sysRecyclingMemberConfig.getContactInformations())) {
                log.error("未设置有效联系方式");
                break c2;
            }
            Integer condition2 = sysRecyclingMemberConfig.getCondition2();
            //加入回收站
            boolean fm = true;
            int day = 0;
            //有设置电话联系
            if (sysRecyclingMemberConfig.getContactInformations().indexOf("phone") != -1) {
                day = MemberUtils.diffDayNow(saleCase.getLastPhoneTime());
                if (day <= condition2.intValue()) {
                    fm = false;
                }
            }
            //有设置小计联系
            if (fm && sysRecyclingMemberConfig.getContactInformations().indexOf("notes") != -1) {
                day = MemberUtils.diffDayNow(saleCase.getLastContactTime());
                if (day <= condition2.intValue()) {
                    fm = false;
                }
            }
            //有设置短信联系
            if (fm && sysRecyclingMemberConfig.getContactInformations().indexOf("sms") != -1) {
                day = MemberUtils.diffDayNow(saleCase.getLastSmsTime());
                if (day <= condition2.intValue()) {
                    fm = false;
                }
            }
            if (fm) {
                resonMap.put(saleCase.getMemberId(), "当客户入库后,距上次有效联系客户后，达到" + day + "天,从未发生有效联系");
                recylingMemberIds.add(saleCase.getMemberId());
                return;
            }
        }
        //当客户入库后，距上次设定客户阶段后，达到 1~365以内整数
        c3:
        if (sysRecyclingMemberConfig.getCondition3() != null && sysRecyclingMemberConfig.getCondition3() != 0) {
            if (saleCase.getClassChangeTime() == null) {
                break c3;
            }
            //超过天数 添加至回收站
            int day = MemberUtils.diffDayNow(saleCase.getClassChangeTime());
            if (day >= sysRecyclingMemberConfig.getCondition3().intValue()) {
                resonMap.put(saleCase.getMemberId(), "当客户入库后，距上次设定客户阶段后，达到" + day + "天,从未发生有效联系");
                recylingMemberIds.add(saleCase.getMemberId());
            }
        }
    }
}