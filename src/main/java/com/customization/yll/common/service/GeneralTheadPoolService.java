package com.customization.yll.common.service;

import cn.hutool.core.thread.NamedThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 通用线程池
 *
 * @author yll
 */
public enum GeneralTheadPoolService {
    /**
     * 线程池实例
     */
    INSTANCE;
    private ExecutorService service;

    GeneralTheadPoolService() {
        service = createThreadPool();
    }

    public void putTask(Task task) {
        if (service.isShutdown()) {
            synchronized (GeneralTheadPoolService.this) {
                if (service.isShutdown()) {
                    service = createThreadPool();
                }
            }
        }
        service.execute(task::handle);
    }

    private ExecutorService createThreadPool() {
        return new ThreadPoolExecutor(32, 64, 1, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(700)
                , new NamedThreadFactory("GeneralTheadPoolService", false)
                , new ThreadPoolExecutor.AbortPolicy());
    }

}
