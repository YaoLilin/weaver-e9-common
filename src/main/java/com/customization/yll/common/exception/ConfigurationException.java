package com.customization.yll.common.exception;

/**
 * @author yaolilin
 * @desc 配置异常
 * @date 2024/8/27
 **/
public class ConfigurationException extends RuntimeException{
    public ConfigurationException() {
    }

    public ConfigurationException(String message) {
        super(message);
    }
}
