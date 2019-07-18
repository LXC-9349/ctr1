package com.ctr.crm.commons.utils;

import com.ctr.crm.moduls.hrm.models.Dept;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @program: marry_crm
 * @description 部门操作工具
 * @author: DoubleLi
 * @create: 2019-05-08 10:22
 **/
public class DeptUtils {
    /**
     * 功能描述:部门子集参数填充
     *
     * @param:
     * @return:
     * @author: DoubleLi
     * @date: 2019/4/25 15:56
     */
    public static String getDeptSonIds(Integer deptId, List<Dept> deList) {
        if (deptId != null) {
            Set<Integer> deptIds = new HashSet<Integer>();
            convetDeptId(deptId, deList, deptIds);
            String deptIdres = ArrayUtils.toString(deptIds);
            return deptIdres.substring(1, deptIdres.length() - 1);
        }
        return null;
    }

    public static void convetDeptId(Integer deptId, List<Dept> deList, Set<Integer> deptIds) {
        if (null == deptId)
            return;
        deptIds.add(deptId);
        for (Dept dept : deList) {
            if (deptId.intValue() == dept.getParentId().intValue()) {
                deptIds.add(deptId);
                convetDeptId(dept.getDeptId(), deList, deptIds);
            }
        }
    }
}
