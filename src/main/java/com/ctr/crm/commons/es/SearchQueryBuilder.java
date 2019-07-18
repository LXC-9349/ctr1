package com.ctr.crm.commons.es;

import com.ctr.crm.moduls.member.models.MemberBaseInfo;
import com.ctr.crm.moduls.member.models.MemberObjectInfo;
import com.ctr.crm.commons.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.Date;

/**
 * 说明：
 *
 * @author eric
 * @date 2019年5月21日 下午3:25:25
 */
public class SearchQueryBuilder {

    public static SearchQuery build(MemberBeanSearch search, MemberBaseInfo u, MemberObjectInfo o, int page, int size) {
        BoolQueryBuilder mainBuilder = new BoolQueryBuilder();
        if (u != null && u.getSex() != null) {
            int sex = u.getSex() == 1 ? 0 : 1;
            mainBuilder.must(QueryBuilders.termQuery("sex", sex));
        }
        if (search.getBusinessType() != null) {
            if (search.getBusinessType() == 1) {//我在找谁
                //我在找谁，也就是符合我的择偶条件的
                buildMateCondition(mainBuilder, o);
            } else if (search.getBusinessType() == 2) {
                //天生一对 即：客户属性符合我的择偶条件，同时我的属性也符合别人的择偶条件
                buildMateCondition(mainBuilder, o);
                buildMyCondition(mainBuilder, u);
            } else if (search.getBusinessType() == 3) {
                //谁在找我 即我的属性符合其他用户的择偶条件
                buildMyCondition(mainBuilder, u);
            }
        }
        // 组装搜索条件
        buildFormCondition(mainBuilder, search);
        // 组装业务搜索条件
        buildBussinessCondition(mainBuilder, search);
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(mainBuilder);
        PageRequest pageRequest = PageRequest.of(page, size);
        return new NativeSearchQueryBuilder()
                .withPageable(pageRequest)
                .withQuery(functionScoreQueryBuilder)
                .build();
    }

    private static void buildFormCondition(BoolQueryBuilder mainBuilder,
                                           MemberBeanSearch search) {
        if (search == null) return;
        if (search.getSex() != null) {
            mainBuilder.must(QueryBuilders.termQuery("sex", search.getSex()));
        }
        if (search.getMemberId() != null) {
            mainBuilder.must(QueryBuilders.termQuery("memberId", search.getMemberId()));
        }
        if (StringUtils.isNotBlank(search.getTrueName())) {
            mainBuilder.must(QueryBuilders.matchPhraseQuery("trueName", search.getTrueName()));
        }
        if (StringUtils.isNotBlank(search.getWechatId())) {
            mainBuilder.must(QueryBuilders.matchPhraseQuery("wechatId", search.getWechatId()));
        }
        if (StringUtils.isNotBlank(search.getMobile())) {
            mainBuilder.must(QueryBuilders.termQuery("mobiles", search.getMobile()));
        }
        if (search.getAgeB() != null) {
            String birthdayE = CommonUtils.getBirthdayByAge(search.getAgeB() - 1);
            mainBuilder.must(QueryBuilders.rangeQuery("birthday").to(CommonUtils.parseDateFormStr(birthdayE, "yyyy-MM-dd").getTime(), false));
        }
        if (search.getAgeE() != null) {
            String birthdayB = CommonUtils.getBirthdayByAge(search.getAgeE());
            mainBuilder.must(QueryBuilders.rangeQuery("birthday").from(CommonUtils.parseDateFormStr(birthdayB, "yyyy-MM-dd").getTime()));
        }
        if (search.getHeightB() != null) {
            mainBuilder.must(QueryBuilders.rangeQuery("height").from(search.getHeightB()));
        }
        if (search.getHeightE() != null) {
            mainBuilder.must(QueryBuilders.rangeQuery("height").to(search.getHeightE()));
        }
        if (search.getWeightB() != null) {
            mainBuilder.must(QueryBuilders.rangeQuery("weight").from(search.getWeightB()));
        }
        if (search.getWeightE() != null) {
            mainBuilder.must(QueryBuilders.rangeQuery("weight").to(search.getWeightE()));
        }
        if (search.getSalary() != null && search.getSalary().length > 0) {
            BoolQueryBuilder childBuilder = new BoolQueryBuilder();
            for (Integer salary : search.getSalary()) {
                childBuilder.should(QueryBuilders.termQuery("salary", salary));
            }
            mainBuilder.must(childBuilder);
        }
        if (search.getEducation() != null && search.getEducation().length > 0) {
            BoolQueryBuilder childBuilder = new BoolQueryBuilder();
            for (Integer education : search.getEducation()) {
                childBuilder.should(QueryBuilders.termQuery("education", education));
            }
            mainBuilder.must(childBuilder);
        }
        if (search.getMarriage() != null && search.getMarriage().length > 0) {
            BoolQueryBuilder childBuilder = new BoolQueryBuilder();
            for (Integer marriage : search.getMarriage()) {
                childBuilder.should(QueryBuilders.termQuery("marriage", marriage));
            }
            mainBuilder.must(childBuilder);
        }
        if (search.getOccupation() != null && search.getOccupation().length > 0) {
            BoolQueryBuilder childBuilder = new BoolQueryBuilder();
            for (Integer occupation : search.getOccupation()) {
                childBuilder.should(QueryBuilders.termQuery("occupation", occupation));
            }
            mainBuilder.must(childBuilder);
        }
        if (search.getAnimals() != null && search.getAnimals().length > 0) {
            BoolQueryBuilder childBuilder = new BoolQueryBuilder();
            for (Integer animals : search.getAnimals()) {
                childBuilder.should(QueryBuilders.termQuery("animals", animals));
            }
            mainBuilder.must(childBuilder);
        }
        if (search.getConstellation() != null && search.getConstellation().length > 0) {
            BoolQueryBuilder childBuilder = new BoolQueryBuilder();
            for (Integer constellation : search.getConstellation()) {
                childBuilder.should(QueryBuilders.termQuery("constellation", constellation));
            }
            mainBuilder.must(childBuilder);
        }
        if (search.getBody() != null && search.getBody().length > 0) {
            BoolQueryBuilder childBuilder = new BoolQueryBuilder();
            for (Integer body : search.getBody()) {
                childBuilder.should(QueryBuilders.termQuery("body", body));
            }
            mainBuilder.must(childBuilder);
        }
        if (search.getHouse() != null && search.getHouse().length > 0) {
            BoolQueryBuilder childBuilder = new BoolQueryBuilder();
            for (Integer house : search.getHouse()) {
                childBuilder.should(QueryBuilders.termQuery("house", house));
            }
            mainBuilder.must(childBuilder);
        }
        if (search.getCar() != null) {
            mainBuilder.must(QueryBuilders.termQuery("car", search.getCar()));
        }
        if (StringUtils.isNotBlank(search.getWorkCity())) {
            mainBuilder.must(QueryBuilders.matchPhraseQuery("workCity", search.getWorkCity()));
        }
        if (StringUtils.isNotBlank(search.getHomeTown())) {
            mainBuilder.must(QueryBuilders.matchPhraseQuery("homeTown", search.getHomeTown()));
        }
        if (StringUtils.isNotBlank(search.getField1())) {
            mainBuilder.must(QueryBuilders.matchPhraseQuery("field1", search.getField1()));
        }
    }

    /**
     * 组装择偶条件,即用户属性符合我的择偶条件
     *
     * @param mainBuilder
     * @param o
     */
    private static void buildMateCondition(BoolQueryBuilder mainBuilder, MemberObjectInfo o) {
        if (o == null) return;
        buildAge(mainBuilder, o.getOage1(), o.getOage2());
        buildHeight(mainBuilder, o.getOheight1(), o.getOheight2());
        buildWeight(mainBuilder, o.getOweight1(), o.getOweight2());
        buildSalary(mainBuilder, o.getOsalary1(), o.getOsalary2());
        buildEducation(mainBuilder, o.getOeducation1(), o.getOeducation2());
        buildOther(mainBuilder, o);
    }

    /**
     * 组装我的属性符合别人的择偶条件
     *
     * @param mainBuilder
     * @param u
     */
    private static void buildMyCondition(BoolQueryBuilder mainBuilder, MemberBaseInfo u) {
        if (u == null) return;
        buildOAge(mainBuilder, u.getBirthday());
        buildOHeight(mainBuilder, u.getHeight());
        buildOWeight(mainBuilder, u.getWeight());
        buildOSalary(mainBuilder, u.getSalary());
        buildOEducation(mainBuilder, u.getEducation());
        buildMyOther(mainBuilder, u);
    }

    /**
     * 组装业务筛选条件
     *
     * @param mainBuilder
     * @param search
     */
    private static void buildBussinessCondition(BoolQueryBuilder mainBuilder, MemberBeanSearch search) {
        if (search == null) return;
        if (search.getWorkerId() != null) {
            mainBuilder.must(QueryBuilders.termQuery("workerId", search.getWorkerId()));
        }
        if (search.getCaseClass() != null) {
            mainBuilder.must(QueryBuilders.termQuery("caseClass", search.getCaseClass()));
        }
        if (StringUtils.isNotBlank(search.getWorkerName())) {
            mainBuilder.must(QueryBuilders.matchPhraseQuery("workerName", search.getWorkerName()));
        }
        if (search.getQuitWorkerId() != null) {
            mainBuilder.must(QueryBuilders.termQuery("quitWorkerId", search.getQuitWorkerId()));
        }
        if (StringUtils.isNotBlank(search.getQuitWorkerName())) {
            mainBuilder.must(QueryBuilders.matchPhraseQuery("quitWorkerName", search.getQuitWorkerName()));
        }
        if (StringUtils.isNotBlank(search.getQuitReason())) {
            mainBuilder.must(QueryBuilders.matchPhraseQuery("quitReason", search.getQuitReason()));
        }
        if (search.getOrderSignWorkerId() != null) {
            mainBuilder.must(QueryBuilders.termQuery("orderSignWorkerId", search.getOrderSignWorkerId()));
        }
        if (StringUtils.isNotBlank(search.getOrderSignWorkerName())) {
            mainBuilder.must(QueryBuilders.matchPhraseQuery("orderSignWorkerName", search.getOrderSignWorkerName()));
        }
        if (search.getInSaleCase() != null) {
            mainBuilder.must(QueryBuilders.termQuery("inSaleCase", search.getInSaleCase()));
        }
        if (search.getIsVip() != null) {
            mainBuilder.must(QueryBuilders.termQuery("isVip", search.getIsVip()));
        }
        if (search.getIsHandsome() != null) {
            mainBuilder.must(QueryBuilders.termQuery("isHandsome", search.getIsHandsome()));
        }
        if (search.getIsBlacklist() != null) {
            mainBuilder.must(QueryBuilders.termQuery("isBlacklist", search.getIsBlacklist()));
        }
        if (search.getIsRecycling() != null) {
            mainBuilder.must(QueryBuilders.termQuery("isRecycling", search.getIsRecycling()));
        }
        if (StringUtils.isNotBlank(search.getWorkerIds())) {
            mainBuilder.must(QueryBuilders.termsQuery("workerId", search.getWorkerIds().split(",")));
        }
        if (StringUtils.isNotBlank(search.getDeptIds())) {
            mainBuilder.must(QueryBuilders.termsQuery("deptId", search.getDeptIds().split(",")));
        }
        if (StringUtils.isNotBlank(search.getCreateTimeA())) {
            mainBuilder.must(QueryBuilders.rangeQuery("createTime").gte(CommonUtils.parseDateFormStr(search.getCreateTimeA(), "yyyy-MM-dd").getTime()));
        }
        if (StringUtils.isNotBlank(search.getCreateTimeB())) {
            mainBuilder.must(QueryBuilders.rangeQuery("createTime").lte(CommonUtils.parseDateFormStr(CommonUtils.strToNextDateStr(search.getCreateTimeB(), "yyyy-MM-dd"), "yyyy-MM-dd").getTime()));
        }
        if (StringUtils.isNotBlank(search.getAllotTimeA())) {
            mainBuilder.must(QueryBuilders.rangeQuery("allotTime").gte(CommonUtils.parseDateFormStr(search.getAllotTimeA(), "yyyy-MM-dd").getTime()));
        }
        if (StringUtils.isNotBlank(search.getAllotTimeB())) {
            mainBuilder.must(QueryBuilders.rangeQuery("allotTime").lte(CommonUtils.parseDateFormStr(CommonUtils.strToNextDateStr(search.getAllotTimeB(), "yyyy-MM-dd"), "yyyy-MM-dd").getTime()));
        }
        if (StringUtils.isNotBlank(search.getLastContactTimeA())) {
            mainBuilder.must(QueryBuilders.rangeQuery("lastContactTime").gte(CommonUtils.parseDateFormStr(search.getLastContactTimeA(), "yyyy-MM-dd").getTime()));
        }
        if (StringUtils.isNotBlank(search.getLastContactTimeB())) {
            mainBuilder.must(QueryBuilders.rangeQuery("lastContactTime").lte(CommonUtils.parseDateFormStr(CommonUtils.strToNextDateStr(search.getLastContactTimeB(), "yyyy-MM-dd"), "yyyy-MM-dd").getTime()));
        }
        if (StringUtils.isNotBlank(search.getOrderSignTimeA())) {
            mainBuilder.must(QueryBuilders.rangeQuery("orderSignTime").gte(CommonUtils.parseDateFormStr(search.getOrderSignTimeA(), "yyyy-MM-dd").getTime()));
        }
        if (StringUtils.isNotBlank(search.getOrderSignTimeB())) {
            mainBuilder.must(QueryBuilders.rangeQuery("orderSignTime").lte(CommonUtils.parseDateFormStr(CommonUtils.strToNextDateStr(search.getOrderSignTimeB(), "yyyy-MM-dd"), "yyyy-MM-dd").getTime()));
        }
        if (StringUtils.isNotBlank(search.getCreateTime())) {
            String[] createTimeRange = search.getCreateTime().split("~");
            mainBuilder.must(QueryBuilders.rangeQuery("createTime").from(CommonUtils.parseDateFormStr(createTimeRange[0], "yyyy-MM-dd").getTime(), true)
                    .to(CommonUtils.parseDateFormStr(CommonUtils.strToNextDateStr(createTimeRange[1], "yyyy-MM-dd"), "yyyy-MM-dd").getTime()));
        }
    }

    /**
     * 组装择偶年龄条件
     *
     * @param mainQuery
     * @param moi
     */
    private static void buildAge(BoolQueryBuilder mainBuilder, Integer oage1, Integer oage2) {
        int to = oage1 != null && oage1 > 10 ? oage1 : 18;
        int from = oage2 != null && oage2 > 10 ? oage2 + 1 : 101;
        String birthdayE = CommonUtils.getBirthdayByAge(to - 1);
        String birthdayB = CommonUtils.getBirthdayByAge(from);
        mainBuilder.must(QueryBuilders.rangeQuery("birthday")
                .from(CommonUtils.parseDateFormStr(birthdayB, "yyyy-MM-dd").getTime())
                .to(CommonUtils.parseDateFormStr(birthdayE, "yyyy-MM-dd").getTime(), false));
    }

    /**
     * 组装择偶身高条件
     *
     * @param mainQuery
     * @param moi
     */
    private static void buildHeight(BoolQueryBuilder mainBuilder, Integer oheight1, Integer oheight2) {
        if (oheight1 != null) {
            mainBuilder.must(QueryBuilders.rangeQuery("height").from(oheight1));
        }
        if (oheight2 != null) {
            mainBuilder.must(QueryBuilders.rangeQuery("height").to(oheight2));
        }
    }

    /**
     * 组装择偶体重条件
     *
     * @param mainQuery
     * @param moi
     */
    private static void buildWeight(BoolQueryBuilder mainBuilder, Integer oweight1, Integer oweight2) {
        if (oweight1 != null) {
            mainBuilder.must(QueryBuilders.rangeQuery("weight").from(oweight1));
        }
        if (oweight2 != null) {
            mainBuilder.must(QueryBuilders.rangeQuery("weight").to(oweight2));
        }
    }

    /**
     * 组装择偶薪水条件
     *
     * @param mainQuery
     * @param moi
     */
    private static void buildSalary(BoolQueryBuilder mainBuilder, Integer osalary1, Integer osalary2) {
        if (osalary1 != null) {
            mainBuilder.must(QueryBuilders.rangeQuery("salary").from(osalary1 % 100));
        }
        if (osalary2 != null) {
            mainBuilder.must(QueryBuilders.rangeQuery("salary").to(osalary2 % 100));
        }
    }

    /**
     * 组装择偶学历条件
     *
     * @param mainQuery
     * @param moi
     */
    private static void buildEducation(BoolQueryBuilder mainBuilder, Integer oeducation1, Integer oeducation2) {
        if (oeducation1 != null) {
            mainBuilder.must(QueryBuilders.rangeQuery("education").from(oeducation1));
        }
        if (oeducation2 != null) {
            mainBuilder.must(QueryBuilders.rangeQuery("education").to(oeducation2));
        }
    }

    /**
     * 组装择偶其他条件
     *
     * @param mainQuery
     * @param moi
     */
    private static void buildOther(BoolQueryBuilder mainBuilder, MemberObjectInfo object) {
        if (object == null) return;
        if (object.getOmarriage() != null) {
            mainBuilder.must(QueryBuilders.termQuery("marriage", object.getOmarriage()));
        }
        if (object.getObody() != null) {
            mainBuilder.must(QueryBuilders.termQuery("body", object.getObody()));
        }
        if (object.getOcar() != null) {
            mainBuilder.must(QueryBuilders.termQuery("car", object.getOcar()));
        }
        if (object.getOchildren() != null) {
            mainBuilder.must(QueryBuilders.termQuery("children", object.getOchildren()));
        }
        if (object.getOdrinking() != null) {
            mainBuilder.must(QueryBuilders.termQuery("drinking", object.getOdrinking()));
        }
        if (object.getOhouse() != null) {
            mainBuilder.must(QueryBuilders.termQuery("house", object.getOhouse()));
        }
        if (object.getOsmoking() != null) {
            mainBuilder.must(QueryBuilders.termQuery("smoking", object.getOsmoking()));
        }
        if (object.getOwantChildren() != null) {
            mainBuilder.must(QueryBuilders.termQuery("wantChildren", object.getOwantChildren()));
        }
        if (StringUtils.isNotBlank(object.getOworkCity())) {
            mainBuilder.must(QueryBuilders.matchPhraseQuery("workCity", object.getOworkCity()));
        }
        if (StringUtils.isNotBlank(object.getOhomeTown())) {
            mainBuilder.must(QueryBuilders.matchPhraseQuery("homeTown", object.getOhomeTown()));
        }
    }

    /**
     * 组装oage条件
     *
     * @param mainFilter
     * @param moi
     */
    private static void buildOAge(BoolQueryBuilder mianBuilder, Date birthday) {
        Integer age = CommonUtils.age(birthday);
        if (age == null) return;
        mianBuilder.must(QueryBuilders.rangeQuery("oage1").from(age));
        mianBuilder.must(QueryBuilders.rangeQuery("oage2").to(age));
    }

    /**
     * 组装oheight条件
     *
     * @param mainQuery
     * @param moi
     */
    private static void buildOHeight(BoolQueryBuilder mainBuilder, Integer height) {
        if (height == null) return;
        mainBuilder.must(QueryBuilders.rangeQuery("oheight1").from(height));
        mainBuilder.must(QueryBuilders.rangeQuery("oheight2").to(height));
    }

    /**
     * 组装oweight条件
     *
     * @param mainQuery
     * @param moi
     */
    private static void buildOWeight(BoolQueryBuilder mainBuilder, Integer weight) {
        if (weight == null) return;
        mainBuilder.must(QueryBuilders.rangeQuery("oweight1").from(weight));
        mainBuilder.must(QueryBuilders.rangeQuery("oweight2").to(weight));
    }

    /**
     * 组装osalary条件
     *
     * @param mainQuery
     * @param moi
     */
    private static void buildOSalary(BoolQueryBuilder mainBuilder, Integer salary) {
        if (salary == null) return;
        mainBuilder.must(QueryBuilders.rangeQuery("osalary1").from(salary + 100));
        mainBuilder.must(QueryBuilders.rangeQuery("osalary2").to(salary + 100));
    }

    /**
     * 组装oeducation条件
     *
     * @param mainQuery
     * @param moi
     */
    private static void buildOEducation(BoolQueryBuilder mainBuilder, Integer education) {
        if (education == null) return;
        mainBuilder.must(QueryBuilders.rangeQuery("oeducation1").from(education));
        mainBuilder.must(QueryBuilders.rangeQuery("oeducation2").to(education));
    }

    private static void buildMyOther(BoolQueryBuilder mainBuilder, MemberBaseInfo u) {
        if (u == null) return;
        if (u.getMarriage() != null) {
            mainBuilder.must(QueryBuilders.termQuery("omarriage", u.getMarriage()));
        }
        if (u.getBody() != null) {
            mainBuilder.must(QueryBuilders.termQuery("obody", u.getBody()));
        }
        if (u.getCar() != null) {
            mainBuilder.must(QueryBuilders.termQuery("ocar", u.getCar()));
        }
        if (u.getChildren() != null) {
            mainBuilder.must(QueryBuilders.termQuery("ochildren", u.getChildren()));
        }
        if (u.getDrinking() != null) {
            mainBuilder.must(QueryBuilders.termQuery("odrinking", u.getDrinking()));
        }
        if (u.getHouse() != null) {
            mainBuilder.must(QueryBuilders.termQuery("ohouse", u.getHouse()));
        }
        if (u.getSmoking() != null) {
            mainBuilder.must(QueryBuilders.termQuery("osmoking", u.getSmoking()));
        }
        if (u.getWantChildren() != null) {
            mainBuilder.must(QueryBuilders.termQuery("owantChildren", u.getWantChildren()));
        }
        if (StringUtils.isNotBlank(u.getWorkCity())) {
            mainBuilder.must(QueryBuilders.matchPhraseQuery("oworkCity", u.getWorkCity()));
        }
        if (StringUtils.isNotBlank(u.getHomeTown())) {
            mainBuilder.must(QueryBuilders.matchPhraseQuery("ohomeTown", u.getHomeTown()));
        }
    }

}
