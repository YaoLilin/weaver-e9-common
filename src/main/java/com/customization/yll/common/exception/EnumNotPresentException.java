package com.customization.yll.common.exception;

/**
 * @author yaolilin
 * @desc 枚举未找到
 * @date 2024/8/23
 **/
public class EnumNotPresentException extends RuntimeException{
    public EnumNotPresentException() {
    }

    public EnumNotPresentException(String message) {
        super(message);
    }
}
