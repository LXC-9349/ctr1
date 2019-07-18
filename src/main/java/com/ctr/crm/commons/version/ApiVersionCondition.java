package com.ctr.crm.commons.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.mvc.condition.RequestCondition;

/**
 * 说明：
 * @author eric
 * @date 2019年3月27日 下午2:25:41
 */
public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {

	private final static Pattern VERSION_PREFIX_PATTERN = Pattern.compile("v(\\d+)/");
	private int apiVersion;
	
	public ApiVersionCondition(int apiVersion) {
		this.apiVersion = apiVersion;
	}
	
	@Override
	public ApiVersionCondition combine(ApiVersionCondition arg0) {
		return new ApiVersionCondition(arg0.getApiVersion());
	}

	@Override
	public int compareTo(ApiVersionCondition arg0, HttpServletRequest arg1) {
		return arg0.getApiVersion() - this.apiVersion;
	}

	@Override
	public ApiVersionCondition getMatchingCondition(HttpServletRequest arg0) {
		Matcher m = VERSION_PREFIX_PATTERN.matcher(arg0.getRequestURI());
        if (m.find()) {
            Integer version = Integer.valueOf(m.group(1));
            if (version >= this.apiVersion) {
                return this;
            }
        }
        return null;
	}
	
	public int getApiVersion() {
		return apiVersion;
	}

}
