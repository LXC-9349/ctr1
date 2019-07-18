package com.ctr.crm.interceptors;

import com.alibaba.fastjson.JSONArray;
import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.annontations.Approval;
import com.ctr.crm.commons.annontations.ApprovalVerify;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.SpringContextUtils;
import com.ctr.crm.moduls.approval.modules.ApprovalData;
import com.ctr.crm.moduls.approval.modules.ApprovalFlow;
import com.ctr.crm.moduls.approval.modules.ApprovalFlowSetting;
import com.ctr.crm.moduls.approval.result.ApprovalResult;
import com.ctr.crm.moduls.approval.service.ApprovalService;
import com.ctr.crm.moduls.approvalgroup.service.ApprovalGroupService;
import com.ctr.crm.moduls.hrm.models.Worker;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 需要审批的方法拦截器 (需要拦截的service 需要在com.ctr.crm.commons.configuration.ApprovalConfig.transactionAutoProxy中
 * 配置 beanNames 拦截指定service)
 *
 * @author DoubleLi
 * @return 返回结果为String类型  approval：（当前操作需要审批通过后才能执行，请在审批管理中查看审批进度！） pass: 已执行操作，不需经过审批流  fail：出现错误
 * @time 2019年4月25日
 */
public class ApprovalInterceptor implements MethodInterceptor {
    private static final Logger log = LoggerFactory.getLogger(ApprovalInterceptor.class);
    @Resource
    private ApprovalService approvalService;
    @Resource
    private ApprovalGroupService approvalGroupService;
    public static final String PASS = "pass";
    public static final String APPROVAL = "approval";
    public static final String FAIL = "fail";

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Method method = methodInvocation.getMethod();
        Approval approval = AnnotationUtils.findAnnotation(method, Approval.class);// Approval注解
        boolean runMethod = false;//标识是否执行实际方法(不满足审批条件需执行)
        String beanName = "";//service名称
        String realMethod = "";//完成流程后执行方法
        boolean submit = true;
        ApprovalResult approvalResult = new ApprovalResult();
        try {
            /**========对添加注解方法进行操作=============*/
            app:
            if (approval != null) {
                Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
                if (currentWorker == null) {
                    approvalResult.setType(FAIL);
                    approvalResult.setErrMsg(ResponseStatus.no_login.getStatusDesc());
                    return approvalResult;
                }
                /** 获取注解的参数 */
                submit = approval.submit();// 为true先执行业务方法返回id存入流程
                String value = approval.value();// 审批类型标识
                String name = approval.name();// 审批类型名称
                beanName = approval.beanName();// service名称
                realMethod = approval.method();// 完成流程后执行方法
                String delmethod = approval.delmethod();// 执行删除的方法
                String viewUrl = approval.viewUrl();//数据展示方法
                String action = approval.action();//执行动作
                /** 超管直接执行*/
                if (currentWorker.getWorkerId().equals(Constants.DEFAULT_WORKER_ID)) {
                    runMethod = true;
                    break app;// 退出审批流
                }
                /** 查询流程设置表是否有添加流程设置 */
                List<ApprovalFlowSetting> afsList = approvalService.searchApprovalFlowSettingByType(value);
                ApprovalFlowSetting fristApprovalFlowSetting = null;
                if (afsList != null && afsList.size() > 0) {
                    fristApprovalFlowSetting = afsList.get(0);
                }
                /** 流程规则是不存在*/
                if (fristApprovalFlowSetting == null) {
                    runMethod = true;
                    break app;// 退出审批流
                }
                /* 进入审批流 */
                if (submit) {
                    /** 先添加数据拿到（返回结果）为新增的数据id*/
                    Object result = methodInvocation.proceed();// 方法执行返回结果
                    Object id = null;
                    String msg = null;
                    approvalResult = (ApprovalResult) result;
                    if (APPROVAL.equals(approvalResult.getType())) {
                        id = approvalResult.getBusinessId();
                        if (id == null) {
                            msg = "处理错误没有返回正确的ID";
                        }
                    } else {
                        msg = approvalResult.getErrMsg();
                    }
                    /** 执行结果没有返回正确业务Id的情况*/
                    if (StringUtils.isNotBlank(msg)) {
                        approvalResult.setType(FAIL);
                        approvalResult.setErrMsg(msg);
                        return approvalResult;
                    }
                    /** 存入流程审批相关表 */
                    Map<String, Object> source = new HashMap<String, Object>();
                    source.put("beanName", beanName);// 业务bean
                    source.put("method", realMethod);// 执行成功方法
                    source.put("name", name);// 名称
                    source.put("submit", submit);//处理方式
                    source.put("delmethod", delmethod);// 执行删除产生的数据方法
                    source.put("viewUrl", viewUrl);// 数据展示方法
                    Map<String, Object[]> parm = new HashMap<String, Object[]>();
                    parm.put("type", new String[]{id.getClass().toString()});
                    parm.put("value", new Object[]{id});
                    source.put("parm", parm);// 参数
                    Integer approvalDataId = insertApprovalData(action, value, source, currentWorker, id);// 写入流程数据表
                    // 写入审批流程表
                    int i = insertAppovalFlow(value, approvalDataId, currentWorker);//写入流程处理表
                    if (i == -1) {
                        approvalResult.setType(FAIL);
                        approvalResult.setErrMsg("未找到流程规则");
                        return approvalResult;
                    } else if (i == -2) {//不需要经过审批流
                        //删除审批数据
                        approvalService.deleteApprovalData(approvalDataId);
                        runMethod = true;//执行实际方法
                        break app;// 退出审批流
                    }
                    approvalResult.setType(APPROVAL);
                    return approvalResult;//流程执行成功返回
                } else {
                    /** 需要判断触发点的流程*/
                    Object[] parm = methodInvocation.getArguments();// 拦截的方法参数
                    /** 初始化走审批流程 */
                    Object listParm = null;// 参数里面的带@ApprovalVerify的集合
                    Annotation[][] pa = method.getParameterAnnotations();
                    firstLoop:
                    for (int i = 0; i < pa.length; i++) {//h
                        for (int j = 0; j < pa[i].length; j++) {
                            if (ApprovalVerify.class.isInstance(pa[i][j])) {
                                listParm = parm[i];// 获取方法集合参数
                                break firstLoop;
                            }
                        }
                    }
                    if (listParm != null) {// 集合长度判断==========自行添加你的集合类型
                        int length = 0;
                        // 此处先定义四种类型还有其他参数类型自行添加
                        if (listParm instanceof ArrayList) {
                            ArrayList al = (ArrayList) listParm;
                            length = al.size();
                        } else if (listParm instanceof LinkedList) {
                            LinkedList ll = (LinkedList) listParm;
                            length = ll.size();
                        } else if (listParm instanceof List) {
                            List ll = (List) listParm;
                            length = ll.size();
                        } else if (listParm instanceof Object[]) {
                            Object[] o = (Object[]) listParm;
                            length = o.length;
                        } else if (listParm instanceof Integer) {
                            Integer o = (Integer) listParm;
                            length = o.intValue();
                        } else {
                            approvalResult.setType(FAIL);
                            approvalResult.setErrMsg("未找到需要处理的集合");
                            return approvalResult;
                        }
                        Integer facto = fristApprovalFlowSetting.getFactor() == null ? 0 : fristApprovalFlowSetting.getFactor();//流程设置定义的触发点
                        if (length < facto.intValue()) {//没有达到审批流条件触发点不走审批
                            runMethod = true;
                            break app;// 退出审批流
                        }
                        /* 满足触发点进入流程审批*/
                        /* 存入流程审批表 */
                        Map<String, Object> source = new HashMap<>();
                        source.put("beanName", beanName);// 业务bean
                        source.put("method", realMethod);// 执行成功方法
                        source.put("name", name);// 名称
                        source.put("submit", submit);//处理方式
                        source.put("delmethod", delmethod);// 执行删除产生的数据方法
                        source.put("viewUrl", viewUrl);// 数据展示方法
                        Map<String, Object> sparm = new HashMap<>();
                        String[] c = new String[parm.length];
                        for (int k = 0; k < parm.length; k++) {
                            c[k] = parm[k].getClass().toString();
                        }
                        sparm.put("type", c);//方法所有的的参数类型
                        sparm.put("value", parm);//方法所有的参数值
                        source.put("parm", sparm);// 参数
                        source.put("length", length);//集合数据长度
                        Integer approvalDataId = insertApprovalData(action, value, source, currentWorker);// 写入流程数据表
                        // 写入审批流程表
                        int i = insertAppovalFlow(value, approvalDataId, currentWorker);
                        if (i == -1) {
                            approvalResult.setType(FAIL);
                            approvalResult.setErrMsg("未找到流程规则");
                            return approvalResult;
                        } else if (i == -2) {//不需要经过审批流
                            //删除审批数据
                            approvalService.deleteApprovalData(approvalDataId);
                            runMethod = true;//执行实际方法
                            break app;// 退出审批流
                        }
                        approvalResult.setType(APPROVAL);
                        return approvalResult;//流程执行成功返回
                    } else {
                        approvalResult.setType(FAIL);
                        approvalResult.setErrMsg("没有设置触发点参数");
                        return approvalResult;
                    }
                }
            }
            /** ========是否要运行实际方法=============*/
            if (runMethod) {
                //通过反射执行方法
                if (submit) {//先执行返回ID的数据
                    Object id = null;
                    if (approvalResult.getBusinessId() == null) {//没有执行过审批方法
                        Object result = methodInvocation.proceed();// 审批方法执行返回Id
                        approvalResult = (ApprovalResult) result;
                        String msg = null;
                        if (APPROVAL.equals(approvalResult.getType())) {
                            id = approvalResult.getBusinessId();
                            if (id == null) {
                                msg = "处理错误没有返回正确的ID";
                            }
                        } else {
                            msg = approvalResult.getErrMsg();
                        }
                        if (StringUtils.isNotBlank(msg)) {
                            approvalResult.setType(FAIL);
                            approvalResult.setErrMsg(msg);
                            return approvalResult;
                        }
                    } else {
                        id = approvalResult.getBusinessId();
                    }
                    Class[] c = {id.getClass()};
                    //执行流程处理完后的方法
                    Method mh = findMethod(beanName, realMethod, c);
                    Object resultData = ReflectionUtils.invokeMethod(mh, SpringContextUtils.getBean(beanName), id);
                    approvalResult.setType(PASS);
                    approvalResult.setResultData(resultData);
                    return approvalResult;//流程执行成功返回
                } else {
                    //直接执行实际方法
                    Object[] parm = methodInvocation.getArguments();// 拦截的方法参数
                    Class[] c = new Class[parm.length];
                    for (int k = 0; k < parm.length; k++) {
                        c[k] = parm[k].getClass();
                    }
                    /*反射查找方法*/
                    Method mh = findMethod(beanName, realMethod, c);
                    Object resultData = ReflectionUtils.invokeMethod(mh, SpringContextUtils.getBean(beanName), parm);
                    approvalResult.setType(PASS);
                    approvalResult.setResultData(resultData);
                    return approvalResult;//流程执行成功返回
                }
            }
        } catch (Exception e) {
            log.error("error0001", e);
            approvalResult.setType(FAIL);
            approvalResult.setErrMsg("审批流方法拦截错误");
            return approvalResult;
        }
        /* ========不为审批注解放行============= */
        Object methodRes = methodInvocation.proceed();// 方法执行返回结果
        return methodRes;

    }

    @SuppressWarnings("rawtypes")
    public static Method findMethod(String beanName, String realMethod, Class[] c) {
        Method[] m = SpringContextUtils.getBean(beanName).getClass().getMethods();
        Method mh = null;
        for (Method method2 : m) {
            if (method2.getName().equals(realMethod) && classArrayEquals(c, method2.getParameterTypes()))
                mh = method2;
        }
        return mh;
    }

    /**
     * 判断两个数组类型是否相同
     *
     * @param a
     * @param a2
     * @return
     * @time 2018年10月12日
     * @author DoubleLi
     */
    public static boolean classArrayEquals(Object[] a, Object[] a2) {
        if (a == a2)
            return true;
        if (a == null || a2 == null)
            return false;

        int length = a.length;
        if (a2.length != length)
            return false;

        for (int i = 0; i < length; i++) {
            Object o1 = a[i];
            Object o2 = a2[i];
            if (!(o1 == null ? o2 == null : classEquals(o1, o2)))
                return false;
        }
        return true;
    }

    public static boolean classEquals(Object o1, Object o2) {
        if (o1.equals(o2)) {
            return true;
        }
        if (o2.getClass().isInstance(o1)) {
            return true;
        }
        return false;
    }

    /**
     * 写入审批流程表
     *
     * @param value
     * @param approvalDataId
     * @param currentWorker
     * @return -1写入失败 >=1正常处理 -2不需要经过审批流
     * @throws Exception
     * @time 2018年9月30日
     * @author DoubleLi
     */
    private int insertAppovalFlow(String value, Integer approvalDataId, Worker currentWorker) throws Exception {
        List<ApprovalFlowSetting> thisafsList = approvalService.searchApprovalFlowSettingByType(value);// 查询审批类型对应的流程设置
        if (thisafsList.size() == 0)
            return -1;
        ApprovalFlowSetting fristflow = null;
        List<String> groupIds = approvalGroupService.getGroupIdByWorkerId(currentWorker.getWorkerId());
        //最后节点处理人或组
        Integer workerId = thisafsList.get(thisafsList.size() - 1).getWorkerId();
        String groupId = thisafsList.get(thisafsList.size() - 1).getGroupId();
        if ((StringUtils.isNotBlank(groupId) && groupIds.contains(groupId)) || currentWorker.getWorkerId().equals(workerId)) {//当前操作用户为最后一级处理人
            return -2;
        }
        int i = 0;
        for (ApprovalFlowSetting approvalFlowSetting : thisafsList) {
            workerId = approvalFlowSetting.getWorkerId();
            groupId = approvalFlowSetting.getGroupId();
            if ((StringUtils.isNotBlank(groupId) && groupIds.contains(groupId)) || currentWorker.getWorkerId().equals(workerId)) {
                //存在下一级别取下一级别
                if (i + 1 <= thisafsList.size()) {
                    fristflow = thisafsList.get(i + 1);
                } else {
                    fristflow = approvalFlowSetting;//取最高级别无须break
                }
            }
            i++;
        }
        if (fristflow == null) {
            fristflow = thisafsList.get(0);
        }
        ApprovalFlow af = new ApprovalFlow();
        af.setApprovalFlowSettingid(fristflow.getId());
        af.setApprovalname(fristflow.getApprovalName());
        af.setApprovalType(fristflow.getApprovalType());
        af.setApprovalDataId(approvalDataId);
        if (StringUtils.isNotBlank(fristflow.getGroupId())) {
            af.setProId(fristflow.getGroupId());
        } else if (fristflow.getWorkerId() != null && fristflow.getWorkerId() != 0) {
            af.setProId(fristflow.getWorkerId().toString());
        }
        af.setStatus(3);
        Integer approvalFlowId = approvalService.insertOrUpdateApprovalFlow(af);// 写入审批流程表
        approvalService.updateApprovalDataApprovalFlowId(approvalDataId, approvalFlowId);//更新审批数据处理流
        return 1;
    }

    /**
     * 写入流程数据表
     *
     * @param value
     * @param source
     * @param currentWorker
     * @param id
     * @return
     * @throws Exception
     * @time 2018年9月29日
     * @author DoubleLi
     */
    private Integer insertApprovalData(String action, String value, Map<String, Object> source, Worker currentWorker, Object... id)
            throws Exception {
        ApprovalData ad = new ApprovalData();
        ad.setApprovalType(value);
        ad.setSource(JSONArray.toJSONString(source));
        ad.setStatus(0);
        ad.setApplicationtime(new Date());
        ad.setWorkerId(currentWorker.getWorkerId());
        ad.setAction(action);
        if (id != null && id.length > 0) {
            ad.setBuisId(id[0].toString());
        }
        return approvalService.insertOrUpdateApprovalData(ad);// 写入流程数据表
    }

}
