package com.ctr.crm.moduls.member.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.member.dao.MemberDao;
import com.ctr.crm.moduls.member.dao.ObjectDao;
import com.ctr.crm.moduls.member.service.MemberAttachmentService;
import com.ctr.crm.moduls.member.service.MemberPhoneService;
import com.ctr.crm.moduls.member.service.MemberService;
import com.ctr.crm.moduls.sales.service.HandsomeService;
import com.ctr.crm.moduls.sales.service.SaleCaseService;
import com.ctr.crm.moduls.tag.models.Tag;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;

import com.ctr.crm.commons.Constants;
import com.ctr.crm.commons.es.MemberBean;
import com.ctr.crm.commons.es.MemberBeanSearch;
import com.ctr.crm.commons.es.SearchClient;
import com.ctr.crm.commons.excel.ExcelUtils;
import com.ctr.crm.commons.jdbc.YunhuJdbcOperations;
import com.ctr.crm.commons.result.ResponsePage;
import com.ctr.crm.commons.utils.Arith;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.Id;
import com.ctr.crm.commons.utils.MemberUtils;
import com.ctr.crm.commons.utils.PhoneUtils;
import com.ctr.crm.commons.utils.file.FileCommonUtils;
import com.ctr.crm.commons.utils.file.FileCommonUtils.UploadInfo;
import com.ctr.crm.moduls.globalsetting.service.GlobalSettingService;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;
import com.ctr.crm.moduls.member.models.MemberInfo;
import com.ctr.crm.moduls.member.models.MemberObjectInfo;
import com.ctr.crm.moduls.sysdict.service.SysDictService;
import com.superaicloud.fileserver.utils.PublicFileUtils;
import com.superaicloud.fileserver.utils.UploadFileUtils;

/**
 * 说明：
 * @author eric
 * @date 2019年4月11日 下午4:33:40
 */
@Service("memberService")
public class MemberServiceImpl implements MemberService {

    private static final Log log = LogFactory.getLog("memberBaseInfo");
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private ObjectDao objectDao;
    @Resource
    private MemberPhoneService memberPhoneService;
    @Autowired
    private SearchClient searchClient;
    @Resource
    private GlobalSettingService globalSettingService;
    @Resource
    private SysDictService sysDictService;
    @Resource
    private YunhuJdbcOperations crmJdbc;
    @Resource
    private MemberAttachmentService memberAttachmentService;
    @Resource
    private SaleCaseService saleCaseService;
    @Resource
    private HandsomeService handsomeService;

    @Override
    public Long getMaxMemberId() {
        return memberDao.getMaxMemberId();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> clazz, Long memberId) {
        if (memberId == null)
            return null;
        if(clazz == MemberBaseInfo.class){
            return (T) memberDao.select(memberId);
        }else if(clazz == MemberObjectInfo.class){
            return (T) objectDao.select(memberId);
        }
        return null;
    }

    @Override
    @Transactional
    public String insert(MemberBaseInfo baseInfo, MemberObjectInfo objectInfo,
                         Worker currentWorker) {
        if(baseInfo == null || StringUtils.isAnyBlank(baseInfo.getMobile()))
            return "手机号为空";
        boolean isDuplicate = globalSettingService.isDuplicate();
        if(isDuplicate){
            if(memberPhoneService.existsPhone(baseInfo.getMobile(),null)){
                return "手机号不能重复";
            }
        }
        Long memberId = Id.generateMemberID();
        baseInfo.setMemberId(memberId);
        baseInfo.setCreator(currentWorker.getWorkerId());
        baseInfo.setCreateTime(new Date());
        boolean success = memberDao.insert(baseInfo);
        if(!success) return "新建失败";
        memberPhoneService.insert(memberId, baseInfo.getMobile());
        if(objectInfo == null) objectInfo = new MemberObjectInfo();
        objectInfo.setMemberId(memberId);
        objectDao.insert(objectInfo);
        //同步至ES
        MemberBean memberBean = new MemberBean();
        BeanUtils.copyProperties(baseInfo, memberBean);
        BeanUtils.copyProperties(objectInfo, memberBean);
        memberBean.setInSaleCase(false);
        memberBean.setIsVip(false);
        memberBean.setIsHandsome(false);
        memberBean.setIsRecycling(false);
        memberBean.setIsBlacklist(false);
        searchClient.update(memberBean);
        return null;
    }

    @Override
    @Transactional
    public String update(MemberBaseInfo baseInfo,
                         MemberObjectInfo objectInfo, Worker currentWorker) {
        if(baseInfo == null || baseInfo.getMemberId() == null)
            return "客户ID为空";
        // 是否需要排重，如果是，并且手机号有修改(即：修改后的号码不在当前客户的号码列表中)，则需要做一次排重检测，如果检测到手机号已存在，则提醒
        boolean isDuplicate = globalSettingService.isDuplicate();
        if(isDuplicate){
            if(memberPhoneService.existsPhone(baseInfo.getMobile(), baseInfo.getMemberId())){
                return "手机号不能重复";
            }
        }
        if(objectInfo == null) objectInfo = new MemberObjectInfo();
        if(objectInfo.getMemberId() == null) objectInfo.setMemberId(baseInfo.getMemberId());
        memberDao.update(baseInfo);
        MemberObjectInfo objectInfo2 = objectDao.select(baseInfo.getMemberId());
        BeanUtils.copyProperties(objectInfo, objectInfo2);
        objectDao.update(objectInfo2);
        memberPhoneService.insert(baseInfo.getMemberId(), baseInfo.getMobile());
        MemberBean memberBean = new MemberBean();
        BeanUtils.copyProperties(baseInfo, memberBean);
        BeanUtils.copyProperties(objectInfo, memberBean);
        searchClient.update(memberBean);
        saleCaseService.updateSaleCase(baseInfo);
        handsomeService.updateHandsome(baseInfo);
        return null;
    }

    @Override
    @Transactional
    public String update(MemberBaseInfo baseInfo) {
        if(baseInfo == null || baseInfo.getMemberId() == null)
            return "客户ID为空";
        // 是否需要排重，如果是，并且手机号有修改(即：修改后的号码不在当前客户的号码列表中)，则需要做一次排重检测，如果检测到手机号已存在，则提醒
        boolean isDuplicate = globalSettingService.isDuplicate();
        if(isDuplicate){
            if(memberPhoneService.existsPhone(baseInfo.getMobile(), baseInfo.getMemberId())){
                return "手机号不能重复";
            }
        }
        memberDao.update(baseInfo);
        memberPhoneService.insert(baseInfo.getMemberId(), baseInfo.getMobile());
        MemberBean memberBean = new MemberBean();
        BeanUtils.copyProperties(baseInfo, memberBean);
        searchClient.update(memberBean);
        saleCaseService.updateSaleCase(baseInfo);
        handsomeService.updateHandsome(baseInfo);
        return null;
    }

    @Override
    public String update(MemberObjectInfo objectInfo) {
        if(objectInfo == null || objectInfo.getMemberId() == null)
            return "客户ID为空";
        objectDao.update(objectInfo);
        MemberBean memberBean = new MemberBean();
        BeanUtils.copyProperties(objectInfo, memberBean);
        searchClient.update(memberBean);
        return null;
    }

    @Override
    public Page<MemberBean> search(MemberBeanSearch memberBean, Worker currentWorker) {
    	Page<MemberBean> result = searchClient.search(memberBean);
    	for (MemberBean bean : result.getContent()) {
			if(bean.getMobiles() == null || bean.getMobiles().isEmpty())
				continue;
			List<String> mobiles = new ArrayList<>();
			for (String mobile : bean.getMobiles()) {
				mobiles.add(MemberUtils.maskPhone(mobile, currentWorker));
			}
			bean.setMobiles(mobiles);
		}
    	return result;
    }
    
    @Override
    public ResponsePage<MemberBean> match(MemberBeanSearch search,
    		MemberBaseInfo u, MemberObjectInfo o) {
    	Page<MemberBean> result = searchClient.match(search, u, o);
    	ResponsePage<MemberBean> responsePage = new ResponsePage<MemberBean>(search.getPage(), search.getPageSize());
		responsePage.setTotal(result.getTotalElements());
		MemberBaseInfo baseInfo = null;
		for (MemberBean memberBean : result.getContent()) {
			memberBean.setMobiles(null);
			memberBean.setPhone(null);
			baseInfo = get(MemberBaseInfo.class, memberBean.getMemberId());
			if(baseInfo != null && StringUtils.isNotBlank(baseInfo.getHeadurl())){
				memberBean.setHeadUrl(PublicFileUtils.getPublicFileUrl(baseInfo.getMemberId(), "0", baseInfo.getHeadurl()));
			}
		}
		responsePage.setList(result.getContent());
		return responsePage;
    }

    @Override
    public boolean updateHeadurl(MultipartFile file, Long memberId, Worker currentWorker) {
        MemberBaseInfo baseInfo = get(MemberBaseInfo.class, memberId);
        if(baseInfo == null) return false;
        boolean success = upload(file, memberId, currentWorker);
        // 如果头像更新成功，则把旧的头像删除
        if(success && baseInfo.getHeadurl() != null){
            try {
                UploadFileUtils.remove(memberId, "0", baseInfo.getHeadurl());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    private boolean upload(MultipartFile file, Long memberId, Worker currentWorker) {
        if (file == null)
            return false;
        UploadInfo info = FileCommonUtils.upload(file, memberId, FileCommonUtils.size100x125);
        if(info == null) return false;
        memberAttachmentService.insert(memberId, file, currentWorker);
        MemberBaseInfo baseInfo = new MemberBaseInfo();
        baseInfo.setMemberId(memberId);
        baseInfo.setHeadurl(info.getUploadFileName());
        return update(baseInfo)==null;
    }

    @Override
    public Map<String, Object> importData(List<MemberInfo> memberList,
                                          Worker currentWorker) {
        // 是否需要排重
        boolean isDuplicate = globalSettingService.isDuplicate();
        // 失败条数
        AtomicInteger failureNum = new AtomicInteger();
        // 成功条数
        AtomicInteger successNum = new AtomicInteger();
        // 错误报告数据
        List<Map<String, Object>> errorList = new ArrayList<>();
        if(!memberList.isEmpty()){
            // 分N个线程同时处理，加快导入效率
            int maxThreads = Runtime.getRuntime().availableProcessors()*2;
            int threads = (int) Math.ceil(Arith.div(memberList.size(), 100));
            if(threads == 0) threads = 1;
            if(threads > maxThreads) threads = maxThreads;
            CountDownLatch countDownLatch = new CountDownLatch(threads);
            int fromIndex = 0;
            for (int i = 0; i < threads; i++) {
                int toIndex = fromIndex + memberList.size()/threads;
                if(i == threads-1) toIndex = memberList.size();
                List<MemberInfo> subRows = memberList.subList(fromIndex, toIndex);
                fromIndex += memberList.size()/threads;
                new Thread(new HandlerThread(subRows, currentWorker, errorList, isDuplicate, failureNum, successNum, countDownLatch)).start();
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(null, "客户资料"), ExcelUtils.convertEntity(MemberInfo.class, "错误信息"), errorList);
        String remoteFile = null, filePath = null;
        try(ByteArrayOutputStream byteOut = new ByteArrayOutputStream()){
            workbook.write(byteOut);
            remoteFile = UploadFileUtils.upload(byteOut.toByteArray(), "xls", currentWorker.getWorkerId().longValue(), currentWorker.getCompanyId());
            filePath = PublicFileUtils.getPublicFileUrl(currentWorker.getWorkerId().longValue(), currentWorker.getCompanyId(), remoteFile);
        }catch(Exception e){
            log.error("import report exception.", e);
        }
        Map<String, Object> resultMap = new HashMap<>();
        // 生成导入历史
        importReport(remoteFile, currentWorker, new Date(), failureNum.get(), successNum.get());
        resultMap.put("failureNum", failureNum.get());
        resultMap.put("successNum", successNum.get());
        resultMap.put("errorfilePath", filePath);
        resultMap.put("importDate", CommonUtils.formateDateToStr(new Date(), "yyyy-MM-dd HH:mm:ss"));

        return resultMap;
    }

    /**
     * 导入客户线程类
     * @author eric
     *
     */
    class HandlerThread implements Runnable {
        private final List<MemberInfo> memberList;
        private final List<Map<String, Object>> errorList;
        private final Worker currentWorker;
        private final boolean isDuplicate;
        private final AtomicInteger failureNum;
        private final AtomicInteger successNum;
        private final CountDownLatch countDownLatch;

        public HandlerThread(List<MemberInfo> memberList, Worker worker,
                             List<Map<String, Object>> errorList,
                             boolean isDuplicate, AtomicInteger failureNum,
                             AtomicInteger successNum, CountDownLatch countDownLatch) {
            this.memberList = memberList;
            this.errorList = errorList;
            this.currentWorker = worker;
            this.isDuplicate = isDuplicate;
            this.failureNum = failureNum;
            this.successNum = successNum;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            if (memberList == null || memberList.isEmpty())
                return;
            try{
                log.info(String.format("thread:%s import %d rows data",Thread.currentThread().getName(), memberList.size()));
                long begin = System.currentTimeMillis();
                MemberBaseInfo memberBaseInfo = null;
                Date now = new Date();
                for (MemberInfo memberInfo: memberList) {
                    memberBaseInfo = new MemberBaseInfo();
                    Map<String, Object> map = com.ctr.crm.commons.utils.BeanUtils.transBeanToMap(memberInfo);
                    // 判重
                    String result = null;
                    if(result == null && !PhoneUtils.isPhoneLegal(memberInfo.getMobile())){
                    	result = "非法手机号";
                    }
                    if(result == null && isDuplicate){
                        if(memberPhoneService.existsPhone(memberInfo.getMobile(), null)){
                            result = "重复手机号";
                        }
                    }
                    /*if(memberInfo.getSex() == null){
                    	result = "性别为空";
                    }*/
                    if(result == null){
                        BeanUtils.copyProperties(memberInfo, memberBaseInfo);
                        memberBaseInfo.setCreateTime(now);
                        memberBaseInfo.setCreator(currentWorker.getWorkerId());
                        memberBaseInfo.setMemberType(MemberUtils.getMemberType(null, Constants.member_type_seas, Constants.YES));
                        result = insert(memberBaseInfo, null, currentWorker);
                    }
                    if (result != null) {
                        map.put("errormsg", result);
                        errorList.add(map);
                        failureNum.incrementAndGet();
                    } else {
                        successNum.incrementAndGet();
                    }
                }
                long dealTime = (System.currentTimeMillis()-begin)/1000;
                log.info(String.format("thread:%s take time:%d",Thread.currentThread().getName(), dealTime));
            }catch(Exception e){
                log.error(e.getMessage());
            }finally{
                countDownLatch.countDown();
            }
        }
    }

    private void importReport(String fileName,Worker worker, Date importTime,int failureNum, int successNum){
        if(fileName == null) return;
        final String sql = "insert into ImportHistory(fileName,workerId,companyId,importTime,failureNum,successNum) values(?,?,?,?,?,?)";
        crmJdbc.update(sql, fileName, worker.getWorkerId(), worker.getCompanyId(),importTime,failureNum, successNum);
    }

    @Override
    public List<MemberBaseInfo> searchByMemberIds(String memberIds) {
        return memberDao.searchByMemberIds(memberIds);
    }

    @Override
    public Integer updateMemberType(List<MemberBaseInfo> updateMemberList) {
        if (updateMemberList.size() == 0)
            return 0;
        StringBuffer sql = new StringBuffer("update MemberBaseInfo set memberType= case memberId ");
        updateMemberList.forEach(member -> sql.append("WHEN " + member.getMemberId() + " THEN " + member.getMemberType() + " "));
        sql.append(" end WHERE memberId in ");
        List<Long> memberIds = updateMemberList.stream()
                .map(MemberBaseInfo::getMemberId).distinct()
                .collect(Collectors.toList());
        String allMember = String.join(",", memberIds.toString());
        sql.append("(").append(allMember.substring(1, allMember.length() - 1).replaceAll(" ", "")).append(")");
        return crmJdbc.update(sql.toString());
    }

    @Override
    public void updateMemberBean(List<MemberBean> updateMemberBeanList) {
        if (updateMemberBeanList.size() == 0) {
            return;
        }
        updateMemberBeanList.forEach(memberBean -> searchClient.update(memberBean));
    }
    
    @Override
    public void handsome(Long memberId, boolean b) {
    	if(memberId == null) return;
    	MemberBaseInfo baseInfo = get(MemberBaseInfo.class, memberId);
    	if(baseInfo == null) return;
    	MemberUtils.setMemberType(baseInfo, Constants.member_type_ishandsome, b?Constants.YES:Constants.NO);
    	update(baseInfo);
    	MemberBean bean = new MemberBean(memberId);
    	bean.setIsHandsome(b);
    	searchClient.update(bean);
    }

	@Override
	public void insertMemberTag(Long memberId, String[] tagIds) {
		if(memberId == null || tagIds == null) return;
		for (String tagId : tagIds) {
			memberDao.insertMemberTag(memberId, tagId);
		}
	}

	@Override
	public void deleteMemberTag(Long memberId, String tagId) {
		if(memberId == null || tagId == null) return;
		memberDao.deleteMemberTag(memberId, tagId);
	}

	@Override
	public List<Tag> getMemberTag(Long memberId) {
		if(memberId == null) return null;
		return memberDao.getMemberTag(memberId);
	}
}
