package com.ctr.crm.moduls.report.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.report.dao.HomepageDao;
import com.ctr.crm.moduls.report.service.HomepageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ctr.crm.commons.rbac.AccessAuth;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.moduls.intentionality.models.Intentionality;
import com.ctr.crm.moduls.intentionality.service.IntentionalityService;
import com.ctr.crm.moduls.purview.models.DataRange;

/**
 * 说明：
 * @author eric
 * @date 2019年5月29日 上午10:46:52
 */
@Service("homepageService")
public class HomepageServiceImpl implements HomepageService {
	
	@Autowired
	private HomepageDao homepageDao;
	@Resource
	private IntentionalityService intentionalityService;

	@Override
	public Map<String, Object> todayAllot(Worker currentWorker) {
		DataRange range = AccessAuth.rangeAuth(null, currentWorker);
		return homepageDao.todayAllot(range);
	}

	@Override
	public Map<String, Object> todayCommunicate(Worker currentWorker) {
		DataRange range = AccessAuth.rangeAuth(null, currentWorker);
		return homepageDao.todayCommunicate(range);
	}

	@Override
	public Map<String, Object> overdueEstranged(Worker currentWorker) {
		DataRange range = AccessAuth.rangeAuth(null, currentWorker);
		return homepageDao.overdueEstranged(range);
	}

	@Override
	public List<Map<String, Object>> chanceHierarchy(Worker currentWorker) {
		String statisticsStr = intentionalityService.getStatisticsStr(1, "sc");
		if(StringUtils.isBlank(statisticsStr))
			return new ArrayList<>();
		DataRange range = AccessAuth.rangeAuth(null, currentWorker);
		Map<String, Object> data = homepageDao.chanceHierarchy(statisticsStr, range);
		if (data == null || data.isEmpty())
			return new ArrayList<>();
		Intentionality intentionality = new Intentionality();
		intentionality.setType(1);
		List<Intentionality> classProgressList = intentionalityService.findList(intentionality);
		if (null == classProgressList || classProgressList.isEmpty())
			return new ArrayList<>();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		String className = null;
		String caseClass = null;
		for (Intentionality classProgress : classProgressList) {
			if (null == classProgress)
				continue;
			className = classProgress.getName();
			caseClass = CommonUtils.evalString(classProgress.getCaseClass());
			if (StringUtils.isBlank(className) || StringUtils.isBlank(caseClass))
				continue;
			Map<String, Object> addMap = new HashMap<String, Object>();
			int number = CommonUtils.evalInt(data.get(caseClass), 0);
			addMap.put("name", className);
			addMap.put("value", number);
			result.add(addMap);
		}
		return result;
	}

}
