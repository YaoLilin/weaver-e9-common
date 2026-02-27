package com.customization.yll.common.exception;

/**
 * @author 姚礼林
 * @desc 配置文件不存在该属性异常
 * @date 2024/6/13
 */
public class PropNotConfigureException extends ConfigurationException{
    public PropNotConfigureException(String message) {
        super(message);
    }

    public PropNotConfigureException() {
    }
}
