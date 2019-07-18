package com.ctr.crm.commons.utils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ctr.crm.commons.Constants;
import com.ctr.crm.moduls.allot.models.AllotMember;
import com.ctr.crm.moduls.globalsetting.service.GlobalSettingService;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import com.ctr.crm.moduls.member.models.MemberBaseInfo;
import com.ctr.crm.moduls.member.models.MemberPhone;
import com.ctr.crm.moduls.sales.models.SaleCase;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;

import com.yunhus.commons.jm.EncoderUtils;

public class MemberUtils {

	private static final GlobalSettingService globalSetting = SpringContextUtils.getBean("globalSettingService", GlobalSettingService.class);
	private static final WorkerService workerService = SpringContextUtils.getBean("workerService", WorkerService.class);
    /**
     * 电话号码解密
     */
    public static MemberPhone decodePhone(MemberPhone memberPhone) {
        if (memberPhone != null) {
            memberPhone.setPhone(decodePhone(memberPhone.getPhone()));
        }
        return memberPhone;
    }

    /**
     * 电话号码解密
     */
    public static String decodePhone(String phone) {
        if (StringUtils.isNotBlank(phone) && isCipherText(phone)) {
            phone = EncoderUtils.decodeM(phone);
        }
        return phone;
    }

    /**
     * 电话号码加密
     */
    public static String encodePhone(String phone) {
        if (StringUtils.isNotBlank(phone) && !isCipherText(phone)) {
            phone = EncoderUtils.encodeM(phone);
        }
        return phone;
    }

    public static MemberPhone encodePhone(MemberPhone memberPhone) {
        if (memberPhone != null && !isCipherText(memberPhone.getPhone())) {
            String phone = EncoderUtils.encodeM(memberPhone.getPhone());
            memberPhone.setPhone(phone);
        }
        return memberPhone;
    }

    public static String decodeFileName(String recordFileName) {
        if (StringUtils.isNotBlank(recordFileName)) {
            String[] underline = StringUtils.split(recordFileName, "_");
            if (underline.length > 1 && !"".equals(underline[1])) {
                String[] dot = StringUtils.split(underline[1], ".");
                dot[0] = decodePhone(dot[0]);
                underline[1] = StringUtils.join(dot, ".");
            }
            return StringUtils.join(underline, "_");
        }
        return recordFileName;
    }

    public static String encodeFileName(String recordFileName) {
        if (StringUtils.isNotBlank(recordFileName)) {
            String[] underline = StringUtils.split(recordFileName, "_");
            if (underline.length > 1 && !"".equals(underline[1])) {
                String[] dot = StringUtils.split(underline[1], ".");
                dot[0] = encodePhone(dot[0]);
                underline[1] = StringUtils.join(dot, ".");
            }
            return StringUtils.join(underline, "_");
        }
        return recordFileName;
    }

    public static String maskPhone(String phone, Worker currentWorker) {
    	// 如果系统配置了系统加密并且当前操作人不在白名单,则对号码进行掩码
    	boolean mask = globalSetting.isPhoneEncrypt() && !workerService.isWhiteList(currentWorker);
        return maskPhone(phone, 2, mask);
    }

    public static boolean mask(Worker currentWorker) {
        return globalSetting.isPhoneEncrypt() && !workerService.isWhiteList(currentWorker);
    }

    public static String maskPhone(String phone, boolean mask) {
        return maskPhone(phone, 2, mask);
    }

    /**
     * 针对手机号码进行掩码
     *
     * @param phone
     * @param type       1直接返回 2部分数字用*号掩码 3解密后再做掩码操作
     * @param mask 只针对手机号,如果该值为true,而phone不为手机号，则返回空
     * @return
     */
    public static String maskPhone(String phone, Integer type, boolean mask) {
        if (StringUtils.isBlank(phone))
            return StringUtils.EMPTY;
        if (type != null && type == 1)
            return phone;
        if (type != null && type == 3) {
            phone = Encrypt.decrypt(phone);
        }
        phone = StringUtils.trim(phone);
        int len = phone.length();
        if (len == 0)
            return StringUtils.EMPTY;
		if (mask) {
			if (len >= 8) {
				String reg = "^(.+)(.{4})(.{4})$";
				String pt = phone.replaceFirst(reg, "$2").replaceAll(".{1}", "*");
				return phone.replaceFirst(reg, "$1" + pt + "$3");
			} else {
				return phone.replaceAll(".{1}", "*");
			}
		}
        return phone;
    }

    public static String maskQQNumber(String qqNumber) {
        if (StringUtils.isBlank(qqNumber))
            return "";
        if (qqNumber.length() > 5) {
            return qqNumber.substring(0, 2) + "****" + qqNumber.substring(6, qqNumber.length());
        } else if (qqNumber.length() > 1) {
            return qqNumber = qqNumber.substring(0, 2) + "***";
        }
        return qqNumber;
    }

    /**
     * 是否已加密
     */
    public static boolean isCipherText(String phone) {
        phone = StringUtils.replace(phone, "-", "");
        if (NumberUtils.isDigits(phone)) {
            return false;
        }
        return true;
    }

    /**
     * 是否计数<br>
     * 是否计数为自定义设置，根据公司业务决定。<br>
     * 一般认为公司的<strong>高意向客户</strong>可标记的计数，利于分析和把控客户成单率
     *
     * @param oldCaseClass
     * @param newCaseClass
     * @return boolean
     */
    public static boolean isCount(Integer oldCaseClass, Integer newCaseClass, String companyId) {
        boolean result = false;
		/*ClassProgress newClass = classProgressService.selectByClassStep(companyId, newCaseClass);
		boolean counting = CommonUtils.evalInt((newClass != null ? newClass.getHighIntention() : null), 0) == 1;
		result = counting && oldCaseClass < newCaseClass;// 新增
		if (!result) {
			ClassProgress oldClass = classProgressService.selectByClassStep(companyId, oldCaseClass);
			counting = CommonUtils.evalInt((oldClass != null ? oldClass.getHighIntention() : null), 0) == 1;
			result = counting && oldCaseClass > newCaseClass;// 新降
		}*/
        return result;
    }

    @SuppressWarnings("unchecked")
	public static <T> void maskPhone(List<T> list, String fieldName, Worker currentWorker) {
        if (list == null || list.isEmpty() || fieldName == null)
            return;
        String fieldValue = null;
        for (T t : list) {
        	if(t.getClass() == Map.class){
        		Map<String, Object> map = (Map<String, Object>)t;
        		fieldValue = CommonUtils.evalString(map.get(fieldName));
        		map.put(fieldName, MemberUtils.maskPhone(fieldValue, currentWorker));
        	}else{
        		fieldValue = (String) PojoUtils.getProperties(t, fieldName);
        		PojoUtils.setProperties(t, fieldName, MemberUtils.maskPhone(fieldValue, currentWorker));
        	}
        }
    }

    /**
     * 设置客户类型字段值<br/>
     * 第1位公海类型(0公海1小公海) 第2位是否分配过 第3位黑名单 第4个是否成单 第5位是否在库 第6位存在回收站
     *
     * @param memberType
     * @param position
     * @param value      一般认为 0否1是 特殊情况可能会有存在0和1以外的值
     * @return
     */
    public static String getMemberType(String memberType, int position, char value) {
        if (position < 0 || position > Constants.MEMBER_TYPE_LENGTH)
            return memberType;
        if (memberType == null) {
            StringBuilder sb = new StringBuilder(Constants.MEMBER_TYPE_LENGTH);
            for (int i = 0; i < Constants.MEMBER_TYPE_LENGTH; i++) {
                if (i == position - 1) {
                    sb.append(value);
                    continue;
                }
                sb.append(Constants.NO);
            }
            return sb.toString();
        }
        char[] arr = memberType.toCharArray();
        arr[position - 1] = value;
        return new String(arr);
    }

    public static void setMemberType(MemberBaseInfo baseInfo, int position, char value) {
        if (baseInfo == null)
            return;
        baseInfo.setMemberType(getMemberType(baseInfo.getMemberType(), position, value));
    }

    public static String generateMemberTypeCondi(String field, int position, char value) {
        return " and substring(" + field + "," + position + ",1)=" + value;
    }

    public static String getColumnMemberType(String field, int position, String aliasName) {
        return " ,substring(" + field + "," + position + ",1) " + aliasName;
    }

    public static void main(String[] args) {
        System.out.println(getMemberType(null, 3, Constants.YES));
    }

    /**
     * 判断memberType指定位是否为value
     *
     * @param memberType
     * @param position
     * @param value
     * @return
     */
    public static boolean memberTypeTrue(String memberType, int position, char value) {
        if (position < 0 || position > Constants.MEMBER_TYPE_LENGTH)
            return false;
        if (StringUtils.isBlank(memberType))
            return false;
        char[] chars = memberType.toCharArray();
        for (int i = 0, len = memberType.length(); i < len; i++) {
            if (i != position - 1)
                continue;
            if (chars[i] == value) {
                return true;
            }
        }
        return false;
    }

    public static SaleCase converSaleCase(AllotMember memberBaseInfo) {
        if (null == memberBaseInfo)
            return null;
        SaleCase saleCase = new SaleCase();
        BeanUtils.copyProperties(memberBaseInfo, saleCase);
        saleCase.setMemberId(memberBaseInfo.getMemberId());
        saleCase.setNickName(memberBaseInfo.getNickName());
        saleCase.setTrueName(memberBaseInfo.getTrueName());
        saleCase.setMobile(memberBaseInfo.getMobile());
        saleCase.setWechatId(memberBaseInfo.getWechatId());
        saleCase.setCreateTime(memberBaseInfo.getCreateTime());
        return saleCase;
    }

    public static SaleCase converSaleCase(MemberBaseInfo memberBaseInfo) {
        if (null == memberBaseInfo)
            return null;
        SaleCase saleCase = new SaleCase();
        BeanUtils.copyProperties(memberBaseInfo, saleCase);
        saleCase.setMemberId(memberBaseInfo.getMemberId());
        saleCase.setNickName(memberBaseInfo.getNickName());
        saleCase.setTrueName(memberBaseInfo.getTrueName());
        saleCase.setMobile(memberBaseInfo.getMobile());
        saleCase.setWechatId(memberBaseInfo.getWechatId());
        saleCase.setCreateTime(memberBaseInfo.getCreateTime());
        return saleCase;
    }

    public static Integer diffDayNow(Date startTime) {
        if (startTime == null) {
            return -1;
        }
        int days = (int) ((System.currentTimeMillis() - startTime.getTime()) / (1000*3600*24));
        return days;
    }

}
