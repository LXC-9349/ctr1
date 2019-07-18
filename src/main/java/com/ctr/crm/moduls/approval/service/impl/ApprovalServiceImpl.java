package com.ctr.crm.moduls.approval.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.ctr.crm.moduls.approval.pojo.ApprovalSource;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.notice.models.Notice;
import com.ctr.crm.moduls.notice.service.NoticeService;
import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.jdbc.YunhuJdbcOperations;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.PojoUtils;
import com.ctr.crm.commons.utils.SpringContextUtils;
import com.ctr.crm.interceptors.ApprovalInterceptor;
import com.ctr.crm.listeners.ApplicationInitialEvent;
import com.ctr.crm.moduls.approval.modules.ApprovalData;
import com.ctr.crm.moduls.approval.modules.ApprovalFlow;
import com.ctr.crm.moduls.approval.modules.ApprovalFlowSetting;
import com.ctr.crm.moduls.approval.modules.ApprovalFlowSettingSon;
import com.ctr.crm.moduls.approval.service.ApprovalService;
import com.ctr.crm.moduls.approvalgroup.models.ApprovalGroup;
import com.ctr.crm.moduls.approvalgroup.service.ApprovalGroupService;
import com.ctr.crm.moduls.contract.models.ContractOrderPayRecord;
import com.ctr.crm.moduls.contract.service.ContractOrderPayRecordService;
import com.ctr.crm.moduls.contract.service.ContractOrderService;
import com.ctr.crm.moduls.meetrecord.models.MeetRecord;
import com.ctr.crm.moduls.meetrecord.service.MeetRecordService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * 功能描述: 审批流服务
 *
 * @author: DoubleLi
 * @date: 2019/4/24 14:45
 */
@Service("approvalService")
public class ApprovalServiceImpl implements ApprovalService {

    private static final Log log = LogFactory.getLog("approval");
    @Resource
    private YunhuJdbcOperations crmJdbc;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private ContractOrderPayRecordService contractOrderPayRecordService;
    @Autowired
    @Lazy
    private MeetRecordService meetRecordService;
    @Autowired
    @Lazy
    private ContractOrderService contractOrderService;
    @Autowired
    private ApprovalGroupService approvalGroupService;

    private static String[] af = new String[10];

    static {
        af[0] = "一级审批人审批";
        af[1] = "二级审批人审批";
        af[2] = "三级审批人审批";
        af[3] = "四级审批人审批";
        af[4] = "五级审批人审批";
        af[5] = "六级审批人审批";
        af[6] = "七级审批人审批";
        af[7] = "八级审批人审批";
        af[8] = "九级审批人审批";
        af[9] = "十级审批人审批";
    }
//	private RedisProxy redisClient = RedisProxy.getInstance();

    @Override
    public List<ApprovalFlowSetting> searchApprovalFlowSettingSing(String approvalType) {
        String sql = PojoUtils.getSelectSql(ApprovalFlowSetting.class, "serialVersionUID", true, null,
                "isUse,approvalType", 1, approvalType);
        return crmJdbc.query(sql + " group by approvalType", BeanPropertyRowMapper.newInstance(ApprovalFlowSetting.class));
    }

    @Override
    public Integer insertOrUpdateApprovalData(ApprovalData ad) throws Exception {
        if (ad == null)
            return 0;
        //新增
        if (ad.getId() == null) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, ad, "serialVersionUID");
            final String insertSql = PojoUtils.getInsertSQL(ApprovalData.class.getSimpleName(), dataMap, "id");
            KeyHolder keyHolder = new GeneratedKeyHolder();
            crmJdbc.getNamedParameterJdbcOperations().update(insertSql, null, keyHolder);
            return keyHolder.getKey().intValue();
        } else {
            //更新
            ApprovalData now = searchApprovalData(ad.getId());
            if (now.getVersion() != ad.getVersion()) {
                //版本不一致修改失败
                return 0;
            }
            ad.setVersion(ad.getVersion() + 1);
            Map<String, String> dataMap = PojoUtils.comparePojo(null, ad, "serialVersionUID");
            final String updateSql = PojoUtils.getUpdateSQL(ApprovalData.class.getSimpleName(), dataMap, "id",
                    ad.getId());
            crmJdbc.update(updateSql);
            return ad.getId();
        }
    }

    @Override
    public List<ApprovalFlowSetting> searchApprovalFlowSettingByType(String value) {
        try {
            String sql = PojoUtils.getSelectSql(ApprovalFlowSetting.class, "serialVersionUID", true, null,
                    "isUse,approvalType", 1, value);
            return crmJdbc.query(sql + " order by sort asc", BeanPropertyRowMapper.newInstance(ApprovalFlowSetting.class));
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<ApprovalFlow> searchApprovalFlowListByDateId(Integer dateId) {
        String sql = PojoUtils.getSelectSql(ApprovalFlow.class, "serialVersionUID", true, null,
                "approvalDataId", dateId);
        return crmJdbc.query(sql + " order by examineTime desc", BeanPropertyRowMapper.newInstance(ApprovalFlow.class));
    }

    @Override
    public Integer insertOrUpdateApprovalFlow(ApprovalFlow af) throws Exception {
        if (af == null)
            return 0;
        //新增
        if (af.getId() == null) {
            Map<String, String> dataMap = PojoUtils.comparePojo(null, af, "serialVersionUID");
            final String insertSql = PojoUtils.getInsertSQL(ApprovalFlow.class.getSimpleName(), dataMap, "id");
            KeyHolder keyHolder = new GeneratedKeyHolder();
            crmJdbc.getNamedParameterJdbcOperations().update(insertSql, null, keyHolder);
            return keyHolder.getKey().intValue();
        } else {//更新
            Map<String, String> dataMap = PojoUtils.comparePojo(null, af, "serialVersionUID");
            final String updateSql = PojoUtils.getUpdateSQL(ApprovalFlow.class.getSimpleName(), dataMap, "id",
                    af.getId());
            crmJdbc.update(updateSql);
            return af.getId();
        }
    }

    @Override
    public ApprovalData searchApprovalData(Integer id) {
        if (id == null)
            return null;
        String sql = PojoUtils.getSelectSql(ApprovalData.class, "serialVersionUID", null, "id", id);
        try {
            return crmJdbc.queryForObject(sql, BeanPropertyRowMapper.newInstance(ApprovalData.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public ApprovalFlow searchApprovalFlow(Integer id) {
        if (id == null)
            return null;
        String sql = PojoUtils.getSelectSql(ApprovalFlow.class, "serialVersionUID", null, "id", id);
        try {
            return crmJdbc.queryForObject(sql, BeanPropertyRowMapper.newInstance(ApprovalFlow.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public String processApproval(ApprovalData approvalData, ApprovalFlow approvalFlow, Worker currentWorker) throws Exception {
        /*流程校验 begin*/
        if (approvalData == null)
            return "流程数据不存在";
        boolean approvalFlowIsNull = approvalFlowIsNull(approvalFlow);
        if (approvalFlowIsNull)
            return "审批流数据参数不能为空";
        //查询最新流程数据
        ApprovalData nowApprovalData = searchApprovalData(approvalFlow.getApprovalDataId());
        if (nowApprovalData == null)
            return "流程数据不存在";
        if (nowApprovalData.getVersion() != approvalData.getVersion())
            return "流程已被处理，流程处理失败";
        if (nowApprovalData.getStatus() == 2 || nowApprovalData.getStatus() == 3 || nowApprovalData.getStatus() == 4)
            return "流程已结束，无法修改";
        String source = approvalData.getSource();
        ApprovalFlow nowApprovalFlow = searchApprovalFlow(approvalFlow.getId());
        if (nowApprovalFlow.getInspector() != null)
            return "当前节点已被处理";
        //解析source
        ApprovalSource as = new ApprovalSource().analyze(source);
        if (as == null)
            return "流程数据参数解析错误";
        /*流程校验 end*/
        /*流程处理begin*/
        List<String> groupIds = approvalGroupService.getGroupIdByWorkerId(currentWorker.getWorkerId());//登陆用户所在组
        List<ApprovalGroup> groupList = approvalGroupService.findListByIds(groupIds);
        List<ApprovalFlowSetting> afsList = searchApprovalFlowSettingByType(approvalFlow.getApprovalType());//查询审批类型对应的流程设置
        List<ApprovalFlowSettingSon> sonAfsListnew = new ArrayList<>();
        if (afsList != null) {
            for (ApprovalFlowSetting approvalFlowSetting : afsList) {
                if (StringUtils.isNotBlank(approvalFlowSetting.getGroupId())) {
                    ApprovalFlowSettingSon ason = new ApprovalFlowSettingSon();
                    ason.setApprovalFlowSetting(approvalFlowSetting);
                    ason.setApprovalGroup(approvalGroupService.get(approvalFlowSetting.getGroupId()));
                    sonAfsListnew.add(ason);
                }
            }
        }
        String proId = approvalFlow.getProId();
        if (approvalFlow.getStatus().equals(0)) {//审批为驳回处理---- 流程结束
            //判断是否能操作节点
            if (!isSonAFSFlag(proId, groupList, currentWorker.getWorkerId().toString()) && !currentWorker.getWorkerId().equals(Constants.DEFAULT_WORKER_ID))
                return "当前登陆用户没有处理权限";
            //获取处理方式submit为true需要删除业务执行产生的数据
            if (StringUtils.isNotBlank(as.getDelmethod()) && as.isSubmit() && as.getParmValue().length > 0) {
                try {
                    Object deleteId = as.getParmValue()[0];
                    Method mh = ApprovalInterceptor.findMethod(as.getBeanName(), as.getDelmethod(), new Class[]{Object.class});
                    Object obj = ReflectionUtils.invokeMethod(mh, SpringContextUtils.getBean(as.getBeanName()), deleteId);
                    approvalData.setExecResult(obj != null ? JSONArray.toJSON(obj).toString() : "");
                    log.info(as.getBeanName() + "." + as.getDelmethod() + "驳回审批，删除数据" + approvalData.getId() + "返回结果" + obj + " 操作人:" + currentWorker.getWorkerName());
                } catch (Exception e) {
                    log.error("驳回删除原数据失败,审批数据Id：" + approvalData.getId() + "执行删除方法" + as.getBeanName() + "." + as.getDelmethod(), e);
                    return "驳回删除原数据参数解析失败";
                }
            }
            approvalData.setApprovalresult("驳回原因：" + approvalFlow.getRemake());
            approvalData.setStatus(3);//设置数据为驳回
            sendMessage(approvalData, currentWorker, approvalFlow.getRemake());//驳回发送站内信
        } else if (approvalFlow.getStatus().equals(2)) {//审批为撤销处理---- 流程结束
            /**     申请人和流程处理人才有撤销权限*/
            if (!isSonAFSFlag(proId, groupList, currentWorker.getWorkerId().toString()) && !approvalData.getWorkerId().equals(currentWorker.getWorkerId()) && !currentWorker.getWorkerId().equals(Constants.DEFAULT_WORKER_ID)) {
                return "当前登陆用户没有处理权限";
            }
            //获取处理方式submit为true需要删除业务执行产生的数据
            if (StringUtils.isNotBlank(as.getDelmethod()) && as.isSubmit() && as.getParmValue().length > 0) {
                try {
                    Object deleteId = as.getParmValue()[0];
                    Method mh = ApprovalInterceptor.findMethod(as.getBeanName(), as.getDelmethod(), new Class[]{Object.class});
                    Object obj = ReflectionUtils.invokeMethod(mh, SpringContextUtils.getBean(as.getBeanName()), deleteId);
                    log.info(as.getBeanName() + "." + as.getDelmethod() + "撤销审批，删除数据" + approvalData.getId() + "返回结果" + obj + " 操作人:" + currentWorker.getWorkerName());
                    approvalData.setExecResult(obj != null ? JSONArray.toJSON(obj).toString() : "");
                } catch (Exception e) {
                    log.error("撤销审批删除原数据失败,审批数据Id：" + approvalData.getId() + "执行删除方法" + as.getBeanName() + "." + as.getDelmethod(), e);
                    return "撤销审批删除原数据参数解析失败";
                }
            }
            approvalData.setApprovalresult("撤销原因：" + approvalFlow.getRemake());
            approvalData.setStatus(4);//设置数据为撤销
            if (approvalData.getWorkerId().intValue() != currentWorker.getWorkerId().intValue())//申请人是自己无须发送站内信
                sendMessage(approvalData, currentWorker, approvalFlow.getRemake());
            /**审批通过操作*/
        } else if (approvalFlow.getStatus() == 1) {
            if (!isSonAFSFlag(proId, groupList, currentWorker.getWorkerId().toString()) && !currentWorker.getWorkerId().equals(Constants.DEFAULT_WORKER_ID))
                return "当前登陆用户没有处理权限";
            int i = 1;
            for (ApprovalFlowSettingSon afs : sonAfsListnew) {
                //当前处理角色
                if (approvalFlow.getProId().equals(afs.getApprovalFlowSetting().getWorkerId().toString()) || approvalFlow.getProId().equals(afs.getApprovalFlowSetting().getGroupId()) || Arrays.asList(afs.getApprovalGroup().getWorkerIds()).contains(approvalFlow.getProId())) {
                    break;
                }
                i++;
            }
            //执行处理完操作
            if (i == afsList.size() || currentWorker.getWorkerId().equals(Constants.DEFAULT_WORKER_ID)) {
                //流程操作为最后一步
                Method mh = ApprovalInterceptor.findMethod(as.getBeanName(), as.getMethod(), as.getParmClass());
                Object obj = ReflectionUtils.invokeMethod(mh, SpringContextUtils.getBean(as.getBeanName()), as.getParmValue());
                log.info("执行处理完后的操作返回结果:" + (obj != null ? obj.toString() : "执行成功") + " approvalDateId:" + approvalData.getId() + " 操作人:" + currentWorker.getWorkerName());
                //更新数据表
                approvalData.setStatus(2);//设置数据为审批通过
                if (obj != null) {
                    if (obj.toString().indexOf("error") != -1) {//返回结果有失败情况
                        approvalData.setApprovalresult(obj.toString().split(":")[1]);
                    } else {
                        approvalData.setExecResult(obj != null ? JSONArray.toJSON(obj).toString() : "");
                        approvalData.setApprovalresult("操作成功");
                    }
                } else {
                    approvalData.setExecResult("执行成功");
                    approvalData.setApprovalresult("操作成功");
                }
            } else {//流程还未处理完
                ApprovalFlowSetting nextFlow = new ApprovalFlowSetting();//下一处理设置
                i = 0;
                for (ApprovalFlowSettingSon afs : sonAfsListnew) {
                    //获取正在处理角色
                    if (approvalFlow.getProId().equals(afs.getApprovalFlowSetting().getWorkerId().toString()) || approvalFlow.getProId().equals(afs.getApprovalFlowSetting().getGroupId()) || Arrays.asList(afs.getApprovalGroup().getWorkerIds()).contains(approvalFlow.getProId())) {
                        nextFlow = sonAfsListnew.get(i + 1).getApprovalFlowSetting();
                    }
                    i++;
                }
                //添加下一处理人
                ApprovalFlow af = new ApprovalFlow();
                af.setApprovalFlowSettingid(nextFlow.getId());
                af.setApprovalname(nextFlow.getApprovalName());
                af.setApprovalType(nextFlow.getApprovalType());
                af.setApprovalDataId(approvalData.getId());
                if (StringUtils.isNotBlank(nextFlow.getGroupId())) {
                    af.setProId(nextFlow.getGroupId());
                } else if (nextFlow.getWorkerId() != null && nextFlow.getWorkerId() != 0) {
                    af.setProId(nextFlow.getWorkerId().toString());
                }
                af.setStatus(3);//未审批
                Integer approvalFlowId = insertOrUpdateApprovalFlow(af);// 写入审批流程表
                approvalData.setApprovalFlowId(approvalFlowId);
                approvalData.setStatus(1);
                log.info("流程节点审批通过,approvalDateId:" + approvalData.getId() + " 操作人:" + currentWorker.getWorkerName());
            }
            sendMessage(approvalData, currentWorker);
        }
        //更新数据表
        int dataResult = insertOrUpdateApprovalData(approvalData); /*============修改流程数据*/
        /**更新审批流数据*/
        insertOrUpdateApprovalFlow(approvalFlow);
        if (approvalData.getStatus() == 2 && Constants.APPROVAL_CONTRACT.equals(approvalData.getApprovalType())) {
            //vip转换操作
            contractOrderService.vipPayRecord(Integer.valueOf(approvalData.getBuisId()), approvalData, currentWorker);
        }
        if (Constants.APPROVAL_CONTRACT.equals(approvalData.getApprovalType()) && StringUtils.isNotBlank(approvalFlow.getRemake())) {
            /** 合同审批处理原因*/
            ContractOrderPayRecord contractOrderPayRecord = new ContractOrderPayRecord();
            contractOrderPayRecord.setId(Integer.valueOf(approvalData.getBuisId()));
            contractOrderPayRecord.setReason(approvalFlow.getRemake());
            contractOrderPayRecordService.update(contractOrderPayRecord);
        } else if (Constants.APPROVAL_MEET.equals(approvalData.getApprovalType()) && StringUtils.isNotBlank(approvalFlow.getRemake())) {
            /** 约见审批处理原因*/
            MeetRecord meetRecord = new MeetRecord();
            meetRecord.setId(Integer.valueOf(approvalData.getBuisId()));
            meetRecord.setReason(approvalFlow.getRemake());
            meetRecordService.update(meetRecord);
        }

        if (dataResult == 0)
            return "修改审批数据错误";
        return "";
    }

    /**
     * 功能描述: 判断登陆用户是否有权限操作节点
     *
     * @param: proId 当前节点处理人或组
     * @param: groupIds 登陆用户组
     * @param: workerId 登陆用户Id
     * @return:
     * @author: DoubleLi
     * @date: 2019/4/25 11:35
     */
    private boolean isSonAFSFlag(String proId, List<ApprovalGroup> lgs, String workerId) {
        if (lgs == null)
            return false;
        for (ApprovalGroup approvalGroup : lgs) {
            if (approvalGroup.getId().equals(proId) || (approvalGroup != null && Arrays.asList(approvalGroup.getWorkerIds()).contains(proId))) {
                return true;
            }
        }
        return false;
    }

    @Async
    @Override
    public void sendMessage(ApprovalData approvalData, Worker currentWorker, String... msg) {
        Notice notice = new Notice();
        StringBuilder content = new StringBuilder();
        content.append("审批(审批编号");
        content.append(approvalData.getId()).append("),");
        content.append("已被:(" + currentWorker.getWorkerName() + ")");
        if (approvalData.getStatus() == 1) {//通过,流程未结束
            content.append("审批通过,审批结果:申请中");
        } else if (approvalData.getStatus() == 2) {//通过,流程结束
            content.append("审批通过,审批结果:通过");
        } else if (approvalData.getStatus() == 3) {//驳回
            content.append("驳回,审批结果:驳回,原因:");
            content.append(msg[0]).append(")");
        } else if (approvalData.getStatus() == 4) {//撤销
            content.append("撤销,审批结果:撤销,原因:");
            content.append(msg[0]).append(")");
        }
        /** 通知操作人*/
        notice.setWorkerId(currentWorker.getWorkerId());
        notice.setWorkerName(currentWorker.getWorkerName());
        notice.setCreateTime(new Date());
        notice.setType(2);
        notice.setContent(content.toString());
        notice.setTitle("审批通知");
        noticeService.insert(notice);
        /**发送通知申请人*/
        notice.setWorkerId(approvalData.getWorkerId());
        notice.setWorkerName(null);
        noticeService.insert(notice);
    }

    private boolean approvalFlowIsNull(ApprovalFlow approvalFlow) throws IllegalAccessException {
        for (Field field : approvalFlow.getClass().getDeclaredFields()) {
            if (field.getName().equals("remake"))
                continue;
            // 把私有属性公有化
            field.setAccessible(true);
            Object object = field.get(approvalFlow);
            if (object instanceof CharSequence) {
                if (StringUtils.isEmpty((String) object)) {
                    return true;
                }
            } else {
                if (object == null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean deleteApprovalData(Integer approvalDataId) {
        String deletesql = "delete from ApprovalData where id =? ";
        return crmJdbc.update(deletesql, approvalDataId) > 0;
    }


    @Override
    public List<ApprovalData> searchApprovalData(String approvalType, String status) {
        String sql = PojoUtils.getSelectSql(ApprovalData.class, "serialVersionUID", true, null,
                "approvalType", approvalType);
        if (StringUtils.isNotBlank(status))
            sql += " and status in(" + status + ")";
        return crmJdbc.query(sql, BeanPropertyRowMapper.newInstance(ApprovalData.class));
    }

    @Override
    public List<ApprovalFlow> searchApprovalFlowList(String approvalDataIds, String status) {
        if (StringUtils.isBlank(approvalDataIds))
            return null;
        String sql = PojoUtils.getSelectSql(ApprovalFlow.class, "serialVersionUID", true, null,
                "status", status);
        sql += " and approvalDataId in(" + approvalDataIds + ")";
        return crmJdbc.query(sql + " order by examineTime desc", BeanPropertyRowMapper.newInstance(ApprovalFlow.class));
    }

    public Integer insertOrUpdateApprovalFlowSetting(ApprovalFlowSetting approvalFlowSetting) throws Exception {
        if (approvalFlowSetting == null)
            return 0;
        if (approvalFlowSetting.getId() == null) {//新增
            Map<String, String> dataMap = PojoUtils.comparePojo(null, approvalFlowSetting, "serialVersionUID");
            final String insertSql = PojoUtils.getInsertSQL(ApprovalFlowSetting.class.getSimpleName(), dataMap, "id");
            KeyHolder keyHolder = new GeneratedKeyHolder();
            crmJdbc.getNamedParameterJdbcOperations().update(insertSql, null, keyHolder);
            return keyHolder.getKey().intValue();
        } else {//更新
            approvalFlowSetting.setUpdateTime(new Date());
            Map<String, String> dataMap = PojoUtils.comparePojo(null, approvalFlowSetting, "serialVersionUID");
            final String updateSql = PojoUtils.getUpdateSQL(ApprovalFlowSetting.class.getSimpleName(), dataMap, "id",
                    approvalFlowSetting.getId());
            crmJdbc.update(updateSql);
            return approvalFlowSetting.getId();
        }
    }

    @Override
    @Transactional
    public boolean insertOrUpdateTypeSetting(Map<String, Object> typeMap, Integer approvalNumber, List<Map> roleList, Worker currentWorker) throws Exception {
        String approvalType = (String) typeMap.get("value");//审批类型
        String name = (String) typeMap.get("name");//流名称
        List<ApprovalFlowSetting> afsListold = searchApprovalFlowSettingByType(approvalType);//获取修改前审批类型流程集合
        List<ApprovalData> ApprovalDataList = searchApprovalData(approvalType, "0,1");//查询出当前正在走审批流程的数据
        StringBuffer sb = new StringBuffer();
        for (ApprovalData approvalData : ApprovalDataList) {
            sb.append(approvalData.getId()).append(",");
        }
        //根据审批类型查询待审批审批流数据
        List<ApprovalFlow> aflist = sb.length() > 0 ? searchApprovalFlowList(sb.substring(0, sb.length() - 1), "3") : null;
        /*重新添加审批设置*/
        if (afsListold.size() > 0) {//把原审批设置不为空全部改为无效
            String sql = "update ApprovalFlowSetting set isUse=0,updateTime=NOW() where approvalType=? and updateTime is null";
            crmJdbc.update(sql, approvalType);
        }
        //审批的角色不为空,添加至审批设置
        if (roleList.size() > 0 && roleList.get(0) != null) {
            StringBuffer insertSql = new StringBuffer("insert into ApprovalFlowSetting(approvalName,approvalType,factor,groupId,workerId,type,sort) values");
            int sort = 1;
            for (Map map : roleList) {
                Integer type = (Integer) map.get("type");
                String groupId = type == 2 ? (String) map.get("id") : "";
                Integer workerId = type == 1 ? (Integer) map.get("id") : 0;
                insertSql.append("(").append("'" + name + "'").append(",").append("'" + approvalType + "'").append(",").append(approvalNumber == null ? 0 : approvalNumber.intValue()).append(",'").append(groupId).append("','").append(workerId).append("','").append(type).append("','").append(sort).append("'),");
                sort++;
            }
            crmJdbc.update(insertSql.substring(0, insertSql.length() - 1));//新增审批设置
        }
        //查询出新增之后的审批设置
        List<ApprovalFlowSetting> afsListnew = searchApprovalFlowSettingByType(approvalType);
        //包含详细信息的流程设置
        List<ApprovalFlowSettingSon> sonAfsListnew = new ArrayList<>();
        if (afsListnew != null)
            for (ApprovalFlowSetting approvalFlowSetting : afsListnew) {
                if (StringUtils.isNotBlank(approvalFlowSetting.getGroupId())) {
                    ApprovalFlowSettingSon as = new ApprovalFlowSettingSon();
                    as.setApprovalFlowSetting(approvalFlowSetting);
                    as.setApprovalGroup(approvalGroupService.get(approvalFlowSetting.getGroupId()));
                    sonAfsListnew.add(as);
                }
            }
        /*批量修改审批流*/
        if (aflist != null)
            for (ApprovalFlow af : aflist) {
                String proId = af.getProId();// 正在处理groupId或者workerId
                Integer dataID = af.getApprovalDataId();// 数据ID
                ApprovalData ad = getApprovalDataById(dataID, ApprovalDataList);// 审批数据
                ApprovalFlowSetting newafs = getApprovalFlowSettingByRoleId(proId, sonAfsListnew);//根据正在处理角色获取修改后的设置位置
                // 解析source
                ApprovalSource as = new ApprovalSource().analyze(ad.getSource());// 解析参数
                if (as == null) {
                    log.error("修改流程设置失败,流程数据解析失败,流程数据id：" + ad.getId());
                    continue;
                }
                //流程设置为不需要审批//即新的流程没有设置角色直接完成审批
                if (roleList.size() < 1 || roleList.get(0) == null) {
                    af.setInspector(currentWorker.getWorkerId());//处理人
                    approvalComplete(currentWorker, ad, as, af);// 流程结束
                    continue;
                }
                // 审批数据的拦截条件小于新的拦截条件直接完成审批
                if (approvalNumber != null && as.getLength() != null && as.getLength() < approvalNumber) {
                    af.setInspector(currentWorker.getWorkerId());
                    approvalComplete(currentWorker, ad, as, af);// 流程结束
                    continue;
                }
                //上一次流程处理人为新的流程最后处理角色
                ApprovalFlow lastaf = getLastTimeApprovalFlow(ad);//获取上一次处理审批流
                ApprovalFlowSettingSon afs = sonAfsListnew.get(sonAfsListnew.size() - 1);
                if (lastaf != null && (lastaf.getProId().equals(afs.getApprovalFlowSetting().getGroupId()) || lastaf.getProId().equals(afs.getApprovalFlowSetting().getWorkerId()) || (afs.getApprovalGroup() != null && Arrays.asList(afs.getApprovalGroup().getWorkerIds()).contains(lastaf.getProId())))) {
                    af.setInspector(currentWorker.getWorkerId());
                    approvalComplete(currentWorker, ad, as, af);// 流程结束
                    continue;
                }
                //申请人为新流程中的最后处理人ad.getWorkerId()
                if (ad.getWorkerId() != null && (ad.getWorkerId().equals(afs.getApprovalFlowSetting().getWorkerId()) || (afs.getApprovalGroup() != null && Arrays.asList(afs.getApprovalGroup().getWorkerIds()).contains(ad.getWorkerId())))) {
                    af.setInspector(ad.getWorkerId());
                    approvalComplete(currentWorker, ad, as, af);// 流程结束
                    continue;
                }
                ApprovalFlowSetting applicationSet = null;
                int j = 0;
                for (ApprovalFlowSettingSon an : sonAfsListnew) {//申请人为流程中的角色
                    if (ad.getWorkerId() != null && (ad.getWorkerId().equals(an.getApprovalFlowSetting().getWorkerId()) || (an.getApprovalGroup() != null && Arrays.asList(an.getApprovalGroup().getWorkerIds()).contains(ad.getWorkerId())))) {//为流程中的角色
                        applicationSet = afsListnew.get(j + 1);
                    }
                    j++;
                }
                if (applicationSet != null) {
                    af.setApprovalFlowSettingid(applicationSet.getId());
                    if (StringUtils.isNotBlank(applicationSet.getGroupId())) {
                        af.setProId(applicationSet.getGroupId());
                    } else if (applicationSet.getWorkerId() != null && applicationSet.getWorkerId() != 0) {
                        af.setProId(applicationSet.getWorkerId().toString());
                    }
                    insertOrUpdateApprovalFlow(af);//修改审批流
                    continue;
                }
                //申请人不在流程设置中
                boolean last = false;
                if (newafs == null) {
                    if (lastaf != null)
                        newafs = getApprovalFlowSettingByRoleId(lastaf.getProId(), sonAfsListnew);//上一次处理角色在新的设置中是否存在
                    if (newafs != null)
                        last = true;
                }
                if (newafs == null) {//处理角色不存在
                    //从新规则的第一级开始处理
                    ApprovalFlowSetting fristafs = afsListnew.get(0);//第一级别
                    if (StringUtils.isNotBlank(fristafs.getGroupId())) {
                        af.setProId(fristafs.getGroupId());
                    } else if (fristafs.getWorkerId() != null && fristafs.getWorkerId() != 0) {
                        af.setProId(fristafs.getWorkerId().toString());
                    }
                    af.setApprovalFlowSettingid(fristafs.getId());
                } else {//处理角色存在//更新流程设置id
                    //操作用户角色为最后处理角色//直接执行
                    if (proId != null && (proId.equals(afs.getApprovalFlowSetting().getGroupId()) || proId.equals(afs.getApprovalFlowSetting().getWorkerId()) || (afs.getApprovalGroup() != null && Arrays.asList(afs.getApprovalGroup().getWorkerIds()).contains(proId)))) {
                        af.setApprovalFlowSettingid(afsListnew.get(afsListnew.size() - 1).getId());
                        if (StringUtils.isNotBlank(afs.getApprovalFlowSetting().getGroupId())) {
                            af.setProId(afs.getApprovalFlowSetting().getGroupId());
                        } else if (afs.getApprovalFlowSetting().getWorkerId() != null && afs.getApprovalFlowSetting().getWorkerId() != 0) {
                            af.setProId(afs.getApprovalFlowSetting().getWorkerId().toString());
                        }
                        af.setInspector(currentWorker.getWorkerId());
                        approvalComplete(currentWorker, ad, as, af);// 流程结束
                        continue;
                    } else {//其它情况更新处理流程设置为最高级别的处理角色
                        if (last) {//设置为下一处理人
                            newafs = getApprovalFlowSettingByRoleIdNext(proId, sonAfsListnew);//下一处理人
                        }
                        af.setApprovalFlowSettingid(newafs.getId());
                        if (StringUtils.isNotBlank(newafs.getGroupId())) {
                            af.setProId(newafs.getGroupId());
                        } else if (newafs.getWorkerId() != null && newafs.getWorkerId() != 0) {
                            af.setProId(newafs.getWorkerId().toString());
                        }
                    }
                }
                insertOrUpdateApprovalFlow(af);//修改审批流
            }
        return true;
    }

    /**
     * 根据当前角色获取下一处理人
     *
     * @param proId
     * @param afsListnew
     * @return
     * @time 2018年10月9日
     * @author DoubleLi
     */
    private ApprovalFlowSetting getApprovalFlowSettingByRoleIdNext(String proId,
                                                                   List<ApprovalFlowSettingSon> afsListnew) {
        ApprovalFlowSetting afs = null;
        int i = 0;
        for (ApprovalFlowSettingSon approvalFlowSetting : afsListnew) {
            if (proId.equals(approvalFlowSetting.getApprovalFlowSetting().getGroupId()) || proId.equals(approvalFlowSetting.getApprovalFlowSetting().getWorkerId()) || (approvalFlowSetting.getApprovalGroup() != null && Arrays.asList(approvalFlowSetting.getApprovalGroup().getWorkerIds()).contains(proId))) {
                afs = afsListnew.get(i + 1).getApprovalFlowSetting();//级别最高的处理角色
                break;
            }
            i++;
        }
        return afs;
    }

    /**
     * 获取上一次处理的审批流
     *
     * @return
     * @time 2018年10月9日
     * @author DoubleLi
     */
    public ApprovalFlow getLastTimeApprovalFlow(ApprovalData ad) {
        List<ApprovalFlow> afList = searchApprovalFlowList(ad.getId().toString(), "1");//审批通过的
        return (afList != null && afList.size() > 0) ? afList.get(0) : null;
    }

    /**
     * 功能描述:
     *
     * @param: proId 当前处理
     * @param: afsListnew 新流程
     * @return:
     * @author: DoubleLi
     * @date: 2019/4/24 17:21
     */
    private ApprovalFlowSetting getApprovalFlowSettingByRoleId(String proId, List<ApprovalFlowSettingSon> afsListnew) {
        ApprovalFlowSetting afs = null;
        for (ApprovalFlowSettingSon as : afsListnew) {
            ApprovalFlowSetting approvalFlowSetting = as.getApprovalFlowSetting();
            ApprovalGroup ag = as.getApprovalGroup();
            if (proId.equals(approvalFlowSetting.getGroupId()) || proId.equals(approvalFlowSetting.getWorkerId()) || (ag != null && Arrays.asList(ag.getWorkerIds()).contains(proId))) {
                afs = approvalFlowSetting;//级别最高的处理角色
            }
        }
        return afs;
    }

    /**
     * 流程结束
     *
     * @param currentWorker
     * @param ad
     * @param as
     * @param af
     * @throws Exception
     * @time 2018年10月8日
     * @author DoubleLi
     */
    private void approvalComplete(Worker currentWorker, ApprovalData ad, ApprovalSource as, ApprovalFlow af) throws Exception {
        Method mh = ApprovalInterceptor.findMethod(as.getBeanName(), as.getMethod(), as.getParmClass());
        Object obj = ReflectionUtils.invokeMethod(mh, SpringContextUtils.getBean(as.getBeanName()),
                as.getParmValue());
        ad.setExecResult(obj != null ? JSONArray.toJSON(obj).toString() : "执行成功");
        // 更新数据表
        ad.setStatus(2);// 设置数据为审批通过
        ad.setApprovalresult("操作成功");
        sendMessage(ad, currentWorker);
        // 更新数据表
        insertOrUpdateApprovalData(ad);
        af.setStatus(1);
        af.setExamineTime(new Date());
        insertOrUpdateApprovalFlow(af);//修改审批流
    }

    @SuppressWarnings("unused")
    private ApprovalFlowSetting getApprovalFlowSettingById(Integer oldsettingId, List<ApprovalFlowSetting> afsListold) {
        for (ApprovalFlowSetting approvalFlowSetting : afsListold) {
            if (approvalFlowSetting.getId().equals(oldsettingId))
                return approvalFlowSetting;
        }
        return null;
    }

    private ApprovalData getApprovalDataById(Integer dataID, List<ApprovalData> approvalDataList) {
        for (ApprovalData approvalData : approvalDataList) {
            if (approvalData.getId().intValue() == dataID.intValue())
                return approvalData;
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> searchPending(Map<String, String> params, Worker currentWorker) throws Exception {
        StringBuffer sql = new StringBuffer(
                "select af.id,af.approvalDataId,date_format(ad.applicationtime,'%Y-%m-%d %H:%i:%s')applicationtime,w.workerName,ad.approvalType,ad.action,ad.version,af.approvalname from ApprovalFlow af inner join ApprovalData ad on af.approvalDataId=ad.id inner join Worker w on w.workerId=ad.workerId");
        sql.append(" where 1=1");
        //if (StringUtils.isBlank(params.get("status"))) {
        sql.append(" and af.status in (3)");//状态为待审批
        // }
        if (!currentWorker.getWorkerId().equals(Constants.DEFAULT_WORKER_ID)) {//不为超管
            List<String> groupIds = approvalGroupService.getGroupIdByWorkerId(currentWorker.getWorkerId());
            StringBuffer gid = new StringBuffer();
            for (String rid : groupIds) {
                gid.append("'").append(rid).append("',");
            }
            if (gid.length() > 0) {
                sql.append(" and (af.proId in (").append(gid.substring(0, gid.length() - 1)).append(")").append(" or af.proId ='").append(currentWorker.getWorkerId()).append("')");
            } else {
                sql.append(" and af.proId ='").append(currentWorker.getWorkerId()).append("'");
            }
        }
        if (params != null) {
            if (StringUtils.isNotBlank(params.get("approvalType"))) {//审批类型
                StringBuffer approvalType = new StringBuffer();
                String[] approvalTypes = params.get("approvalType").split(",");
                for (String at : approvalTypes) {
                    approvalType.append("'").append(at).append("'").append(",");
                }
                sql.append(" and af.approvalType in (").append(approvalType.substring(0, approvalType.length() - 1)).append(")");
            }
            if (StringUtils.isNotBlank(params.get("applicationTimeA"))) {//申请开始时间
                sql.append(" and ad.applicationtime >=").append("'").append(params.get("applicationTimeA")).append("'");
            }
            if (StringUtils.isNotBlank(params.get("applicationTimeB"))) {//申请结束时间
                sql.append(" and ad.applicationtime <=").append("DATE_ADD('").append(params.get("applicationTimeB")).append("',INTERVAL 1 DAY)");
            }
            if (StringUtils.isNotBlank(params.get("approvalDataId"))) {//审批编号
                sql.append(" and af.approvalDataId =").append(params.get("approvalDataId"));
            }
            if (StringUtils.isNotBlank(params.get("workerId"))) {//申请人
                Integer wid = -1;
                try {
                    wid = Integer.valueOf(params.get("workerId"));
                } catch (Exception e) {
                }
                sql.append(" and (w.workerName like '%").append(params.get("workerId")).append("%'").append(" or ad.workerId=").append(wid).append(")");
            }
        }
        sql.append(" order by ad.applicationtime asc");
        return crmJdbc.queryForList(sql.toString());
    }

    @Override
    public List<Map<String, Object>> searchApprovalHistory(ApprovalData approvalData) {
        List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
        //新建申请数据
        Map<String, Object> map = new HashMap<>();
        map.put("examineTime", CommonUtils.formateDateToStr(approvalData.getApplicationtime(), "yyyy-MM-dd HH:mm:ss"));
        String sql = "select workerName from Worker where workerId=?";
        Map<String, Object> wmap = crmJdbc.queryForMap(sql, approvalData.getWorkerId());
        map.put("workerName", wmap.get("workerName"));
        map.put("workerId", approvalData.getWorkerId());
        map.put("remark", "提交申请");
        resList.add(map);
        String sqlr = "select af.status,date_format(af.examineTime,'%Y-%m-%d %H:%i:%s') examineTime,af.remake remark,w.workerName,w.workerId,af.proId,af.approvalFlowSettingid from ApprovalFlow af inner join Worker w on w.workerId=af.inspector where af.approvalDataId=? and af.status <> 3 order by af.examineTime asc";
        List<Map<String, Object>> searList = crmJdbc.queryForList(sqlr, approvalData.getId());
        if (searList != null && searList.size() > 0) {
            Integer approvalFlowSettingid = (Integer) searList.get(0).get("approvalFlowSettingid");
            sql = "select ifnull((select updateTime from ApprovalFlowSetting where id=?),'')";
            String updateTime = crmJdbc.queryForObject(sql, String.class, approvalFlowSettingid);
            sql = "select  (case when workerId=0 then groupId else workerId end) proId from ApprovalFlowSetting afs where afs.isUse=1 and  afs.approvalType=?";
            if (StringUtils.isNotBlank(updateTime)) {
                sql += " and afs.updateTime ='" + updateTime + "'";
            } else {
                sql += " and afs.updateTime is null";
            }
            List<Map<String, Object>> afsList = crmJdbc.queryForList(sql, approvalData.getApprovalType());
            int i;
            for (Map<String, Object> map2 : searList) {
                i = 0;
                String proIdn = map2.get("proId").toString();
                if (StringUtils.isNotBlank(proIdn)) {
                    for (Map<String, Object> afs : afsList) {
                        String proId = (String) afs.get("proId");
                        if (proId.equals(proIdn)) {
                            break;
                        }
                        i++;
                    }
                }
                Integer status = (Integer) map2.get("status");
                Integer workerId = (Integer) map2.get("workerId");
                String remake = map2.get("remark") != null ? map2.get("remark").toString() : "";
                switch (status) {
                    case 0:
                        map2.put("remark", (workerId.intValue() == Constants.DEFAULT_WORKER_ID ? "超级管理员审批" : af[i]) + "驳回,原因:" + remake);
                        break;
                    case 1:
                        map2.put("remark", (workerId.intValue() == Constants.DEFAULT_WORKER_ID ? "超级管理员审批" : af[i]) + "通过");
                        break;
                    case 2:
                        map2.put("remark", (workerId.intValue() == Constants.DEFAULT_WORKER_ID ? "超级管理员审批" : af[i]) + "撤销,原因:" + remake);
                        break;
                    default:
                        break;
                }
            }
            resList.addAll(searList);
        }
        return resList;
    }

    @Override
    public void searchMeet(ResponseData responseData, PageMode pageMode, Map<String, String> params, Worker currentWorker) {
        StringBuffer sqlfrom = new StringBuffer(" from ApprovalData ad inner join Worker w on w.workerId=ad.workerId left join ApprovalFlow af on af.id=ad.approvalFlowId left join Worker ww on ww.workerId=af.inspector  left join MeetRecord mt on mt.id=ad.buisId");
        StringBuffer sqlWhere = new StringBuffer(" where ad.approvalType='" + Constants.APPROVAL_MEET + "' ");
        List<ApprovalGroup> glist = dataFilter(params, currentWorker, sqlWhere);
        if (StringUtils.isNotBlank(params.get("meetStatus"))) {//约见状态
            sqlWhere.append(" and mt.status in (" + params.get("meetStatus") + ") ");
        }
        if (StringUtils.isNotBlank(params.get("meetTimeA"))) {//约见时间开始时间
            sqlWhere.append(" and mt.meetTime >=").append("'").append(params.get("meetTimeA")).append("'");
        }
        if (StringUtils.isNotBlank(params.get("meetTimeB"))) {//约见时间结束时间
            sqlWhere.append(" and mt.meetTime <=").append("DATE_ADD('").append(params.get("meetTimeB")).append("',INTERVAL 1 DAY)");
        }
        if (StringUtils.isNotBlank(params.get("memberId"))) {//客户id
            sqlWhere.append(" and mt.memberId in (" + params.get("memberId") + ") ");
        }
        StringBuffer sql = new StringBuffer("select ww.workerName realProcessName,ww.workerId realProcessId,(case mt.status when 0 then '未完成' when 1 then '已完成' when 2 then '作废' end) meetStatus,mt.memberId,mt.meetId,mt.reason,mt.id mtId,ad.id approvalDataId,date_format(mt.meetTime,'%Y-%m-%d %H:%i:%s') meetTime,date_format(ad.applicationtime,'%Y-%m-%d %H:%i:%s') applicationtime,ad.workerId applicant,w.workerName applicantName,ad.approvalType,ad.action,ad.`status`,ad.approvalresult,date_format(af.examineTime,'%Y-%m-%d %H:%i:%s') examineTime,(case ad.`status` when 0 then '待审批' when 1 then '审批中' when 2 then '已通过' when 3 then '已驳回' when 4 then '已撤销' else '未知' end) statusName,af.proId processId,ad.version,af.id id,af.remake remark,mt.memberName,mt.meetName ");
        StringBuffer csql = new StringBuffer("select count(1) ");
        csql.append(sqlfrom).append(sqlWhere);
        pageMode.setTotal(crmJdbc.queryForObject(csql.toString(), Long.class));
        sql.append(sqlfrom).append(sqlWhere).append(" order by (case when mt.status=0 then 0 else 1 end),mt.approvalTime desc  limit " + pageMode.getFirst() + "," + pageMode.getPageSize());
        List<Map<String, Object>> list = crmJdbc.queryForList(sql.toString());
        List<Map<String, Object>> workerList = crmJdbc.queryForList("select workerId,workerName from Worker");
        List<ApprovalGroup> ag = approvalGroupService.findAllList(new ApprovalGroup());
        for (Map<String, Object> map : list) {
            Integer status = (Integer) map.get("status");
            Integer workerId = (Integer) map.get("applicant");// 申请人
            String proId = (String) map.get("processId");// 当前处理proId
            // 获取角色和当前用户比较是否有撤销权限
            List<String> t = new ArrayList<>();
            boolean isf = isSonAFSFlag(proId, glist, currentWorker.getWorkerId().toString());
            if ((status == 0 || status == 1) && (currentWorker.getWorkerId().equals(workerId.intValue())
                    || isf || currentWorker.getWorkerId().equals(Constants.DEFAULT_WORKER_ID))) {
                t.add("cancel");
            }
            if (isf && (status == 0 || status == 1)) {
                t.add("approval");
            } else {
                t.add("view");
            }
            map.put("function", t);
            if (ag != null) {
                ag.forEach(g -> {
                    if (g.getId().equals(proId)) {
                        map.put("processName", g.getName());
                    }
                });
            }
            workerList.forEach(w -> {
                if (w.get("workerId").toString().equals(proId)) {
                    map.put("processName", w.get("workerName"));
                }
            });
            if (ApplicationInitialEvent.approval_list.size() > 0)
                conversion(map, ApplicationInitialEvent.approval_list);
        }
        pageMode.setApiResult(responseData, list);
    }

    private List<ApprovalGroup> dataFilter(Map<String, String> params, Worker currentWorker, StringBuffer sqlWhere) {
        //登陆用户所在组
        List<String> groupIds = approvalGroupService.getGroupIdByWorkerId(currentWorker.getWorkerId());
        List<ApprovalGroup> glist = approvalGroupService.findListByIds(groupIds);
        StringBuffer cond = new StringBuffer();
        if (glist != null) {
            glist.forEach(g -> {
                cond.append("'").append(g.getId()).append("',");
            });
            cond.deleteCharAt(cond.length() - 1);
        }
        if (!currentWorker.getWorkerId().equals(Constants.DEFAULT_WORKER_ID)) {
            if (StringUtils.isNotBlank(params.get("processId"))) {//处理人ID
                Integer processId = Integer.valueOf(params.get("processId"));
                List<String> pgIds = approvalGroupService.getGroupIdByWorkerId(processId);
                List<ApprovalGroup> pglist = approvalGroupService.findListByIds(pgIds);
                StringBuffer cc = new StringBuffer();
                if (pglist != null) {
                    pglist.forEach(g -> {
                        cc.append("'").append(g.getId()).append("',");
                    });
                    cc.deleteCharAt(cc.length() - 1);
                }
                String ccc = cond.length() > 0 ? " or af.proId in(" + cc + ")" : "";
                sqlWhere.append(" and (af.proId ='" + processId + "' " + ccc + ")");
            } else if (StringUtils.isNotBlank(params.get("processedId"))) {
                Integer processId = Integer.valueOf(params.get("processedId"));
                List<String> pgIds = approvalGroupService.getGroupIdByWorkerId(processId);
                List<ApprovalGroup> pglist = approvalGroupService.findListByIds(pgIds);
                StringBuffer cc = new StringBuffer();
                if (pglist != null) {
                    pglist.forEach(g -> {
                        cc.append("'").append(g.getId()).append("',");
                    });
                    cc.deleteCharAt(cc.length() - 1);
                }
                String ccc = cond.length() > 0 ? " or af.proId in(" + cc + ")" : "";
                sqlWhere.append(" and (ad.id in (select approvalDataId from ApprovalFlow where inspector=" + params.get("processedId") + " group by approvalDataId) or af.proId ='" + processId + "' " + ccc + " )");
            } else if (StringUtils.isNotBlank(params.get("workerIds"))) {//申请人数据范围
                String c = cond.length() > 0 ? " or af.proId in(" + cond + ")" : "";
                sqlWhere.append(" and (w.workerId  in (" + params.get("workerIds") + ") or af.proId ='" + currentWorker.getWorkerId() + "' " + c + ")");
            }
        }
        if (StringUtils.isNotBlank(params.get("groupId"))) {//处理组Id
            sqlWhere.append(" and af.inspector  in (" + params.get("groupId") + ")");
        }
        if (StringUtils.isNotBlank(params.get("applicant"))) {//申请人
            Integer wid = -1;
            try {
                wid = Integer.valueOf(params.get("applicant"));
            } catch (Exception e) {
            }
            sqlWhere.append(" and (w.workerName like '%").append(params.get("applicant")).append("%'").append(" or ad.workerId=").append(wid).append(")");
        }
        if (StringUtils.isNotBlank(params.get("status"))) {//状态
            sqlWhere.append(" and ad.status in (" + params.get("status") + ") ");
        }
        if (StringUtils.isNotBlank(params.get("applicantTimeA"))) {//申请开始时间
            sqlWhere.append(" and ad.applicationtime >=").append("'").append(params.get("applicantTimeA")).append("'");
        }
        if (StringUtils.isNotBlank(params.get("applicantTimeB"))) {//申请结束时间
            sqlWhere.append(" and ad.applicationtime <=").append("DATE_ADD('").append(params.get("applicantTimeB")).append("',INTERVAL 1 DAY)");
        }
        return glist;
    }

    @Override
    public void searchContract(ResponseData responseData, PageMode pageMode, Map<String, String> params,
                               Worker currentWorker2) throws Exception {
        StringBuffer sqlfrom = new StringBuffer(" from ApprovalData ad inner join Worker w on w.workerId=ad.workerId left join ApprovalFlow af on af.id=ad.approvalFlowId left join Worker ww on ww.workerId=af.inspector  left join ContractOrder co on co.id=(select contractId from ContractOrderPayRecord cop where ad.buisId=cop.id limit 1)");
        StringBuffer sqlWhere = new StringBuffer(" where ad.approvalType='" + Constants.APPROVAL_CONTRACT + "' ");
        //登陆用户所在组
        List<ApprovalGroup> glist = dataFilter(params, currentWorker2, sqlWhere);
        if (StringUtils.isNotBlank(params.get("contractStatus"))) {//合同状态
            sqlWhere.append(" and co.status in (" + params.get("contractStatus") + ") ");
        }
        if (StringUtils.isNotBlank(params.get("payStatus"))) {//支付状态
            sqlWhere.append(" and co.payStatus in (" + params.get("payStatus") + ") ");
        }
        if (StringUtils.isNotBlank(params.get("signTimeA"))) {//签订时间开始时间
            sqlWhere.append(" and co.signTime >=").append("'").append(params.get("signTimeA")).append("'");
        }
        if (StringUtils.isNotBlank(params.get("signTimeB"))) {//签订时间结束时间
            sqlWhere.append(" and co.signTime <=").append("DATE_ADD('").append(params.get("signTimeB")).append("',INTERVAL 1 DAY)");
        }
        if (StringUtils.isNotBlank(params.get("memberId"))) {//客户id
            sqlWhere.append(" and co.memberId in (" + params.get("memberId") + ") ");
        }
        StringBuffer sql = new StringBuffer("select co.memberId,ww.workerName realProcessName,ww.workerId realProcessId,(case co.status when 0 then '未完成' when 1 then '已完成' end) contractStatus,(case co.payStatus when 0 then '未付款' when 1 then '未付清' when 2 then '付款完成' end) payStatus,co.contractName,co.serveCount,co.serveCycle,co.contractAmount,co.id contractId,ad.id approvalDataId,date_format(co.signTime,'%Y-%m-%d %H:%i:%s') signTime,date_format(ad.applicationtime,'%Y-%m-%d %H:%i:%s') applicationtime,ad.workerId applicant,w.workerName applicantName,ad.approvalType,ad.action,ad.`status`,ad.approvalresult,date_format(af.examineTime,'%Y-%m-%d %H:%i:%s') examineTime,(case ad.`status` when 0 then '待审批' when 1 then '审批中' when 2 then '已通过' when 3 then '已驳回' when 4 then '已撤销' else '未知' end) statusName,af.proId processId,ad.version,af.id id,af.remake remark ");
        StringBuffer csql = new StringBuffer("select count(1) ");
        csql.append(sqlfrom).append(sqlWhere);
        pageMode.setTotal(crmJdbc.queryForObject(csql.toString(), Long.class));
        sql.append(sqlfrom).append(sqlWhere).append(" order by (case when ad.status in(0,1) then 0 else 1 end),af.examineTime desc,co.signTime desc  limit " + pageMode.getFirst() + "," + pageMode.getPageSize());
        List<Map<String, Object>> list = crmJdbc.queryForList(sql.toString());
        List<Map<String, Object>> workerList = crmJdbc.queryForList("select workerId,workerName from Worker");
        List<ApprovalGroup> ag = approvalGroupService.findAllList(new ApprovalGroup());
        for (Map<String, Object> map : list) {
            Integer status = (Integer) map.get("status");
            Integer workerId = (Integer) map.get("applicant");// 申请人
            String proId = (String) map.get("processId");// 当前处理proId
            String signTime = (String) map.get("signTime");// 转换vip时间
            // 获取角色和当前用户比较是否有撤销权限
            List<String> t = new ArrayList<>();
            boolean isf = isSonAFSFlag(proId, glist, currentWorker2.getWorkerId().toString());
            if ((status == 0 || status == 1) && (currentWorker2.getWorkerId().equals(workerId.intValue())
                    || isf || currentWorker2.getWorkerId().equals(Constants.DEFAULT_WORKER_ID))) {
                t.add("cancel");
            }
            if ((isf || currentWorker2.getWorkerId().equals(Constants.DEFAULT_WORKER_ID)) && (status == 0 || status == 1)) {
                t.add("approval");
            } else {
                t.add("view");
            }
            if (signTime == null && isf && status == 2) {
                t.add("vip");
            }
            map.put("function", t);
            if (ag != null) {
                ag.forEach(g -> {
                    if (g.getId().equals(proId)) {
                        map.put("processName", g.getName());
                    }
                });
            }
            workerList.forEach(w -> {
                if (w.get("workerId").toString().equals(proId)) {
                    map.put("processName", w.get("workerName"));
                }
            });
            if (ApplicationInitialEvent.approval_list.size() > 0)
                conversion(map, ApplicationInitialEvent.approval_list);
        }
        pageMode.setApiResult(responseData, list);
    }

    @Override
    public void searchApprovalResult(ResponseData responseData, PageMode pageMode, Map<String, String> params,
                                     Worker currentWorker2) {
        StringBuffer sqlfrom = new StringBuffer(" from ApprovalData ad inner join Worker w on w.workerId=ad.workerId left join ApprovalFlow af on af.id=ad.approvalFlowId left join Worker ww on ww.workerId=af.inspector");
        StringBuffer sqlWhere = new StringBuffer(" where w.workerId is not null ");
        //去掉了默认的权限设置
        if (StringUtils.isNotBlank(params.get("deptId"))) {//部门
            sqlWhere.append(" and w.deptId  in (" + params.get("deptId") + ")");
        }
        if (StringUtils.isNotBlank(params.get("workerId"))) {//员工
            sqlWhere.append(" and w.workerId='" + params.get("workerId") + "'");
        }
        if (StringUtils.isNotBlank(params.get("approvalType"))) {//审批类型
            sqlWhere.append(" and ad.approvalType in (" + params.get("approvalType") + ")");
        }
        if (StringUtils.isNotBlank(params.get("action"))) {//审批动作
            sqlWhere.append(" and ad.action in(" + params.get("action") + ")");
        }
        if (StringUtils.isNotBlank(params.get("status"))) {//状态
            sqlWhere.append(" and ad.status in (" + params.get("status") + ") ");
        }
        if (StringUtils.isNotBlank(params.get("approvalDataId"))) {//审批编号
            sqlWhere.append(" and ad.id='" + params.get("approvalDataId") + "'");
        }
        if (StringUtils.isNotBlank(params.get("applicant"))) {//申请人
            Integer wid = -1;
            try {
                wid = Integer.valueOf(params.get("applicant"));
            } catch (Exception e) {
            }
            sqlWhere.append(" and (w.workerName like '%").append(params.get("applicant")).append("%'").append(" or ad.workerId=").append(wid).append(")");
        }
        StringBuffer sql = new StringBuffer("select ad.id,date_format(ad.applicationtime,'%Y-%m-%d %H:%i:%s') applicationtime,ad.workerId,w.workerName,ad.approvalType,ad.action,ad.`status`,ad.approvalresult,ww.workerName applicant,date_format(af.examineTime,'%Y-%m-%d %H:%i:%s') examineTime,(case ad.`status` when 0 then '待审批' when 1 then '审批中' when 2 then '已通过' when 3 then '已驳回' when 4 then '已撤销' else '未知' end) statusName,af.proId,ad.version,af.id afId");
        StringBuffer csql = new StringBuffer("select count(1) ");
        csql.append(sqlfrom).append(sqlWhere);
        pageMode.setTotal(crmJdbc.queryForObject(csql.toString(), Long.class));
        sql.append(sqlfrom).append(sqlWhere).append(" order by ad.applicationtime desc  limit " + pageMode.getFirst() + "," + pageMode.getPageSize());
        List<Map<String, Object>> list = crmJdbc.queryForList(sql.toString());
        List<String> groupIds = approvalGroupService.getGroupIdByWorkerId(currentWorker2.getWorkerId());
        List<ApprovalGroup> glist = approvalGroupService.findListByIds(groupIds);
        for (Map<String, Object> map : list) {
            Integer status = (Integer) map.get("status");
            Integer workerId = (Integer) map.get("workerId");// 申请人
            String proId = (String) map.get("proId");// 当前处理proId
            // 获取角色和当前用户比较是否有撤销权限
            String t = "";
            if (status == 0 || status == 1) {
                t = "view";
            }
            if ((status == 0 || status == 1) && (currentWorker2.getWorkerId().equals(workerId.intValue())
                    || isSonAFSFlag(proId, glist, currentWorker2.getWorkerId().toString()) || currentWorker2.getWorkerId().equals(Constants.DEFAULT_WORKER_ID))) {
                t = "approval";
            }
            map.put("function", t);
            if (ApplicationInitialEvent.approval_list.size() > 0)
                conversion(map, ApplicationInitialEvent.approval_list);
        }
        pageMode.setApiResult(responseData, list);
    }

    /**
     * 功能描述: 获取流程名称
     *
     * @return:
     * @author: DoubleLi
     * @date: 2019/4/25 16:04
     */
    private void conversion(Map<String, Object> map, Set<Map<String, Object>> approvalType) {
        for (Map<String, Object> map1 : approvalType) {
            if (map1.get("value").toString().equals(map.get("approvalType").toString())) {
                map.put("approvalTypeName", map1.get("name").toString());
            }
        }
    }

    @Override
    public void updateApprovalDataApprovalFlowId(Integer approvalDataId, Integer approvalFlowId) {
        String sql = "update ApprovalData set approvalFlowId=? where id=?";
        crmJdbc.update(sql, approvalFlowId, approvalDataId);
    }

    @Override
    public Map<String, Object> searchApprovalDetail(Integer approvalDataId) {
        String sql =
                "select af.id,af.approvalDataId,date_format(ad.applicationtime,'%Y-%m-%d %H:%i:%s')applicationtime,w.workerName,ad.approvalType,ad.action,ad.version,af.approvalname,ad.execResult,ad.status from ApprovalFlow af inner join ApprovalData ad on af.approvalDataId=ad.id inner join Worker w on w.workerId=ad.workerId where ad.id=? order by af.id desc limit 1";
        return crmJdbc.queryForMap(sql, approvalDataId);
    }

    @Override
    public Integer schedule(Integer approvalDataId) {
        try {
            //最后一次处理
            ApprovalFlow approvalFlow = getLastTimeApprovalFlow(searchApprovalData(approvalDataId));
            if (approvalFlow == null)
                return 0;
            //查询审批类型对应的流程设置
            List<ApprovalFlowSetting> afsList = searchApprovalFlowSettingByType(approvalFlow.getApprovalType());
            int i = 0;
            for (ApprovalFlowSetting approvalFlowSetting : afsList) {
                if (approvalFlowSetting.getId().equals(approvalFlow.getApprovalFlowSettingid())) {
                    i = afsList.lastIndexOf(approvalFlowSetting) + 1;
                    break;
                }
            }
            BigDecimal a = new BigDecimal(i);
            BigDecimal b = new BigDecimal(afsList.size());
            Double sc = a.divide(b).doubleValue() * 100;
            return sc.intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public List<Map<String, Object>> scheduleDetail(ApprovalData approvalData) {
        //最后一次处理
        ApprovalFlow approvalFlow = getLastTimeApprovalFlow(searchApprovalData(approvalData.getId()));
        String updateTime = null;
        if (approvalFlow != null) {
            updateTime = crmJdbc.queryForObject("select updateTime from ApprovalFlowSetting afs where afs.id=?", String.class, approvalFlow.getApprovalFlowSettingid());
        }
        StringBuffer sql = new StringBuffer("select afs.id,ifnull(w.workerName,ag.`name`) `name` from ApprovalFlowSetting afs" +
                " left join Worker w on w.workerId=afs.workerId left join ApprovalGroup ag on ag.id=afs.groupId " +
                "where approvalType=? and ");
        sql.append(updateTime == null ? "afs.updateTime is null" : "afs.updateTime='" + updateTime + "'");
        sql.append(" order by sort");
        List<Map<String, Object>> resList = crmJdbc.queryForList(sql.toString(), approvalData.getApprovalType());
        boolean falg = false;
        for (Map<String, Object> res : resList) {
            Integer id = (Integer) res.get("id");
            if (approvalFlow != null && id.equals(approvalFlow.getApprovalFlowSettingid())) {
                res.put("status", 1);
                falg = true;
            } else {
                res.put("status", 0);
            }
        }
        if (falg) {
            for (Map<String, Object> res : resList) {
                res.put("status", 1);
                Integer id = (Integer) res.get("id");
                if (id.equals(approvalFlow.getApprovalFlowSettingid())) {
                    break;
                }
            }
        }
        return resList;
    }
}