package com.customization.yll.common.exception;

/**
 * @author yaolilin
 * @desc 流程表单字段未找到异常
 * @date 2024/12/23
 **/
public class FieldNotFoundException extends RuntimeException implements FrontErrorMessage {
    private String fieldName;

    public FieldNotFoundException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }
}
