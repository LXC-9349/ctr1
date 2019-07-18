package com.ctr.crm.commons.sms.pojo;
/**
 * 说明：下行短信响应结构体
 * @author eric
 * @date 2018年3月2日 上午10:25:06
 */
public class SmsBody {

	/**短信平台唯一标识符*/
	private String uid;
	private String mobile;
	/**true成功 false失败*/
	private String success;
	/**失败时的原因*/
	private String failReason;
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getFailReason() {
		return failReason;
	}
	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	@Override
	public String toString() {
		return "SmsBody{" +
				"uid='" + uid + '\'' +
				", mobile='" + mobile + '\'' +
				", success='" + success + '\'' +
				", failReason='" + failReason + '\'' +
				'}';
	}
}
