package com.customization.yll.common.exception;

/**
 * @author 姚礼林
 * @desc 配置文件里的属性没有配置相关值异常
 * @date 2024/7/15
 */
public class ValueNotConfigureException extends ConfigurationException{
    public ValueNotConfigureException() {
    }

    public ValueNotConfigureException(String message) {
        super(message);
    }
}
