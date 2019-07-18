package com.ctr.crm.moduls.contract.service;

import com.ctr.crm.moduls.approval.result.ApprovalResult;
import com.ctr.crm.moduls.contract.models.ContractOrderPayRecord;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.PageMode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author DoubleLi
 * @date  2019-05-14
 */
public interface  ContractOrderPayRecordService {

    void searchPage(ResponseData responseData, PageMode pageMode, ContractOrderPayRecord contractOrderPayRecord);

    ContractOrderPayRecord get(Integer id);

    List<ContractOrderPayRecord> findList(ContractOrderPayRecord contractOrderPayRecord);

    List<ContractOrderPayRecord> findAllList();

    ApprovalResult insert(ContractOrderPayRecord contractOrderPayRecord, MultipartFile file, Worker currentWorker);

    int insertBatch(List<ContractOrderPayRecord> contractOrderPayRecords);

    int update(ContractOrderPayRecord contractOrderPayRecord);

    Boolean delete(Integer id);

}
