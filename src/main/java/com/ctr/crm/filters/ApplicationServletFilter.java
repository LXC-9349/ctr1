package com.ctr.crm.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@WebFilter(urlPatterns="/*",filterName="applicationServletFilter")
public class ApplicationServletFilter implements Filter {

	protected static final Log log = LogFactory.getLog(ApplicationServletFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		ApplicationRequestWrapper requestWrapper = new ApplicationRequestWrapper((HttpServletRequest)request);
		chain.doFilter(requestWrapper, response);
	}
	
}
