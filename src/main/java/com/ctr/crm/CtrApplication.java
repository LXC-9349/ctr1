package com.ctr.crm;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import com.ctr.crm.listeners.ApplicationInitialEvent;
import com.ctr.crm.listeners.ApplicationInitialListener;
import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ServletComponentScan
@EnableSwagger2Doc
//@EnableApi2Doc
@MapperScan("com.ctr.crm.moduls.*.dao")
@EnableTransactionManagement
@EnableDubboConfiguration
@EnableAsync
public class CtrApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(CtrApplication.class);
		application.addListeners(new ApplicationInitialListener());
		ConfigurableApplicationContext context = application.run(args);
		context.publishEvent(new ApplicationInitialEvent(context));
	}

}
