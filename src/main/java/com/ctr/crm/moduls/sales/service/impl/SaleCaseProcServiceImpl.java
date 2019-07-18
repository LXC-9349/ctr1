package com.ctr.crm.moduls.sales.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.service.DeptService;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import com.ctr.crm.moduls.sales.dao.SaleCaseProcDao;
import com.ctr.crm.moduls.sales.models.SaleCaseProc;
import com.ctr.crm.moduls.sales.service.SaleCaseProcService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.Id;
import com.ctr.crm.commons.utils.file.FileCommonUtils;
import com.ctr.crm.commons.utils.file.FileCommonUtils.UploadInfo;
import com.ctr.crm.moduls.record.models.CallRecord;
import com.ctr.crm.moduls.record.service.RecordService;
import com.superaicloud.fileserver.utils.PublicFileUtils;

/**
 * 说明：
 * @author eric
 * @date 2019年5月17日 上午11:11:43
 */
@Service("saleCaseProcService")
public class SaleCaseProcServiceImpl implements SaleCaseProcService {

	@Autowired
	private SaleCaseProcDao caseProcDao;
	@Resource
	private WorkerService workerService;
	@Resource
	private DeptService deptService;
	@Resource
	private RecordService recordService;
	
	@Override
	public void save(SaleCaseProc caseProc, MultipartFile[] files) {
		if (caseProc == null) return;
		if (caseProc.getProcId() == null){
			caseProc.setProcId(Id.generateSaleProcID());
		}
		if(files != null){
			List<UploadInfo> infos = new ArrayList<>(files.length);
			for (MultipartFile file : files) {
				UploadInfo info = FileCommonUtils.upload(file, caseProc.getMemberId(), FileCommonUtils.original);
				if(info != null){
					infos.add(info);
				}
			}
			if(infos.size() > 0){
				caseProc.setAttachments(JSON.toJSONString(infos));
			}
		}
		boolean result = caseProcDao.update(caseProc);
		if(!result){
			caseProcDao.insert(caseProc);
		}
	}
	
	@Override
	public SaleCaseProc select(Long procId) {
		if(procId == null || procId <= 0) return null;
		return caseProcDao.selectByProcId(procId);
	}

	@Override
	public List<SaleCaseProc> selectByNotesType(Long memberId, Integer notesType, Worker currentWorker) {
		if (null == memberId)
			return null;
		List<SaleCaseProc> results = caseProcDao.select(memberId, notesType);
		if (null != results && !results.isEmpty()) {
			Worker worker = null;
			CallRecord callRecord = null;
			for (SaleCaseProc saleCaseProc : results) {
				if (null == saleCaseProc)
					continue;
				// 如果小记中有号码，页面展示号码根据权限是否掩码
				saleCaseProc.setProcItem(CommonUtils.mask(saleCaseProc.getProcItem(), currentWorker));
				// 附件信息
				if(saleCaseProc.getAttachments() != null){
					List<UploadInfo> attachments = JSON.parseArray(saleCaseProc.getAttachments(), UploadInfo.class);
					for (UploadInfo attachment : attachments) {
						attachment.setUploadFileName(PublicFileUtils.getPublicFileUrl(memberId, "0", attachment.getUploadFileName()));
					}
					saleCaseProc.setAttachmentList(attachments);
				}
				// 员工组织架构信息
				worker = workerService.selectCache(saleCaseProc.getWorkerId());
				if (null == worker || null == worker.getDeptId())
					continue;
				String deptName = deptService.getDeptName(worker.getDeptId());
				if (StringUtils.isNotBlank(deptName) && StringUtils.isNotBlank(worker.getWorkerName())) {
					StringBuilder detailName = new StringBuilder(deptName);
					detailName.append("/").append(worker.getWorkerName());
					saleCaseProc.setWorkerName(detailName.toString());
				}
				// 关联录音ID
				callRecord = recordService.selectByAnswerTime(saleCaseProc.getWorkerId(), saleCaseProc.getTelStart());
				if(callRecord != null){
					saleCaseProc.setRecord(recordService.getDownloadUrl(callRecord));
				}
			}
		}
		return results;
	}

}
