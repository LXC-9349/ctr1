package com.ctr.crm.moduls.hrm.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ctr.crm.moduls.hrm.models.Worker;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.rbac.AccessAuth;
import com.ctr.crm.commons.rbac.RangeField;
import com.ctr.crm.commons.utils.Base64;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.moduls.hrm.dao.CompanyDao;
import com.ctr.crm.moduls.hrm.dao.DeptDao;
import com.ctr.crm.moduls.hrm.models.Company;
import com.ctr.crm.moduls.hrm.models.Dept;
import com.ctr.crm.moduls.hrm.service.DeptService;
import com.ctr.crm.moduls.purview.models.DataRange;
import com.yunhus.redisclient.RedisProxy;

/**
 * 说明：
 *
 * @author eric
 * @date 2019年4月16日 下午6:27:42
 */
@Service("deptService")
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptDao deptDao;
    @Autowired
    private CompanyDao companyDao;
    private RedisProxy redis = RedisProxy.getInstance();

    @Override
    public Integer insert(String deptName, Integer parentId, Integer custom) {
        if (StringUtils.isBlank(deptName)) return null;
        Dept parent = select(parentId);
        Dept dept = new Dept();
        dept.setDeptName(deptName);
        dept.setParentId(parentId);
        dept.setCustom(custom);
        boolean success = deptDao.insert(dept);
        if (success) {
            dept.setStructure(CommonUtils.leftPaddingZero(dept.getDeptId().toString(), Constants.DEPT_STRUCTURE_LENGTH));
            if (parent != null) {
                // 更新层级结构
                dept.setStructure(parent.getStructure()
                        + CommonUtils.leftPaddingZero(dept.getDeptId().toString(), Constants.DEPT_STRUCTURE_LENGTH));
            }
            deptDao.update(dept);
        }
        Integer deptId = dept.getDeptId();
        if (deptId == null) {
            Dept old = deptDao.selectByName(deptName);
            if (old != null) deptId = old.getDeptId();
        }
        return deptId;
    }

    @Override
    public boolean update(Dept dept) {
        if (dept == null || dept.getDeptId() == null
                || dept.getDeptName() == null) return false;
        Dept src = select(dept.getDeptId());
        if (src == null) return false;
        dept.setStructure(src.getStructure());
        deptDao.update(dept);
        redis.delete(Constants.Dept_Key + dept.getDeptId());
        return true;
    }

    @Override
    public Dept select(Integer deptId) {
        if (deptId == null) return null;
        return deptDao.select(deptId);
    }

    @Override
    public Dept selectCache(Integer deptId) {
        if (deptId == null)
            return null;
        String key = Constants.Dept_Key + deptId;
        Object v = redis.get(key);
        if (v != null) {
            return (Dept) v;
        }
        try {
            Dept dept = select(deptId);
            redis.set(key, dept, Constants.exp_12hours);
            return dept;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean delete(Integer deptId) {
        if (deptId == null) return false;
        Dept dept = select(deptId);
        if (dept == null) return false;
        if (dept.getCustom() == 0) return false;
        redis.delete(Constants.Dept_Key + deptId);
        return deptDao.delete(deptId);
    }

    @Override
    public boolean hasChildDept(Integer deptId) {
        List<Dept> depts = deptDao.selectByParentId(deptId);
        return depts != null && depts.size() > 0 ? true : false;
    }

    @Override
    public List<Dept> treeDept() {
        List<Dept> depts = deptDao.selectAll();
        return treeDept(depts, 0);
    }

    @Override
    public List<Dept> treeDeptRange(Worker currentWorker) {
        DataRange range = AccessAuth.rangeAuth(null, currentWorker);
        List<Dept> depts = deptDao.selectByRange(range);
        int parentId = 0;
        if (range.getRange() != RangeField.Company.getRangeValue()) {
            Dept currentDept = deptDao.select(currentWorker.getDeptId());
            parentId = currentDept.getParentId();
        }
        return treeDept(depts, parentId);
    }

    private List<Dept> treeDept(List<Dept> childList, int parentId) {
        List<Dept> listParentDept = new ArrayList<Dept>();
        List<Dept> listNotParentDept = new ArrayList<Dept>();
        // 找出所有的根节点和非根节点
        if (childList != null && childList.size() > 0) {
            for (Dept dept : childList) {
                if (dept.getParentId() == parentId) {
                    listParentDept.add(dept);
                } else {
                    listNotParentDept.add(dept);
                }
            }
        }
        // 递归获取所有子节点
        if (listParentDept.size() > 0) {
            for (Dept dept : listParentDept) {
                // 添加所有子级
                List<Dept> childsDept = dept.getChildren();
                if (childsDept != null && childsDept.size() > 0) {
                    dept.getChildren().addAll(this.treeDept(listNotParentDept, dept.getDeptId()));
                } else {
                    dept.setChildren(this.treeDept(listNotParentDept, dept.getDeptId()));
                }
            }
        }
        return listParentDept;
    }

    @Override
    public void defaultCompany() {
        Company company = new Company();
        company.setCompanyId("0");
        company.setCompanyName(Constants.DEFAULT_COMPANY_NAME);
        company.setAccountId(CommonUtils.getUUID());
        company.setAccountSecret(Base64.encode(CommonUtils.getUUID()));
        companyDao.insert(company);
    }

    @Override
    public Integer defaultDept() {
        Dept top = deptDao.selectTop();
        if (top != null) {
            return top.getDeptId();
        }
        return insert(Constants.DEFAULT_DEPT_NAME, 0, 0);
    }

    @Override
    public String getDeptName(Integer deptId) {
        if (null == deptId)
            return null;
        Dept dept = selectCache(deptId);
        if (null == dept)
            return null;
        if (0 == dept.getParentId())
            return dept.getDeptName();
        StringBuilder sb = new StringBuilder();
        sb.append(dept.getDeptName());
        String str = null;
        for (; ; ) {
            dept = selectCache(dept.getParentId());
            str = sb.toString();
            if (null == dept || 0 == dept.getParentId())
                break;
            sb.setLength(0);
            sb.append(null != dept ? dept.getDeptName() + "/" + str : "");

        }
        return sb.toString();
    }
    
    @Override
    public Integer[] getDeptIds(Integer deptId) {
    	if (null == deptId)
            return null;
        Dept dept = selectCache(deptId);
        if (null == dept)
            return null;
        Integer[] deptIds = new Integer[]{};
        if (0 == dept.getParentId())
            return ArrayUtils.add(deptIds, dept.getDeptId());
        deptIds = ArrayUtils.add(deptIds, dept.getDeptId());
        for (; ; ) {
            dept = selectCache(dept.getParentId());
            if (null == dept)
                break;
            deptIds = ArrayUtils.add(deptIds, dept.getDeptId());
        }
        ArrayUtils.reverse(deptIds);
        return deptIds;
    }

    @Override
    public List<Dept> selectAll() {
        return deptDao.selectAll();
    }
}
