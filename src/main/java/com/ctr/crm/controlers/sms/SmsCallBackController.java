package com.ctr.crm.controlers.sms;

import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.sms.channel.SuperChannel;
import com.ctr.crm.commons.utils.SpringContextUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 功能描述: 短信上行与状态上行回调接口
 *
 * @author: DoubleLi
 * @date: 2019/4/28 15:59
 */
@Api(tags = "短信推送")
@RequestMapping(value = "/api/sms_back")
@Controller
@Secure(-1)
public class SmsCallBackController {

    private final static Log log = LogFactory.getLog("sms");

    @ApiOperation(value = "短信上行接口", notes = "用于接收用户短信，根据各通道商返回相应的json字符串结果")
    @RequestMapping(value = "up/{service}", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String smsCallBack(HttpServletRequest request, @PathVariable String service) {
        try {
            if (StringUtils.isBlank(service)) {
                return service + "请求未找到";
            }
            SuperChannel channel = null;
            try {
                channel = (SuperChannel) SpringContextUtils.getBean(service);
            } catch (Exception e) {
                log.error("短信未找到渠道" + service);
                return service + "错误";
            }
            String body = IOUtils.toString(request.getInputStream(),"UTF-8");
            String result = channel.handlerSms(body);
            return result;
        } catch (IOException e) {
            log.error("短信上行", e);
        }
        return "异常请求";
    }

    @ApiOperation(value = "状态回调接口", notes = "用于接收发送短信的状态，根据各通道商返回相应的json字符串结果")
    @RequestMapping(value = "status/{service}", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String statusCallBack(HttpServletRequest request, @PathVariable String service) {
        if (StringUtils.isBlank(service)) {
            return service + "请求未找到";
        }
        try {
            SuperChannel channel = null;
            try {
                channel = (SuperChannel) SpringContextUtils.getBean(service);
            } catch (Exception e) {
                log.error("短信未找到渠道" + service);
                return service + "渠道错误";
            }
            String body = IOUtils.toString(request.getInputStream(),"UTF-8");
            String result = channel.handlerStatus(body);
            return result;
        } catch (IOException e) {
            log.error("短信状态回执", e);
        }
        return "异常请求";
    }
}
