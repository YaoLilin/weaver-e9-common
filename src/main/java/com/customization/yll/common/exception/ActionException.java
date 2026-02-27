package com.customization.yll.common.exception;

/**
 * @author 姚礼林
 * @desc 流程 Action 异常
 * @date 2025/8/13
 **/
public class ActionException extends RuntimeException {
    public ActionException() {
    }

    public ActionException(String message) {
        super(message);
    }

    public ActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
