package com.customization.yll.common.exception;

/**
 * 定时任务异常
 */
public class CronJobException extends RuntimeException {

    public CronJobException(String message) {
        super(message);
    }

    public CronJobException(String message, Throwable cause) {
        super(message, cause);
    }
}
