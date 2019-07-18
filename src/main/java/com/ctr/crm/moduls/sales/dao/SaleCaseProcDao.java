package com.ctr.crm.moduls.sales.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;

import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.PojoUtils;
import com.ctr.crm.moduls.sales.models.SaleCaseProc;

/**
 * 说明：
 * @author eric
 * @date 2019年5月17日 下午3:00:05
 */
public interface SaleCaseProcDao {

	@InsertProvider(type=SaleCaseProcSqlBuilder.class,method="buildInsertSql")
	boolean insert(SaleCaseProc caseProc);
	@UpdateProvider(type=SaleCaseProcSqlBuilder.class,method="buildUpdateSql")
	boolean update(SaleCaseProc caseProc);
	@SelectProvider(type=SaleCaseProcSqlBuilder.class,method="buildSelectByIdSql")
	SaleCaseProc selectByProcId(Long procId);
	@SelectProvider(type=SaleCaseProcSqlBuilder.class,method="buildSelectSql")
	List<SaleCaseProc> select(@Param("memberId") Long memberId, @Param("notesType") Integer notesType);
	
	public class SaleCaseProcSqlBuilder{
		
		public String buildInsertSql(SaleCaseProc caseProc){
			return new SQL(){
				{
					INSERT_INTO(SaleCaseProc.class.getSimpleName());
					INTO_COLUMNS(PojoUtils.getArrayFields(caseProc, "serialVersionUID,attachmentList,record", false));
					INTO_VALUES(PojoUtils.getArrayFields(caseProc, "serialVersionUID,attachmentList,record", true));
				}
			}.toString();
		}
		
		public String buildUpdateSql(SaleCaseProc caseProc){
			return new SQL(){
				{
					UPDATE(SaleCaseProc.class.getSimpleName());
					SET(PojoUtils.getUpdateSets(caseProc, "serialVersionUID,attachmentList,record", false));
                    WHERE("procId=#{procId}");
				}
			}.toString();
		}
		
		public String buildSelectByIdSql(Long procId){
			StringBuilder sqlBuilder = new StringBuilder(PojoUtils.getSelectSqlOfFields(SaleCaseProc.class, "scp", "serialVersionUID,attachmentList,record"));
			sqlBuilder.append(" from SaleCaseProc scp");
			sqlBuilder.append(" where scp.procId=#{procId}");
			return sqlBuilder.toString();
		}
		
		public String buildSelectSql(Map<String, Object> map){
			StringBuilder sqlBuilder = new StringBuilder(PojoUtils.getSelectSqlOfFields(SaleCaseProc.class, "scp", "serialVersionUID,attachmentList,record"));
			sqlBuilder.append(" from SaleCaseProc scp");
			sqlBuilder.append(" where scp.memberId=#{memberId}");
			Integer notesType = CommonUtils.evalInteger(map.get("notesType"));
			if(notesType != null){
				sqlBuilder.append(" and scp.notesType=#{notesType}");
			}
			sqlBuilder.append(" order by scp.procTime desc");
			return sqlBuilder.toString();
		}
	}
}
