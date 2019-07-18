package com.ctr.crm.moduls.report.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.moduls.purview.models.DataRange;

/**
 * 说明：
 * @author eric
 * @date 2019年5月29日 上午10:45:46
 */
public interface HomepageDao {

	@SelectProvider(type=HomepageSqlBuilder.class, method="chanceHierarchy")
	Map<String, Object> chanceHierarchy(@Param("statisticsStr")String statisticsStr, @Param("range")DataRange range);
	@SelectProvider(type=HomepageSqlBuilder.class, method="todayAllot")
	Map<String, Object> todayAllot(@Param("range")DataRange range);
	@SelectProvider(type=HomepageSqlBuilder.class, method="todayCommunicate")
	Map<String, Object> todayCommunicate(@Param("range")DataRange range);
	@SelectProvider(type=HomepageSqlBuilder.class, method="overdueEstranged")
	Map<String, Object> overdueEstranged(@Param("range")DataRange range);
	
	public class HomepageSqlBuilder{
		public String chanceHierarchy(Map<String, Object> map){
			StringBuilder sql = new StringBuilder("select ");
			sql.append(map.get("statisticsStr"));
			sql.append(" from Worker w");
			sql.append(" left join Dept d on w.deptId=d.deptId");
			sql.append(" left join SaleCase sc on w.workerId=sc.workerId");
			sql.append(" where 1=1");
			DataRange range = (DataRange)map.get("range");
			if (range != null) {
				if(range.getWorkerId() != null){
					sql.append(" and w.workerId").append(range.getSymbol()).append(range.getWorkerId());
				}else if(range.getStructure() != null){
					sql.append(" and d.structure ").append(range.getSymbol()).append(" '").append(range.getStructure()).append("'");
				}
			}
			return sql.toString();
		}
		
		public String todayAllot(Map<String, Object> map){
			StringBuilder sql = new StringBuilder("select count(1) as todayAllot");
			sql.append(" from SaleCase sc");
			sql.append(" left join Worker w on sc.workerId=w.workerId");
			sql.append(" left join Dept d on d.deptId=w.deptId");
			sql.append(" where sc.caseClass in(select caseClass from Intentionality where type=1)");
			sql.append(" and sc.allotTime>='" +CommonUtils.todayToStr() + "'");
			sql.append(" and sc.allotTime<'" +CommonUtils.tomorrowToStr() + "'");
			DataRange range = (DataRange)map.get("range");
			if (range != null) {
				if(range.getWorkerId() != null){
					sql.append(" and w.workerId").append(range.getSymbol()).append(range.getWorkerId());
				}else if(range.getStructure() != null){
					sql.append(" and d.structure ").append(range.getSymbol()).append(" '").append(range.getStructure()).append("'");
				}
			}
			return sql.toString();
		}
		
		public String todayCommunicate(Map<String, Object> map){
			StringBuilder sql = new StringBuilder("select count(1) as todayCommunicate");
			sql.append(" from SaleCase sc");
			sql.append(" left join Worker w on sc.workerId=w.workerId");
			sql.append(" left join Dept d on d.deptId=w.deptId");
			sql.append(" where sc.caseClass in(select caseClass from Intentionality where type=1)");
			sql.append(" and sc.nextContactTime>='" +CommonUtils.todayToStr() + "'");
			sql.append(" and sc.nextContactTime<'" +CommonUtils.tomorrowToStr() + "'");
			DataRange range = (DataRange)map.get("range");
			if (range != null) {
				if(range.getWorkerId() != null){
					sql.append(" and w.workerId").append(range.getSymbol()).append(range.getWorkerId());
				}else if(range.getStructure() != null){
					sql.append(" and d.structure ").append(range.getSymbol()).append(" '").append(range.getStructure()).append("'");
				}
			}
			return sql.toString();
		}
		
		public String overdueEstranged(Map<String, Object> map){
			StringBuilder sql = new StringBuilder("select count(1) as overdueEstranged");
			sql.append(" from SaleCase sc");
			sql.append(" left join Worker w on sc.workerId=w.workerId");
			sql.append(" left join Dept d on d.deptId=w.deptId");
			sql.append(" where 1=1 ");
			sql.append(" and sc.nextContactTime<'" +CommonUtils.todayToStr() + "'");
			DataRange range = (DataRange)map.get("range");
			if (range != null) {
				if(range.getWorkerId() != null){
					sql.append(" and w.workerId").append(range.getSymbol()).append(range.getWorkerId());
				}else if(range.getStructure() != null){
					sql.append(" and d.structure ").append(range.getSymbol()).append(" '").append(range.getStructure()).append("'");
				}
			}
			return sql.toString();
		}
		
	}
}
