package com.ctr.crm.moduls.approval.pojo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ctr.crm.commons.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("rawtypes")
public class ApprovalSource {
    private String name;// 即审批类型名称 流程配置需要针对具体审批来定义
    private String beanName;// 即service的bean名称
    private String method;// 实际执行的方法
    private boolean submit;// 先执行业务方法，如知识库新增功能，先新增，得到的ID作为审批数据的参数
    private String delmethod; // 处理驳回执行删除的方法
    private Class[] parmClass;// 参数类型
    private Object[] parmValue;// 参数值
    private String viewUrl;//数据展示方法
    private Integer length;//集合长度

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean isSubmit() {
        return submit;
    }

    public void setSubmit(boolean submit) {
        this.submit = submit;
    }

    public String getDelmethod() {
        return delmethod;
    }

    public void setDelmethod(String delmethod) {
        this.delmethod = delmethod;
    }

    public Class[] getParmClass() {
        return parmClass;
    }

    public void setParmClass(Class[] parmClass) {
        this.parmClass = parmClass;
    }

    public Object[] getParmValue() {
        return parmValue;
    }

    public void setParmValue(Object[] parmValue) {
        this.parmValue = parmValue;
    }

    public String getViewUrl() {
        return viewUrl;
    }

    public void setViewUrl(String viewUrl) {
        this.viewUrl = viewUrl;
    }


    /*
     * public static void main(String[] args) { try { Map<String, Object>
     * jsonMap=strJson2Map(json()); Map<String, Object>
     * jsonMap1=strJson2Map(jsonMap.get("parm").toString()); String
     * type=jsonMap1.get("type").toString(); String[]
     * t=JSONArray.parseObject(type, String[].class); Class[] c = new
     * Class[t.length]; for (int i = 0; i < t.length; i++) { c[i] =
     * Class.forName(t[i].replace("class ", "")); } String value =
     * jsonMap1.get("value").toString(); String[] tt =
     * JSONArray.parseObject(value, String[].class); for (int i = 0; i <
     * tt.length; i++) { Object ttt=JSONArray.parseObject(tt[i], c[i]);
     * System.out.println(ttt); } } catch (Exception e) { e.printStackTrace(); }
     * }
     *
     * private static String json(){ Map<String, Object> source = new
     * HashMap<String, Object>(); source.put("beanName", "123");// 业务bean
     * source.put("method", "123");// 执行成功方法 source.put("name", "123");// 名称
     * source.put("submit", "true");//处理方式 source.put("delmethod", "123");//
     * 执行删除产生的数据方法 Map<String, Object[]> parm = new HashMap<String, Object[]>();
     *
     * List<ApprovalFlow> ll=new ArrayList<>(); ApprovalFlow aa=new
     * ApprovalFlow(); aa.setApprovalDataId(123); aa.setApprovalType("qweqw");
     * ll.add(aa); parm.put("type", new
     * String[]{Integer.class.toString(),ll.getClass().toString()});
     * parm.put("value", new Object[]{"123",ll}); source.put("parm", parm);// 参数
     * return JSONArray.toJSON(source).toString(); }
     */

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "ApprovalSource [name=" + name + ", beanName=" + beanName + ", method=" + method + ", submit=" + submit
                + ", delmethod=" + delmethod + ", parmClass=" + Arrays.toString(parmClass) + ", parmValue="
                + Arrays.toString(parmValue) + ", viewUrl=" + viewUrl + "]";
    }

    @SuppressWarnings("unchecked")
    public ApprovalSource analyze(String source) {
        try {
            Map<String, Object> jsonMap = strJson2Map(source);
            this.name = jsonMap.get("name") != null ? jsonMap.get("name").toString().replace("\"", "") : null;
            this.beanName = jsonMap.get("beanName") != null ? jsonMap.get("beanName").toString().replace("\"", "") : null;
            this.method = jsonMap.get("method") != null ? jsonMap.get("method").toString().replace("\"", "") : null;
            this.submit = jsonMap.get("submit") != null && ("true".equals(jsonMap.get("submit").toString()));
            this.delmethod = jsonMap.get("delmethod") != null ? jsonMap.get("delmethod").toString().replace("\"", "") : null;
            this.viewUrl = jsonMap.get("viewUrl") != null ? jsonMap.get("viewUrl").toString().replace("\"", "") : null;
            this.length = jsonMap.get("length") != null ? CommonUtils.evalInteger(jsonMap.get("length")) : null;
            String parm = jsonMap.get("parm") != null ? jsonMap.get("parm").toString() : null;
            if (StringUtils.isNotBlank(parm)) {
                Map<String, Object> jsonMap1 = strJson2Map(jsonMap.get("parm").toString());
                String type = jsonMap1.get("type").toString();
                if (StringUtils.isNotBlank(type)) {
                    String[] t = JSONArray.parseObject(type, String[].class);
                    Class[] c = new Class[t.length];
                    for (int i = 0; i < t.length; i++) {
                        c[i] = Class.forName(t[i].replace("class ", ""));
                    }
                    String value = jsonMap1.get("value").toString();
                    Object[] o = new Object[t.length];
                    if (StringUtils.isNotBlank(value)) {
                        String[] tt = JSONArray.parseObject(value, String[].class);
                        for (int i = 0; i < tt.length; i++) {
                            if (c[i].getName().equals(String.class.getName())) {
                                tt[i] = "'" + tt[i] + "'";
                            }
                            Object thisval = JSONArray.parseObject(tt[i], c[i]);
                            o[i] = thisval;
                        }
                    }
                    this.parmClass = c;
                    this.parmValue = o;
                }
            }
            return this;
        } catch (Exception e) {
//			e.printStackTrace();
            return null;
        }
    }

    private static Map<String, Object> strJson2Map(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        Map<String, Object> resMap = new HashMap<String, Object>();
        Iterator<Entry<String, Object>> it = jsonObject.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> param = it.next();
            resMap.put(param.getKey(), JSONObject.toJSONString(param.getValue(), SerializerFeature.WriteClassName));
        }
        return resMap;
    }

}
