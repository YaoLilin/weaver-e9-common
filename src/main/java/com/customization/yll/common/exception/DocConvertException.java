package com.customization.yll.common.exception;

/**
 * @author 姚礼林
 * @desc 文档转换失败异常
 * @date 2025/6/17
 **/
public class DocConvertException extends RuntimeException{
    public DocConvertException() {
    }

    public DocConvertException(String message) {
        super(message);
    }

    public DocConvertException(String message, Throwable cause) {
        super(message, cause);
    }
}
