package com.ctr.crm.moduls.hrm.service;

import java.util.List;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.models.Dept;

/**
 * 说明：
 * @author eric
 * @date 2019年4月16日 下午6:26:27
 */
public interface DeptService {

	/**
	 * 新增部门
	 * @param deptName
	 * @param parentId
	 * @param custom 是否自定义，非自定义不可删除 1是 0否
	 * @return 部门ID
	 */
	Integer insert(String deptName, Integer parentId, Integer custom);
	boolean update(Dept dept);
	
	Dept select(Integer deptId);
	
	boolean delete(Integer deptId);
	
	boolean hasChildDept(Integer deptId);
	
	List<Dept> treeDept();
	
	/**
	 * 当前登录人的最大的部门数据范围
	 * @param currentWorker
	 * @return
	 */
	List<Dept> treeDeptRange(Worker currentWorker);
	
	void defaultCompany();
	Integer defaultDept();

	Dept selectCache(Integer deptId);

	String getDeptName(Integer deptId);
	Integer[] getDeptIds(Integer deptId);

	List<Dept> selectAll();

}
