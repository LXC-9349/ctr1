package com.ctr.crm.moduls.hrm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.models.WorkerLog;
import com.ctr.crm.moduls.hrm.models.WorkerSearch;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import com.ctr.crm.moduls.allot.service.AllotService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.jdbc.YunhuJdbcOperations;
import com.ctr.crm.commons.rbac.AccessAuth;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.Id;
import com.ctr.crm.commons.utils.Md5;
import com.ctr.crm.moduls.hrm.dao.WorkerDao;
import com.ctr.crm.moduls.hrm.models.Dept;
import com.ctr.crm.moduls.hrm.service.DeptService;
import com.ctr.crm.moduls.purview.models.DataRange;
import com.yunhus.redisclient.RedisProxy;

/**
 * 说明：
 * @author eric
 * @date 2019年4月9日 下午2:40:28
 */
@Service("workerService")
public class WorkerServiceImpl implements WorkerService {

	private static final Log logger = LogFactory.getLog("exception");
	@Autowired
	private WorkerDao workerDao;
	@Resource
	private DeptService deptService;
	@Resource
	private YunhuJdbcOperations crmJdbc;
	private RedisProxy redisClient = RedisProxy.getInstance();
	@Autowired
	private AllotService allotService;

	@Override
	public List<Worker> selectAll() {
		List<Worker> result = workerDao.selectAll();
		for (Worker w : result) {
			if(w != null){
				w.setDeptName(deptService.getDeptName(w.getDeptId()));
			}
		}
		return result;
	}
	
	@Override
	public List<Worker> selectLeaveAll() {
		List<Worker> result = workerDao.selectLeaveAll();
		for (Worker w : result) {
			if(w != null){
				w.setDeptName(deptService.getDeptName(w.getDeptId()));
			}
		}
		return result;
	}
	
	@Override
	public boolean insert(Worker worker){
		if(worker == null) return false;
		if(worker.getWorkerId() == null){
			worker.setWorkerId(Id.generateWorkerID());
		}
		if(worker.getPsw() == null){
			worker.setPsw("111111");
		}
		String encrpytPsw = Md5.encrpyt(worker.getPsw().trim(), null);
		worker.setPsw(encrpytPsw);
		String account = CommonUtils.convertPinyin(worker.getWorkerName());
		String original = account;
		Integer number = 1;
		for(;;){
			if(selectByAccount(account) == null)
				break;
			account = original + number;
			number++;
		}
		worker.setWorkerAccount(account);
		try {
			workerDao.insert(worker);
			return true;
		} catch (Exception e) {
			logger.error(worker, e);
		}
		return false;
	}
	
	@Override
	public boolean update(Worker worker) {
		if(worker == null || worker.getWorkerId()==null) return false;
		if(StringUtils.isNotBlank(worker.getPsw())){
			worker.setPsw(Md5.encrpyt(worker.getPsw().trim(), null));
		}
		if(StringUtils.isBlank(worker.getPsw())) worker.setPsw(null);
		String account = CommonUtils.convertPinyin(worker.getWorkerName());
		Integer number = 1;
		Worker accountWorker = null;
		for(;;){
			accountWorker = selectByAccount(account);
			if(accountWorker == null || (accountWorker != null 
					&& worker.getWorkerId().equals(accountWorker.getWorkerId())))
				break;
			account = account + number;
		}
		worker.setWorkerAccount(account);
		try {
			boolean success = workerDao.update(worker);
			if(success){
				// 如果分机号未选，则置空
				if(worker.getLineNum() == null){
					workerDao.emptyLineNum(worker.getWorkerId());
				}
				// 如果员工设置为离职状态，则置空该员工占用的分机
				if(worker.getWorkerStatus() != null && worker.getWorkerStatus() == 1){
					workerDao.delete(worker.getWorkerId());
				}
				/** 更新库容*/
				allotService.updateWorkerAllot(worker.getWorkerId());
				removeWorkerRedis(worker.getWorkerId());
			}
			return success;
		} catch (Exception e) {
			logger.error(worker, e);
		}
		return false;
	}
	
	@Override
	public boolean isOccupy(Integer lineNum, Integer workerId) {
		if(lineNum == null) return false;
		Worker w = selectByExtNumber(lineNum);
		//分机号没被任何人占用，返回false
		if(w == null) return false;
		if(workerId == null) return true;
		//如果分机号绑定的工号等于workerId,返回false
		if(w.getWorkerId().equals(workerId)) return false;
		return true;
	}
	
	@Override
	public Integer getMaxWorkerId() {
		return workerDao.getMaxWorkerId();
	}
	
	@Override
	public void defaultWorker(Integer deptId) {
		Worker worker = new Worker();
		worker.setWorkerId(Constants.DEFAULT_WORKER_ID);
		worker.setWorkerName(Constants.DEFAULT_WORKER_NAME);
		worker.setPsw(Constants.DEFAULT_WORKER_PASSWORD);
		worker.setSkill(Constants.DIRECTOR);
		worker.setDeptId(deptId);
		insert(worker);
	}
	
	@Override
	public Worker selectByExtNumber(Integer lineNum) {
		if(lineNum == null) return null;
		Worker w = workerDao.selectByLineNum(lineNum);
		if(w != null){
			w.setDeptName(deptService.getDeptName(w.getDeptId()));
		}
		return w;
	}
	
	@Override
	public Worker selectByAccount(String workerAccount) {
		if(workerAccount == null) return null;
		return workerDao.selectByAccount(workerAccount);
	}
	
	@Override
	public Worker select(Integer workerId) {
		Worker w = selectExact(workerId);
		if(w != null){
			w.setPsw(null);
		}
		return w;
	}
	
	@Override
	public Worker selectExact(Integer workerId) {
		if(workerId == null) return null;
		Worker w = workerDao.select(workerId);
		if(w != null){
			w.setDeptName(deptService.getDeptName(w.getDeptId()));
			w.setDeptIds(deptService.getDeptIds(w.getDeptId()));
		}
		return w;
	}
	
	@Override
	public Worker selectCache(Integer workerId) {
		if (workerId == null) {
			return null;
		}
		String key = Constants.Worker_Key + workerId;
		Object v = redisClient.get(key);
		if (v != null) {
			return (Worker) v;
		}
		try {
			Worker worker = select(workerId);
			redisClient.set(key, worker, Constants.exp_12hours);
			return worker;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	@Override
	public List<Worker> selectByDept(Integer deptId) {
		Dept dept = deptService.select(deptId);
		if(dept == null) return null;
		List<Worker> result = workerDao.selectByDept(dept.getStructure());
		for (Worker w : result) {
			if(w != null){
				w.setDeptName(deptService.getDeptName(w.getDeptId()));
			}
		}
		return result;
	}

	@Override
	public void login(Integer workerId, Date loginTime, String token) {
		WorkerLog log = new WorkerLog();
		log.setWorkerId(workerId);
		log.setLogType(1);
		log.setLogTime(loginTime);
		log.setToken(token);
		workerDao.insertLog(log);
		removeWorkerLogRedis(workerId);
	}
	
	@Override
	public void logout(Integer workerId, Date logoutTime) {
		WorkerLog log = new WorkerLog();
		log.setWorkerId(workerId);
		log.setLogType(2);
		log.setLogTime(logoutTime);
		workerDao.insertLog(log);
		removeWorkerLogRedis(workerId);
	}
	
	@Override
	public WorkerLog getLastWorkerLog(Integer workerId) {
		if (workerId == null) {
			return null;
		}
		String key = Constants.WorkerLog_Key + workerId;
		Object v = redisClient.get(key);
		if (v != null) {
			return (WorkerLog) v;
		}
		try {
			WorkerLog workerLog = workerDao.selectLog(workerId);
			redisClient.set(key, workerLog, Constants.exp_12hours);
			return workerLog;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	private void removeWorkerRedis(Integer workerId) {
		if (workerId == null)
			return;
		String key = Constants.Worker_Key + workerId;
		redisClient.delete(key);
	}
	
	private void removeWorkerLogRedis(Integer workerId) {
		if (workerId == null)
			return;
		String key = Constants.WorkerLog_Key + workerId;
		redisClient.delete(key);
	}
	
	@Override
	public List<Worker> monitor(WorkerSearch condition, Worker currentWorker) {
		// 设置数据范围
		if(condition == null ) condition = new WorkerSearch();
		condition.setRange(AccessAuth.rangeAuth(condition.getDeptId(), currentWorker));
		return workerDao.monitor(condition);
	}

	@Override
	public List<Worker> selectByWorkerIds(String workerIds) {
		return  workerDao.selectByWorkerIds(workerIds);
	}
	
	@Override
	public List<Worker> workerRange(Integer deptId, Worker currentWorker) {
		DataRange range = AccessAuth.rangeAuth(deptId, currentWorker);
		return workerDao.workerRange(range);
	}
	
	@Override
	public boolean isWhiteList(Worker currentWorker) {
		if(currentWorker == null) return false;
		Object v = redisClient.get("whitelist");
		if(v != null){
			List<Integer> whitelist = JSON.parseArray((String)v, Integer.class);
			if(whitelist != null){
				return whitelist.contains(currentWorker.getWorkerId());
			}
		}
		List<Integer> whitelist = getWhiteList();
		if(whitelist == null || whitelist.isEmpty()) {
			redisClient.set("whitelist", JSON.toJSONString(new ArrayList<>()), Constants.exp_24hours);
			return false;
		}
		redisClient.set("whitelist", JSON.toJSONString(whitelist), Constants.exp_24hours);
		return whitelist.contains(currentWorker.getWorkerId());
	}
	
	@Override
	public List<Integer> getWhiteList() {
		return workerDao.getWhiteList();
	}
	
	@Override
	public void addWhiteList(Integer[] workerIds) {
		List<Integer> whiteList = getWhiteList();
		// 遍历已在白名单列表，如果不在新提交的白名单workerIds中，则删除
		for (Integer id : whiteList) {
			if(workerIds == null || !ArrayUtils.contains(workerIds, id)){
				workerDao.delWhiteList(id);
			}
		}
		if(workerIds != null && workerIds.length > 0){
			for (Integer workerId : workerIds) {
				workerDao.addWhiteList(workerId);
			}
		}
		redisClient.delete("whitelist");
	}
}
