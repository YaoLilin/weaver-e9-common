package com.customization.yll.common.config;

import java.lang.annotation.*;

/**
 * 建模配置属性注解。
 * <p>
 * 可配合 {@link ModePropertiesGenerator} 使用，通过注解方式指定建模配置中心的配置 id 和属性名称，
 * 自动通过 ModeConfigUtil 注入配置属性值。
 * </p>
 *
 * @author 姚礼林
 * @date 2026/3/25
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ModeConfigProperty {
    /**
     * 配置id，为在建模中配置的id
     */
    String configId();

    /**
     * 配置项名称 , 如果为空则使用当前字段名称
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
