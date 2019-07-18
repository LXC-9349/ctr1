package com.ctr.crm.commons.configuration;

import java.util.Date;

import com.ctr.crm.commons.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

/**
 * 说明：
 * @author eric
 * @date 2019年5月24日 下午2:19:46
 */
public class DateConverter implements Converter<String, Date>{

	@Override
	public Date convert(String source) {
		String date = StringUtils.trim(source);
		if(StringUtils.isBlank(date)){
			return null;
		}
		if(CommonUtils.isMatched("^\\d{4}-\\d{1,2}$", date)){
			return CommonUtils.parseDateFormStr(date, "yyyy-MM");
		}else if(CommonUtils.isMatched("^\\d{4}-\\d{1,2}-\\d{1,2}$", date)){
			return CommonUtils.parseDateFormStr(date, "yyyy-MM-dd");
		}else if(CommonUtils.isMatched("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$", date)){
			return CommonUtils.parseDateFormStr(date, "yyyy-MM-dd HH:mm");
		}else if(CommonUtils.isMatched("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$", date)){
			return CommonUtils.parseDateFormStr(date, "yyyy-MM-dd HH:mm:ss");
		}else if(CommonUtils.isMatched("^\\d{4}\\d{1,2}$", date)){
			return CommonUtils.parseDateFormStr(date, "yyyyMM");
		}else if(CommonUtils.isMatched("^\\d{4}\\d{1,2}\\d{1,2}$", date)){
			return CommonUtils.parseDateFormStr(date, "yyyyMMdd");
		}else if(CommonUtils.isMatched("^\\d{4}\\d{1,2}\\d{1,2}\\d{1,2}\\d{1,2}$", date)){
			return CommonUtils.parseDateFormStr(date, "yyyyMMddHHmm");
		}else if(CommonUtils.isMatched("^\\d{4}\\d{1,2}\\d{1,2}\\d{1,2}\\d{1,2}\\d{1,2}$", date)){
			return CommonUtils.parseDateFormStr(date, "yyyyMMddHHmmss");
		}else{
			throw new IllegalArgumentException("Invalid date value "+ source);
		}
	}

}
