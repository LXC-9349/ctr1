package com.ctr.crm.commons.rbac;
/**
 * 说明：系统数据范围
 * @author eric
 * @date 2019年4月16日 下午2:38:25
 */
public enum RangeField {
	
	Personal(1){
		@Override
		public String getRangeName() {
			return "个人";
		}
	},
	Department(2){
		@Override
		public String getRangeName() {
			return "本部门";
		}
	},
	Departments(3){
		@Override
		public String getRangeName() {
			return "本部门及下属部门";
		}
	},
	Company(4){
		@Override
		public String getRangeName() {
			return "全公司";
		}
	};

	private int rangeValue;
	public abstract String getRangeName();
	private RangeField(int rangeValue) {
		this.rangeValue = rangeValue;
	}
	public int getRangeValue() {
		return rangeValue;
	}
}
