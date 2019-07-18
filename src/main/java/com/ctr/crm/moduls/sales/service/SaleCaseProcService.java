package com.ctr.crm.moduls.sales.service;

import java.util.List;

import com.ctr.crm.moduls.hrm.models.Worker;
import org.springframework.web.multipart.MultipartFile;

import com.ctr.crm.moduls.sales.models.SaleCaseProc;

/**
 * 说明：
 * @author eric
 * @date 2019年5月17日 上午10:53:47
 */
public interface SaleCaseProcService {

	void save(SaleCaseProc caseProc, MultipartFile[] files);
	SaleCaseProc select(Long procId);
	/**
	 * 获取指定客户的小记记录
	 * @param memberId
	 * @param currentWorker
	 * @param notesType 小记类型 ，不填为所有小记
	 * @return
	 */
	public List<SaleCaseProc> selectByNotesType(Long memberId, Integer notesType, Worker currentWorker);
}
