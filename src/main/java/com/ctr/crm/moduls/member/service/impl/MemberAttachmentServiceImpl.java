package com.ctr.crm.moduls.member.service.impl;

import java.io.IOException;
import java.util.List;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.member.dao.AttachmentDao;
import com.ctr.crm.moduls.member.service.MemberAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ctr.crm.commons.utils.file.FileCommonUtils;
import com.ctr.crm.commons.utils.file.FileCommonUtils.UploadInfo;
import com.ctr.crm.moduls.member.models.MemberAttachment;
import com.superaicloud.fileserver.utils.PublicFileUtils;
import com.superaicloud.fileserver.utils.UploadFileUtils;

/**
 * 说明：
 * @author eric
 * @date 2019年5月20日 上午11:46:03
 */
@Service("memberAttachmentService")
public class MemberAttachmentServiceImpl implements MemberAttachmentService {

	@Autowired
	private AttachmentDao attachmentDao;
	
	@Override
	public void insert(Long memberId, MultipartFile file, Worker currentWorker) {
		if(memberId == null || file == null) return;
		UploadInfo info = FileCommonUtils.upload(file, memberId, FileCommonUtils.original);
		if(info == null) return;
		MemberAttachment attachment = new MemberAttachment();
		attachment.setMemberId(memberId);
		attachment.setFileName(info.getFileName());
		attachment.setOperator(currentWorker.getWorkerId());
		attachment.setAttachmentUrl(info.getUploadFileName());
		attachmentDao.insert(attachment);
	}

	@Override
	public void delete(Integer attachId) {
		if(attachId == null) return;
		MemberAttachment attachment = attachmentDao.selectById(attachId);
		if(attachment == null) return;
		attachmentDao.delete(attachId);
		try {
			UploadFileUtils.remove(attachment.getMemberId(), "0", attachment.getAttachmentUrl());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<MemberAttachment> select(Long memberId) {
		if(memberId == null) return null;
		List<MemberAttachment> attachments = attachmentDao.select(memberId);
		for (MemberAttachment attachment : attachments) {
			attachment.setAttachmentUrl(PublicFileUtils.getPublicFileUrl(attachment.getMemberId(), "0", attachment.getAttachmentUrl()));
		}
		return attachments;
	}

}
