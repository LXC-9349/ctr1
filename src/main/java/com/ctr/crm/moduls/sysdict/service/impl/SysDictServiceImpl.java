package com.ctr.crm.moduls.sysdict.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ctr.crm.moduls.base.dao.BaseDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.moduls.sysdict.dao.SysDictDao;
import com.ctr.crm.moduls.sysdict.models.SysDict;
import com.ctr.crm.moduls.sysdict.service.SysDictService;
import com.yunhus.redisclient.RedisProxy;

/**
 * @author DoubleLi
 * @date 2019-04-23
 */
@Service("sysDictService")
public class SysDictServiceImpl implements SysDictService {

    @Autowired
    private SysDictDao sysDictDao;
    @Autowired
    private BaseDao baseDao;
    private RedisProxy redis = RedisProxy.getInstance();

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, SysDict sysDict) {
        pageMode.setSqlFrom("select * from SysDict");
        pageMode.setSqlWhere("deleted=0");
        /*类型为空时查找所有父级字典*/
        if (StringUtils.isBlank(sysDict.getType())) {
            pageMode.setSqlWhere("parent = '0'");
        } else {
            /*查询所有子集*/
            pageMode.setSqlWhere("parent <> '0'");
            pageMode.setSqlWhere("type = '" + pageMode.noSqlInjection(sysDict.getType()) + "'");
        }
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public SysDict get(String id) {
        return sysDictDao.get(id);
    }

    @Override
    public List<SysDict> findList(SysDict sysDict) {
        return sysDictDao.findList(sysDict);
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public List<SysDict> findList(String... types) {
    	if(types == null || types.length == 0) return null;
    	String key = "SysDict_"+StringUtils.join(types, "_");
    	Object v = redis.get(key);
    	if(v != null){
    		return (List<SysDict>) JSON.parse((String)v);
    	}
    	List<SysDict> result = sysDictDao.findListByTypes(Arrays.asList(types));
    	if(result != null && !result.isEmpty()){
    		redis.set(key, JSON.toJSONString(result), Constants.exp_24hours);
    	}
    	return result;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public List<Map<String, Object>> treeArea() {
    	String key = "P_C_C_SysDict";
    	Object v = redis.get(key);
    	if(v != null){
    		return (List<Map<String, Object>>) JSON.parse((String)v);
    	}
    	List<SysDict> list = sysDictDao.findListByTypes(Arrays.asList("province","city","county"));
    	List<Map<String, Object>> result = treeArea(list, "0");
    	if(result != null && !result.isEmpty()){
    		redis.set(key, JSON.toJSONString(result), Constants.exp_24hours);
    	}
    	return result;
    }
    
    private List<Map<String, Object>> treeArea(List<SysDict> childList, String parentId) {
		List<Map<String, Object>> parent = new ArrayList<>();
		List<SysDict> child = new ArrayList<>();
		// 找出所有的根节点和非根节点
		if (childList != null && childList.size() > 0) {
			for (SysDict dict : childList) {
				if (parentId.equalsIgnoreCase(dict.getParent())) {
					@SuppressWarnings("serial")
					Map<String, Object> map = new HashMap<String, Object>(){
						{
							put("id", dict.getValue());
							put("name", dict.getLabel());
							put("parent", dict.getParent());
						}
					};
					parent.add(map);
				} else {
					child.add(dict);
				}
			}
		}
		// 递归获取所有子节点
		if (parent.size() > 0) {
			for (Map<String, Object> map : parent) {
				// 添加所有子级
				String childParent = (String)map.get("id");
				map.put("children", treeArea(child, childParent));
			}
		}
		return parent;
	}

    @Override
    public List<SysDict> findAllList(SysDict sysDict) {
        return sysDictDao.findAllList(sysDict);
    }

    @Override
    public String findValue(String type, String label) {
        try {
            Map<String, String> values = (Map<String, String>) redis.get("sysdictfindValue");
            if (values != null && values.keySet().contains(type + "_" + label)) {
                return values.get(type + "_" + label);
            }else{
                values=new HashMap<>();
            }
            String value = sysDictDao.findValue(type, label);
            values.put(type + "_" + label, value);
            redis.set("sysdictfindValue", values);
            return value;
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public String findLabel(String type, String value) {
    	return sysDictDao.findLabel(type, value);
    }
    
    @Override
    public int update(String type, String value, String label) {
    	int row = sysDictDao.updateLabel(type, value, label);
    	if(row == 0){
    		SysDict sysDict = new SysDict();
    		sysDict.setType(type);
    		sysDict.setValue(value);
    		sysDict.setLabel(label);
    		insert(sysDict);
    	}
    	return 1;
    }

    @Override
    public String insert(SysDict sysDict) {
        if (StringUtils.isBlank(sysDict.getId())) {
            sysDict.setId(CommonUtils.getUUID());
        }
        if (sysDict.getCreateTime() == null) {
            sysDict.setCreateTime(new Date());
        }
        sysDictDao.insert(sysDict);
        return sysDict.getId();
    }

    @Override
    public int insertBatch(List<SysDict> sysDicts) {
        return sysDictDao.insertBatch(sysDicts);
    }

    @Override
    public int update(SysDict sysDict) {
        redis.delete("sysdictfindValue");
        return sysDictDao.update(sysDict);
    }

    @Override
    public boolean delete(String id) {
        return sysDictDao.delete(id);
    }

    @Override
    public List<SysDict> findListByType(String type) {
        return sysDictDao.findListByType(type);
    }
}
