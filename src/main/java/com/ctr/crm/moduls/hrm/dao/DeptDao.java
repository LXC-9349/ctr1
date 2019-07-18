package com.ctr.crm.moduls.hrm.dao;

import java.util.List;

import com.ctr.crm.moduls.hrm.models.Dept;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import com.ctr.crm.moduls.purview.models.DataRange;

/**
 * 说明：
 * @author eric
 * @date 2019年4月16日 下午5:08:39
 */
public interface DeptDao {

	@Insert("insert ignore into Dept(parentId,deptName,structure,custom) values(#{parentId},#{deptName},#{structure},#{custom})")
	@Options(useGeneratedKeys=true, keyProperty="deptId", keyColumn="deptId")
	boolean insert(Dept dept);
	
	@Select("select deptId,deptName,parentId,structure,custom from Dept where parentId=0 limit 1")
	Dept selectTop();
	
	@Select("select deptId,deptName,parentId,structure,custom from Dept where deleted=0")
	List<Dept> selectAll();
	
	@Select("select deptId,deptName,parentId,structure,custom from Dept where deptId=#{deptId} and deleted=0")
	Dept select(Integer deptId);
	
	@Select("select deptId,deptName,parentId,structure,custom from Dept where parentId=#{deptId} and deleted=0")
	List<Dept> selectByParentId(Integer deptId);
	
	@Update("update Dept set deptName=#{deptName},structure=#{structure} where deptId=${deptId}")
	void update(Dept dept);
	
	@Delete("delete from Dept where deptId=#{deptId}")
	boolean delete(Integer deptId);
	
	@Select("select deptId,parentId,deptName,custom from Dept where deptName=#{deptName} limit 1")
	Dept selectByName(@Param("deptName") String deptName);
	
	@SelectProvider(type=DeptSqlBuilder.class, method="buildRangeSql")
	List<Dept> selectByRange(DataRange range);
	
	public class DeptSqlBuilder{
		public String buildRangeSql(DataRange range){
			StringBuilder sqlBuilder = new StringBuilder("select deptId,parentId,deptName,custom from Dept where 1=1");
			if(range != null && range.getStructure() != null){
				sqlBuilder.append(" and structure ")
						  .append(range.getSymbol())
						  .append(" '")
						  .append(range.getStructure())
						  .append("'");
			}
			return sqlBuilder.toString();
		}
	}
}
