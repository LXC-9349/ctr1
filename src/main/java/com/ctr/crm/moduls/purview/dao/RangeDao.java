package com.ctr.crm.moduls.purview.dao;

import java.util.List;

import com.ctr.crm.moduls.purview.models.Range;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

/**
 * 说明：
 * @author eric
 * @date 2019年4月15日 下午6:21:27
 */
public interface RangeDao {

	@Insert("insert ignore into Ranges(rangeName,rangeValue,orderNo) values(#{rangeName},#{rangeValue},#{orderNo})")
	void insertRange(Range range);
	
	@Select("select rangeId,rangeValue,rangeName,orderNo from Ranges")
	List<Range> selectAll();

	@Insert("insert ignore into RangeOfRole(roleId,rangeId) values(#{roleId},#{rangeId})")
	void insertRangeOfRole(@Param("roleId") Integer roleId, @Param("rangeId") Integer rangeId);
	
	@Insert("update RangeOfRole set rangeId=#{rangeId} where roleId=#{roleId}")
	boolean updateRangeOfRole(@Param("roleId") Integer roleId, @Param("rangeId") Integer rangeId);
	
	/**
	 * 获取最大数据范围值
	 * @param workerId
	 * @return
	 */
	@Select("select ifnull(max(r.rangeValue),1) from RangeOfRole ror join WorkerOfRole wor on ror.roleId=wor.roleId join Ranges r on ror.rangeId=r.rangeId where wor.workerId=#{workerId}")
	@ResultType(value=Integer.class)
	Integer getMaxRangeValue(@Param("workerId") Integer workerId);
	
	@Select("select r.rangeId from Ranges r join RangeOfRole ror on r.rangeId=ror.rangeId where ror.roleId=#{roleId}")
	@ResultType(value=Integer.class)
	Integer getRangeValueOfRole(Integer roleId);
}
