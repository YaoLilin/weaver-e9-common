package com.customization.yll.common.exception;

/**
 * 字段校验不通过
 * @author yaolilin
 */
public class FieldInvalidException extends RuntimeException{
    public FieldInvalidException() {
    }

    public FieldInvalidException(String message) {
        super(message);
    }
}
