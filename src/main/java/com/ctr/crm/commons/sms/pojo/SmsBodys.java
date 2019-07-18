package com.ctr.crm.commons.sms.pojo;

import java.util.List;

public class SmsBodys {

    private Integer respCode;
    private List<SmsBody> successList;
    private List<SmsBody> failList;

    public Integer getRespCode() {
        return respCode;
    }

    public void setRespCode(Integer respCode) {
        this.respCode = respCode;
    }

    public List<SmsBody> getSuccessList() {
        return successList;
    }

    public void setSuccessList(List<SmsBody> successList) {
        this.successList = successList;
    }

    public List<SmsBody> getFailList() {
        return failList;
    }

    public void setFailList(List<SmsBody> failList) {
        this.failList = failList;
    }

    public class Status {
        public static final int SUCCESS = 0;
        public static final int FAIL = 1;
    }
}
