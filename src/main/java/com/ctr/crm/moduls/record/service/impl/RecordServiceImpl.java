package com.ctr.crm.moduls.record.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.service.DeptService;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;
import com.ctr.crm.moduls.member.service.MemberPhoneService;
import com.ctr.crm.moduls.member.service.MemberService;
import com.ctr.crm.moduls.record.dao.RecordDao;
import com.ctr.crm.moduls.record.models.CallRecord;
import com.ctr.crm.moduls.record.models.RecordSearch;
import com.ctr.crm.moduls.record.service.CallStatService;
import com.ctr.crm.moduls.sales.service.SaleCaseService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ctr.crm.commons.configuration.Config;
import com.ctr.crm.commons.rbac.AccessAuth;
import com.ctr.crm.commons.result.ResponsePage;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.moduls.record.service.RecordService;
import com.ctr.crm.moduls.system.models.CallAddress;
import com.ctr.crm.moduls.system.service.CallAddressService;

@Service("recordService")
public class RecordServiceImpl implements RecordService {

	private static final Log LOG = LogFactory.getLog("api");

	@Autowired
	private RecordDao recordDao;
	@Resource
	private WorkerService workerService;
	@Resource
	private DeptService deptService;
	@Resource
	private CallStatService callStatService;
	@Resource
	private MemberService memberService;
	@Resource
	private MemberPhoneService memberPhoneService;
	private Executor executor = Executors.newFixedThreadPool(10);
    @Resource
    private SaleCaseService saleCaseService;
    @Autowired
	private Config config;
    @Resource
    private CallAddressService callAddressService;

	@Override
	public ResponsePage<Map<String, Object>> query(RecordSearch search, Worker currentWorker) {
		// 设置数据范围
		if(search == null ) search = new RecordSearch();
		search.setRange(AccessAuth.rangeAuth(search.getDeptId(), currentWorker));
		PageHelper.startPage(search.getPage(), search.getPageSize(), "C.startTime desc");
		List<Map<String, Object>> result = recordDao.search(search);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(result);
		ResponsePage<Map<String, Object>> page = new ResponsePage<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
		for (Map<String, Object> map : pageInfo.getList()) {
			Long memberId = CommonUtils.evalLong(map.get("memberId"));
			if(memberId == null || memberId <= 0){
				map.put("trueName", null);
				continue;
			}
			MemberBaseInfo baseInfo = memberService.get(MemberBaseInfo.class, memberId);
			if(baseInfo != null) map.put("trueName", baseInfo.getTrueName());
			map.put("downLoadUrl", getDownloadUrl(CommonUtils.evalInteger(map.get("callId"))));
		}
        page.setList(pageInfo.getList());
		return page;
	}
	
	@Override
	public CallRecord selectByCallId(Integer callId) {
		if (callId == null)
			return null;
		return recordDao.select(callId);
	}
	
	@Override
	public CallRecord selectByAnswerTime(Integer workerId, Date answerTime) {
		if(workerId == null || answerTime == null) return null;
		return recordDao.selectByAnswerTime(workerId, answerTime);
	}

	@Override
	public boolean insertCallRecord(CallRecord callRecord) {
		// 录音对象为空，直接返回false
		if(callRecord == null) return false;
		if(callRecord.getCallId() == null || callRecord.getCallType() == null)
			return false;
		
		boolean lostCall = formatCallRecord(callRecord);// 是否呼损
		
		try {
			int row = recordDao.insert(callRecord);
            if (row > 0) {
                executor.execute(() -> {
                    if (callRecord.getMemberId() != null){
                        saleCaseService.updateLastPhoneTime(callRecord.getMemberId(),callRecord.getStartTime());
                    }
                    callStatService.updateCallStat(callRecord, lostCall);
                });
            }
		} catch (Exception e) {
			LOG.info("insert CallRecord." + JSON.toJSONString(callRecord));
			return false;
		}
		return true;
	}
	
	private boolean formatCallRecord(CallRecord callRecord) {
		boolean result = false;
		// 未接通录音:接通时间空 或录音文件名为空
		if (null == callRecord.getAnswerTime() || null == callRecord.getRecordFileName()) {
			result = true;
		}
		// 计算通话时长
		if (callRecord.getAnswerTime() != null && callRecord.getCallTime() == null) {
			callRecord.setCallTime(
					(int) ((callRecord.getHangupTime().getTime() - callRecord.getAnswerTime().getTime()) / 1000));
		}
		Integer workerId = CommonUtils.evalInt(callRecord.getWorkerId(), 0);
		if (workerId <= 0) {
			// 根据lineNum(分机号)获取员工工号
			Worker worker = workerService.selectByExtNumber(callRecord.getLineNum());
			if (null != worker && null != worker.getWorkerId())
				callRecord.setWorkerId(worker.getWorkerId());
		}
		if (callRecord.getMemberId() == null) {
			callRecord.setMemberId(memberPhoneService.getMemberIdByPhone(callRecord.getPhone()));
		}
		return result;
	}
	
	@Override
	public String getDownloadUrl(CallRecord record) {
		if(record == null) return null;
		if(StringUtils.isBlank(record.getRecordFileName())) 
			return null;
		CallAddress address = callAddressService.select();
		String downFileIp = null;
		if(address != null && StringUtils.isNotBlank(address.getAddress())){
			downFileIp = address.getAddress().replaceFirst("^(http://)(.*?)(:\\d+)(/*)$", "$2");
		}
		if(downFileIp == null) downFileIp = record.getDownFileIp();
		if(StringUtils.isBlank(downFileIp)) return null;
		return "http://" + downFileIp + ":"+config.getDownloadPort()+"/down.php?file=" + record.getRecordFileName();
	}
	
	@Override
	public String getDownloadUrl(Integer callId) {
		CallRecord record = selectByCallId(callId);
		return getDownloadUrl(record);
	}

}
