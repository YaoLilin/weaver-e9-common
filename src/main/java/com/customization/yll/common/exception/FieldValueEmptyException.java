package com.customization.yll.common.exception;

/**
 * @author 姚礼林
 * @desc 字段值空异常
 * @date 2024/7/10
 */
public class FieldValueEmptyException extends RuntimeException{
    private String fieldName;

    public FieldValueEmptyException(String message) {
        super(message);
    }

    public FieldValueEmptyException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
