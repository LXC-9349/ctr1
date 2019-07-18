package com.ctr.crm.commons.sms.channel.emay.model.http.v1.dto.request;

import com.ctr.crm.commons.sms.channel.emay.model.framework.dto.PersonalityParams;


/**
 * 批量短信发送参数
 * @author Frank
 *
 */
public class SmsPersonalityAllRequest extends BaseRequest {

	private static final long serialVersionUID = 1L;

	private PersonalityParams[] smses;

	public PersonalityParams[] getSmses() {
		return smses;
	}

	public void setSmses(PersonalityParams[] smses) {
		this.smses = smses;
	}
	

}
