package com.ctr.crm.listeners;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ctr.crm.commons.annontations.Approval;
import com.ctr.crm.commons.annontations.Menu;
import com.ctr.crm.commons.annontations.Secure;
import com.ctr.crm.commons.configuration.Config;
import com.ctr.crm.commons.rbac.RangeField;
import com.ctr.crm.commons.utils.AnnontationUtils;
import com.ctr.crm.moduls.purview.service.RbacService;
import com.ctr.crm.moduls.system.service.CallAddressService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

/**
 * 说明：系统初始化事件
 *
 * @author eric
 * @date 2019年4月11日 下午6:29:22
 */
public class ApplicationInitialEvent extends ApplicationEvent{

	private final static Log logger = LogFactory.getLog(ApplicationInitialEvent.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 4746886198818580227L;
	private ApplicationContext applicationContext;
	private RbacService rbacService;
	// 系统初始化两个角色
	private final List<String> roles = Arrays.asList("管理员,此角色有全部权限","标准,此角色没有管理员的权限");
	/** 审批流，不存键值重复的*/
	public static Set<Map<String, Object>> approval_list = new HashSet<>();

	public ApplicationInitialEvent(Object source) {
		super(source);
		this.applicationContext = (ApplicationContext) source;
		rbacService = applicationContext.getBean("rbacService", RbacService.class);
	}
	
	public void initApplicationData(){
		initDefaultRole();
		initActionData();
		initMenuData();
		initRangeData();
		initCallAddress();
		rbacService.initDefaultAuth();
		initApproval();
	}
	
	/**
	 * 初始化默认角色
	 */
	private void initDefaultRole(){
		long beginTime = System.currentTimeMillis();
		for (String role : roles) {
			String[] roleInfo = role.split(",");
			rbacService.addRole(roleInfo[0], roleInfo[1], 1);
		}
		logger.info("initial default roles. times:"+(System.currentTimeMillis()-beginTime)+"ms");
	}
	
	/**
	 * 初始化操作权限数据<br>
	 * 权限的初始化是通过扫描secure注解进行的
	 */
	private void initActionData(){
		long beginTime = System.currentTimeMillis();
		Map<String, Object> beanNames = applicationContext.getBeansWithAnnotation(Secure.class);
		Set<Entry<String, Object>> set = beanNames.entrySet();
		for (Iterator<Entry<String, Object>> it = set.iterator();it.hasNext();) {
			Entry<String, Object> entry = it.next();
			if(entry.getValue() == null) return;
			List<Secure> secures = AnnontationUtils.getAnnotations(Secure.class, entry.getValue());
			for (Secure secure : secures) {
				if(secure == null || "".equals(secure.actionName())
						|| "".equals(secure.actionUri())) 
					continue;
				rbacService.addAction(secure.actionName(), secure.actionUri(), secure.actionNote(), secure.foundational());
			}
		}
		logger.info("initial actions. times:"+(System.currentTimeMillis()-beginTime)+"ms");
	}
	
	/**
	 * 初始化菜单权限数据<br>
	 * 权限的初始化是通过扫描menu注解进行的
	 */
	private void initMenuData(){
		long beginTime = System.currentTimeMillis();
		String[] beanNames = applicationContext.getBeanDefinitionNames();
		if(beanNames == null || beanNames.length == 0) return;
		for (String beanName: beanNames) {
			Class<?> clazz = applicationContext.getType(beanName);
			if(clazz == null) return;
			List<Menu> Menus = AnnontationUtils.getAnnotations(Menu.class, clazz);
			for (Menu menu : Menus) {
				if(menu == null || "".equals(menu.menuName())
						|| "".equals(menu.menuUrl())) continue;
				Integer parentId = null;
				if(!"".equals(menu.parent().menuName())){
					//如果有父菜单，则先初始化父菜单
					parentId = rbacService.addMenu(menu.parent().menuName(), menu
							.parent().menuUrl(), menu.parent().orderNo(), menu
							.parent().foundational(), null);
				}
				rbacService.addMenu(menu.menuName(), menu.menuUrl(), menu.orderNo(), menu.foundational(), parentId);
			}
		}
		logger.info("initial menus. times:"+(System.currentTimeMillis()-beginTime)+"ms");
	}
	
	/**
	 * 初始化数据范围
	 */
	private void initRangeData(){
		long beginTime = System.currentTimeMillis();
		RangeField[] ranges = RangeField.values();
		for (int i=0,len=ranges.length;i<len;i++) {
			rbacService.addRange(ranges[i].getRangeValue(), ranges[i].getRangeName(), i);
		}
		logger.info("initial ranges. times:"+(System.currentTimeMillis()-beginTime)+"ms");
	}
	
	/**
	 * 初始化呼叫地址
	 */
	private void initCallAddress(){
		long beginTime = System.currentTimeMillis();
		Config config = applicationContext.getBean(Config.class);
		CallAddressService callService = applicationContext.getBean("callAddressService", CallAddressService.class);
		callService.insert(config.getCompanyCode(), config.getAppid(), config.getAccessKey(), config.getAddressIp());
		logger.info("initial calladdress. times:"+(System.currentTimeMillis()-beginTime)+"ms");
	}

    /**
     * 功能描述: 初始化权限管理寻找@Approval
     *
     * @author: DoubleLi
     * @date: 2019/4/25 17:53
     */
    public void initApproval() {
    	logger.info("--------ApprovalScan Begin scan  @Approval--");
        String[] beans = applicationContext.getBeanDefinitionNames();
        List<Map<String, Object>> sonAction = new ArrayList<>();
        for (String beanName : beans) {
            Class<?> beanClass = applicationContext.getType(beanName);
            Method[] methods = ReflectionUtils.getUniqueDeclaredMethods(beanClass);
            if (null != methods) {
                for (Method method : methods) {
                    Approval approval = AnnotationUtils.findAnnotation(method, Approval.class);// Approval注解
                    if (null != approval) {// 审批注解处理
                        Map<String, Object> map = new HashMap<>(2);
                        map.put("value", approval.value());//即审批类型标识值
                        map.put("name", approval.name());//即审批类型名称 流程配置需要针对具体审批来定义
                        approval_list.add(map);
                        Map<String, Object> mapson = new HashMap<>(2);
                        mapson.put("value", approval.value());//类型
                        mapson.put("action", approval.action());//执行动作
                        sonAction.add(mapson);
                    }
                }
            }
        }
        if (approval_list.size() > 0) {
            //添加类型下面的执行动作
            for (Map<String, Object> map : approval_list) {
                Set<Map<String, Object>> sonActionList = new HashSet<Map<String, Object>>();
                String f_type = map.get("value").toString();
                for (Map<String, Object> son_action : sonAction) {
                    String stype = son_action.get("value").toString();
                    if (f_type.equals(stype)) {
                        sonActionList.add(son_action);
                    }
                }
                map.put("actions", sonActionList);
            }
            logger.info(approval_list);
        }
        logger.info("======ApprovalScan End scan annontations @Approval======");
    }
}
