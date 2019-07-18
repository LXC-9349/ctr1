package com.ctr.crm.moduls.approval.modules;

import java.io.Serializable;
import java.util.Date;

/**
 * 功能描述: 流程实例
 *
 * @author: DoubleLi
 * @date: 2019/4/24 14:33
 */
public class ApprovalData implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7469586378838273914L;
    private Integer id;
    private String approvalType;
    private String source;
    private int status;//审批状态(0待审批,1审批中,2已通过,3已驳回,4已撤销)
    private String approvalresult;
    private String buisId;
    private Integer workerId;
    private Date applicationtime;
    private int version;
    private String action;
    private Integer approvalFlowId;
    private String execResult;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBuisId() {
        return buisId;
    }

    public void setBuisId(String buisId) {
        this.buisId = buisId;
    }

    public String getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(String approvalType) {
        this.approvalType = approvalType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getApprovalresult() {
        return approvalresult;
    }

    public void setApprovalresult(String approvalresult) {
        this.approvalresult = approvalresult;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public Date getApplicationtime() {
        return applicationtime;
    }

    public void setApplicationtime(Date applicationtime) {
        this.applicationtime = applicationtime;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }


    public Integer getApprovalFlowId() {
        return approvalFlowId;
    }

    public void setApprovalFlowId(Integer approvalFlowId) {
        this.approvalFlowId = approvalFlowId;
    }

    public String getExecResult() {
        return execResult;
    }

    public void setExecResult(String execResult) {
        this.execResult = execResult;
    }

    @Override
    public String toString() {
        return "ApprovalData [id=" + id + ", approvalType=" + approvalType + ", source=" + source + ", status=" + status
                + ", approvalresult=" + approvalresult + ", workerId=" + workerId + ", applicationtime="
                + applicationtime + ", version=" + version + ", action=" + action + ", approvalFlowId=" + approvalFlowId
                + ", execResult=" + execResult + "]";
    }
	
	/*public Object execResult(Object execResult){
		if(execResult instanceof Blob){
			Blob inblob = (Blob) execResult;
			try {
				InputStream is = inblob.getBinaryStream();
				Object object=null;
				if(is.available()>0){
					ObjectInputStream ois=new ObjectInputStream(is);
					object=ois.readObject();
					ois.close();
				}
				is.close();
				return object;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return execResult;
	}*/
}
