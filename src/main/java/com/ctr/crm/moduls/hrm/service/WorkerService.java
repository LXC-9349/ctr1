package com.ctr.crm.moduls.hrm.service;

import java.util.Date;
import java.util.List;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.models.WorkerLog;
import com.ctr.crm.moduls.hrm.models.WorkerSearch;

/**
 * 说明：
 * @author eric
 * @date 2019年4月9日 下午2:39:45
 */
public interface WorkerService {

	public List<Worker> selectAll();
	public List<Worker> selectLeaveAll();
	
	public Integer getMaxWorkerId();

	boolean insert(Worker worker);
	boolean update(Worker worker);
	
	/**
	 * 判断分机号是否被占用<br>
	 * 当workerId不为空时,如果被判断的分机号已经配置给了指定的workerId,返回false<br>
	 * 一般修改员工时传workerId参数,新增员工时不传
	 * @param lineNum
	 * @param workerId
	 * @return
	 */
	boolean isOccupy(Integer lineNum, Integer workerId);
	
	void defaultWorker(Integer deptId);
	
	Worker selectByExtNumber(Integer lineNum);
	Worker selectByAccount(String workerAccount);
	
	Worker select(Integer workerId);
	Worker selectExact(Integer workerId);
	Worker selectCache(Integer workerId);
	List<Worker> selectByDept(Integer deptId);
	List<Worker> monitor(WorkerSearch condition, Worker currentWorker);
	/**
	 * 当前登录人最大数据范围下的所有员工
	 * @param deptId 所选部门
	 * @param currentWorker 当前登录人
	 * @return
	 */
	List<Worker> workerRange(Integer deptId, Worker currentWorker);
	void login(Integer workerId, Date loginTime, String token);
	void logout(Integer workerId, Date logoutTime);
	WorkerLog getLastWorkerLog(Integer workerId);

    List<Worker> selectByWorkerIds(String workerIds);
    boolean isWhiteList(Worker currentWorker);
    List<Integer> getWhiteList();
    void addWhiteList(Integer[] workerIds);
}
