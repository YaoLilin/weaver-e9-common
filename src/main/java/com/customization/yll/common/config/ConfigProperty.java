package com.customization.yll.common.config;

import java.lang.annotation.*;

/**
 * @author 姚礼林
 * @desc 配置属性信息，可配合 {@link PropertiesGenerator} 使用，通过注解方式指定配置文件和配置项名称，自动注入配置属性值
 * @date 2026/3/25
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ConfigProperty {
    /**
     * 配置文件名称，不带扩展名后缀
     */
    String fileName();

    /**
     * 配置项名称，如果为空则使用当前字段名称
     */
    String name() default "";

    /**
     * 是否必填，如果为必填，获取到的配置属性值为空时会抛出异常
     */
    boolean required() default false;

    /**
     * 是否使用缓存
     */
    boolean cache() default false;

    /**
     * 缓存过期时间
     */
    int expireSeconds() default 180;
}
