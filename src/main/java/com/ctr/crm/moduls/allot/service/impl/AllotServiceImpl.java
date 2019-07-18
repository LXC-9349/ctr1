package com.ctr.crm.moduls.allot.service.impl;

import com.ctr.crm.moduls.allot.models.*;
import com.ctr.crm.moduls.allot.service.*;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.sales.models.SaleCase;
import com.ctr.crm.moduls.sales.models.SaleCaseLost;
import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.es.MemberBean;
import com.ctr.crm.commons.es.SearchClient;
import com.ctr.crm.commons.jdbc.YunhuJdbcOperations;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.MemberUtils;
import com.ctr.crm.moduls.allocation.models.AllocationCondition;
import com.ctr.crm.moduls.allocation.models.AllocationRule;
import com.ctr.crm.moduls.allocation.service.AllocationConditionService;
import com.ctr.crm.moduls.globalsetting.service.GlobalSettingService;
import com.ctr.crm.moduls.intentionality.models.Intentionality;
import com.ctr.crm.moduls.intentionality.service.IntentionalityService;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;
import com.ctr.crm.moduls.member.service.MemberService;
import com.ctr.crm.moduls.notice.models.Notice;
import com.ctr.crm.moduls.notice.service.NoticeService;
import com.ctr.crm.moduls.recyclebin.models.RecycleBin;
import com.ctr.crm.moduls.recyclebin.service.RecycleBinService;
import com.ctr.crm.moduls.sales.service.SaleCaseService;
import com.ctr.crm.moduls.syscirculationconfig.models.SysCirculationConfig;
import com.ctr.crm.moduls.sysdict.service.SysDictService;
import com.ctr.crm.moduls.syslog.models.SysLog;
import com.ctr.crm.moduls.syslog.service.SysLogService;
import com.yunhus.redisclient.RedisProxy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author DoubleLi
 * @date 2019-05-05
 */
@Service("allotService")
public class AllotServiceImpl implements AllotService {

    private static final Log log = LogFactory.getLog("allot");

    @Resource
    private YunhuJdbcOperations crmJdbc;
    @Autowired
    private AllotCursorService allotCursorService;
    @Autowired
    private AllocationConditionService allocationConditionService;
    @Resource
    private MemberService memberService;
    @Resource
    private SaleCaseService saleCaseService;
    @Autowired
    @Lazy
    private SysLogService sysLogService;
    @Autowired
    @Lazy
    private SysDictService sysDictService;
    @Autowired
    private AllotHistoryService allotHistoryService;
    @Autowired
    private SearchClient searchClient;
    private RedisProxy redisClient = RedisProxy.getInstance();
    @Autowired
    private AllotWorkerService allotWorkerService;
    @Autowired
    private IntentionalityService intentionalityService;
    @Autowired
    private AdjustHistoryService adjustHistoryService;
    @Autowired
    private RecycleBinService recycleBinService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private GlobalSettingService globalSettingService;
    /**
     * 自动分配 begin==============================================================================================
     */

    /**
     * 功能描述: 销售自动分配--填充资源至临时表
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/8 14:40
     */
    @Override
    public Long initMember() {
        /** 清空*/
        crmJdbc.update("delete from AllotMember");
        /** 未分配加入临时表 未分配的非vip非回收站回收*/
        crmJdbc.update("INSERT into AllotMember(memberId,nickName,trueName,mobile,wechatId,headurl,createTime,creator,companyId,memberType,birthday,sex,marriage,height,weight,education,animals,constellation,bloodtype,occupation,salary,body,house,car,children,wantChildren,sibling,parents,workCity,homeTown,nation,smoking,drinking,field1,field2,field3,field4,field5,field6,field7,field8,field9,field10) select memberId,nickName,trueName,mobile,wechatId,headurl,createTime,creator,companyId,memberType,birthday,sex,marriage,height,weight,education,animals,constellation,bloodtype,occupation,salary,body,house,car,children,wantChildren,sibling,parents,workCity,homeTown,nation,smoking,drinking,field1,field2,field3,field4,field5,field6,field7,field8,field9,field10 from MemberBaseInfo m where NOT EXISTS(select 1 from SaleCase sc where sc.memberId=m.memberId or sc.mobile=m.mobile) and  SUBSTRING(memberType,4,1)=0 and SUBSTRING(memberType,6,1)=0 and SUBSTRING(memberType,3,1)=0 and SUBSTRING(memberType,5,1)=0 ");
        return crmJdbc.queryForObject("select count(1) from AllotMember", Long.class);
    }

    /**
     * 功能描述: 销售自动分配--初始化库容
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/8 14:41
     */
    @Override
    public void initWorker() {
        /** 今日分配归0,总库容和每日最大分配同步*/
        crmJdbc.update("update AllotWorker aw,Worker w set alrAllotDay =0,gainNum=0,aw.maxAllot=w.allCount,aw.maxAllotTheDay=ifnull(w.maxAllotTheDay,0) where aw.workerId=w.workerId");
        /**新添加用户初始化*/
        crmJdbc.update("INSERT into  AllotWorker(workerId,maxAllot,maxAllotTheDay,hasAllot,alrAllotDay,createTime) select workerId,allCount,ifnull(maxAllotTheDay,0),0,0,CURRENT_TIMESTAMP from Worker w where not EXISTS(select 1 from AllotWorker aw where aw.workerId=w.workerId) and workerStatus<>1");
        log.info("初始化员工分配数据记录");
    }

    @Override
    public void updateWorkerAllot(Integer workerId) {
        crmJdbc.update("update AllotWorker aw,Worker w set aw.maxAllot=w.allCount,aw.maxAllotTheDay=ifnull(w.maxAllotTheDay,0) where aw.workerId=w.workerId and w.workerId=?", workerId);
    }

    /**
     * 功能描述: 销售自动分配--游标表初始化
     * 根据updateTime排序来选择游标分配
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/5 16:41
     */
    @Override
    public void initAllotCursor(AllocationRule r) {
        /** 销售人员*/
        String[] wids = r.getWorkerIds().split(",");
        int way = r.getWay();//1平均分配2累计分配 默认1
        List<AllotCursor> las = allotCursorService.findByRuleId(r.getId());
        if (las.isEmpty()) {
            /** 游标不存在*/
            for (String wid : wids) {
                Integer workerId = CommonUtils.evalInteger(wid);
                if (workerId != null)
                    allotCursorService.insert(new AllotCursor(r.getId(), workerId));
            }
        } else {
            /** 游标存在*/
            crmJdbc.update("delete from AllotCursor where ruleId=?", r.getId());
            if (way == 1) {
                /**平均分配*/
                for (String wid : wids) {
                    Integer workerId = CommonUtils.evalInteger(wid);
                    if (workerId != null)
                        allotCursorService.insert(new AllotCursor(r.getId(), workerId));
                }
            } else {
                /** 累计分配*/
                //Long lastUpdateTime = null;
                for (String wid : wids) {
                    Integer workerId = CommonUtils.evalInteger(wid);
                    if (workerId == null)
                        continue;
                    AllotCursor ac = null;//存在的游标
                    for (AllotCursor la : las) {
                        if (workerId.equals(la.getWorkerId())) {
                            ac = la;
                            break;
                        }
                    }
                    if (ac != null) {
                        allotCursorService.insert(ac);
                        //lastUpdateTime = ac.getUpdateTime();
                    } else {
                        AllotCursor newac = new AllotCursor(r.getId(), workerId);
                        /*if (lastUpdateTime == null) {
                         *//** 游标第一处理人在昨日分配不存在 根据规则创建时间将她放置第一位*//*
                            lastUpdateTime = newac.getCreatetime().getTime();
                        } else {
                            *//** 添加1毫秒放置上一处理人后面*//*
                            lastUpdateTime = lastUpdateTime + 1L;
                        }*/
                        newac.setUpdateTime(System.currentTimeMillis());
                        allotCursorService.insert(newac);
                    }
                }
            }
        }
    }

    /**
     * 功能描述: 销售自动分配--执行分配
     * 1、查找对应分配规则
     * 2、根据规则找出合适客户资源
     * 3、给资源找到规则里面配置的合适销售
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/6 16:03
     */
    @Override
    public void allot(List<AllocationRule> rules) {
        Integer caseClass = intentionalityService.getByTypeFirst(1).getCaseClass();
        ExecutorService executor = Executors.newCachedThreadPool();
        /** 规则轮循分配*/
        for (AllocationRule rule : rules) {
            /** 取出对应分配游标*/
            List<AllotCursor> acs = allotCursorService.findByRuleId(rule.getId());
            if (acs.isEmpty()) {
                log.warn(rule.getId() + "分配规则，分配销售游标不存在无法分配");
                continue;
            }
            /***  取出符合条件资源*/
            List<AllocationCondition> conds = allocationConditionService.findList(new AllocationCondition(rule.getId()));
            String wheresql = "";
            if (!conds.isEmpty()) {
                wheresql = getWhereSql(conds);
            }
            /** 查找待分配资源*/
            List<AllotMember> ams = crmJdbc.query("select  * from AllotMember where 1=1 " + wheresql, BeanPropertyRowMapper.newInstance(AllotMember.class));
            if (ams.size() == 0) {
                log.warn(rule.getId() + "分配规则,没有符合规则的资源进行分配");
                continue;
            }
            /** 开始分配*/
            for (AllotMember allotMember : ams) {
                /** 取下一合适处理销售，并更新销售库容*/
                Worker worker = allotCursorService.findNext(rule.getId(), acs.size(), 0);
                if (worker == null) {
                    log.warn(rule.getId() + "当前分配规则没有合适的销售进行分配");
                    break;
                }
                executor.submit(() -> {
                    /** 用于添加销售机会，分配历史，修改客户信息,通知*/
                    updateMemberAndSaleCase(allotMember, worker, caseClass);
                });
                /** 分配成功删掉资源临时数据*/
                crmJdbc.update("delete from AllotMember where memberId=?", allotMember.getMemberId());
                String logId = sysLogService.insert(new SysLog(allotMember.getMemberId().toString(), worker.getWorkerId().toString(), null, null, "客户id:" + allotMember.getMemberId() + " 客户名:" + allotMember.getTrueName() + " 分配给员工ID:" + worker.getWorkerId() + " 员工名:" + worker.getWorkerName(), sysDictService.findValue("log_type", "自动分配"), Constants.DEFAULT_WORKER_ID, sysDictService.findValue("log_action", "分配")));
                log.info("客户资源分配成功客户id:" + allotMember.getMemberId() + " ,销售workerId:" + worker.getWorkerId() + " ,时间:" + CommonUtils.serialObject(new Date()) + " 日志ID" + logId);
            }
        }
        executor.shutdown();
    }

    /**
     * 功能描述:
     * 销售自动分配--添加销售机会和修改客户类型
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/6 16:05
     */
    private void updateMemberAndSaleCase(AllotMember allotMember, Worker worker, Integer caseClass) {
        /** 添加至销售机会*/
        log.info("当前分配资源:" + allotMember);
        try {
            Date now = new Date();
            SaleCase sc = MemberUtils.converSaleCase(allotMember);
            sc.setCreateTime(now);
            sc.setAllotTime(now);
            sc.setAllotType("sales_auto_allot");
            sc.setWorkerDept(worker.getDeptId());
            sc.setWorkerId(worker.getWorkerId());
            sc.setWorkerName(worker.getWorkerName());
            sc.setNextContactTime(null);
            sc.setCaseClass(caseClass);
            Integer insertSaleCase = saleCaseService.insert(sc);
            if (insertSaleCase == null || insertSaleCase.intValue() < 1) {
                log.error("添加销售机会信息失败，memberId" + sc);
            }
            /**添加至分配历史*/
            AllotHistory ah = new AllotHistory(allotMember.getMemberId(), now, "sales_auto_allot", worker.getWorkerId(), worker.getWorkerName(), worker.getDeptId(), 1, allotMember.getMobile(), worker.getCompanyId());
            if (!allotHistoryService.insert(ah)) {
                log.error("添加至分配历史失败" + ah);
            }
            /** 更新客户信息*/
            // 更新是否在库标识位
            MemberBaseInfo memberBaseInfo = new MemberBaseInfo(allotMember.getMemberId(), allotMember.getMemberType());
            MemberUtils.setMemberType(memberBaseInfo, Constants.member_type_insalecase, Constants.YES);
            // 更新是否分配过标识位
            MemberUtils.setMemberType(memberBaseInfo, Constants.member_type_alloted, Constants.YES);
            //更新资源类型
            memberService.update(memberBaseInfo, null, null);
            //更新es
            MemberBean mb = new MemberBean();
            mb.setMemberId(memberBaseInfo.getMemberId());
            mb.setInSaleCase(true);
            mb.setCaseClass(caseClass);
            mb.setWorkerId(worker.getWorkerId());
            mb.setWorkerName(worker.getWorkerName());
            mb.setDeptId(worker.getDeptId());
            mb.setAllotTime(new Date());
            searchClient.update(mb);
            /** 通知*/
            Notice notice = new Notice();
            notice.setWorkerId(worker.getWorkerId());
            notice.setWorkerName(worker.getWorkerName());
            notice.setCreateTime(new Date());
            notice.setType(1);
            notice.setContent("客户ID:" + allotMember.getMemberId() + " 客户名:" + allotMember.getTrueName() + ",已被系统自动分配给您");
            notice.setTitle("系统自动分配客户");
            noticeService.insert(notice);
        } catch (Exception e) {
            log.error("销售自动分配", e);
        }
    }

    /**
     * 功能描述:
     * 组装资源过滤条件sql
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/6 16:07
     */
    private String getWhereSql(List<AllocationCondition> conds) {
        StringBuffer sql = new StringBuffer(" ");
        conds.stream().sorted((a1, a2) -> a1.getPosition().compareTo(a2.getPosition()));
        Map<Integer, List<AllocationCondition>> alcMap = conds.stream().collect(Collectors.groupingBy(AllocationCondition::getRow));
        for (Integer key : alcMap.keySet()) {
            /**分行条件*/
            List<AllocationCondition> rowconds = alcMap.get(key);
            sql.append(" and ( ");
            for (AllocationCondition rowcond : rowconds) {
                /*and,or,null排第一*/
                if (rowcond.getConnect().equals("and") || rowcond.getConnect().equals("or"))
                    sql.append(rowcond.getConnect()).append(" ");
                /*1区间值用"-"隔开;2包含值,隔开*/
                if (rowcond.getType() == 1L) {
                    String vals[] = null;
                    try {
                        vals = rowcond.getFiledValue().split("-");
                    } catch (Exception e) {
                        log.error("筛选条件错误：" + rowcond.getId(), e);
                    }
                    if (vals == null) {
                        return null;
                    }
                    sql.append(rowcond.getFiled()).append(">='").append(vals[0]).append("' ");
                    sql.append("and").append(rowcond.getFiled()).append("<='").append(vals[1]).append("' ");
                } else if (rowcond.getType() == 2L) {
                    String vals[] = null;
                    try {
                        vals = rowcond.getFiledValue().split(",");
                    } catch (Exception e) {
                        log.error("筛选条件错误：" + rowcond.getId(), e);
                    }
                    if (vals == null) {
                        return null;
                    }
                    StringBuffer inwhere = new StringBuffer();
                    for (String val : vals) {
                        inwhere.append("'").append(val).append("',");
                    }
                    inwhere.deleteCharAt(inwhere.length() - 1);
                    sql.append(rowcond.getFiled()).append(" in (").append(inwhere).append(") ");
                }
            }
            sql.append(" )");
        }
        return sql.toString();
    }

    /**
     * 自动分配 end================================================================================================
     *
     */


    /**
     *  手动分配/调配 begin=============================================================================================
     */

    /**
     * 功能描述: 销售分配
     *
     * @param: isCapacity是否操作库容
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/9 10:43
     */
    @Override
    public Map<String, Object> salesAllot(List<MemberBaseInfo> memberList, List<Worker> workerList, Worker currWorker, boolean isCapacity) {
        log.info("==========销售分配开始时间" + CommonUtils.serialObject(new Date()));
        List<Long> allIds = new ArrayList<>();
        memberList.forEach(m -> {
            allIds.add(m.getMemberId());
        });
        Map<String, Object> map = new HashMap<>();
        map.put("success", 0);
        map.put("fail", allIds.size());
        /** 并发处理*/
        String key = "salesMember";
        concurrentHandle(memberList, key);
        if (memberList.size() == 0) {
            log.info("选择的资源被占用无法分配");
            map.put("failReason", "选择的资源被占用无法分配");
            return map;
        }
        /**新添加用户初始化库容*/
        crmJdbc.update("INSERT into  AllotWorker(workerId,maxAllot,maxAllotTheDay,hasAllot,alrAllotDay,createTime) select workerId,allCount,ifnull(maxAllotTheDay,0),0,0,CURRENT_TIMESTAMP from Worker w where not EXISTS(select 1 from AllotWorker aw where aw.workerId=w.workerId)");
        /**初始化游标*/
        Map<Long, Worker> workerMap = new ConcurrentHashMap<>(workerList.size());
        putWorker(workerList, workerMap);
        /** 销售意向度*/
        Intentionality intentionality = intentionalityService.getByTypeFirst(1);
        if (intentionality == null) {
            concurrentDeleteRedis(key, memberList.stream().mapToLong(m -> m.getMemberId()).toArray());
            map.put("failReason", "销售意向度未设置");
            log.info("销售意向度未设置");
            return map;
        }
        Integer caseClass = intentionality.getCaseClass();
        /** 黑名单*/
        List<Long> blackIds = new ArrayList<>();
        /** 已在库*/
        List<Long> inSaleCaseIds = new ArrayList<>();
        /** 不是vip*/
        List<Long> isNotVipIds = new ArrayList<>();
        /** 不在库*/
        List<Long> isNotInSaleCaseIds = new ArrayList<>();
        /** 成功*/
        List<Long> successIds = new ArrayList<>();
        for (MemberBaseInfo memberBaseInfo : memberList) {
            //0正常 1黑名单 2已在库 3不在库 4 不是vip
            int res = checkMember(memberBaseInfo, Constants.NO, false);
            if (res > 0) {
                putIds(blackIds, inSaleCaseIds, isNotVipIds, isNotInSaleCaseIds, memberBaseInfo, res);
                concurrentDeleteRedis(key, memberBaseInfo.getMemberId());
                continue;
            }
            /** 取下一合适处理销售，并更新销售库容*/
            Worker worker = workerNext(workerMap, 0, isCapacity);
            if (worker == null) {
                concurrentDeleteRedis(key, memberList.stream().mapToLong(m -> m.getMemberId()).toArray());
                log.error("没有合适的销售进行分配资源");
                map.put("failReason", "选择的销售库容已满，无法进行分配资源");
                break;
            }
            /** 用于添加销售机会，分配历史，修改客户信息,添加通知*/
            updateMemberAndSaleCase(memberBaseInfo, worker, caseClass, currWorker, "sales_allot");
            String logId = sysLogService.insert(new SysLog(memberBaseInfo.getMemberId().toString(), worker.getWorkerId().toString(), "操作人:" + currWorker.getWorkerId(), currWorker.getWorkerId().toString(), "客户id:" + memberBaseInfo.getMemberId() + " 客户名:" + memberBaseInfo.getTrueName() + " 分配给员工ID:" + worker.getWorkerId() + " 员工名:" + worker.getWorkerName(), sysDictService.findValue("log_type", "销售分配"), currWorker.getWorkerId(), sysDictService.findValue("log_action", "分配")));
            log.info("客户资源分配成功客户id:" + memberBaseInfo.getMemberId() + " ,销售workerId:" + worker.getWorkerId() + " ,时间:" + CommonUtils.serialObject(new Date()) + " 日志ID" + logId);
            successIds.add(memberBaseInfo.getMemberId());
            /** 删掉redis*/
            concurrentDeleteRedis(key, memberBaseInfo.getMemberId());
        }
        putMap(allIds, map, blackIds, inSaleCaseIds, isNotVipIds, isNotInSaleCaseIds, successIds);
        log.info("==========销售分配结束时间" + CommonUtils.serialObject(new Date()));
        return map;
    }


    /**
     * 功能描述: 红娘分配
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/8 14:43
     */
    @Override
    public Map<String, Object> matchmakerAllot(List<MemberBaseInfo> memberList, List<Worker> workerList, Worker currWorker) {
        log.info("==========红娘分配开始时间" + CommonUtils.serialObject(new Date()));
        List<Long> allIds = new ArrayList<>();
        memberList.forEach(m -> {
            allIds.add(m.getMemberId());
        });
        Map<String, Object> map = new HashMap<>();
        map.put("success", 0);
        map.put("fail", allIds.size());
        /** 并发处理*/
        String key = "matchmakerMember";
        concurrentHandle(memberList, key);
        if (memberList.size() == 0) {
            log.info("选择的资源被占用无法分配");
            map.put("failReason", "选择的资源被占用无法分配");
            return map;
        }
        /**新添加用户初始化库容*/
        crmJdbc.update("INSERT into  AllotWorker(workerId,maxAllot,maxAllotTheDay,hasAllot,alrAllotDay,createTime) select workerId,allCount,ifnull(maxAllotTheDay,0),0,0,CURRENT_TIMESTAMP from Worker w where not EXISTS(select 1 from AllotWorker aw where aw.workerId=w.workerId)");
        /**初始化游标*/
        Map<Long, Worker> workerMap = new ConcurrentHashMap<>(workerList.size());
        putWorker(workerList, workerMap);
        /** 红娘意向度*/
        Intentionality intentionality = intentionalityService.getByTypeFirst(2);
        if (intentionality == null) {
            concurrentDeleteRedis(key, memberList.stream().mapToLong(m -> m.getMemberId()).toArray());
            map.put("failReason", "红娘意向度未设置");
            log.info("红娘意向度未设置");
            return map;
        }
        Integer caseClass = intentionality.getCaseClass();
        /** 黑名单*/
        List<Long> blackIds = new ArrayList<>();
        /** 已在库*/
        List<Long> inSaleCaseIds = new ArrayList<>();
        /** 不是vip*/
        List<Long> isNotVipIds = new ArrayList<>();
        /** 不在库*/
        List<Long> isNotInSaleCaseIds = new ArrayList<>();
        /** 成功*/
        List<Long> successIds = new ArrayList<>();
        for (MemberBaseInfo memberBaseInfo : memberList) {
            int res = checkMember(memberBaseInfo, Constants.NO, true);
            if (res > 0) {
                putIds(blackIds, inSaleCaseIds, isNotVipIds, isNotInSaleCaseIds, memberBaseInfo, res);
                concurrentDeleteRedis(key, memberBaseInfo.getMemberId());
                continue;
            }
            /** 取下一合适处理红娘，并更新红娘库容*/
            Worker worker = workerNext(workerMap, 0, false);
            if (worker == null) {
                concurrentDeleteRedis(key, memberList.stream().mapToLong(m -> m.getMemberId()).toArray());
                log.error("没有合适的红娘进行分配资源");
                map.put("failReason", "选择的红娘库容已满，无法进行分配资源");
                break;
            }
            /** 用于添加销售机会，分配历史，修改客户信息，添加通知*/
            updateMemberAndSaleCase(memberBaseInfo, worker, caseClass, currWorker, "matchmaker_allot");
            String logId = sysLogService.insert(new SysLog(memberBaseInfo.getMemberId().toString(), worker.getWorkerId().toString(), "操作人:" + currWorker.getWorkerId(), currWorker.getWorkerId().toString(), "客户id:" + memberBaseInfo.getMemberId() + " 客户名:" + memberBaseInfo.getTrueName() + " 分配给员工ID:" + worker.getWorkerId() + " 员工名:" + worker.getWorkerName(), sysDictService.findValue("log_type", "红娘分配"), currWorker.getWorkerId(), sysDictService.findValue("log_action", "分配")));
            log.info("客户资源分配成功客户id:" + memberBaseInfo.getMemberId() + " ,红娘workerId:" + worker.getWorkerId() + " ,时间:" + CommonUtils.serialObject(new Date()) + " 日志ID" + logId);
            successIds.add(memberBaseInfo.getMemberId());
            /** 删掉redis*/
            concurrentDeleteRedis(key, memberBaseInfo.getMemberId());
        }
        putMap(allIds, map, blackIds, inSaleCaseIds, isNotVipIds, isNotInSaleCaseIds, successIds);
        log.info("==========红娘分配结束时间" + CommonUtils.serialObject(new Date()));
        return map;
    }

    /**
     * 功能描述:
     * 分配--添加销售机会和修改客户类型
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/6 16:05
     */
    private void updateMemberAndSaleCase(MemberBaseInfo memberBaseInfo, Worker worker, Integer caseClass, Worker currWorker, String allotType) {
        /** 添加至销售机会*/
        log.info("当前分配资源:" + memberBaseInfo);
        try {
            Date now = new Date();
            SaleCase sc = MemberUtils.converSaleCase(memberBaseInfo);
            //sc.setCreateTime(now);
            sc.setAllotTime(now);
            sc.setAllotType(allotType);
            sc.setWorkerDept(worker.getDeptId());
            sc.setWorkerId(worker.getWorkerId());
            sc.setWorkerName(worker.getWorkerName());
            sc.setNextContactTime(null);
            sc.setCaseClass(caseClass);
            Integer insertSaleCase = saleCaseService.insert(sc);
            if (insertSaleCase == null || insertSaleCase.intValue() < 1) {
                log.error("添加销售机会信息失败，memberId" + sc);
            }
            /**添加至分配历史*/
            AllotHistory ah = new AllotHistory(memberBaseInfo.getMemberId(), now, allotType, worker.getWorkerId(), worker.getWorkerName(), worker.getDeptId(), 1, memberBaseInfo.getMobile(), worker.getCompanyId());
            if (!allotHistoryService.insert(ah)) {
                log.error("添加至分配历史失败" + ah);
            }
            /** 更新客户信息*/
            // 更新是否在库标识位
            MemberBaseInfo newMemberBaseInfo = new MemberBaseInfo(memberBaseInfo.getMemberId(), memberBaseInfo.getMemberType());
            MemberUtils.setMemberType(newMemberBaseInfo, Constants.member_type_insalecase, Constants.YES);
            // 更新是否分配过标识位
            MemberUtils.setMemberType(newMemberBaseInfo, Constants.member_type_alloted, Constants.YES);
            //更新资源类型
            memberService.update(newMemberBaseInfo, null, null);
            //更新es
            MemberBean mb = new MemberBean();
            mb.setMemberId(memberBaseInfo.getMemberId());
            mb.setInSaleCase(true);
            mb.setCaseClass(caseClass);
            mb.setWorkerId(worker.getWorkerId());
            mb.setWorkerName(worker.getWorkerName());
            mb.setDeptId(worker.getDeptId());
            mb.setAllotTime(new Date());
            searchClient.update(mb);
            /** 通知*/
            Notice notice = new Notice();
            notice.setWorkerId(worker.getWorkerId());
            notice.setWorkerName(worker.getWorkerName());
            notice.setCreateTime(new Date());
            notice.setType(2);
            notice.setContent("客户ID:" + memberBaseInfo.getMemberId() + " 客户名:" + memberBaseInfo.getTrueName() + ",已被" + currWorker.getWorkerName() + "手动分配给您");
            notice.setTitle("手动分配客户");
            noticeService.insert(notice);
        } catch (Exception e) {
            log.error("手动分配", e);
        }
    }

    /**
     * 功能描述:
     * 分配--取出销售下一处理人
     *
     * @param: i记录操作超过游标队列
     * @param: isCapacity 是否操作库容
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/8 15:59
     */
    private Worker workerNext(Map<Long, Worker> workerMap, int i, boolean isCapacity) {
        try {
            if (isCapacity && i >= workerMap.size()) {
                log.warn("当前分配任务分配已满");
                return null;
            }
            /** 取出处理人并重新放入游标*/
            Long minKey = getMinKey(workerMap);
            Worker execWorker = workerMap.get(minKey);
            workerMap.remove(minKey);
            workerMap.put(System.currentTimeMillis(), execWorker);
            if (isCapacity) {
                /** 取出库容*/
                AllotWorker allotWorker = allotWorkerService.getByWorkerId(execWorker.getWorkerId());
                Integer hasAllot = allotWorker.getHasAllot();//已库存
                Integer alrAllotDay = allotWorker.getAlrAllotDay();//今天已分配
                if (hasAllot.intValue() >= allotWorker.getMaxAllot().intValue() && allotWorker.getMaxAllot() != 0) {
                    log.warn(execWorker.getWorkerId() + "已达到最大库容,无法分配");
                    i++;
                    return workerNext(workerMap, i, isCapacity);
                }
                if (alrAllotDay.intValue() >= allotWorker.getMaxAllotTheDay().intValue() && allotWorker.getMaxAllotTheDay() != 0) {
                    log.warn(execWorker.getWorkerId() + "已达到今日最大库容,无法分配");
                    i++;
                    return workerNext(workerMap, i, isCapacity);
                }
                //更新库容
                crmJdbc.update("update AllotWorker set hasAllot=hasAllot+1,alrAllotDay=alrAllotDay+1 where workerId=?", execWorker.getWorkerId());
            }
            return execWorker;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 功能描述:
     * 并发--取最小key
     *
     * @author: DoubleLi
     * @date: 2019/5/8 16:02
     */
    private Long getMinKey(Map<Long, Worker> workerMap) {
        Object[] keys = workerMap.keySet().toArray();
        Arrays.sort(keys);
        return (Long) keys[0];
    }

    /**
     * 功能描述:
     * 并发--处理资源
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/8 15:16
     */
    private void concurrentHandle(List<MemberBaseInfo> memberList, String redisKey) {
        List<MemberBaseInfo> removeMemberList = new ArrayList<>();
        List<Long> redisMemberList = null;
        try {
            redisMemberList = (List<Long>) redisClient.get(redisKey);
            redisMemberList = redisMemberList == null ? new ArrayList<>() : redisMemberList;
        } catch (Exception e) {
            redisMemberList = redisMemberList == null ? new ArrayList<>() : redisMemberList;
        }
        for (MemberBaseInfo m : memberList) {
            if (!redisMemberList.contains(m.getMemberId())) {
                redisMemberList.add(m.getMemberId());
            } else {
                removeMemberList.add(m);
            }
        }
        redisClient.set(redisKey, redisMemberList, Constants.exp_30minutes);
        /*剔除并发分配资源*/
        if (removeMemberList.size() > 0)
            memberList.removeAll(removeMemberList);
    }

    /**
     * 功能描述:
     * 并发--redis处理 删掉已处理的资源
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/8 15:16
     */
    private void concurrentDeleteRedis(String redisKey, Object... memberId) {
        List<Long> redisMemberList = (List<Long>) redisClient.get(redisKey);
        redisMemberList.removeAll(Arrays.asList(memberId));
        redisClient.set(redisKey, redisMemberList);
    }

    /**
     * 功能描述: 红娘调配
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/8 17:14
     */
    @Override
    public Map<String, Object> matchmakerDeploy(List<MemberBaseInfo> memberList, List<Worker> workerList, Worker currWorker) {
        log.info("==========红娘调配开始时间" + CommonUtils.serialObject(new Date()));
        List<Long> allIds = new ArrayList<>();
        memberList.forEach(m -> {
            allIds.add(m.getMemberId());
        });
        Map<String, Object> map = new HashMap<>();
        map.put("success", 0);
        map.put("fail", allIds.size());
        /** 并发处理*/
        String key = "matchmakerMemberDeploy";
        concurrentHandle(memberList, key);
        if (memberList.size() == 0) {
            log.info("选择的资源被占用无法分配");
            map.put("failReason", "选择的资源被占用无法分配");
            return map;
        }
        /**初始化游标*/
        Map<Long, Worker> workerMap = new ConcurrentHashMap<>(workerList.size());
        putWorker(workerList, workerMap);
        /** 红娘意向度*/
        Intentionality intentionality = intentionalityService.getByTypeFirst(2);
        if (intentionality == null) {
            concurrentDeleteRedis(key, memberList.stream().mapToLong(m -> m.getMemberId()).toArray());
            map.put("failReason", "红娘意向度未设置");
            log.info("红娘意向度未设置");
            return map;
        }
        Integer caseClass = intentionality.getCaseClass();
        /** 黑名单*/
        List<Long> blackIds = new ArrayList<>();
        /** 已在库*/
        List<Long> inSaleCaseIds = new ArrayList<>();
        /** 不是vip*/
        List<Long> isNotVipIds = new ArrayList<>();
        /** 不在库*/
        List<Long> isNotInSaleCaseIds = new ArrayList<>();
        /** 成功*/
        List<Long> successIds = new ArrayList<>();
        for (MemberBaseInfo memberBaseInfo : memberList) {
            //0正常 1黑名单 2已在库 3不在库 4 不是vip
            int res = checkMember(memberBaseInfo, Constants.YES, true);
            if (res > 0) {
                putIds(blackIds, inSaleCaseIds, isNotVipIds, isNotInSaleCaseIds, memberBaseInfo, res);
                concurrentDeleteRedis(key, memberBaseInfo.getMemberId());
                continue;
            }
            /** 取下一合适处理红娘,不更新库容*/
            Worker worker = workerNext(workerMap, 0, false);
            if (worker == null) {
                concurrentDeleteRedis(key, memberList.stream().mapToLong(m -> m.getMemberId()).toArray());
                log.error("没有合适的红娘进行分配资源");
                map.put("failReason", "选择的红娘库容已满，无法进行调配资源");
                break;
            }
            /** 用于修改销售机会，调配历史，修改客户信息，添加通知*/
            updateMemberAndSaleCaseDeploy(memberBaseInfo, worker, caseClass, currWorker, "matchmaker_deploy");
            String logId = sysLogService.insert(new SysLog(memberBaseInfo.getMemberId().toString(), worker.getWorkerId().toString(), "操作人:" + currWorker.getWorkerId(), currWorker.getWorkerId().toString(), "客户id:" + memberBaseInfo.getMemberId() + " 客户名:" + memberBaseInfo.getTrueName() + "调配给,员工ID:" + worker.getWorkerId() + " 员工名:" + worker.getWorkerName(), sysDictService.findValue("log_type", "红娘调配"), currWorker.getWorkerId(), sysDictService.findValue("log_action", "调配")));
            successIds.add(memberBaseInfo.getMemberId());
            log.info("客户资源调配成功客户id:" + memberBaseInfo.getMemberId() + " ,调配至workerId:" + worker.getWorkerId() + " ,时间:" + CommonUtils.serialObject(new Date()) + " 日志ID" + logId);
            /** 删掉redis*/
            concurrentDeleteRedis(key, memberBaseInfo.getMemberId());
        }
        putMap(allIds, map, blackIds, inSaleCaseIds, isNotVipIds, isNotInSaleCaseIds, successIds);
        log.info("==========红娘调配结束时间" + CommonUtils.serialObject(new Date()));
        return map;
    }

    /**
     * 功能描述: 分配Worker队列游标
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/6/4 10:37
     */
    private void putWorker(List<Worker> workerList, Map<Long, Worker> workerMap) {
        long i = 0;
        for (Worker worker : workerList) {
            workerMap.put(System.currentTimeMillis() + i, worker);
            i++;
        }
    }

    /**
     * 功能描述: 销售调配
     *
     * @author: DoubleLi
     * @date: 2019/5/9 11:08
     */
    @Override
    public Map<String, Object> salesDeploy(List<MemberBaseInfo> memberList, List<Worker> workerList, Worker currWorker) {
        log.info("==========销售调配开始时间" + CommonUtils.serialObject(new Date()));
        List<Long> allIds = new ArrayList<>();
        memberList.forEach(m -> {
            allIds.add(m.getMemberId());
        });
        Map<String, Object> map = new HashMap<>();
        map.put("success", 0);
        map.put("fail", allIds.size());
        /** 并发处理*/
        String key = "salesMemberDeploy";
        concurrentHandle(memberList, key);
        if (memberList.size() == 0) {
            log.info("选择的资源被占用无法分配");
            map.put("failReason", "选择的资源被占用无法分配");
            return map;
        }
        /**初始化游标*/
        Map<Long, Worker> workerMap = new ConcurrentHashMap<>(workerList.size());
        putWorker(workerList, workerMap);
        /** 销售意向度*/
        Intentionality intentionality = intentionalityService.getByTypeFirst(1);
        if (intentionality == null) {
            concurrentDeleteRedis(key, memberList.stream().mapToLong(m -> m.getMemberId()).toArray());
            map.put("failReason", "销售意向度未设置");
            log.info("销售意向度未设置");
            return map;
        }
        Integer caseClass = intentionality.getCaseClass();
        /** 黑名单*/
        List<Long> blackIds = new ArrayList<>();
        /** 已在库*/
        List<Long> inSaleCaseIds = new ArrayList<>();
        /** 不是vip*/
        List<Long> isNotVipIds = new ArrayList<>();
        /** 不在库*/
        List<Long> isNotInSaleCaseIds = new ArrayList<>();
        /** 成功*/
        List<Long> successIds = new ArrayList<>();
        for (MemberBaseInfo memberBaseInfo : memberList) {
            int res = checkMember(memberBaseInfo, Constants.YES, false);
            if (res > 0) {
                putIds(blackIds, inSaleCaseIds, isNotVipIds, isNotInSaleCaseIds, memberBaseInfo, res);
                concurrentDeleteRedis(key, memberBaseInfo.getMemberId());
                continue;
            }
            /** 取下一合适处理销售,不更新库容*/
            Worker worker = workerNext(workerMap, 0, false);
            if (worker == null) {
                concurrentDeleteRedis(key, memberList.stream().mapToLong(m -> m.getMemberId()).toArray());
                log.error("没有合适的销售进行分配资源");
                map.put("failReason", "选择的销售库容已满，无法进行调配资源");
                break;
            }
            /** 用于修改销售机会，调配历史，修改客户信息，添加通知*/
            updateMemberAndSaleCaseDeploy(memberBaseInfo, worker, caseClass, currWorker, "sales_deploy");
            String logId = sysLogService.insert(new SysLog(memberBaseInfo.getMemberId().toString(), worker.getWorkerId().toString(), "操作人:" + currWorker.getWorkerId(), currWorker.getWorkerId().toString(), "客户id:" + memberBaseInfo.getMemberId() + " 客户名:" + memberBaseInfo.getTrueName() + "调配给,员工ID:" + worker.getWorkerId() + " 员工名:" + worker.getWorkerName(), sysDictService.findValue("log_type", "销售调配"), currWorker.getWorkerId(), sysDictService.findValue("log_action", "调配")));
            log.info("客户资源调配成功客户id:" + memberBaseInfo.getMemberId() + " ,调配至workerId:" + worker.getWorkerId() + " ,时间:" + CommonUtils.serialObject(new Date()) + " 日志ID" + logId);
            successIds.add(memberBaseInfo.getMemberId());
            /** 删掉redis*/
            concurrentDeleteRedis(key, memberBaseInfo.getMemberId());
        }
        putMap(allIds, map, blackIds, inSaleCaseIds, isNotVipIds, isNotInSaleCaseIds, successIds);
        log.info("==========销售调配结束时间" + CommonUtils.serialObject(new Date()));
        return map;
    }

    /**
     * 功能描述: 添加至返回map
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/6/21 17:54
     */
    private void putIds(List<Long> blackIds, List<Long> inSaleCaseIds, List<Long> isNotVipIds, List<Long> isNotInSaleCaseIds, MemberBaseInfo memberBaseInfo, int res) {
        /** 0正常 1黑名单 2已在库 3不在库 4 不是vip*/
        switch (res) {
            case 1:
                blackIds.add(memberBaseInfo.getMemberId());
                break;
            case 2:
                inSaleCaseIds.add(memberBaseInfo.getMemberId());
                break;
            case 3:
                isNotInSaleCaseIds.add(memberBaseInfo.getMemberId());
                break;
            case 4:
                isNotVipIds.add(memberBaseInfo.getMemberId());
                break;
            default:
        }
    }

    private void putMap(List<Long> allIds, Map<String, Object> map, List<Long> blackIds, List<Long> inSaleCaseIds, List<Long> isNotVipIds, List<Long> isNotInSaleCaseIds, List<Long> successIds) {
        map.put("success", successIds.size());
        map.put("fail", allIds.size() - successIds.size());
        map.put("blackIds", blackIds);
        map.put("inSaleCaseIds", inSaleCaseIds);
        map.put("isNotVipIds", isNotVipIds);
        map.put("isNotInSaleCaseIds", isNotInSaleCaseIds);
    }

    /**
     * 功能描述:
     * 校验资源
     *
     * @param:
     * @return: 0正常 1黑名单 2已在库 3不在库 4 不是vip
     * @author: DoubleLi
     * @date: 2019/6/21 15:02
     */
    private int checkMember(MemberBaseInfo memberBaseInfo, char type, boolean isVip) {
        int res = 0;
        Boolean cond;
        String msg;
        if (isVip) {
            cond = MemberUtils.memberTypeTrue(memberBaseInfo.getMemberType(), 4, Constants.YES);
            if (!cond) {
                res = 4;
            }
        }
        if (MemberUtils.memberTypeTrue(memberBaseInfo.getMemberType(), 3, Constants.YES)) {
            res = 1;
        }
        if (!MemberUtils.memberTypeTrue(memberBaseInfo.getMemberType(), 5, type)) {
            if (type == Constants.YES) {
                res = 3;
            } else {
                res = 2;
            }
        }
        Long mobileCheck = crmJdbc.queryForObject("select count(1) from SaleCase where mobile =?", Long.class, memberBaseInfo.getMobile());
        if (mobileCheck > 0 && type== Constants.NO) {
            res = 2;
        }
        if (type == Constants.YES) {
            if (!saleCaseService.isInSaleCase(memberBaseInfo.getMemberId())) {
                msg = "客户资源分配校验未通过客户id:" + memberBaseInfo.getMemberId() + " ,客户不在库,无法调配.";
                log.info(msg);
                res = 3;
            }
        }
        if (res > 0) {
            msg = "客户资源分配校验未通过客户id:" + memberBaseInfo.getMemberId() + " ,客户已在库,或为黑名单.";
            log.info(msg);
        }
        return res;
    }

    /**
     * 功能描述:
     * 红娘、销售调配--添加销售机会和修改客户类型
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/8 18:05
     */
    private void updateMemberAndSaleCaseDeploy(MemberBaseInfo memberBaseInfo, Worker worker, Integer caseClass, Worker currWorker, String allotType) {
        /** 添加至销售机会*/
        log.info("当前调配资源:" + memberBaseInfo);
        try {
            Date now = new Date();
            SaleCase sc = saleCaseService.getByMemberId(memberBaseInfo.getMemberId());
            Integer fromWorkerId = sc.getWorkerId();
            String fromWorkerName = sc.getWorkerName();
            sc.setAllotTime(now);
            sc.setAllotType(allotType);
            sc.setWorkerDept(worker.getDeptId());
            sc.setWorkerId(worker.getWorkerId());
            sc.setWorkerName(worker.getWorkerName());
            sc.setNextContactTime(null);
            sc.setCaseClass(caseClass);
            sc.setAdjustTime(now);
            sc.setAdjustWorker(currWorker.getWorkerId().toString());
            if (!saleCaseService.update(sc)) {
                log.error("调配更新销售机会信息失败，memberId" + sc);
            }
            /**添加至调配历史*/
            AdjustHistory adj = new AdjustHistory(sc.getCaseId(), memberBaseInfo.getMemberId(), worker.getWorkerName(), now, fromWorkerId, worker.getWorkerId(), caseClass, worker.getCompanyId());
            if (!adjustHistoryService.insert(adj)) {
                log.error("添加至调配历史失败" + adj);
            }
            /** 更新客户信息*/
            //更新es
            MemberBean mb = new MemberBean();
            mb.setMemberId(memberBaseInfo.getMemberId());
            mb.setInSaleCase(true);
            mb.setCaseClass(caseClass);
            mb.setWorkerId(worker.getWorkerId());
            mb.setWorkerName(worker.getWorkerName());
            mb.setQuitWorkerName(fromWorkerName);
            mb.setQuitWorkerId(fromWorkerId);
            mb.setQuitReason("资源调配");
            mb.setDeptId(worker.getDeptId());
            mb.setAllotTime(new Date());
            searchClient.update(mb);
            /** 通知*/
            Notice notice = new Notice();
            notice.setWorkerId(worker.getWorkerId());
            notice.setWorkerName(worker.getWorkerName());
            notice.setCreateTime(new Date());
            notice.setType(2);
            notice.setContent("客户ID:" + memberBaseInfo.getMemberId() + " 客户名:" + memberBaseInfo.getTrueName() + ",已被" + currWorker.getWorkerName() + "手动调配给您");
            notice.setTitle("手动调配客户");
            noticeService.insert(notice);
        } catch (Exception e) {
            log.error("调配", e);
        }
    }

    /**
     * 手动分配/调配 end==============================================================================================
     *
     */


    /**
     * 提供其他调用接口 begin==============================================================================================
     *
     */

    /**
     * 功能描述: 销售捞取
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/10 17:26
     */
    @Override
    public String gain(Long memberId, Worker worker) {
        String result = "";
        crmJdbc.update("insert ignore into AllotWorker(workerId,maxAllot,maxAllotTheDay,hasAllot,alrAllotDay,createTime) select workerId,allCount,ifnull(maxAllotTheDay,0),0,0,current_timestamp from Worker w where workerId=?", worker.getWorkerId());
        AllotWorker allotWorker = allotWorkerService.getByWorkerId(worker.getWorkerId());
        Integer hasAllot = allotWorker.getHasAllot();//已库存
        Integer alrAllotDay = allotWorker.getAlrAllotDay();//今天已分配
        Integer alrGainNum = allotWorker.getGainNum();//今天已捞取
        int gainNum = globalSettingService.gainNum();
        if (alrGainNum > gainNum) {
            return "已达到今日最大捞取量,无法捞取";
        }
        if (hasAllot.intValue() >= allotWorker.getMaxAllot().intValue() && allotWorker.getMaxAllot() != 0) {
            return "已达到最大库容,无法捞取";
        }
        if (alrAllotDay.intValue() >= allotWorker.getMaxAllotTheDay().intValue() && allotWorker.getMaxAllotTheDay() != 0) {
            return "已达到今日最大库容,无法捞取";
        }
        MemberBaseInfo memberBaseInfo = memberService.get(MemberBaseInfo.class, memberId);
        Long mobileCheck = crmJdbc.queryForObject("select count(1) from SaleCase where mobile =?", Long.class, memberBaseInfo.getMobile());
        if (mobileCheck > 0) {
            return "客户手机号已在资源库，无法捞取";
        }
        //更新库容
        crmJdbc.update("update AllotWorker set hasAllot=hasAllot+1,alrAllotDay=alrAllotDay+1 where workerId=?", worker.getWorkerId());
        Integer caseClass = intentionalityService.getByTypeFirst(1).getCaseClass();
        Date now = new Date();
        SaleCase sc = MemberUtils.converSaleCase(memberBaseInfo);
        sc.setCreateTime(now);
        sc.setAllotTime(now);
        sc.setAllotType("sales_gain");
        sc.setWorkerDept(worker.getDeptId());
        sc.setWorkerId(worker.getWorkerId());
        sc.setWorkerName(worker.getWorkerName());
        sc.setNextContactTime(null);
        sc.setCaseClass(caseClass);
        Integer insertSaleCase = saleCaseService.insert(sc);
        if (insertSaleCase == null || insertSaleCase.intValue() < 1) {
            result += "添加销售机会信息失败";
        }
        /**添加至分配历史*/
        AllotHistory ah = new AllotHistory(memberBaseInfo.getMemberId(), now, "sales_gain", worker.getWorkerId(), worker.getWorkerName(), worker.getDeptId(), 1, memberBaseInfo.getMobile(), worker.getCompanyId());
        if (!allotHistoryService.insert(ah)) {
            result += "添加至分配捞取历史失败";
        }
        /** 更新客户信息*/
        // 更新是否在库标识位
        MemberBaseInfo newMemberBaseInfo = new MemberBaseInfo(memberBaseInfo.getMemberId(), memberBaseInfo.getMemberType());
        //存在机会库
        MemberUtils.setMemberType(newMemberBaseInfo, Constants.member_type_insalecase, Constants.YES);
        // 更新是否分配过标识位
        MemberUtils.setMemberType(newMemberBaseInfo, Constants.member_type_alloted, Constants.YES);
        //更新资源类型
        memberService.update(newMemberBaseInfo, null, null);
        //更新es
        MemberBean mb = new MemberBean();
        mb.setMemberId(memberBaseInfo.getMemberId());
        mb.setInSaleCase(true);
        mb.setCaseClass(caseClass);
        mb.setWorkerId(worker.getWorkerId());
        mb.setWorkerName(worker.getWorkerName());
        mb.setDeptId(worker.getDeptId());
        mb.setAllotTime(new Date());
        searchClient.update(mb);
        String logId = sysLogService.insert(new SysLog(memberBaseInfo.getMemberId().toString(), worker.getWorkerId().toString(), null, null, "客户id:" + memberBaseInfo.getMemberId() + " 客户名:" + memberBaseInfo.getTrueName() + " 捞取员工ID:" + worker.getWorkerId() + " 员工名:" + worker.getWorkerName(), sysDictService.findValue("log_type", "销售捞取"), Constants.DEFAULT_WORKER_ID, sysDictService.findValue("log_action", "捞取")));
        log.info("客户资源捞取成功客户id:" + memberBaseInfo.getMemberId() + " ,销售workerId:" + worker.getWorkerId() + " ,时间:" + CommonUtils.serialObject(new Date()) + " 日志ID" + logId);
        Notice notice = new Notice();
        notice.setWorkerId(worker.getWorkerId());
        notice.setWorkerName(worker.getWorkerName());
        notice.setCreateTime(new Date());
        notice.setType(2);
        notice.setContent("客户ID:" + memberBaseInfo.getMemberId() + " 客户名:" + memberBaseInfo.getTrueName() + ",捞取成功");
        notice.setTitle("手动捞取客户");
        noticeService.insert(notice);
        return result;
    }

    /**
     * 功能描述: 销售放弃资源
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/5/10 17:26
     */
    @Override
    @Transactional
    public String quit(SaleCase saleCase, Worker worker, SysCirculationConfig circulation) {
        String result = "";
        crmJdbc.update("insert ignore into AllotWorker(workerId,maxAllot,maxAllotTheDay,hasAllot,alrAllotDay,createTime) select workerId,allCount,ifnull(maxAllotTheDay,0),0,0,current_timestamp from Worker w where workerId=?", saleCase.getWorkerId());
        /** 更新库容*/
        if (crmJdbc.update("update AllotWorker set hasAllot=hasAllot-1 where workerId=?", saleCase.getWorkerId()) == 0) {
            return "更新库容失败";
        }

        MemberBaseInfo memberBaseInfo = memberService.get(MemberBaseInfo.class, saleCase.getMemberId());
        /** 删除机会库*/
        Integer deleteSaleCase = saleCaseService.deleteByMemberIds(Arrays.asList(new Long[]{memberBaseInfo.getMemberId()}));
        if (deleteSaleCase < 1) {
            result += "删除销售机会信息失败";
        }
        /** 添加放弃表和回收站*/
        SaleCaseLost saleCaseLost = new SaleCaseLost();
        BeanUtils.copyProperties(saleCase, saleCaseLost);
        saleCaseLost.setLostTime(new Date());
        saleCaseLost.setLostReason(circulation.getReason());
        saleCaseLost.setLostWorker(worker.getWorkerId());
        if (saleCaseService.insertSaleCaseLost(saleCaseLost) == null) {
            result += "添加放弃机会记录失败";
        }
        RecycleBin recycleBin = new RecycleBin();
        recycleBin.setRecycleWorker(worker.getWorkerId());
        recycleBin.setRecycleReason(circulation.getReason());
        recycleBin.setRecycleTime(new Date());
        BeanUtils.copyProperties(saleCase, recycleBin);
        if (!recycleBinService.insert(recycleBin)) {
            result += "添加至回收站失败";
        }
        /** 更新客户信息*/
        //存在回收站
        MemberUtils.setMemberType(memberBaseInfo, Constants.member_type_isrecycling, Constants.YES);
        //不存在机会库
        MemberUtils.setMemberType(memberBaseInfo, Constants.member_type_insalecase, Constants.NO);
        // 更新是否在库标识位
        MemberBaseInfo newMemberBaseInfo = new MemberBaseInfo(memberBaseInfo.getMemberId(), memberBaseInfo.getMemberType());
        //更新资源类型
        memberService.update(newMemberBaseInfo, null, null);
        //更新es
        searchClient.lost(saleCase.getMemberId(), worker, circulation.getReason());
        String logId = sysLogService.insert(new SysLog(memberBaseInfo.getMemberId().toString(), worker.getWorkerId().toString(), null, null, "客户id:" + memberBaseInfo.getMemberId() + " 客户名:" + memberBaseInfo.getTrueName() + " 放弃员工ID:" + worker.getWorkerId() + " 员工名:" + worker.getWorkerName(), sysDictService.findValue("log_type", "销售放弃"), worker.getWorkerId(), sysDictService.findValue("log_action", "放弃")));
        log.info("客户资源放弃成功客户id:" + memberBaseInfo.getMemberId() + " ,销售workerId:" + worker.getWorkerId() + " ,时间:" + CommonUtils.serialObject(new Date()) + " 日志ID" + logId);
        Notice notice = new Notice();
        notice.setWorkerId(saleCase.getWorkerId());
        notice.setWorkerName(saleCase.getWorkerName());
        notice.setCreateTime(new Date());
        notice.setType(2);
        notice.setContent("客户ID:" + memberBaseInfo.getMemberId() + " 客户名:" + memberBaseInfo.getTrueName() + ",已被放弃,原因：" + circulation.getReason());
        notice.setTitle("手动放弃客户");
        noticeService.insert(notice);
        return result;
    }

    /**
     * 提供其他调用接口 ends==============================================================================================
     *
     */
}
