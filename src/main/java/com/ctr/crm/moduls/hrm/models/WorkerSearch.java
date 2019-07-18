package com.ctr.crm.moduls.hrm.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.ctr.crm.commons.CommonSearch;

/**
 * 说明：
 * @author eric
 * @date 2019年4月28日 下午6:21:54
 */
@ApiModel
public class WorkerSearch extends CommonSearch {

	@ApiModelProperty(value="部门ID")
	private Integer deptId;

	public Integer getDeptId() {
		return deptId;
	}

	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}
}
