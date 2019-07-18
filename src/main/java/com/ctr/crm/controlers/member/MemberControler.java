package com.ctr.crm.controlers.member;

import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Parent;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.es.MemberBean;
import com.ctr.crm.commons.es.MemberBeanSearch;
import com.ctr.crm.commons.excel.ExcelUtils;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponsePage;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.MemberUtils;
import com.ctr.crm.commons.utils.PhoneUtils;
import com.ctr.crm.commons.utils.file.FileCommonUtils;
import com.ctr.crm.moduls.allot.service.AllotService;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import com.ctr.crm.moduls.intentionality.models.Intentionality;
import com.ctr.crm.moduls.intentionality.service.IntentionalityService;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;
import com.ctr.crm.moduls.member.models.MemberInfo;
import com.ctr.crm.moduls.member.models.MemberObjectInfo;
import com.ctr.crm.moduls.member.service.MemberPhoneService;
import com.ctr.crm.moduls.member.service.MemberService;
import com.ctr.crm.moduls.sales.models.SaleCase;
import com.ctr.crm.moduls.sales.service.SaleCaseService;
import com.ctr.crm.moduls.sysdict.service.SysDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
import com.superaicloud.fileserver.utils.PublicFileUtils;

/**
 * 说明：
 *
 * @author eric
 * @date 2019年4月10日 下午4:12:14
 */
@Api(tags = "资源管理")
@Secure(0)
@RestController
@RequestMapping("/api/member")
public class MemberControler implements CurrentWorkerAware {

    private static final Log logger = LogFactory.getLog("memberBaseInfo");
    @Resource
    private MemberService memberService;
    @Resource
    private MemberPhoneService memberPhoneService;
    @Resource
    private SaleCaseService saleCaseService;
    @Resource
    private SysDictService sysDictService;
    @Resource
    private IntentionalityService intentionalityService;
    @Resource
    private WorkerService workerService;
    @Resource
    private AllotService allotService;

    @ApiOperation("新建客户")
    @RequestMapping(value = "insert", method = {RequestMethod.POST})
    @Menu(menuName = "新建客户", menuUrl = "member_new", foundational = false, parent = @Parent(menuName = "资源管理", menuUrl = "member", foundational = false))
    public ResponseData save(@ModelAttribute MemberBaseInfo baseInfo, @ModelAttribute MemberObjectInfo objectInfo) {
        if (baseInfo == null) {
            return new ResponseData(ResponseStatus.failed, "客户信息为空");
        }
        if (StringUtils.isBlank(baseInfo.getTrueName())) {
            return new ResponseData(ResponseStatus.failed, "客户姓名为空");
        }
        if (StringUtils.isAnyBlank(baseInfo.getMobile())) {
            return new ResponseData(ResponseStatus.failed, "手机号为空");
        }
        if (!PhoneUtils.isPhoneLegal(baseInfo.getMobile())) {
            return new ResponseData(ResponseStatus.failed, "非法手机号");
        }
        if (baseInfo.getSex() == null) {
            return new ResponseData(ResponseStatus.failed, "性别为空");
        }
        Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();

        String result = memberService.insert(baseInfo, objectInfo, currentWorker);
        if (StringUtils.isNotBlank(result)) {
            return new ResponseData(ResponseStatus.failed, result);
        }
        return new ResponseData(ResponseStatus.success, "新建成功");
    }

    @ApiOperation("同步客户(同时分配给操作用户,不计入库容)")
    @RequestMapping(value = "sync", method = {RequestMethod.POST})
    @Menu(menuName = "同步客户", menuUrl = "member_sync", foundational = false, parent = @Parent(menuName = "资源管理", menuUrl = "member", foundational = false))
    public ResponseData sync(@ModelAttribute MemberBaseInfo baseInfo, @ModelAttribute MemberObjectInfo objectInfo) {
        String msg = null;
        if (baseInfo == null) {
            msg = "客户信息为空";
        } else if (StringUtils.isBlank(baseInfo.getTrueName())) {
            msg = "客户姓名为空";
        } else if (StringUtils.isAnyBlank(baseInfo.getMobile())) {
            msg = "手机号为空";
        } else if (!PhoneUtils.isPhoneLegal(baseInfo.getMobile())) {
            msg = "非法手机号";
        } else if (baseInfo.getSex() == null) {
            msg = "性别为空";
        } else if (memberPhoneService.existsPhone(baseInfo.getMobile(), null)) {
            msg = "手机号不能重复";
        }
        if (StringUtils.isNotBlank(msg)) {
            return new ResponseData(ResponseStatus.failed, msg);
        }
        Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
        String result = memberService.insert(baseInfo, objectInfo, currentWorker);
        Long memberId=baseInfo.getMemberId();
        if (StringUtils.isNotBlank(result)||memberId==null) {
            return new ResponseData(ResponseStatus.failed, result);
        }
        //分配给登陆销售
        List<MemberBaseInfo> memberList = memberService.searchByMemberIds(memberId.toString());
        Map<String,Object> map = allotService.salesAllot(memberList, Arrays.asList(new Worker[]{currentWorker}), currentWorker,false);
        if((int)(map.get("success"))>0){
            return new ResponseData(ResponseStatus.success, "同步成功",map);
        }else{
            return new ResponseData(ResponseStatus.failed, "同步失败",map);
        }

    }

    @ApiOperation("修改客户")
    @RequestMapping(value = "update", method = {RequestMethod.POST})
    @Secure(value = 1, actionName = "修改客户", actionUri = "/api/member/update", actionNote = "资源管理")
    public ResponseData update(@ModelAttribute MemberBaseInfo baseInfo, @ModelAttribute MemberObjectInfo objectInfo) {
        if (baseInfo == null) {
            return new ResponseData(ResponseStatus.failed, "客户信息为空");
        }
        if (baseInfo.getMemberId() == null) {
            return new ResponseData(ResponseStatus.failed, "客户ID为空");
        }
        MemberBaseInfo info = memberService.get(MemberBaseInfo.class, baseInfo.getMemberId());
        if (info == null) {
            return new ResponseData(ResponseStatus.failed, "客户不存在");
        }
        Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
        String result = memberService.update(baseInfo, objectInfo, currentWorker);
        if (StringUtils.isNotBlank(result)) {
            return new ResponseData(ResponseStatus.failed, result);
        }
        return new ResponseData(ResponseStatus.success, "修改成功");
    }

    @ApiOperation("资源公海")
    @RequestMapping(value = "search", method = {RequestMethod.GET})
    @Menu(menuName = "资源公海", menuUrl = "member_all", foundational = false, parent = @Parent(menuName = "资源管理", menuUrl = "member", foundational = false))
    public ResponseData search(@ModelAttribute MemberBeanSearch search) {
        search.setIsBlacklist(false);
        search.setIsRecycling(false);
        Page<MemberBean> result = memberService.search(search, CurrentWorkerLocalCache.getCurrentWorker());
        ResponsePage<MemberBean> responsePage = new ResponsePage<MemberBean>(search.getPage(), search.getPageSize());
        responsePage.setTotal(result.getTotalElements());
        responsePage.setList(result.getContent());
        return new ResponseData(ResponseStatus.success, responsePage);
    }

    @ApiOperation(value = "导入模板", notes = "客户导入模板")
    @RequestMapping(value = "excel/template", method = {RequestMethod.GET})
    public ResponseData excelTemplate(HttpServletResponse response) throws Exception {
        String excelFileName = CommonUtils.formateDateToStr(new Date(), "yyyyMMddHHmmss") + "-template";
        ExcelUtils.export(ExcelUtils.createExcelTempalte(MemberInfo.class, "客户资料"), excelFileName, response);
        return null;
    }

    @ApiOperation(value = "客户数据导入", notes = "客户数据导入")
    @RequestMapping(value = "import", method = {RequestMethod.POST})
    @Menu(menuName = "导入客户", menuUrl = "member_import", foundational = false, parent = @Parent(menuName = "资源管理", menuUrl = "member", foundational = false))
    public ResponseData excelImport(@RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
        if (file == null) {
            return new ResponseData(ResponseStatus.failed, "excel文件为空");
        }
        String fileName = file.getOriginalFilename();
        if (!CommonUtils.isMatched("^.+\\.(?i)((xls)|(xlsx))$", fileName)) {
            return new ResponseData(ResponseStatus.file_format_incorrect);
        }
        List<MemberInfo> infos = null;
        long beginTime = System.currentTimeMillis();
        try {
            logger.info("excel analysis begin");
            infos = ExcelUtils.importExcel(file, MemberInfo.class);
            logger.info("excel analysis end time:" + (System.currentTimeMillis() - beginTime) + "ms");
        } catch (Exception e) {
            return new ResponseData(ResponseStatus.failed, e.getMessage());
        }
        if (infos == null) {
            return new ResponseData(ResponseStatus.failed, "excel解析异常");
        }
        beginTime = System.currentTimeMillis();
        logger.info("excel import begin");
        Map<String, Object> resultMap = memberService.importData(infos, currentWorker);
        logger.info("excel import end time:" + (System.currentTimeMillis() - beginTime) + "ms");
        return new ResponseData(ResponseStatus.success, resultMap);
    }

    @ApiOperation(value = "更新客户头像", notes = "更新客户头像")
    @RequestMapping(value = "headurl/{memberId}", method = {RequestMethod.POST})
    public ResponseData updateHeadurl(@RequestParam(value = "file", required = false) MultipartFile file,
                                      @PathVariable("memberId") Long memberId) throws Exception {
        if (null == file) {
            return new ResponseData(ResponseStatus.failed, "头像文件为空");
        }
        if (!FileCommonUtils.checkImage(file.getInputStream())) {
            return new ResponseData(ResponseStatus.failed, "非法图片格式");
        }
        Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
        boolean success = memberService.updateHeadurl(file, memberId, currentWorker);
        if (!success) {
            return new ResponseData(ResponseStatus.failed);
        }
        return new ResponseData(ResponseStatus.success);
    }

    @ApiOperation("客户详情")
    @ApiImplicitParam(name = "memberId", value = "客户ID或手机号", dataTypeClass = String.class, required = true)
    @RequestMapping(value = "{memberId}", method = {RequestMethod.GET})
    public ResponseData memberInfo(@PathVariable("memberId") String memberId) throws Exception {
        if (memberId == null) {
            return new ResponseData(ResponseStatus.failed, "参数为空");
        }
        MemberBaseInfo baseInfo = null;
        Long userId = CommonUtils.evalLong(memberId);
        baseInfo = memberService.get(MemberBaseInfo.class, userId);
        if (baseInfo == null) {
            userId = memberPhoneService.getMemberIdInSaleCaseByPhone(memberId, true);
            baseInfo = memberService.get(MemberBaseInfo.class, userId);
        }
        if (baseInfo == null) {
            return new ResponseData(ResponseStatus.failed, "客户不存在");
        }
        baseInfo.setMobile(MemberUtils.maskPhone(baseInfo.getMobile(), CurrentWorkerLocalCache.getCurrentWorker()));
        if (StringUtils.isNotBlank(baseInfo.getHeadurl())) {
            baseInfo.setHeadurl(PublicFileUtils.getPublicFileUrl(userId, "0", baseInfo.getHeadurl()));
        }
        MemberObjectInfo objectInfo = memberService.get(MemberObjectInfo.class, userId);
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> salecase = new HashMap<>();
        salecase.put("rubbish", MemberUtils.memberTypeTrue(baseInfo.getMemberType(), Constants.member_type_rubbish, Constants.YES));
        salecase.put("handsome", MemberUtils.memberTypeTrue(baseInfo.getMemberType(), Constants.member_type_ishandsome, Constants.YES));
        // 分配信息
        SaleCase sc = saleCaseService.getByMemberId(userId);
        if (sc == null) {
            salecase.put("label", "未分配");
        } else {
            salecase.put("caseClass", sc.getCaseClass());
            Intentionality intentionality = intentionalityService.select(sc.getCaseClass());
            if (intentionality != null) {
                salecase.put("label", intentionality.getType() == 1 ? "销售库" : (intentionality.getType() == 2 ? "红娘库" : "才俊佳丽库"));
            }
            Worker w = workerService.selectCache(sc.getWorkerId());
            salecase.put("workerInfo", w.getDeptName() + " " + w.getWorkerName() + " 工号：" + w.getWorkerId());
            salecase.put("intensive", sc.getIntensive());
        }
        data.put("baseInfo", baseInfo);
        data.put("objectInfo", objectInfo);
        data.put("salecase", salecase);
        return new ResponseData(ResponseStatus.success, data);
    }

    @ApiOperation("匹配搜索")
    @RequestMapping(value = "match", method = {RequestMethod.GET})
    public ResponseData matchSearch(@ModelAttribute MemberBeanSearch search, Long userId) throws Exception {
        if (userId == null) {
            return new ResponseData(ResponseStatus.failed, "客户ID为空");
        }
        MemberBaseInfo baseInfo = memberService.get(MemberBaseInfo.class, userId);
        if (baseInfo == null) {
            return new ResponseData(ResponseStatus.failed, "客户不存在");
        }
        MemberObjectInfo objectInfo = memberService.get(MemberObjectInfo.class, userId);
        return new ResponseData(ResponseStatus.success, memberService.match(search, baseInfo, objectInfo));
    }

}
