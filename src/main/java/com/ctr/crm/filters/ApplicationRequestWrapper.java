package com.ctr.crm.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.ctr.crm.commons.utils.StringFilterUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ApplicationRequestWrapper extends HttpServletRequestWrapper {

	protected static final Log log = LogFactory
			.getLog(ApplicationRequestWrapper.class);

	public ApplicationRequestWrapper(HttpServletRequest request) {
		super(request);
	}
	
	@Override
	public String getParameter(String name) {
		String value = super.getParameter(StringFilterUtils.filterXss(name));
		value = StringFilterUtils.filterXss(value);
		value = cleanSql(value);
		return value;
	}
	
	@Override
	public String[] getParameterValues(String name) {
		String[] values = super.getParameterValues(StringFilterUtils.filterXss(name));
		if(values != null && values.length > 0){  
            for(int i =0; i< values.length ;i++){  
                values[i] = StringFilterUtils.filterXss(values[i]);  
                values[i] = cleanSql(values[i]);
            }  
        }  
       return values;
	}
	
	@Override
	public String getHeader(String name) {
		String value = super.getHeader(StringFilterUtils.filterXss(name));
		value = StringFilterUtils.filterXss(value);
		value = cleanSql(value);
		return value;
	}
	
	@Override
	public String getRemoteAddr() {
		String remoteAddr = super.getHeader("HTTP_X_FORWARDED_FOR");
		if (StringUtils.isEmpty(remoteAddr)) {
			return super.getRemoteAddr();
		} else {
			return remoteAddr;
		}
	}
	
	private String cleanSql(String value){
		if(StringUtils.isBlank(value)) return value;
		return StringFilterUtils.filterSql(value);
	}

}
