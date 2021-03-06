package com.gen.utils;

import java.util.LinkedList;

import com.gen.task.ControllerTask;
import com.gen.task.DaoTask;
import com.gen.task.EntityTask;
import com.gen.task.ServiceTask;

public class TaskQueue<E> extends LinkedList<E> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 695906814920860792L;

	/**
     * 根据类型检查是否配置了相应的代码路径，未配置则不添加任务
     *
     * @param task 任务
     * @return
     */
    @Override
    public boolean add(E task) {
        if (task instanceof ControllerTask) {
            if (StringUtil.isBlank(ConfigUtil.configuration.getPath().getController())) {
                return false;
            }
        }
        if (task instanceof ServiceTask) {
            if (StringUtil.isBlank(ConfigUtil.configuration.getPath().getService())) {
                return false;
            }
        }
        if (task instanceof DaoTask) {
            if (StringUtil.isBlank(ConfigUtil.configuration.getPath().getDao())) {
                return false;
            }
        }
        if (task instanceof EntityTask) {
            if (StringUtil.isBlank(ConfigUtil.configuration.getPath().getEntity())) {
                return false;
            }
        }
        return super.add(task);
    }
}
