package com.ctr.crm.moduls.contract.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ctr.crm.moduls.approval.result.ApprovalResult;
import com.ctr.crm.moduls.base.dao.BaseDao;
import com.ctr.crm.moduls.contract.dao.ContractOrderPayRecordDao;
import com.ctr.crm.moduls.contract.models.ContractOrder;
import com.ctr.crm.moduls.contract.models.ContractOrderPayRecord;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.interceptors.ApprovalInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.commons.utils.file.FileCommonUtils;
import com.ctr.crm.moduls.contract.service.ContractOrderPayRecordService;
import com.ctr.crm.moduls.contract.service.ContractOrderService;
import com.superaicloud.fileserver.utils.PublicFileUtils;
import com.superaicloud.fileserver.utils.UploadFileUtils;

/**
 * @author DoubleLi
 * @date 2019-05-14
 */
@Service("contractOrderPayRecordService")
public class ContractOrderPayRecordServiceImpl implements ContractOrderPayRecordService {

    @Autowired
    private ContractOrderPayRecordDao contractOrderPayRecordDao;
    @Autowired
    private BaseDao baseDao;
    @Autowired
    @Lazy
    private ContractOrderService contractOrderService;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, ContractOrderPayRecord contractOrderPayRecord) {
        pageMode.setSqlFrom("select * from ContractOrderPayRecord");
        pageMode.setSqlWhere("deleted=0");
        if (contractOrderPayRecord.getContractId() != null) {
            pageMode.setSqlWhere("contractId = '" + contractOrderPayRecord.getContractId() + "'");
        }
        if (contractOrderPayRecord.getWorkerId() != null) {
            pageMode.setSqlWhere("workerId = '" + contractOrderPayRecord.getWorkerId() + "'");
        }
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        pageMode.setOrderBy("order by createTime desc");
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        for (Map<String, Object> datum : data) {
            String pic=(String)datum.get("pic");
            Integer contractId=(Integer)datum.get("contractId");
            if(StringUtils.isNotBlank(pic)&&contractId!=null){
                datum.put("pic",PublicFileUtils.getPublicFileUrl(contractId.longValue(), "0", pic));
            }
        }
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public ContractOrderPayRecord get(Integer id) {
        if (id == null) {
            return null;
        }
        ContractOrderPayRecord contractOrderPayRecord=contractOrderPayRecordDao.get(id);
        if(StringUtils.isNotBlank(contractOrderPayRecord.getPic())&&contractOrderPayRecord.getContractId()!=null){
            contractOrderPayRecord.setPic(PublicFileUtils.getPublicFileUrl(contractOrderPayRecord.getContractId().longValue(), "0", contractOrderPayRecord.getPic()));
        }
        return contractOrderPayRecord;
    }

    @Override
    public List<ContractOrderPayRecord> findList(ContractOrderPayRecord contractOrderPayRecord) {
        return contractOrderPayRecordDao.findList(contractOrderPayRecord);
    }

    @Override
    public List<ContractOrderPayRecord> findAllList() {
        return contractOrderPayRecordDao.findAllList();
    }

    @Override
    public ApprovalResult insert(ContractOrderPayRecord contractOrderPayRecord, MultipartFile file, Worker currentWorker) {
        ApprovalResult approvalResult=new ApprovalResult();
        ContractOrder contractOrder = contractOrderService.get(contractOrderPayRecord.getContractId());
        if (contractOrder == null || file == null) {
            approvalResult.setErrMsg("合同不存在");
            approvalResult.setType(ApprovalInterceptor.FAIL);
            return approvalResult;
        }
        BigDecimal contractAmount = contractOrder.getContractAmount();//总金额
        BigDecimal alreadyAmoun = contractOrder.getAlreadyAmount();//已缴费金额
        //计算剩余未缴费
        BigDecimal alrAmount = contractAmount.subtract(alreadyAmoun).subtract(contractOrderPayRecord.getAmount());
        contractOrderPayRecord.setAlrAmount(alrAmount.compareTo(BigDecimal.ZERO) == -1 ? BigDecimal.ZERO : alrAmount);
        if (contractOrderPayRecord.getCreateTime() == null) {
            contractOrderPayRecord.setCreateTime(new Date());
        }
        contractOrderPayRecord.setWorkerId(currentWorker.getWorkerId());
        synchronized (this) {
            contractOrderPayRecord.setId(contractOrderPayRecordDao.getID());
        }
        contractOrderPayRecord.setStatus(0);
        if (contractOrderPayRecordDao.insert(contractOrderPayRecord) > 0) {
            //添加图片凭证
            String fileName = uploadFile(file, contractOrderPayRecord.getContractId());
            contractOrderPayRecord.setPic(fileName);
            contractOrderPayRecordDao.update(contractOrderPayRecord);
            //走审批流程
            approvalResult=contractOrderService.approvalUpdateStatus(contractOrderPayRecord.getId());
        }
        return approvalResult;
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
    public int insertBatch(List<ContractOrderPayRecord> contractOrderPayRecords) {
        return contractOrderPayRecordDao.insertBatch(contractOrderPayRecords);
    }

    @Override
    public int update(ContractOrderPayRecord contractOrderPayRecord) {
        return contractOrderPayRecordDao.update(contractOrderPayRecord);
    }

    @Override
    public Boolean delete(Integer id) {
        if (id == null) {
            return false;
        }
        return contractOrderPayRecordDao.delete(id);
    }

}
