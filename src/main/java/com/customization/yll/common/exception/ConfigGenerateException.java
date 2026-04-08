package com.customization.yll.common.exception;

/**
 * 配置属性对象生成异常。
 * <p>
 * 当通过 {@code PropertiesGenerator} 生成配置属性对象时，若反射实例化或字段注入失败，
 * 则抛出此异常。
 * </p>
 *
 * @author 姚礼林
 * @date 2026/3/25
 */
public class ConfigGenerateException extends RuntimeException {

    public ConfigGenerateException(String message) {
        super(message);
    }

    public ConfigGenerateException(String message, Throwable cause) {
        super(message, cause);
    }
}
