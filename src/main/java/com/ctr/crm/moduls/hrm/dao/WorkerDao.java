package com.ctr.crm.moduls.hrm.dao;

import java.util.List;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.models.WorkerLog;
import com.ctr.crm.moduls.hrm.models.WorkerSearch;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;

import com.ctr.crm.commons.utils.PojoUtils;
import com.ctr.crm.moduls.purview.models.DataRange;

/**
 * 说明：
 *
 * @author eric
 * @date 2019年4月9日 下午2:34:25
 */
public interface WorkerDao {

    @SelectProvider(type = WorkerSqlBuilder.class, method = "buildSelectAllSql")
    List<Worker> selectAll();

    @SelectProvider(type = WorkerSqlBuilder.class, method = "buildSelectLeaveAllSql")
    List<Worker> selectLeaveAll();

    @InsertProvider(type = WorkerSqlBuilder.class, method = "buildInsertSql")
    void insert(Worker worker);

    @UpdateProvider(type = WorkerSqlBuilder.class, method = "buildUpdateSql")
    boolean update(Worker worker);

    @Update("update Worker set workerStatus=1,lineNum=null where workerId=#{workerId}")
    void delete(Integer workerId);

    @Update("update Worker set lineNum=null where workerId=#{workerId}")
    void emptyLineNum(Integer workerId);

    @Select("select max(workerId) from Worker where workerId<>8888")
    Integer getMaxWorkerId();

    @SelectProvider(type = WorkerSqlBuilder.class, method = "buildSelectSqlByLineNum")
    Worker selectByLineNum(Integer lineNum);

    @SelectProvider(type = WorkerSqlBuilder.class, method = "buildSelectSqlByWorkerId")
    Worker select(Integer workerId);

    @SelectProvider(type = WorkerSqlBuilder.class, method = "buildSelectSqlByWorkerAccount")
    Worker selectByAccount(String workerAccount);

    @SelectProvider(type = WorkerSqlBuilder.class, method = "buildSelectSqlByDept")
    List<Worker> selectByDept(String structure);

    @SelectProvider(type = WorkerSqlBuilder.class, method = "buildMonitorSql")
    List<Worker> monitor(WorkerSearch condition);
    
    @SelectProvider(type = WorkerSqlBuilder.class, method = "buildRangeSql")
    List<Worker> workerRange(DataRange range);

    @Update("insert into WorkerLog(workerId,logType,logTime,token) values(#{workerId},#{logType},#{logTime},#{token})")
    void insertLog(WorkerLog log);

    @Select("select id,workerId,logType,logTime,token from WorkerLog where workerId=#{workerId} order by logTime desc limit 1")
    WorkerLog selectLog(Integer workerId);

    @SelectProvider(type = WorkerSqlBuilder.class, method = "selectByWorkerIds")
    List<Worker> selectByWorkerIds(String workerIds);
    
    @Select("select workerId from WorkerWhiteList")
    List<Integer> getWhiteList();
    @Select("select count(0) from WorkerWhiteList where workerId=#{workerId}")
    boolean isWhiteList(Integer workerId);
    @Insert("insert ignore into WorkerWhiteList(workerId) values(#{workerId})")
    void addWhiteList(Integer workerId);
    @Delete("delete from WorkerWhiteList where workerId=#{workerId}")
    void delWhiteList(Integer workerId);

    public static class WorkerSqlBuilder {

        public static String buildInsertSql(Worker worker) {
            return new SQL() {
                {
                    INSERT_INTO("Worker");
                    INTO_COLUMNS(PojoUtils.getArrayFields(worker, "serialVersionUID,deptName,deptIds", false));
                    INTO_VALUES(PojoUtils.getArrayFields(worker, "serialVersionUID,deptName,deptIds", true));
                }
            }.toString();
        }

        public static String buildUpdateSql(Worker worker) {
            return new SQL() {
                {
                    UPDATE("Worker");
                    SET(PojoUtils.getUpdateSets(worker, "serialVersionUID,deptName,deptIds", false));
                    WHERE("workerId=#{workerId}");
                }
            }.toString();
        }

        public static String buildSelectAllSql(Worker worker) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFields(Worker.class, "serialVersionUID", "deptName", "psw", "deptIds"));
                    FROM("Worker");
                    WHERE("workerStatus in(0,2)");
                }
            }.toString();
        }

        public static String buildSelectLeaveAllSql(Worker worker) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFields(Worker.class, "serialVersionUID", "deptName", "psw", "deptIds"));
                    FROM("Worker");
                    WHERE("workerStatus=1");
                }
            }.toString();
        }

        public static String buildSelectSqlByLineNum(Integer lineNum) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFields(Worker.class, "serialVersionUID", "deptName", "psw", "deptIds"));
                    FROM("Worker");
                    WHERE("lineNum=#{lineNum}");
                    AND().WHERE("workerStatus in(0,2)");
                }
            }.toString();
        }

        public static String buildSelectSqlByWorkerId(Integer workerId) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFields(Worker.class, "serialVersionUID", "deptName", "deptIds"));
                    FROM("Worker");
                    WHERE("workerId=#{workerId}");
                    AND().WHERE("workerStatus in(0,2)");
                }
            }.toString();
        }

        public static String buildSelectSqlByWorkerAccount(String workerAccount) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFields(Worker.class, "serialVersionUID", "deptName", "deptIds"));
                    FROM("Worker");
                    WHERE("workerAccount=#{workerAccount}");
                    AND().WHERE("workerStatus in(0,2)");
                }
            }.toString();
        }

        public static String buildSelectSqlByDept(String structure) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFields("w", Worker.class, "serialVersionUID", "deptName", "psw", "deptIds"));
                    FROM("Worker w");
                    LEFT_OUTER_JOIN("Dept d on w.deptId=d.deptId");
                    WHERE("d.structure like concat(#{structure},'%')");
                    AND().WHERE("w.workerStatus in(0,2)");
                }
            }.toString();
        }

        public static String buildMonitorSql(WorkerSearch search) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFields("w", Worker.class, "serialVersionUID", "deptName", "psw", "deptIds"));
                    FROM("Worker w");
                    LEFT_OUTER_JOIN("Dept d on w.deptId=d.deptId");
                    WHERE("1=1");
                    // 数据范围条件
                    DataRange range = search.getRange();
                    if (range != null) {
                    	if (range.getWorkerId() != null) {
                        	AND().WHERE("w.workerId" + range.getSymbol() + range.getWorkerId());
                        }else if (range.getStructure() != null) {
                            AND().WHERE("d.structure " + range.getSymbol() + "'" + range.getStructure() + "'");
                        } 
                    }
                    AND().WHERE("w.workerStatus in(0,2)");
                    AND().WHERE("w.skill=0");
                }
            }.toString();
        }
        
        public static String buildRangeSql(DataRange range) {
        	return new SQL() {
        		{
        			SELECT(PojoUtils.getArrayFields("w", Worker.class, "serialVersionUID", "deptName", "psw", "deptIds"));
        			FROM("Worker w");
        			LEFT_OUTER_JOIN("Dept d on w.deptId=d.deptId");
        			WHERE("1=1");
        			if (range != null) {
        				if (range.getWorkerId() != null) {
        					AND().WHERE("w.workerId" + range.getSymbol() + range.getWorkerId());
        				}else if (range.getStructure() != null) {
        					AND().WHERE("d.structure " + range.getSymbol() + "'" + range.getStructure() + "'");
        				} 
        			}
        			AND().WHERE("w.workerStatus in(0,2)");
        		}
        	}.toString();
        }

        public static String selectByWorkerIds(String workerIds) {
            return new SQL() {
                {
                    SELECT(PojoUtils.getArrayFields(Worker.class, "serialVersionUID", "deptName", "psw", "deptIds"));
                    FROM("Worker");
                    WHERE("workerStatus in(0,2)");
                    WHERE("workerId in(" + workerIds + ")");
                }
            }.toString();
        }
    }
}
