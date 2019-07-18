package com.ctr.crm.moduls.record.service;

import java.util.Date;
import java.util.Map;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.record.models.CallRecord;
import com.ctr.crm.moduls.record.models.RecordSearch;
import com.ctr.crm.commons.result.ResponsePage;

public interface RecordService {
	
	boolean insertCallRecord(CallRecord callRecord);
	
	CallRecord selectByCallId(Integer callId);
	String getDownloadUrl(Integer callId);
	String getDownloadUrl(CallRecord record);
	
	CallRecord selectByAnswerTime(Integer workerId, Date answerTime);

	ResponsePage<Map<String, Object>> query(RecordSearch search, Worker currentWorker);
	
}
