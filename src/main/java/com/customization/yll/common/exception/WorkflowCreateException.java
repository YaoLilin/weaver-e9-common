package com.customization.yll.common.exception;

/**
 * @author 姚礼林
 * @desc 流程创建异常
 * @date 2025/7/7
 **/
public class WorkflowCreateException extends RuntimeException{
    public WorkflowCreateException() {
    }

    public WorkflowCreateException(String message) {
        super(message);
    }

    public WorkflowCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
