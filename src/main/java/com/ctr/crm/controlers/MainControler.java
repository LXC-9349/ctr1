package com.ctr.crm.controlers;

import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.CommonUtils;
import com.ctr.crm.commons.utils.JwtUtil;
import com.ctr.crm.commons.utils.Md5;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import com.ctr.crm.moduls.purview.service.RbacService;
import com.ctr.crm.interceptors.CurrentWorkerAware;
import com.ctr.crm.interceptors.CurrentWorkerLocalCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 说明：
 *
 * @author eric
 * @date 2019年4月19日 上午10:41:20
 */
@Api(tags = "登入/出接口")
@RequestMapping("/api/")
@RestController
public class MainControler implements CurrentWorkerAware {

    @Resource
    private WorkerService workerService;
    @Resource
    private RbacService rbacService;

    @RequestMapping(value = "test", method = {RequestMethod.GET})
    public void test() {

    }

    @ApiOperation(value = "登录接口", notes = "登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workerId", value = "工号或帐号", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "password", value = "密码(md5加密)", required = true, dataTypeClass = String.class)
    })
    @RequestMapping(value = "login", method = {RequestMethod.POST})
    public ResponseData login(String workerId, String password) {
        if (StringUtils.isAnyBlank(workerId, password)) {
            return new ResponseData(ResponseStatus.null_param);
        }
        Worker worker = null;
        try {
            Integer wid = CommonUtils.evalInteger(workerId);
            worker = workerService.selectExact(wid);
            if (worker == null) {
                worker = workerService.selectByAccount(workerId);
            }
        } catch (Exception e) {
            if (StringUtils.contains(e.getMessage(), "maximum statement execution time")) {
                return new ResponseData(ResponseStatus.failed, "登录超时");
            }
        }
        if (worker == null) {
            return new ResponseData(ResponseStatus.non_workerId, "工号不存在");
        }
        String pswDbStr = Md5.decrpyt(worker.getPsw(), null);
        String psw = Md5.getMD5ofStr(pswDbStr);
        if (!StringUtils.equals(password, psw)) {
            return new ResponseData(ResponseStatus.failed, "工号或密码不正确");
        }
        String token = JwtUtil.generateToken(worker.getWorkerId());
        if (token == null) {
            return new ResponseData(ResponseStatus.failed, "登录异常");
        }
        @SuppressWarnings("serial")
        Map<String, Object> data = new HashMap<String, Object>() {
            {
                put("token", token);
            }
        };
        workerService.login(worker.getWorkerId(), new Date(), token);
        return new ResponseData(ResponseStatus.success, data);
    }

    @Secure(0)
    @ApiOperation(value = "登出接口", notes = "登出接口")
    @RequestMapping(value = "logout", method = {RequestMethod.POST})
    public ResponseData logout() {
        Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
        workerService.logout(currentWorker.getWorkerId(), new Date());
        return new ResponseData(ResponseStatus.success);
    }

    @Secure(0)
    @ApiOperation(value = "登录人信息接口", notes = "当前登录人信息")
    @RequestMapping(value = "login/info", method = {RequestMethod.GET})
    public ResponseData info() {
        Worker currentWorker = CurrentWorkerLocalCache.getCurrentWorker();
        Map<String, Object> data = new HashMap<>();
        data.put("worker", currentWorker);
        data.put("menus", rbacService.getMenuPermissions(currentWorker.getWorkerId()));
        data.put("actions", rbacService.getActionPermissions(currentWorker.getWorkerId()));
        data.put("range", rbacService.getRangePermission(currentWorker.getWorkerId()));
        return new ResponseData(ResponseStatus.success, data);
    }
}
