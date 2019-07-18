package ${BasePackageName}${ServicePackageName}.impl;

import ${BasePackageName}${DaoPackageName}.${ClassName}Dao;
import ${BasePackageName}${EntityPackageName}.${ClassName};
import ${BasePackageName}${ServicePackageName}.${ClassName}Service;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.PageMode;
import com.ctr.crm.moduls.base.dao.BaseDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author ${Author}
 * @date  ${Date}
 */
@Service("${EntityName}Service")
public class ${ClassName}ServiceImpl implements ${ClassName}Service {

    @Autowired
    private ${ClassName}Dao ${EntityName}Dao;
    @Autowired
    private BaseDao baseDao;

    @Override
    public void searchPage(ResponseData responseData, PageMode pageMode, ${ClassName} ${EntityName}) {
        pageMode.setSqlFrom("select * from ${ClassName}");
        pageMode.setSqlWhere("deleted=0");
        // 查询总条数
        Long total = baseDao.selectLong(pageMode.getSearchCountSql());
        pageMode.setTotal(total);
        List<Map<String, Object>> data = baseDao.select(pageMode.getMysqlLimit());
        pageMode.setApiResult(responseData, data);
    }

    @Override
    public ${ClassName} get(Integer id) {
        if(id==null){
            return null;
        }
        return ${EntityName}Dao.get(id);
    }

    @Override
    public List<${ClassName}> findList(${ClassName} ${EntityName}) {
        return ${EntityName}Dao.findList(${EntityName});
    }

    @Override
    public List<${ClassName}> findAllList(${ClassName} ${EntityName}) {
        return ${EntityName}Dao.findAllList(${EntityName});
    }

    @Override
    public int insert(${ClassName} ${EntityName}) {
        ${EntityName}.setId(${EntityName}Dao.getID());
        if (${EntityName}.getCreateTime() == null){
            ${EntityName}.setCreateTime(new Date());
        }
        return ${EntityName}Dao.insert(${EntityName});
    }

    @Override
    public int insertBatch(List<${ClassName}> ${EntityName}s) {
        return ${EntityName}Dao.insertBatch(${EntityName}s);
    }

    @Override
    public int update(${ClassName} ${EntityName}) {
        return ${EntityName}Dao.update(${EntityName});
    }

    @Override
    public boolean delete(Integer id) {
        if(id==null){
            return false;
        }
        return ${EntityName}Dao.delete(id);
    }

}
