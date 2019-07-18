package com.ctr.crm.moduls.member.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModelProperty.AccessMode;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

@ApiModel(value="MemberBaseInfo", description="客户信息")
public class MemberBaseInfo implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6036304369107902967L;
	@ApiModelProperty(value="客户ID", accessMode=AccessMode.READ_ONLY,notes="系统生成", hidden=true)
	private Long memberId;
	@ApiModelProperty(value="客户昵称")
    private String nickName;
	@ApiModelProperty(value="真实姓名")
    private String trueName;
	@ApiModelProperty(value="手机号")
    private String mobile;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value="出生日期")
	private Date birthday;
	@ApiModelProperty(value="性别 注：0男 1女")
	private Integer sex;
	@ApiModelProperty(value="月收入 注：1:1000元以下 2:1001-2000元 3:2001-3000元 4:3001-5000元 5:5001-8000元 6:8001-10000元 7:10001-20000元 8:20001-50000元 9:50000元以上")
	private Integer salary;
	@ApiModelProperty(value="学历 注：1高中及以下 2中专 3大专 4大学本科 5硕士 6博士")
	private Integer education;
	@ApiModelProperty(value="婚姻状况 注：1未婚 2离异 3丧偶")
	private Integer marriage;
	@ApiModelProperty(value="身高 注：范围值140cm~200cm 140表示140以下, 200表示200以上")
	private Integer height;
	@ApiModelProperty(value="体重 注：范围值30kg~120kg 30表示30以下,120表示120以上")
	private Integer weight;
	@ApiModelProperty(value="工作地")
	private String workCity;
	@ApiModelProperty(value="籍贯")
	private String homeTown;
	@ApiModelProperty(value="生肖 注：1鼠 2牛 3虎 4兔 5龙 6蛇 7马 8羊 9猴 10鸡 11狗 12猪")
	private Integer animals;
	@ApiModelProperty(value="星座 注：1牡羊座(03.21-04.20) 2金牛座(04.21-05.20) 3双子座(05.21-06.21) 4巨蟹座(06.22-07.22) 5狮子座(07.23-08.22) 6处女座(08.23-09.22) 7天秤座(09.23-10.22) 8天蝎座(10.23-11.21) 9射手座(11.22-12.21) 10魔羯座(12.22-01.19) 11水瓶座(01.20-02.19) 12双鱼座(02.20-03.20)")
	private Integer constellation;
	@ApiModelProperty(value="血型 注：1A型 2B型 3AB型 4o型 5不确定")
	private Integer bloodtype;
	@ApiModelProperty(value="职业 注：1运动员 2教师 3教授 4学生 5董事长 6总经理 7副总/总监 8部门经理 9中层管理 10企业家 11个体老板 12高级干部 13公务员 14律师 15医生 16护士 17专家学者 18工程师 19设计师 20艺术家 21演员 22模特 23离/退休 24技术员 25服务员 26普通员工 27自由职业 28无业")
	private Integer occupation;
	@ApiModelProperty(value="体型 注：1一般 2较瘦 3苗条 4壮实 5丰满 6较胖")
	private Integer body;
	@ApiModelProperty(value="购房情况 注：1租房 2全款购房 3按揭购房 4老家有住房")
	private Integer house;
	@ApiModelProperty(value="购车情况 注：1未购车 2已购车")
	private Integer car;
	@ApiModelProperty(value="是否有孩子 注：1没有 2有,住一起 3有,偶尔住一起 4有,不在身边")
	private Integer children;
	@ApiModelProperty(value="是否想要孩子 注：1想要孩子 2不想要孩子 3视情况而定")
	private Integer wantChildren;
	@ApiModelProperty(value="兄弟姐妹 注：1独生子女 2:2 3:3 4:4 5:5 6:6")
	private Integer sibling;
	@ApiModelProperty(value="父母状况 注：1父母均健在 2只有母亲健在 3只有父亲健在 4父母均已离世")
	private Integer parents;
	@ApiModelProperty(value="民族 注：1汉族 2藏族 3朝鲜族 4蒙古族 5回族 6满族 7维吾尔族 8壮族 9彝族 10苗族 11侗族 12瑶族 13白族 14布依族 15傣族 16京族 17黎族 18羌族 19怒族 20佤族 21水族 22畲族 23土族 24阿昌族 25哈尼族 26高山族 27景颇族 28珞巴族 29锡伯族 30德昂(崩龙)族 31保安族 32基诺族 33门巴族 34毛南族 35赫哲族 36裕固族 37撒拉族 38独龙族 39普米族 40仫佬族 41仡佬族 42东乡族 43拉祜族 44土家族 45纳西族 46傈僳族 47布朗族 48哈萨克族 49达斡尔族 50鄂伦春族 51鄂温克族 52俄罗斯族 53塔塔尔族 54塔吉克族 55柯尔克孜族 56乌兹别克族 57国外")
	private Integer nation;
	@ApiModelProperty(value="是否吸烟 注：1不吸烟 2稍微抽一点 3社交场合抽 4抽得很多")
	private Integer smoking;
	@ApiModelProperty(value="是否喝酒 注：1不喝酒 2稍微喝一点 3社交场合喝 4喝得很多")
	private Integer drinking;
	@ApiModelProperty(value="微信号")
	private String wechatId;
	@ApiModelProperty(value="头像")
    private String headurl;
	@ApiModelProperty(value="创建时间",notes="系统时间", hidden=true)
    private Date createTime;
	@ApiModelProperty(value="创建人",notes="当前操作人", hidden=true)
    private Integer creator;
	@ApiModelProperty(hidden=true)
    private String companyId;
	@ApiModelProperty(value="客户标识",notes="第1位分配标识 第2位黑名单标识 第3位VIP标识 第4位在库标识 第5位才俊佳丽标识", hidden=true)
    private String memberType;
	@ApiModelProperty(value="资源来源 如：广告 联谊 转介绍")
    private String field1;
	@ApiModelProperty(hidden=true)
    private String field2;
	@ApiModelProperty(hidden=true)
    private String field3;
	@ApiModelProperty(hidden=true)
    private String field4;
	@ApiModelProperty(hidden=true)
    private String field5;
	@ApiModelProperty(hidden=true)
    private String field6;
	@ApiModelProperty(hidden=true)
    private String field7;
	@ApiModelProperty(hidden=true)
    private String field8;
	@ApiModelProperty(hidden=true)
    private String field9;
	@ApiModelProperty(hidden=true)
    private String field10;

    public Long getMemberId(){
        return this.memberId;
    }
    public void setMemberId(Long memberId){
        this.memberId = memberId;
    }
    public String getNickName(){
        return this.nickName;
    }
    public void setNickName(String nickName){
        this.nickName = nickName;
    }
    public String getTrueName(){
        return this.trueName;
    }
    public void setTrueName(String trueName){
        this.trueName = trueName;
    }
    public String getMobile(){
        return this.mobile;
    }
    public void setMobile(String mobile){
        this.mobile = StringUtils.trim(mobile);
    }
    public String getWechatId(){
        return this.wechatId;
    }
    public void setWechatId(String wechatId){
        this.wechatId = wechatId;
    }
    public String getHeadurl(){
        return this.headurl;
    }
    public void setHeadurl(String headurl){
        this.headurl = headurl;
    }
    public Date getCreateTime(){
        return this.createTime;
    }
    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }
    public Integer getCreator(){
        return this.creator;
    }
    public void setCreator(Integer creator){
        this.creator = creator;
    }
    public String getCompanyId(){
        return this.companyId;
    }
    public void setCompanyId(String companyId){
        this.companyId = companyId;
    }
    public String getMemberType(){
        return this.memberType;
    }
    public void setMemberType(String memberType){
        this.memberType = memberType;
    }
    public Date getBirthday() {
		return birthday;
	}
    public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
    public Integer getSex(){
        return this.sex;
    }
    public void setSex(Integer sex){
        this.sex = sex;
    }
    public Integer getMarriage(){
        return this.marriage;
    }
    public void setMarriage(Integer marriage){
        this.marriage = marriage;
    }
    public Integer getHeight(){
        return this.height;
    }
    public void setHeight(Integer height){
        this.height = height;
    }
    public Integer getWeight(){
        return this.weight;
    }
    public void setWeight(Integer weight){
        this.weight = weight;
    }
    public Integer getEducation(){
        return this.education;
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
	public void setEducation(Integer education){
        this.education = education;
    }
    public Integer getAnimals(){
        return this.animals;
    }
    public void setAnimals(Integer animals){
        this.animals = animals;
    }
    public Integer getConstellation(){
        return this.constellation;
    }
    public void setConstellation(Integer constellation){
        this.constellation = constellation;
    }
    public Integer getBloodtype(){
        return this.bloodtype;
    }
    public void setBloodtype(Integer bloodtype){
        this.bloodtype = bloodtype;
    }
    public Integer getOccupation(){
        return this.occupation;
    }
    public void setOccupation(Integer occupation){
        this.occupation = occupation;
    }
    public Integer getSalary(){
        return this.salary;
    }
    public void setSalary(Integer salary){
        this.salary = salary;
    }
    public Integer getBody(){
        return this.body;
    }
    public void setBody(Integer body){
        this.body = body;
    }
    public Integer getHouse(){
        return this.house;
    }
    public void setHouse(Integer house){
        this.house = house;
    }
    public Integer getCar(){
        return this.car;
    }
    public void setCar(Integer car){
        this.car = car;
    }
    public Integer getChildren(){
        return this.children;
    }
    public void setChildren(Integer children){
        this.children = children;
    }
    public Integer getWantChildren(){
        return this.wantChildren;
    }
    public void setWantChildren(Integer wantChildren){
        this.wantChildren = wantChildren;
    }
    public String getWorkCity(){
        return this.workCity;
    }
    public void setWorkCity(String workCity){
        this.workCity = workCity;
    }
    public String getHomeTown(){
        return this.homeTown;
    }
    public void setHomeTown(String homeTown){
        this.homeTown = homeTown;
    }
    public Integer getNation(){
        return this.nation;
    }
    public void setNation(Integer nation){
        this.nation = nation;
    }
    public Integer getSmoking(){
        return this.smoking;
    }
    public void setSmoking(Integer smoking){
        this.smoking = smoking;
    }
    public Integer getDrinking(){
        return this.drinking;
    }
    public void setDrinking(Integer drinking){
        this.drinking = drinking;
    }
    public String getField1(){
        return this.field1;
    }
    public void setField1(String field1){
        this.field1 = field1;
    }
    public String getField2(){
        return this.field2;
    }
    public void setField2(String field2){
        this.field2 = field2;
    }
    public String getField3(){
        return this.field3;
    }
    public void setField3(String field3){
        this.field3 = field3;
    }
    public String getField4(){
        return this.field4;
    }
    public void setField4(String field4){
        this.field4 = field4;
    }
    public String getField5(){
        return this.field5;
    }
    public void setField5(String field5){
        this.field5 = field5;
    }
    public String getField6(){
        return this.field6;
    }
    public void setField6(String field6){
        this.field6 = field6;
    }
    public String getField7(){
        return this.field7;
    }
    public void setField7(String field7){
        this.field7 = field7;
    }
    public String getField8(){
        return this.field8;
    }
    public void setField8(String field8){
        this.field8 = field8;
    }
    public String getField9(){
        return this.field9;
    }
    public void setField9(String field9){
        this.field9 = field9;
    }
    public String getField10(){
        return this.field10;
    }
    public void setField10(String field10){
        this.field10 = field10;
    }

    public MemberBaseInfo() {
    }

    public MemberBaseInfo(Long memberId, String memberType) {
        this.memberId = memberId;
        this.memberType = memberType;
    }

    @Override
    public String toString() {
        return "MemberBaseInfo{" +
                "memberId=" + memberId +
                ", nickName='" + nickName + '\'' +
                ", trueName='" + trueName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", birthday=" + birthday +
                ", sex=" + sex +
                ", salary=" + salary +
                ", education=" + education +
                ", marriage=" + marriage +
                ", height=" + height +
                ", weight=" + weight +
                ", workCity=" + workCity +
                ", homeTown=" + homeTown +
                ", animals=" + animals +
                ", constellation=" + constellation +
                ", bloodtype=" + bloodtype +
                ", occupation=" + occupation +
                ", body=" + body +
                ", house=" + house +
                ", car=" + car +
                ", children=" + children +
                ", wantChildren=" + wantChildren +
                ", sibling=" + sibling +
                ", parents=" + parents +
                ", nation=" + nation +
                ", smoking=" + smoking +
                ", drinking=" + drinking +
                ", wechatId='" + wechatId + '\'' +
                ", headurl='" + headurl + '\'' +
                ", createTime=" + createTime +
                ", creator=" + creator +
                ", companyId='" + companyId + '\'' +
                ", memberType='" + memberType + '\'' +
                ", field1='" + field1 + '\'' +
                ", field2='" + field2 + '\'' +
                ", field3='" + field3 + '\'' +
                ", field4='" + field4 + '\'' +
                ", field5='" + field5 + '\'' +
                ", field6='" + field6 + '\'' +
                ", field7='" + field7 + '\'' +
                ", field8='" + field8 + '\'' +
                ", field9='" + field9 + '\'' +
                ", field10='" + field10 + '\'' +
                '}';
    }
}
