package com.customization.yll.common.web;

import java.lang.annotation.*;

/**
 * @author 姚礼林
 * @desc API 参数注解，可用于标记接口参数
 * @date 2025/12/17
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Documented
public @interface ApiParam {
    /**
     * 字段显示名称，也就是字段的中文名称
     */
    String displayName() default "";

    String description() default "";

    /**
     * 字段是否必需传入
     */
    boolean required() default false;

    /**
     * 接口字段值的示例
     */
    String example() default "";
}
