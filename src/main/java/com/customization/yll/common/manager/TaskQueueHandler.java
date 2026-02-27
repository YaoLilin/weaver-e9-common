package com.customization.yll.common.manager;

import cn.hutool.core.thread.NamedThreadFactory;
import com.customization.yll.common.exception.QueueTaskHandleException;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author 姚礼林
 * @desc 异步队列处理，可将数据添加到队列中，然后异步依次处理
 * @date 2024/7/8
 */
public class TaskQueueHandler {
    private final LinkedBlockingQueue<QueueTask> eventDataQueue;
    private ExecutorService executorService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private volatile boolean isRunning = false;
    private Future<Boolean> taskResult;

    public TaskQueueHandler() {
        eventDataQueue = new LinkedBlockingQueue<>();
        createExecutorService();
    }

    public synchronized void putTask(QueueTask queueTask) throws InterruptedException {
        eventDataQueue.put(queueTask);
        try {
            if (!isRunning) {
                if (executorService.isShutdown() && taskResult.get()) {
                    createExecutorService();
                } else {
                    executeTask();
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            createExecutorService();
            logger.error("获取执行结果发生异常", e);
        }
    }

    private void executeTask() {
        isRunning = true;
        taskResult = executorService.submit(() -> {
            while (isRunning) {
                try {
                    eventDataQueue.take().handleEvent();
                } catch (QueueTaskHandleException e) {
                    logger.error("队列任务处理失败", e);
                } catch (InterruptedException e) {
                    logger.error("从队列中取出发生异常", e);
                    isRunning = false;
                }
            }
            return true;
        });
    }

    private void createExecutorService() {
        executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new NamedThreadFactory("feishu-event-handle", false));
        executeTask();
        logger.info("线程池已启动");
    }

    public void shutdownService() {
        isRunning = false;
        executorService.shutdown();
        logger.info("线程池关闭");
    }
}
