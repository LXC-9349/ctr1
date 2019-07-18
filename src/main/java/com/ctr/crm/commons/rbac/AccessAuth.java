package com.ctr.crm.commons.rbac;

import javax.annotation.Resource;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.purview.models.DataRange;
import com.ctr.crm.moduls.purview.service.RbacService;
import org.springframework.stereotype.Component;

/**
 * 说明：
 * @author eric
 * @date 2019年4月28日 下午6:36:03
 */
@Component
public class AccessAuth {

	private static RbacService rbacService;
	
	/**
	 * 获取员工最大的数据范围<br>
	 * 详情见{@link DataRange}
	 * @param deptId
	 * @param currentWorker
	 * @return
	 */
	public static DataRange rangeAuth(Integer deptId, Worker currentWorker){
		return rbacService.getDataRange(deptId, currentWorker);
	}
	
	@Resource
	public void setRbacService(RbacService rbacService) {
		AccessAuth.rbacService = rbacService;
	}
}
