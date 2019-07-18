package com.ctr.crm.interceptors;

import com.ctr.crm.moduls.hrm.models.Worker;

public class CurrentWorkerLocalCache {
	private static ThreadLocal<Worker> currentWorkerLocal = new ThreadLocal<Worker>();

	public static Worker getCurrentWorker() {
		return currentWorkerLocal.get();
	}

	public static void setCurrentWorker(Worker currentWorker) {
		currentWorkerLocal.set(currentWorker);
	}

}
