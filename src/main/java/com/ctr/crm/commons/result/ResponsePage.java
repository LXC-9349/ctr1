package com.ctr.crm.commons.result;

import java.util.List;

/**
 * 说明：分页数据格式
 * @author eric
 * @date 2019年5月13日 上午10:03:58
 */
public class ResponsePage<T> {

	private Integer page;
	private Integer pageSize;
	private Long total;
	private List<T> list;
	public ResponsePage() {}
	public ResponsePage(Integer page, Integer pageSize){
		this.page = page;
		this.pageSize = pageSize;
	}
	public ResponsePage(Integer page, Integer pageSize, Long total){
		this.page = page;
		this.pageSize = pageSize;
		this.total = total;
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
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	
	 @Override
     public String toString() {
         return "Page [total=" + total + ", pageSize=" + pageSize + ", page=" + page + "]";
     }
	
}
