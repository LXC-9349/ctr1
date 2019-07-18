package com.ctr.crm.moduls.contract.service.impl;

import com.ctr.crm.moduls.approval.modules.ApprovalData;
import com.ctr.crm.moduls.approval.result.ApprovalResult;
import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.contract.dao.ContractOrderDao;
import com.ctr.crm.moduls.contract.models.ContractOrder;
import com.ctr.crm.moduls.contract.models.ContractOrderPayRecord;
import com.ctr.crm.moduls.contract.service.ContractOrderPayRecordService;
import com.ctr.crm.moduls.contract.service.ContractOrderService;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;
import com.ctr.crm.moduls.member.models.MemberVip;
import com.ctr.crm.moduls.member.service.MemberService;
import com.ctr.crm.moduls.member.service.MemberVipService;
import com.ctr.crm.moduls.sales.models.SaleCase;
import com.ctr.crm.moduls.sales.service.SaleCaseService;
import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.es.MemberBean;
import com.ctr.crm.commons.es.SearchClient;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.MemberUtils;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.file.FileCommonUtils;
import com.ctr.crm.interceptors.ApprovalInterceptor;
import com.ctr.crm.moduls.intentionality.models.Intentionality;
import com.ctr.crm.moduls.intentionality.service.IntentionalityService;
import com.superaicloud.fileserver.utils.PublicFileUtils;
import com.superaicloud.fileserver.utils.UploadFileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author DoubleLi
 * @date 2019-05-14
 */
@Service("contractOrderService")
public class ContractOrderServiceImpl implements ContractOrderService {

    @Autowired
    private ContractOrderDao contractOrderDao;
    @Autowired
    private BaseDao baseDao;
    @Autowired
    @Lazy
    private ContractOrderPayRecordService contractOrderPayRecordService;
    @Resource
    private MemberService memberService;
    @Autowired
    @Lazy
    private MemberVipService memberVipService;
    @Autowired
    private SearchClient searchClient;
    @Autowired
    private SaleCaseService saleCaseService;
    @Autowired
    private IntentionalityService intentionalityService;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, ContractOrder contractOrder) {
        pageMode.setSqlFrom("select * from ContractOrder");
        pageMode.setSqlWhere("deleted=0");
        if (contractOrder.getMemberId() != null) {
            pageMode.setSqlWhere("memberId = '" + contractOrder.getMemberId() + "'");
        }
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        pageMode.setOrderBy("order by (case status when 0 then 0 else 1 end),createTime desc");
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        for (Map<String, Object> datum : data) {
            String pic=(String)datum.get("annexFile");
            Integer contractId=(Integer)datum.get("id");
            if(StringUtils.isNotBlank(pic)&&contractId!=null){
                datum.put("annexFile", PublicFileUtils.getPublicFileUrl(contractId.longValue(), "0", pic));
            }
        }
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public ContractOrder get(Integer id) {
        if (id == null) {
            return null;
        }
        ContractOrder contractOrder=contractOrderDao.get(id);
        if(StringUtils.isNotBlank(contractOrder.getAnnexFile())&&contractOrder.getId()!=null){
            contractOrder.setAnnexFile(PublicFileUtils.getPublicFileUrl(contractOrder.getId().longValue(), "0", contractOrder.getAnnexFile()));
        }
        return contractOrder;
    }

    @Override
    public List<ContractOrder> findList(ContractOrder contractOrder) {
        return contractOrderDao.findList(contractOrder);
    }

    @Override
    public List<ContractOrder> findAllList(ContractOrder contractOrder) {
        return contractOrderDao.findAllList(contractOrder);
    }

    @Override
    public String insert(ContractOrder contractOrder, MultipartFile file) {
        SaleCase saleCase = saleCaseService.getByMemberId(contractOrder.getMemberId());
        if (!saleCase.getWorkerId().equals(contractOrder.getWorkerId()) && !contractOrder.getWorkerId().equals(Constants.DEFAULT_WORKER_ID)) {
            return "非跟进人无法添加合同";
        }
        if (contractOrder.getCreateTime() == null) {
            contractOrder.setCreateTime(new Date());
        }
        synchronized (this) {
            contractOrder.setId(contractOrderDao.getID());
        }
        contractOrder.setContractNo(CommonUtils.getDay() + "_" + contractOrder.getId());
        contractOrder.setStatus(0);
        contractOrder.setPayStatus(0);
        contractOrder.setAlreadyAmount(new BigDecimal("0.00"));
        if (file != null) {
            String fileName = uploadFile(file, contractOrder.getId());
            contractOrder.setAnnexFile(fileName);
        }
        return contractOrderDao.insert(contractOrder) > 0 ? null : "添加失败";
    }

    private String uploadFile(MultipartFile file, Integer id) {
        String fileName = file.getOriginalFilename();
        String ext = FileCommonUtils.getExtensionName(fileName);
        String upLoadFileName = null;
        try {
            upLoadFileName = UploadFileUtils.upload(file.getBytes(), ext, id.longValue(), "0");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return upLoadFileName;
    }

    @Override
    public int insertBatch(List<ContractOrder> contractOrders) {
        return contractOrderDao.insertBatch(contractOrders);
    }

    @Override
    public int update(ContractOrder contractOrder) {
        return contractOrderDao.update(contractOrder);
    }

    @Override
    public Boolean delete(Integer id) {
        if (id == null) {
            return false;
        }
        return contractOrderDao.delete(id);
    }

    @Override
    public ApprovalResult approvalUpdateStatus(Integer re_id) {
        ApprovalResult approvalResult = new ApprovalResult();
        approvalResult.setBusinessId(re_id);
        approvalResult.setType(ApprovalInterceptor.APPROVAL);
        return approvalResult;
    }

    @Override
    public String realApprovalUpdateStatus(Integer re_id) {
        ContractOrderPayRecord contractOrderPayRecord = contractOrderPayRecordService.get(re_id);
        ContractOrder contractOrder = get(contractOrderPayRecord.getContractId());
        BigDecimal alrAmount = contractOrderPayRecord.getAlrAmount();//剩余未缴费
        contractOrderPayRecord.setStatus(1);
        contractOrderPayRecord.setPic(null);
        if (alrAmount.compareTo(BigDecimal.ZERO) == 0) {
            contractOrder.setStatus(1);//合同完成
            contractOrder.setPayStatus(2);//付清
        } else {
            contractOrder.setPayStatus(1);//未付清
        }
        contractOrder.setAlreadyAmount(contractOrder.getAlreadyAmount().add(contractOrderPayRecord.getAmount()));
        update(contractOrder);
        contractOrderPayRecord.setApprovalTime(new Date());
        contractOrderPayRecordService.update(contractOrderPayRecord);
        return "对账成功,审批通过";
    }

    @Override
    public String delApprovalUpdateStatus(Integer re_id) {
        ContractOrderPayRecord contractOrderPayRecord = contractOrderPayRecordService.get(re_id);
        contractOrderPayRecord.setStatus(2);
        contractOrderPayRecord.setApprovalTime(new Date());
        contractOrderPayRecord.setPic(null);
        contractOrderPayRecordService.update(contractOrderPayRecord);
        return "处理成功";
    }

    @Override
    public String isVip(Integer contractId, ApprovalData approvalData, Worker worker) {
        ContractOrder contractOrder = get(contractId);
        if (memberVipService.isVip(contractOrder.getMemberId())) {
            return "客户当前已是VIP,还有未服务完的合同,无法转换";
        }
        MemberBaseInfo memberBaseInfo = memberService.get(MemberBaseInfo.class, contractOrder.getMemberId());
        SaleCase saleCase = saleCaseService.getByMemberId(contractOrder.getMemberId());
        if (contractOrder == null || memberBaseInfo == null || saleCase == null) {
            return "参数错误";
        }
        if (!worker.getWorkerId().equals(Constants.DEFAULT_WORKER_ID)) {
            if (approvalData.getStatus() != 2) {
                return "合同审批还未通过无法转换VIP";
            }
        }
        MemberVip memberVip = new MemberVip(contractOrder.getMemberId());
        memberVip.setContractId(contractId);
        memberVip.setCompanyId(worker.getCompanyId());
        memberVip.setWorkerId(worker.getWorkerId());
        memberVip.setBeginTime(new Date());
        memberVip.setStatus(0);
        memberVip.setEndTime(CommonUtils.add(new Date(), Calendar.MONTH, contractOrder.getServeCycle()));
        //添加数据库
        memberVipService.insert(memberVip);
        contractOrder.setSignTime(new Date());
        contractOrderDao.update(contractOrder);
        MemberBean mb = new MemberBean(contractOrder.getMemberId());
        mb.setIsVip(true);
        mb.setOrderSignWorkerName(saleCase.getWorkerName());
        mb.setOrderSignWorkerId(saleCase.getWorkerId());
        mb.setOrderSignTime(contractOrder.getSignTime());
        //vip标识
        MemberUtils.setMemberType(memberBaseInfo, Constants.member_type_vip, Constants.YES);
        MemberBaseInfo mbi = new MemberBaseInfo(memberBaseInfo.getMemberId(), memberBaseInfo.getMemberType());
        //删除机会库
        Intentionality intentionality = new Intentionality();
        /** 红娘意向度*/
        boolean flag = true;
        intentionality.setType(2);
        List<Intentionality> intentionalityList = intentionalityService.findList(intentionality);
        for (Intentionality intentionality1 : intentionalityList) {
            if (intentionality1.getCaseClass().equals(saleCase.getCaseClass())) {
                flag = false;
                break;
            }
        }
        if (flag) {
            saleCaseService.deleteByMemberIds(Arrays.asList(new Long[]{memberBaseInfo.getMemberId()}));
            mb.setInSaleCase(false);
            mb.setQuitReason("客户升级VIP");
            mb.setQuitWorkerName(saleCase.getWorkerName());
            mb.setQuitWorkerId(saleCase.getWorkerId());
            mb.setWorkerName("");
            mb.setWorkerId(0);
            //不存在机会库
            MemberUtils.setMemberType(mbi, Constants.member_type_insalecase, Constants.NO);
        } else {
            //已是vip
            mb.setInSaleCase(true);
        }
        memberService.update(mbi);
        searchClient.update(mb);
        return null;
    }

    @Async
    @Override
    public void vipPayRecord(Integer recordId, ApprovalData approvalData, Worker currentWorker) {
        ContractOrderPayRecord contractOrderPayRecord = contractOrderPayRecordService.get(recordId);
        if (contractOrderPayRecord.getAlrAmount().compareTo(BigDecimal.ZERO) == 0) {//交完了全部的钱
            isVip(contractOrderPayRecord.getContractId(), approvalData, currentWorker);
        }
    }

    @Async
    @Override
    public void renewVip(Long memberId) {
        //找到已付钱的合同
        ContractOrder contractOrder = contractOrderDao.getByMemberId(memberId);
        if (contractOrder != null) {
            //系统自动操作
            Worker w = new Worker(Constants.DEFAULT_WORKER_ID, "0");
            isVip(contractOrder.getId(), null, w);
        }
    }
}
