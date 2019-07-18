package com.ctr.crm.commons;

import com.ctr.crm.moduls.purview.models.DataRange;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 说明：
 * @author eric
 * @date 2019年4月28日 下午5:29:11
 */
@ApiModel(value="CommonSearch", description="公用查询对象")
public class CommonSearch {

	/**数据范围对象*/
	@ApiModelProperty(hidden=true)
	private DataRange range;
	@ApiModelProperty(value="当前页")
	private Integer page = 1;
	@ApiModelProperty(value="页大小")
	private Integer pageSize = 15;
	public DataRange getRange() {
		return range;
	}
	public void setRange(DataRange range) {
		this.range = range;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}
