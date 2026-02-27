package com.customization.yll.common.web.exception;

import com.customization.yll.common.exception.FrontErrorMessage;

/**
 * api 调用异常
 */
public class ApiCallException extends RuntimeException implements FrontErrorMessage {
    public ApiCallException() {
    }

    public ApiCallException(String message) {
        super(message);
    }

    public ApiCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
