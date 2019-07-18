package com.ctr.crm.commons.sms.channel.emay;

import com.ctr.crm.commons.sms.channel.emay.util.AES;
import com.ctr.crm.commons.sms.channel.emay.util.GZIPUtils;
import com.ctr.crm.commons.sms.channel.emay.util.JsonHelper;
import com.ctr.crm.commons.sms.channel.emay.model.http.v1.dto.request.MoRequest;
import com.ctr.crm.commons.sms.channel.emay.model.http.v1.dto.request.ReportRequest;
import com.ctr.crm.commons.sms.channel.emay.model.http.v1.dto.request.SmsSingleRequest;
import com.ctr.crm.commons.sms.channel.emay.model.http.v1.dto.response.ResultModel;
import com.ctr.crm.commons.sms.channel.emay.util.http.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * /**
 *
 * @author: DoubleLi
 * @date: 2019/5/30 10:34
 * @description:
 */
public class EmayUtils {

    private final static Log log = LogFactory.getLog("sms");
    public static final String SUCCESS = "SUCCESS";

    /**
     * 发送单条短信
     *
     * @param isGzip 是否压缩
     */
    public static ResultModel setSingleSms(String appId, String secretKey, String host, String algorithm, String content, String customSmsId, String extendCode, String mobile, boolean isGzip, String encode) {
        SmsSingleRequest pamars = new SmsSingleRequest();
        pamars.setContent(content);
        pamars.setCustomSmsId(customSmsId);
        pamars.setExtendedCode(extendCode);
        pamars.setMobile(mobile);
        ResultModel result = request(appId, secretKey, algorithm, pamars, host + "/inter/sendSingleSMS", isGzip, encode);
        return result;
    }

    /**
     * 获取状态报告
     *
     * @param isGzip 是否压缩
     */
    public static ResultModel getReport(String appId, String secretKey, String host, String algorithm, boolean isGzip, String encode) {
        ReportRequest pamars = new ReportRequest();
        ResultModel result = request(appId, secretKey, algorithm, pamars, host + "/inter/getReport", isGzip, encode);
        return result;
    }

    /**
     * 获取上行
     *
     * @param isGzip 是否压缩
     */
    public static ResultModel getMo(String appId, String secretKey, String host, String algorithm, boolean isGzip, String encode) {
        MoRequest pamars = new MoRequest();
        ResultModel result = request(appId, secretKey, algorithm, pamars, host + "/inter/getMo", isGzip, encode);
        return result;
    }

    /**
     * 获取余额
     *
     * @param isGzip 是否压缩
     */
    public static ResultModel getBalance(String appId, String secretKey, String host, String algorithm, boolean isGIzp, String encode) {
        MoRequest pamars = new MoRequest();
        ResultModel result = request(appId, secretKey, algorithm, pamars, host + "/simpleinter/getBalance", isGIzp, encode);
        return result;
    }

    /**
     * 公共请求方法
     */
    public static ResultModel request(String appId, String secretKey, String algorithm, Object content, String url, final boolean isGzip, String encode) {
        Map<String, String> headers = new HashMap<>();
        HttpRequest<byte[]> request = null;
        try {
            headers.put("appId", appId);
            headers.put("encode", encode);
            String requestJson = JsonHelper.toJsonString(content);
            byte[] bytes = requestJson.getBytes(encode);
            if (isGzip) {
                headers.put("gzip", "on");
                bytes = GZIPUtils.compress(bytes);
            }
            byte[] parambytes = AES.encrypt(bytes, secretKey.getBytes(), algorithm);
            if (parambytes == null)
                return null;
            HttpRequestParams<byte[]> params = new HttpRequestParams<byte[]>();
            params.setCharSet("UTF-8");
            params.setMethod("POST");
            params.setHeaders(headers);
            params.setParams(parambytes);
            params.setUrl(url);
            if (url.startsWith("https://")) {
                request = new HttpsRequestBytes(params, null);
            } else {
                request = new HttpRequestBytes(params);
            }
        } catch (Exception e) {
            log.error("加密异常", e);
        }
        HttpClient client = new HttpClient();
        String code = null;
        String result = null;
        try {
            HttpResponseBytes res = client.service(request, new HttpResponseBytesPraser());
            if (res == null) {
                return new ResultModel(code, result);
            }
            if (res.getResultCode().equals(HttpResultCode.SUCCESS)) {
                if (res.getHttpCode() == 200) {
                    code = res.getHeaders().get("result");
                    if (code.equals(SUCCESS)) {
                        byte[] data = res.getResult();
                        data = AES.decrypt(data, secretKey.getBytes(), algorithm);
                        if (isGzip) {
                            data = GZIPUtils.decompress(data);
                        }
                        result = new String(data, encode);
                    }
                } else {
                    log.error("请求接口异常,请求码:" + res.getHttpCode());
                }
            } else {
                log.error("请求接口网络异常:" + res.getResultCode().getCode());
            }
        } catch (Exception e) {
            log.error("解析失败");
        }
        ResultModel re = new ResultModel(code, result);
        return re;
    }

}
