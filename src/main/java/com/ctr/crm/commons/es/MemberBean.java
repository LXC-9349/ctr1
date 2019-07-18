package com.ctr.crm.commons.es;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 说明：
 *
 * @author eric
 * @date 2019年4月10日 下午2:20:58
 */
@Document(indexName = "marry_crm", type = "member", shards = 3, replicas = 0)
public class MemberBean {

    @Id
    @Field(type = FieldType.Long, store = true)
    private Long memberId;
    //////////客户基本信息///////////
    @Field(type = FieldType.Text, store = true)
    private String nickName;
    @Field(type = FieldType.Text, store = true)
    private String trueName;
    @Field(type = FieldType.Text, store = true)
    private Collection<String> mobiles;
    private String phone;
    @Field(type = FieldType.Date, store = true)
    private Date birthday;
    @Field(type = FieldType.Integer, store = true)
    private Integer sex;
    @Field(type = FieldType.Integer, store = true)
    private Integer marriage;
    @Field(type = FieldType.Integer, store = true)
    private Integer education;
    @Field(type = FieldType.Integer, store = true)
    private Integer height;
    @Field(type = FieldType.Integer, store = true)
    private Integer weight;
    @Field(type = FieldType.Integer, store = true)
    private Integer salary;
    @Field(type = FieldType.Text, store = true)
    private String workCity;
    @Field(type = FieldType.Text, store = true)
    private String homeTown;
    @Field(type = FieldType.Integer, store = true)
    private Integer animals;
    @Field(type = FieldType.Integer, store = true)
    private Integer constellation;
    @Field(type = FieldType.Integer, store = true)
    private Integer bloodtype;
    @Field(type = FieldType.Integer, store = true)
    private Integer occupation;
    @Field(type = FieldType.Integer, store = true)
    private Integer body;
    @Field(type = FieldType.Integer, store = true)
    private Integer house;
    @Field(type = FieldType.Integer, store = true)
    private Integer car;
    @Field(type = FieldType.Integer, store = true)
    private Integer children;
    @Field(type = FieldType.Integer, store = true)
    private Integer wantChildren;
    @Field(type = FieldType.Integer, store = true)
    private Integer sibling;
    @Field(type = FieldType.Integer, store = true)
    private Integer parents;
    @Field(type = FieldType.Integer, store = true)
    private Integer nation;
    @Field(type = FieldType.Integer, store = true)
    private Integer smoking;
    @Field(type = FieldType.Integer, store = true)
    private Integer drinking;
    @Field(type = FieldType.Text, store = true)
    private String wechatId;
    @Field(type = FieldType.Text, store = true)
    private String field1;
    @Field(type = FieldType.Date, store = true)
    private Date createTime;
    ///////////客户择偶属性/////////
    @Field(type = FieldType.Integer, store = true)
    private Integer oage1;
    @Field(type = FieldType.Integer, store = true)
    private Integer oage2;
    @Field(type = FieldType.Integer, store = true)
    private Integer oheight1;
    @Field(type = FieldType.Integer, store = true)
    private Integer oheight2;
    @Field(type = FieldType.Integer, store = true)
    private Integer osalary1;
    @Field(type = FieldType.Integer, store = true)
    private Integer osalary2;
    @Field(type = FieldType.Integer, store = true)
    private Integer oweight1;
    @Field(type = FieldType.Integer, store = true)
    private Integer oweight2;
    @Field(type = FieldType.Integer, store = true)
    private Integer oeducation1;
    @Field(type = FieldType.Integer, store = true)
    private Integer oeducation2;
    @Field(type = FieldType.Integer, store = true)
    private Integer omarriage;
    @Field(type = FieldType.Integer, store = true)
    private Integer obody;
    @Field(type = FieldType.Text, store = true)
    private String oworkCity;
    @Field(type = FieldType.Text, store = true)
    private String ohomeTown;
    @Field(type = FieldType.Integer, store = true)
    private Integer ochildren;
    @Field(type = FieldType.Integer, store = true)
    private Integer owantChildren;
    @Field(type = FieldType.Integer, store = true)
    private Integer osmoking;
    @Field(type = FieldType.Integer, store = true)
    private Integer odrinking;
    @Field(type = FieldType.Integer, store = true)
    private Integer ohouse;
    @Field(type = FieldType.Integer, store = true)
    private Integer ocar;
    ///////////业务属性///////////
    @Field(type = FieldType.Boolean, store = true)
    private Boolean isVip;
    @Field(type = FieldType.Boolean, store = true)
    private Boolean inSaleCase;
    @Field(type = FieldType.Boolean, store = true)
    private Boolean isHandsome;
    @Field(type = FieldType.Boolean, store = true)
    private Boolean isRecycling;
    @Field(type = FieldType.Boolean, store = true)
    private Boolean isBlacklist;
    @Field(type = FieldType.Integer, store = true)
    private Integer caseClass;
    @Field(type = FieldType.Integer, store = true)
    private Integer workerId;
    @Field(type = FieldType.Text, store = true)
    private String workerName;
    @Field(type = FieldType.Integer, store = true)
    private Integer quitWorkerId;
    @Field(type = FieldType.Text, store = true)
    private String quitWorkerName;
    @Field(type = FieldType.Text, store = true)
    private String quitReason;
    @Field(type = FieldType.Integer, store = true)
    private Integer deptId;
    @Field(type = FieldType.Date, store = true)
    private Date allotTime;
    @Field(type = FieldType.Date, store = true)
    private Date lastContactTime;
    @Field(type = FieldType.Date, store = true)
    private Date orderSignTime;
    @Field(type = FieldType.Integer, store = true)
    private Integer orderSignWorkerId;
    @Field(type = FieldType.Text, store = true)
    private String orderSignWorkerName;
    private String headUrl;

    public MemberBean() {
        super();
    }

    public Integer getOrderSignWorkerId() {
        return orderSignWorkerId;
    }

    public void setOrderSignWorkerId(Integer orderSignWorkerId) {
        this.orderSignWorkerId = orderSignWorkerId;
    }

    public String getOrderSignWorkerName() {
        return orderSignWorkerName;
    }

    public void setOrderSignWorkerName(String orderSignWorkerName) {
        this.orderSignWorkerName = orderSignWorkerName;
    }

    public Date getOrderSignTime() {
        return orderSignTime;
    }

    public void setOrderSignTime(Date orderSignTime) {
        this.orderSignTime = orderSignTime;
    }

    public MemberBean(Long _memberId) {
        this.memberId = _memberId;
    }

    public Date getLastContactTime() {
        return lastContactTime;
    }

    public void setLastContactTime(Date lastContactTime) {
        this.lastContactTime = lastContactTime;
    }

    public String getQuitReason() {
        return quitReason;
    }

    public void setQuitReason(String quitReason) {
        this.quitReason = quitReason;
    }

    public String getWorkerName() {
        return workerName;
    }
    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public Integer getQuitWorkerId() {
        return quitWorkerId;
    }

    public void setQuitWorkerId(Integer quitWorkerId) {
        this.quitWorkerId = quitWorkerId;
    }

    public String getQuitWorkerName() {
        return quitWorkerName;
    }

    public void setQuitWorkerName(String quitWorkerName) {
        this.quitWorkerName = quitWorkerName;
    }

    public Date getAllotTime() {
        return allotTime;
    }

    public void setAllotTime(Date allotTime) {
        this.allotTime = allotTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getIsBlacklist() {
        return isBlacklist;
    }

    public MemberBean setIsBlacklist(Boolean isBlacklist) {
        this.isBlacklist = isBlacklist;
        return this;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public Collection<String> getMobiles() {
        return mobiles;
    }

    public void setMobiles(Collection<String> mobiles) {
        this.mobiles = mobiles;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getMarriage() {
        return marriage;
    }

    public void setMarriage(Integer marriage) {
        this.marriage = marriage;
    }

    public Integer getEducation() {
        return education;
    }

    public void setEducation(Integer education) {
        this.education = education;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public String getWorkCity() {
        return workCity;
    }

    public void setWorkCity(String workCity) {
        this.workCity = workCity;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public Integer getAnimals() {
        return animals;
    }

    public void setAnimals(Integer animals) {
        this.animals = animals;
    }

    public Integer getConstellation() {
        return constellation;
    }

    public void setConstellation(Integer constellation) {
        this.constellation = constellation;
    }

    public Integer getBloodtype() {
        return bloodtype;
    }

    public void setBloodtype(Integer bloodtype) {
        this.bloodtype = bloodtype;
    }

    public Integer getOccupation() {
        return occupation;
    }

    public void setOccupation(Integer occupation) {
        this.occupation = occupation;
    }

    public Integer getBody() {
        return body;
    }

    public void setBody(Integer body) {
        this.body = body;
    }

    public Integer getHouse() {
        return house;
    }

    public void setHouse(Integer house) {
        this.house = house;
    }

    public Integer getCar() {
        return car;
    }

    public void setCar(Integer car) {
        this.car = car;
    }

    public Integer getChildren() {
        return children;
    }

    public void setChildren(Integer children) {
        this.children = children;
    }

    public Integer getWantChildren() {
        return wantChildren;
    }

    public void setWantChildren(Integer wantChildren) {
        this.wantChildren = wantChildren;
    }

    public Integer getSibling() {
        return sibling;
    }

    public void setSibling(Integer sibling) {
        this.sibling = sibling;
    }

    public Integer getParents() {
        return parents;
    }

    public void setParents(Integer parents) {
        this.parents = parents;
    }

    public Integer getNation() {
        return nation;
    }

    public void setNation(Integer nation) {
        this.nation = nation;
    }

    public Integer getSmoking() {
        return smoking;
    }

    public void setSmoking(Integer smoking) {
        this.smoking = smoking;
    }

    public Integer getDrinking() {
        return drinking;
    }

    public void setDrinking(Integer drinking) {
        this.drinking = drinking;
    }

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public Integer getOage1() {
        return oage1;
    }

    public void setOage1(Integer oage1) {
        this.oage1 = oage1;
    }

    public Integer getOage2() {
        return oage2;
    }

    public void setOage2(Integer oage2) {
        this.oage2 = oage2;
    }

    public Integer getOheight1() {
        return oheight1;
    }

    public void setOheight1(Integer oheight1) {
        this.oheight1 = oheight1;
    }

    public Integer getOheight2() {
        return oheight2;
    }

    public void setOheight2(Integer oheight2) {
        this.oheight2 = oheight2;
    }

    public Integer getOsalary1() {
        return osalary1;
    }

    public void setOsalary1(Integer osalary1) {
        this.osalary1 = osalary1;
    }

    public Integer getOsalary2() {
        return osalary2;
    }

    public void setOsalary2(Integer osalary2) {
        this.osalary2 = osalary2;
    }

    public Integer getOweight1() {
        return oweight1;
    }

    public void setOweight1(Integer oweight1) {
        this.oweight1 = oweight1;
    }

    public Integer getOweight2() {
        return oweight2;
    }

    public void setOweight2(Integer oweight2) {
        this.oweight2 = oweight2;
    }

    public Integer getOeducation1() {
        return oeducation1;
    }

    public void setOeducation1(Integer oeducation1) {
        this.oeducation1 = oeducation1;
    }

    public Integer getOeducation2() {
        return oeducation2;
    }

    public void setOeducation2(Integer oeducation2) {
        this.oeducation2 = oeducation2;
    }

    public Integer getOmarriage() {
        return omarriage;
    }

    public void setOmarriage(Integer omarriage) {
        this.omarriage = omarriage;
    }

    public Integer getObody() {
        return obody;
    }

    public void setObody(Integer obody) {
        this.obody = obody;
    }

    public String getOworkCity() {
        return oworkCity;
    }

    public void setOworkCity(String oworkCity) {
        this.oworkCity = oworkCity;
    }

    public String getOhomeTown() {
        return ohomeTown;
    }

    public void setOhomeTown(String ohomeTown) {
        this.ohomeTown = ohomeTown;
    }

    public Integer getOchildren() {
        return ochildren;
    }

    public void setOchildren(Integer ochildren) {
        this.ochildren = ochildren;
    }

    public Integer getOwantChildren() {
        return owantChildren;
    }

    public void setOwantChildren(Integer owantChildren) {
        this.owantChildren = owantChildren;
    }

    public Integer getOsmoking() {
        return osmoking;
    }

    public void setOsmoking(Integer osmoking) {
        this.osmoking = osmoking;
    }

    public Integer getOdrinking() {
        return odrinking;
    }

    public void setOdrinking(Integer odrinking) {
        this.odrinking = odrinking;
    }

    public Integer getOhouse() {
        return ohouse;
    }

    public void setOhouse(Integer ohouse) {
        this.ohouse = ohouse;
    }

    public Integer getOcar() {
        return ocar;
    }

    public void setOcar(Integer ocar) {
        this.ocar = ocar;
    }

    public Boolean getIsVip() {
        return isVip;
    }

    public void setIsVip(Boolean isVip) {
        this.isVip = isVip;
    }

    public Boolean getInSaleCase() {
        return inSaleCase;
    }

    public void setInSaleCase(Boolean inSaleCase) {
        this.inSaleCase = inSaleCase;
    }

    public Boolean getIsHandsome() {
        return isHandsome;
    }

    public void setIsHandsome(Boolean isHandsome) {
        this.isHandsome = isHandsome;
    }

    public Integer getCaseClass() {
        return caseClass;
    }

    public void setCaseClass(Integer caseClass) {
        this.caseClass = caseClass;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getIsRecycling() {
        return isRecycling;
    }

    public void setIsRecycling(Boolean isRecycling) {
        this.isRecycling = isRecycling;
    }

	public String getHeadUrl() {
		return headUrl;
	}

	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

}
