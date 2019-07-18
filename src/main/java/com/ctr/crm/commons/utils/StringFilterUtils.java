package com.ctr.crm.commons.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

/**
 * xss处理工具类
 * sql防注入
 * @author Administrator
 *
 */
public class StringFilterUtils {

	/**
	 * xss过滤
	 * @param value
	 * @return
	 */
	public static String filterXss(String value){
		if(StringUtils.isBlank(value)) return value;
		return StringEscapeUtils.escapeHtml4(value);
	}
	
	/**
	 * 防SQL注入
	 * @param str
	 * @return
	 */
	public static String filterSql(String str) {
        if (str == null) {
            return null;
        }
        return StringUtils.replace(str, "'", "''");
    }
}
