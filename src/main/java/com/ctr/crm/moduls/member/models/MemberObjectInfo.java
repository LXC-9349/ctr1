package com.ctr.crm.moduls.member.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModelProperty.AccessMode;

import java.io.Serializable;

@ApiModel(value="MemberObjectInfo", description="客户择偶信息")
public class MemberObjectInfo implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5498796401747187847L;
	@ApiModelProperty(value="客户ID", accessMode=AccessMode.READ_ONLY,notes="系统生成", hidden=true)
	private Long memberId;
	@ApiModelProperty(value="择偶-年龄1 注：范围值18~99")
    private Integer oage1;
	@ApiModelProperty(value="择偶-年龄2 注：范围值18~99")
    private Integer oage2;
	@ApiModelProperty(value="择偶-身高1 注：范围值140cm~200cm 140表示140以下, 200表示200以上")
    private Integer oheight1;
	@ApiModelProperty(value="择偶-身高2 注：范围值140cm~200cm 140表示140以下, 200表示200以上")
    private Integer oheight2;
	@ApiModelProperty(value="择偶-月收入1 注：101:1000元 102:2000元 103:3000元 104:5000元 105:8000元 106:10000元 107:20000元 108:50000元")
    private Integer osalary1;
	@ApiModelProperty(value="择偶-月收入2 注：101:1000元 102:2000元 103:3000元 104:5000元 105:8000元 106:10000元 107:20000元 108:50000元")
    private Integer osalary2;
	@ApiModelProperty(value="择偶-体重1 注：范围值30kg~120kg 30表示30以下,120表示120以上")
    private Integer oweight1;
	@ApiModelProperty(value="择偶-体重2 注：范围值30kg~120kg 30表示30以下,120表示120以上")
    private Integer oweight2;
	@ApiModelProperty(value="择偶-学历1 注：1高中及以下 2中专 3大专 4大学本科 5硕士 6博士")
    private Integer oeducation1;
	@ApiModelProperty(value="择偶-学历2 注：1高中及以下 2中专 3大专 4大学本科 5硕士 6博士")
    private Integer oeducation2;
	@ApiModelProperty(value="择偶-婚姻状况 注：1未婚 2离异 3丧偶")
    private Integer omarriage;
	@ApiModelProperty(value="择偶-体型 注：1一般 2较瘦 3苗条 4壮实 5丰满 6较胖")
    private Integer obody;
	@ApiModelProperty(value="择偶-工作地")
    private String oworkCity;
	@ApiModelProperty(value="择偶-籍贯")
    private String ohomeTown;
	@ApiModelProperty(value="择偶-是否有小孩 注：1没有 2有,住一起 3有,偶尔住一起 4有,不在身边")
    private Integer ochildren;
	@ApiModelProperty(value="择偶-是否想要小孩 注：1想要孩子 2不想要孩子 3视情况而定")
    private Integer owantChildren;
	@ApiModelProperty(value="择偶-是否吸烟 注：1不吸烟 2稍微抽一点 3社交场合抽 4抽得很多")
    private Integer osmoking;
	@ApiModelProperty(value="择偶-是否喝酒 注：1不喝酒 2稍微喝一点 3社交场合喝 4喝得很多")
    private Integer odrinking;
	@ApiModelProperty(value="择偶-购房情况 注：1租房 2全款购房 3按揭购房 4老家有住房")
    private Integer ohouse;
	@ApiModelProperty(value="择偶-购车情况 注：1未购车 2已购车")
    private Integer ocar;

    public Long getMemberId(){
        return this.memberId;
    }
    public void setMemberId(Long memberId){
        this.memberId = memberId;
    }
    public Integer getOage1(){
        return this.oage1;
    }
    public void setOage1(Integer oage1){
        this.oage1 = oage1;
    }
    public Integer getOage2(){
        return this.oage2;
    }
    public void setOage2(Integer oage2){
        this.oage2 = oage2;
    }
    public Integer getOheight1(){
        return this.oheight1;
    }
    public void setOheight1(Integer oheight1){
        this.oheight1 = oheight1;
    }
    public Integer getOheight2(){
        return this.oheight2;
    }
    public void setOheight2(Integer oheight2){
        this.oheight2 = oheight2;
    }
    public Integer getOsalary1(){
        return this.osalary1;
    }
    public void setOsalary1(Integer osalary1){
        this.osalary1 = osalary1;
    }
    public Integer getOsalary2(){
        return this.osalary2;
    }
    public void setOsalary2(Integer osalary2){
        this.osalary2 = osalary2;
    }
    public Integer getOweight1(){
        return this.oweight1;
    }
    public void setOweight1(Integer oweight1){
        this.oweight1 = oweight1;
    }
    public Integer getOweight2(){
        return this.oweight2;
    }
    public void setOweight2(Integer oweight2){
        this.oweight2 = oweight2;
    }
    public Integer getOeducation1(){
        return this.oeducation1;
    }
    public void setOeducation1(Integer oeducation1){
        this.oeducation1 = oeducation1;
    }
    public Integer getOeducation2(){
        return this.oeducation2;
    }
    public void setOeducation2(Integer oeducation2){
        this.oeducation2 = oeducation2;
    }
    public Integer getOmarriage(){
        return this.omarriage;
    }
    public void setOmarriage(Integer omarriage){
        this.omarriage = omarriage;
    }
    public Integer getObody(){
        return this.obody;
    }
    public void setObody(Integer obody){
        this.obody = obody;
    }
    public String getOworkCity(){
        return this.oworkCity;
    }
    public void setOworkCity(String oworkCity){
        this.oworkCity = oworkCity;
    }
    public String getOhomeTown(){
        return this.ohomeTown;
    }
    public void setOhomeTown(String ohomeTown){
        this.ohomeTown = ohomeTown;
    }
    public Integer getOchildren(){
        return this.ochildren;
    }
    public void setOchildren(Integer ochildren){
        this.ochildren = ochildren;
    }
    public Integer getOwantChildren(){
        return this.owantChildren;
    }
    public void setOwantChildren(Integer owantChildren){
        this.owantChildren = owantChildren;
    }
    public Integer getOsmoking(){
        return this.osmoking;
    }
    public void setOsmoking(Integer osmoking){
        this.osmoking = osmoking;
    }
    public Integer getOdrinking(){
        return this.odrinking;
    }
    public void setOdrinking(Integer odrinking){
        this.odrinking = odrinking;
    }
    public Integer getOhouse(){
        return this.ohouse;
    }
    public void setOhouse(Integer ohouse){
        this.ohouse = ohouse;
    }
    public Integer getOcar(){
        return this.ocar;
    }
    public void setOcar(Integer ocar){
        this.ocar = ocar;
    }
}
