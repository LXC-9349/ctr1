package com.ctr.crm.listeners;

import org.springframework.context.ApplicationListener;

/**
 * 说明：
 * @author eric
 * @date 2019年4月11日 下午6:31:33
 */
public class ApplicationInitialListener implements ApplicationListener<ApplicationInitialEvent> {

	@Override
	public void onApplicationEvent(ApplicationInitialEvent arg0) {
		arg0.initApplicationData();
	}

}
