package com.ctr.crm.interceptors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.result.ResponseData;
import com.ctr.crm.commons.result.ResponseStatus;
import com.ctr.crm.commons.utils.AnnontationUtils;
import com.ctr.crm.commons.utils.JwtUtil;
import com.ctr.crm.commons.utils.PojoUtils;
import com.ctr.crm.moduls.hrm.models.Worker;
import com.ctr.crm.moduls.hrm.models.WorkerLog;
import com.ctr.crm.moduls.hrm.service.WorkerService;
import com.ctr.crm.moduls.purview.service.RbacService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.alibaba.fastjson.JSON;

/**
 * 说明：
 * @author eric
 * @date 2019年4月9日 下午6:24:47
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

	@Resource
	private RbacService rbacService;
	@Resource
	private WorkerService workerService;
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		Object bean;
		try {
			bean = ((HandlerMethod) handler).getBean();
		} catch (Exception e) {
			return true;
		}
		final Secure secure = AnnontationUtils.getSecureAnnontation(bean, ((HandlerMethod) handler).getMethod().getName(),
				((HandlerMethod) handler).getMethod().getParameterTypes());
		Integer currentWorkerId = null;
		if(secure == null || secure.value() == -1){
			// 如果secure为空，则直接通过，不做任何验证
		}else if(secure.value() >= 0){
			// 验证是否登录
			String token = request.getHeader("token");
			if(!JwtUtil.verify(token)){
				PojoUtils.sendClient(response, JSON.toJSONString(new ResponseData(ResponseStatus.no_login)));
				return false;
			}
			currentWorkerId = JwtUtil.extractWorkerId(token);
			if(currentWorkerId == null){
				PojoUtils.sendClient(response, JSON.toJSONString(new ResponseData(ResponseStatus.no_login)));
				return false;
			}
			WorkerLog log = workerService.getLastWorkerLog(currentWorkerId);
			if(log != null && (StringUtils.isBlank(log.getToken()) 
					|| !StringUtils.equals(token, log.getToken()))){
				PojoUtils.sendClient(response, JSON.toJSONString(new ResponseData(ResponseStatus.no_login)));
				return false;
			}
		}
		final Menu menu = AnnontationUtils.getMenuAnnontation(bean, ((HandlerMethod) handler).getMethod().getName(),
				((HandlerMethod) handler).getMethod().getParameterTypes());
		if(menu != null && menu.verify()){
			if(!rbacService.auth(currentWorkerId, menu.menuUrl(), 1)){
				PojoUtils.sendClient(response, JSON.toJSONString(new ResponseData(ResponseStatus.no_access, "无菜单权限["+menu.menuName()+"]")));
				return false;
			}
		}else{
			// 如果secure.value为1并且uri不为空，则要进行权限验证
			if(secure != null && secure.value() == 1 && !"".equals(secure.actionUri())){
				if(!rbacService.auth(currentWorkerId, secure.actionUri(), 2)){
					PojoUtils.sendClient(response, JSON.toJSONString(new ResponseData(ResponseStatus.no_access, "无操作权限["+secure.actionName()+"]")));
					return false;
				}
			}
		}
		Worker currentWorker = null;
		if(secure != null && secure.value()>=0){
			if (currentWorkerId != null) {
				currentWorker = workerService.selectCache(currentWorkerId);
			}
			if (currentWorker == null) {
				PojoUtils.sendClient(response, JSON.toJSONString(new ResponseData(ResponseStatus.no_login, null)));
				return false;
			}
		}
		// 如果当前需验证登录并且实现了CurrentWorkerAware接口，则获取并注入当前Worker
		if (bean instanceof CurrentWorkerAware) {
			CurrentWorkerLocalCache.setCurrentWorker(currentWorker);
		}
		return true;
	}
}
