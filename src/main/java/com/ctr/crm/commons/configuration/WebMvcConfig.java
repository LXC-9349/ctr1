package com.ctr.crm.commons.configuration;

import com.ctr.crm.commons.version.CustomRequestMappingHandlerMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.ctr.crm.interceptors.AuthInterceptor;

@Configuration
 public class WebMvcConfig extends WebMvcConfigurationSupport {

	@Autowired
	private AuthInterceptor authInterceptor;
	
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
        super.addResourceHandlers(registry);
    }
    
    @Override
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
    	return new CustomRequestMappingHandlerMapping();
    }
    
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
    	registry.addInterceptor(authInterceptor);
    }
    
    @Override
    protected void addFormatters(FormatterRegistry registry) {
    	registry.addConverter(new DateConverter());
    	super.addFormatters(registry);
    }
    
}