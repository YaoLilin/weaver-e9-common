package com.customization.yll.common.web.exception;

import com.customization.yll.common.exception.FrontErrorMessage;

/**
 * @author 姚礼林
 * @desc 接口返回结果错误
 * @date 2024/7/5
 */
public class ApiResultFailedException extends RuntimeException implements FrontErrorMessage {
    public ApiResultFailedException(String message) {
        super(message);
    }

    public ApiResultFailedException(String message, Throwable cause) {
        super(message,cause);
    }
}
