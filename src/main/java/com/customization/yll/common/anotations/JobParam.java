package com.customization.yll.common.anotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * 计划任务参数注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JobParam {
    boolean required() default false;
    String desc() default "";
    String defaultValue() default "";
}
