package com.ctr.crm.moduls.member.service;

import java.util.List;

import com.ctr.crm.moduls.hrm.models.Worker;
import org.springframework.web.multipart.MultipartFile;

import com.ctr.crm.moduls.member.models.MemberAttachment;

/**
 * 说明：
 * @author eric
 * @date 2019年5月20日 上午11:22:03
 */
public interface MemberAttachmentService {

	void insert(Long memberId, MultipartFile file, Worker currentWorker);
	void delete(Integer attachId);
	List<MemberAttachment> select(Long memberId);
}
